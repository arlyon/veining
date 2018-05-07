package arlyon.veining;

import arlyon.veining.util.ValueUniqueQueue;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;
import java.util.stream.Collectors;

import static net.minecraft.init.Enchantments.FORTUNE;
import static net.minecraft.init.Enchantments.SILK_TOUCH;

public class VeiningAlgorithm {

    public static boolean veiningAlgorithm(BlockPos blockPosition, World world, EntityPlayer player) {
        return VeiningAlgorithm.veiningAlgorithm(blockPosition, world, player, Configuration.serverSide.maxBlocks);
    }

    /**
     * Breaks the block at a given position and then for
     * each path continues felling on that block as well.
     *
     * @param blockPosition The position of the block.
     * @param world         The world.
     * @param player     The player.
     */
    public static boolean veiningAlgorithm(BlockPos blockPosition, World world, EntityPlayer player, int maxBlocks) {
        String veinType = getOreType(world, blockPosition, player);
        if (veinType == null) return false;

        ValueUniqueQueue<BlockPos> blocksToBreak = new ValueUniqueQueue<>(value -> value <= Configuration.serverSide.maxDistance, Integer::compareTo);
        blocksToBreak.add(blockPosition, 0);
        int blocksBroken = 0;

        while (
            !blocksToBreak.isEmpty() &&
            (blocksBroken < maxBlocks || maxBlocks == 0) &&
            player.getHeldItemMainhand() != ItemStack.EMPTY
        ) {
            blockPosition = blocksToBreak.peek(); // next block
            int distance = blocksToBreak.getValue(blockPosition);
            blocksToBreak.remove();

            if (distance == 0) {
                breakBlock(blockPosition, world, player);
                blocksBroken += 1;
                player.getHeldItemMainhand().damageItem(Configuration.serverSide.durabilityDamage, player);
            }

            for (EnumFacing dir : EnumFacing.values()) {
                BlockPos nextBlockPosition = blockPosition.offset(dir);

                if (shouldBreak(veinType, world, nextBlockPosition, player)) {
                    blocksToBreak.add(nextBlockPosition, 0);
                } else {
                    blocksToBreak.add(nextBlockPosition, distance + 1);
                }
            }
        }

        return true;
    }

    /**
     * Attempts to break a block, doing the appropriate damage to the tool in the process.
     * Additionally silk harvests if needed.
     *
     * @param blockPosition The position of the block that is to be broken.
     * @param world The current world state.
     * @param thePlayer The player executing the enchantment.
     */
    private static void breakBlock(BlockPos blockPosition, World world, EntityPlayer thePlayer) {

        if (!thePlayer.capabilities.isCreativeMode) {

            int silkTouch = EnchantmentHelper.getEnchantmentLevel(SILK_TOUCH, thePlayer.getHeldItemMainhand());

            if (
                world.getBlockState(blockPosition).getBlock().canSilkHarvest(world, blockPosition, world.getBlockState(blockPosition), thePlayer) &&
                silkTouch == 1 &&
                Configuration.serverSide.silkTouch
            ) {
                world.getBlockState(blockPosition).getBlock().harvestBlock(
                        world,
                        thePlayer,
                        blockPosition,
                        world.getBlockState(blockPosition),
                        null,
                        thePlayer.getHeldItemMainhand()
                );
            } else {
                world.getBlockState(blockPosition).getBlock().dropBlockAsItem(world, blockPosition, world.getBlockState(blockPosition), mainHandFortuneLevel(thePlayer)); // drop the block
                world.getBlockState(blockPosition).getBlock().dropXpOnBlockBreak( // drop the xp
                        world,
                        blockPosition,
                        world.getBlockState(blockPosition).getBlock().getExpDrop(
                                world.getBlockState(blockPosition),
                                world,
                                blockPosition,
                                mainHandFortuneLevel(thePlayer)
                        )
                );
            }
        }

        world.setBlockToAir(blockPosition); // delete the block
    }

    /**
     * Determines if, given the config files, the block should be veined.
     *
     * @return A boolean value indicating whether the block should be destroyed.
     */
    private static boolean shouldBreak(String veinType, World world, BlockPos position, EntityPlayer player) {
        String oreType = getOreType(world, position, player);

        return (veinType != null && oreType != null) && // should break only when veinType & oreType != null and
                (Configuration.serverSide.multiOre || oreType.equals(veinType)); // when multiOre == true or the oreType == veinType
    }

    /**
     * Gets the fortune level of the tool in the player's main hand.
     * @param player The player who is holding the tool.
     * @return The enchantment level.
     */
    private static int mainHandFortuneLevel(EntityPlayer player) {
        return EnchantmentHelper.getEnchantmentLevel(FORTUNE, player.getHeldItemMainhand());
    }

    /**
     * Returns the type of ore from a given block state.
     *
     * @param world The world that contains the block to poll.
     * @param position The position of the block to poll.
     * @param player The player breaking the block.
     * @return A string containing the type of ore, or null if it isn't one.
     */
    private static String getOreType(World world, BlockPos position, EntityPlayer player) {

        ItemStack stack;
        String oreName;
        IBlockState blockState = world.getBlockState(position);

        stack = new ItemStack(blockState.getBlock().getItemDropped(blockState, new Random(), mainHandFortuneLevel(player)));
        if (stack.isEmpty()) return null;

        oreName = Arrays.stream(OreDictionary.getOreIDs(stack))
                .mapToObj(OreDictionary::getOreName)
                .filter(name -> name.contains("ore") || name.contains("dust") || name.contains("gem"))
                .findFirst()
                .orElse(null);

        if (oreName != null) return oreName;

        NonNullList<ItemStack> itemDrops = NonNullList.create();
        blockState.getBlock().getDrops(itemDrops, world, position, blockState, mainHandFortuneLevel(player));

        oreName = itemDrops.stream()
                .map(item ->
                        Arrays.stream(OreDictionary.getOreIDs(item))
                                .mapToObj(OreDictionary::getOreName)
                                .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .filter(name -> name.contains("ore") || name.contains("dust") || name.contains("gem"))
                .findFirst()
                .orElse(null);

        return oreName;
    }
}
