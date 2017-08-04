package arlyon.veining;

import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles the destroying of trees with the veining enchantment.
 */

public class EventHandler {
    @Mod.EventBusSubscriber(modid = Constants.MODID)
    public static class VeiningEventHandler {

        /**
         * Determines if, given the config files, that the block should be veined
         *
         * @return a boolean value indicating whether the block should be destroyed
         */
        private static boolean shouldBreak(String veinType, IBlockState blockState) {
            String oreType = getOreDictType(blockState);

            return (veinType != null && oreType != null) && // should break when neither are null and..
                    (Configuration.multiOre || oreType.equals(veinType)); // when either multiore is on or the oretype is equal to the veintype
        }

        private static String getOreDictType(IBlockState blockState) {

            // check for industrialcraft or actuallyadditions ores
            if (blockState.toString().matches("ic2:resource|actuallyadditions:block_misc")) {
                // if its an ic2 resource it has a property named type that defines what ore it is.

                UnmodifiableIterator<IProperty<?>> keySetIter = blockState.getProperties().keySet().iterator();
                UnmodifiableIterator<Comparable<?>> valueIter = blockState.getProperties().values().iterator();
                for (int i = 0; i < blockState.getProperties().size(); i++) {
                    Comparable<?> value = valueIter.next();
                    if (keySetIter.next().getName().equals("type")) {
                        return value.toString().matches("^.*_ore$|^ORE_.*$") ? value.toString() : null;
                    }
                }

                return null;
            }

            //

            Block defaultBlock = blockState.getBlock() == Blocks.LIT_REDSTONE_ORE ? Blocks.REDSTONE_ORE : blockState.getBlock();
            ItemStack stack = new ItemStack(defaultBlock, 1);

            if (stack.isEmpty()) {
                return null;
            }

            int[] blockIDs = OreDictionary.getOreIDs(stack);

            // check for any _ore suffix that isnt already registered
            if (blockIDs.length == 0 && blockState.toString().matches("^.*:.*_ore$")) {
                Pattern p = Pattern.compile("^.*:(.*)_ore$");
                Matcher m = p.matcher(blockState.toString());

                m.matches();

                // quark:biotite_ore -> oreBiotite
                OreDictionary.registerOre("ore"+m.group(1).substring(0,1).toUpperCase()+m.group(1).substring(1), blockState.getBlock());

                blockIDs = OreDictionary.getOreIDs(stack);
            }

            return Arrays.stream(blockIDs)
                    .mapToObj(OreDictionary::getOreName)
                    .filter(name -> name.matches("^ore(.+)"))
                    .findFirst()
                    .orElse(null);
        }

        /**
         * Intercepts the block break event to inject the veining enchantment logic.
         *
         * @param event the block break event that is called each time a minecraft block is broken.
         */
        @SubscribeEvent
        public static void veiningSubscriber(BlockEvent.BreakEvent event) {
            EntityPlayer thePlayer = event.getPlayer();
            ItemStack mainHandItem = thePlayer.getHeldItemMainhand();
            int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Constants.veining, mainHandItem);

            // ignore anything without the enchantment
            if (enchantmentLevel == 0) {
                return;
            }

            IBlockState blockState = event.getState();
            String veinType = getOreDictType(blockState);


            // if it isn't an ore, then end
            if (!shouldBreak(veinType, blockState)) {
                return;
            }

            // sneaking players break blocks normally
            if ((thePlayer.isSneaking() && Configuration.disableWhenCrouched) || (!thePlayer.isSneaking() && Configuration.disableWhenStanding)) {
                return;
            }

            World world = event.getWorld();
            BlockPos blockPosition = event.getPos();

            veiningAlgorithm(blockState, blockPosition, world, mainHandItem, thePlayer, veinType);
        }

        /**
         * Attempts to break the block and returns false if the tool breaks during the operation.
         *
         * @param blockState
         * @param blockPosition
         * @param world
         * @param mainHandItem
         * @param thePlayer
         * @return
         */
        private static boolean tryBreakBlock(IBlockState blockState, BlockPos blockPosition, World world, ItemStack mainHandItem, EntityPlayer thePlayer) {

            // delete the block
            world.setBlockToAir(blockPosition);

            // if in creative, we are done
            if (thePlayer.capabilities.isCreativeMode) {
                return true;
            }

            // drop the block
            blockState.getBlock().dropBlockAsItem(world,
                    blockPosition,
                    blockState,
                    EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, mainHandItem));

            // damage the tool, and if it is broken, return false (which signifies we should halt operation)
            if (mainHandItem.attemptDamageItem(Configuration.durabilityDamage, new Random(), (EntityPlayerMP) thePlayer)) {
                thePlayer.inventory.deleteStack(mainHandItem);
                return false;
            }

            return true;
        }

        private static void veiningAlgorithm(IBlockState blockState, BlockPos blockPosition, World world, ItemStack mainHandItem, EntityPlayer thePlayer, String veinType) {
            // try to break the block and if it fails then return
            if (!tryBreakBlock(blockState, blockPosition, world, mainHandItem, thePlayer)) {
                return;
            }

            // for each of the cardinal directions (NSEW + UD) poll for a log and recursively try and break it
            for (EnumFacing direction : EnumFacing.values()) {
                BlockPos nextBlockPosition = blockPosition.offset(direction);
                IBlockState nextBlockState = world.getBlockState(nextBlockPosition);

                // if the block in the specified direction relative to the parent block is a log, then cut it down too!
                if (shouldBreak(veinType, nextBlockState)) {
                    veiningAlgorithm(nextBlockState, nextBlockPosition, world, mainHandItem, thePlayer, veinType);
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = Constants.MODID)
    public static class EnchantmentHandler {
        @SubscribeEvent
        public static void registerEnchantment(RegistryEvent.Register<net.minecraft.enchantment.Enchantment> event) {
            event.getRegistry().register(Constants.veining);
        }
    }
}