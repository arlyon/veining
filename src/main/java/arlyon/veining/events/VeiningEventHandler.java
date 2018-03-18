package arlyon.veining.events;

import arlyon.veining.Configuration;
import arlyon.veining.Veining;
import arlyon.veining.network.PlayerSettings;
import arlyon.veining.support.UniqueQueue;
import net.minecraft.block.BlockOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;
import java.util.stream.Collectors;

import static net.minecraft.init.Enchantments.FORTUNE;
import static net.minecraft.init.Enchantments.SILK_TOUCH;

/**
 * The veining event subscriber class, which contains all the required functions for the subscriber.
 */
public class VeiningEventHandler {

    /**
     * Intercepts the block break event to inject the veining enchantment logic.
     *
     * @param event the block break event that is called each time a minecraft block is broken.
     */
    @SubscribeEvent
    public void veiningSubscriber(BreakEvent event) {
        if (shouldStartVeining(event)) veiningAlgorithm(
            event.getPos(),
            event.getWorld(),
            event.getPlayer(),
            getOreType(event.getState(), event.getPos(), event.getPlayer())
        );
    }

    /**
     * Makes some checks to see if it is a valid felling event.
     *
     * @param event The break event.
     * @return Whether the felling should run.
     */
    private boolean shouldStartVeining(BreakEvent event) {
        return mainHandFellingLevel(event.getPlayer()) > 0 &&
                eventIsServerSide(event) &&
                configAllowsBreak(event.getPlayer()) &&
                getOreType(event.getState(), event.getPos(), event.getPlayer()) != null;
    }

    /**
     * Checks if the event is server-side (mainly for readability).
     *
     * @param event The break event.
     * @return Whether the event is being called on the server side.
     */
    private static boolean eventIsServerSide(BlockEvent.BreakEvent event) {
        return !event.getWorld().isRemote; // remote compared to the server
    }

    /**
     * A simple check to see if the player that caused the break event has the enchantment in their main hand.
     *
     * @param player The player who is holding the tool.
     * @return The enchantment level.
     */
    private static int mainHandFellingLevel(EntityPlayer player) {
        return EnchantmentHelper.getEnchantmentLevel(Veining.veining, player.getHeldItemMainhand());
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
     * Checks if the block should break according to the player's settings.
     * @param player The player to check.
     * @return Whether the current server settings allow the break.
     */
    private static boolean configAllowsBreak(EntityPlayer player) {
        PlayerSettings playerSettings = getOrCreatePlayerSettings(player);
        return player.isSneaking() ? !playerSettings.disableWhenCrouched : !playerSettings.disableWhenStanding;
    }

    /**
     * Given a player, gets or creates a player settings profile for a user.
     *
     * @param thePlayer The player to check.
     * @return The given player's settings.
     */
    private static PlayerSettings getOrCreatePlayerSettings(EntityPlayer thePlayer) {
        PlayerSettings playerSettings = Veining.playerSettings.get(thePlayer.getGameProfile().hashCode());

        if (playerSettings == null) {
            playerSettings = new PlayerSettings(true, true);
            thePlayer.sendMessage(new TextComponentString("Your Felling settings aren't synced with the server. Please update the settings in the mod config to resend them."));
        }

        return playerSettings;
    }

    /**
     * Returns the type of ore from a given block state.
     *
     * @param blockState The block state of the block to poll.
     * @return A string containing the type of ore, or null if it isn't one.
     */
    private static String getOreType(IBlockState blockState, BlockPos position, EntityPlayer player) {

        ItemStack stack;
        String oreName;

        stack = new ItemStack(blockState.getBlock().getItemDropped(blockState, null, mainHandFortuneLevel(player)));
        if (stack.isEmpty()) return null;

        oreName = Arrays.stream(OreDictionary.getOreIDs(stack))
                .mapToObj(OreDictionary::getOreName)
                .filter(name -> name.contains("ore") || name.contains("dust") || name.contains("gem"))
                .findFirst()
                .orElse(null);

        if (oreName != null) return oreName;

        List<ItemStack> itemDrops = blockState.getBlock().getDrops(player.getEntityWorld(), position, blockState, mainHandFortuneLevel(player));
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

    /**
     * Breaks the block at a given position and then for
     * each path continues felling on that block as well.
     *
     * @param blockPosition The position of the block.
     * @param world         The world.
     * @param player     The player.
     * @param veinType      The type of vein.
     */
    private static void veiningAlgorithm(BlockPos blockPosition, World world, EntityPlayer player, String veinType) {
        Queue<BlockPos> blocks = new UniqueQueue<>();
        blocks.offer(blockPosition);

        while (blocks.size() > 0) {
            blockPosition = blocks.remove();

            breakBlock(blockPosition, world, player);
            if (mainHandBreaksWhenDamaged(player)) return;

            getSurroundingBlocks(blockPosition, world, veinType, player).forEach(blocks::offer);
        }
    }

    /**
     * Checks all the blocks reached from the list of paths and checks
     * if they are valid breaks before returning the lost of all valid.
     *
     * @param blockPosition The starting position.
     * @param world         The world.
     * @param veinType      The vein type.
     * @return The block positions that are valid blocks to break.
     */
    private static Collection<BlockPos> getSurroundingBlocks(BlockPos blockPosition, World world, String veinType, EntityPlayer player) {
        List<BlockPos> newBlocks = new LinkedList<>();

        for (EnumFacing dir : EnumFacing.values()) {
            BlockPos nextBlockPosition = blockPosition.offset(dir);

            if (shouldBreak(veinType, world.getBlockState(nextBlockPosition), nextBlockPosition, player)) {
                newBlocks.add(nextBlockPosition);
            }
        }

        return newBlocks;
    }

    /**
     * Determines if, given the config files, the block should be veined.
     *
     * @return A boolean value indicating whether the block should be destroyed.
     */
    private static boolean shouldBreak(String veinType, IBlockState blockState, BlockPos position, EntityPlayer player) {
        String oreType = getOreType(blockState, position, player);

        return (veinType != null && oreType != null) && // should break only when veinType & oreType != null and
                (Configuration.serverSide.multiOre || oreType.equals(veinType)); // when multiOre == true or the oreType == veinType
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
     * Deals damage to the enchant and breaks if needed.
     *
     * @param thePlayer The player.
     * @return Whether the tool was broken.
     */
    private static boolean mainHandBreaksWhenDamaged(EntityPlayer thePlayer) {
        if (thePlayer.isCreative()) return false;
        if (!toolBreaksWhenDamaged((EntityPlayerMP) thePlayer, thePlayer.getHeldItemMainhand())) return false;

        thePlayer.inventory.deleteStack(thePlayer.getHeldItemMainhand());
        return true;
    }

    /**
     * Damages the given item and returns whether it should break.
     *
     * @param thePlayer The player to deal damage to.
     * @param theTool   The tool to deal damage to.
     * @return Whether the tool breaks.
     */
    private static boolean toolBreaksWhenDamaged(EntityPlayerMP thePlayer, ItemStack theTool) {
        return theTool.attemptDamageItem(
                Configuration.serverSide.durabilityDamage,
                new Random(),
                thePlayer);
    }
}
