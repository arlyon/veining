/*
 * veining (c) by Alexander Lyon
 *
 * veining is licensed under a
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by-nc-sa/4.0/>
 */

/*
 * veining (c) by arlyon
 *
 * veining is licensed under a
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by-nc-sa/4.0/>
 */

package arlyon.veining;

import arlyon.veining.util.WeightedUniqueQueue;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;

import static net.minecraft.init.Enchantments.FORTUNE;

public class VeiningAlgorithm {

    private static VeiningAlgorithm instance;
    private final WeightedUniqueQueue<BlockPos> blocksToBreak = new WeightedUniqueQueue<>(value -> value <= Configuration.serverSide.maxDistance, Integer::compareTo);

    private VeiningAlgorithm() {
    }

    public static VeiningAlgorithm getInstance() {
        if (instance == null) instance = new VeiningAlgorithm();
        return instance;
    }

    /**
     * Attempts to harvest a block, doing the appropriate damage to the tool in the process.
     * Additionally silk harvests if needed.
     *
     * @param pos    The position of the block that is to be broken.
     * @param world  The current world state.
     * @param player The player executing the enchantment.
     * @return True if the block was broken.
     */
    private static boolean tryHarvestBlock(BlockPos pos, World world, EntityPlayer player) {

        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (world.isAirBlock(pos)) return false; // isn't a block
        if (!ForgeHooks.canHarvestBlock(block, player, world, pos)) return false; // cant harvest

        if (!world.isRemote) {
            int xpToDrop = ForgeHooks.onBlockBreakEvent(world, ((EntityPlayerMP) player).interactionManager.getGameType(), (EntityPlayerMP) player, pos);
            if (xpToDrop == -1) return false; // block break event was cancelled

            if (!block.removedByPlayer(state, world, pos, player, !player.capabilities.isCreativeMode))
                return false; // block wasn't removed
            block.onBlockDestroyedByPlayer(world, pos, state); // trigger block break function

            if (!player.capabilities.isCreativeMode) {
                block.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getHeldItemMainhand());
                if (xpToDrop > 0) {
                    block.dropXpOnBlockBreak(world, pos, xpToDrop);
                }
            }

            ((EntityPlayerMP) player).connection.sendPacket(new SPacketBlockChange(world, pos)); // tell the client
        } else {
            if (!block.removedByPlayer(state, world, pos, player, !player.capabilities.isCreativeMode))
                return false; // block wasn't removed
            block.onBlockDestroyedByPlayer(world, pos, state); // trigger block break function
        }

        return true;
    }

    /**
     * Determines if, given the config files, the block should be veined.
     *
     * @return A boolean value indicating whether the block should be destroyed.
     */
    private static boolean shouldBreak(String veinType, World world, BlockPos position, EntityPlayer player) {
        String oreType = getOreType(world, position, player);
        return
            (veinType != null && oreType != null) &&
                (Configuration.serverSide.multiOre || oreType.equals(veinType));
    }

    /**
     * Returns the type of ore from a given block state.
     *
     * @param world    The world that contains the block to poll.
     * @param position The position of the block to poll.
     * @param player   The player breaking the block.
     * @return A string containing the type of ore, or null if it isn't one.
     */
    private static String getOreType(World world, BlockPos position, EntityPlayer player) {

        ItemStack stack;
        String oreName;
        IBlockState blockState = world.getBlockState(position);

        stack = new ItemStack(blockState.getBlock().getItemDropped(blockState, new Random(), EnchantmentHelper.getEnchantmentLevel(FORTUNE, player.getHeldItemMainhand())));
        if (stack.isEmpty()) return null;

        oreName = Arrays.stream(OreDictionary.getOreIDs(stack))
            .mapToObj(OreDictionary::getOreName)
            .filter(name -> name.contains("ore") || name.contains("dust") || name.contains("gem"))
            .findFirst()
            .orElse(null);

        if (oreName != null) return oreName;

        NonNullList<ItemStack> itemDrops = NonNullList.create();
        blockState.getBlock().getDrops(itemDrops, world, position, blockState, EnchantmentHelper.getEnchantmentLevel(FORTUNE, player.getHeldItemMainhand()));

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

    public boolean veiningAlgorithm(BlockPos blockPosition, World world, EntityPlayer player) {
        return veiningAlgorithm(blockPosition, world, player, Configuration.serverSide.maxBlocks);
    }

    /**
     * Breaks the block at a given position and then for
     * each path continues veining on that block as well.
     *
     * @param blockPosition The position of the block.
     * @param world         The world.
     * @param player        The player.
     * @param maxBlocks     The max number of blocks to break.
     * @return Whether the block was broken.
     */
    public boolean veiningAlgorithm(BlockPos blockPosition, World world, EntityPlayer player, int maxBlocks) {
        if (blocksToBreak.contains(blockPosition))
            return false;

        String veinType = getOreType(world, blockPosition, player);
        if (veinType == null) return false;

        blocksToBreak.add(blockPosition, 0);
        int blocksBroken = 0;

        while (
            !blocksToBreak.isEmpty() &&
                (blocksBroken < maxBlocks || maxBlocks == 0) &&
                player.getHeldItemMainhand() != ItemStack.EMPTY
            ) {
            WeightedUniqueQueue<BlockPos>.WeightedPair pair = blocksToBreak.remove();

            if (pair.weight == 0 && tryHarvestBlock(pair.element, world, player)) {
                blocksBroken += 1;
                if (!world.isRemote)
                    player.getHeldItemMainhand().attemptDamageItem(Configuration.serverSide.durabilityDamage, new Random(), (EntityPlayerMP) player);
            }

            for (EnumFacing dir : EnumFacing.values()) {
                BlockPos nextBlockPosition = pair.element.offset(dir);
                if (shouldBreak(veinType, world, nextBlockPosition, player))
                    blocksToBreak.add(nextBlockPosition, 0);
                else
                    blocksToBreak.add(nextBlockPosition, pair.weight + 1);
            }
        }

        blocksToBreak.reset();
        return true;
    }
}
