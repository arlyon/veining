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

package arlyon.veining.events;

import arlyon.veining.Veining;
import arlyon.veining.VeiningAlgorithm;
import arlyon.veining.network.PlayerSettings;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * The veining event subscriber class, which contains all the required functions for the subscriber.
 */
public class VeiningEventHandler {

    /**
     * Checks if the block should break according to the player's settings.
     *
     * @param player The player to check.
     * @return Whether the current server settings allow the break.
     */
    public static boolean configAllowsBreak(EntityPlayer player) {
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
            thePlayer.sendMessage(new TextComponentString("Your Veining settings aren't synced with the server. Please update the settings in the mod config to resend them."));
        }

        return playerSettings;
    }

    /**
     * Intercepts the block break event to inject the veining enchantment logic.
     *
     * @param event the block break event that is called each time a minecraft block is broken.
     */
    @SubscribeEvent
    public void veiningBlockBreakSubscriber(BreakEvent event) {
        if (shouldStartVeining(event)) VeiningAlgorithm.getInstance().veiningAlgorithm(
            event.getPos(),
            event.getWorld(),
            event.getPlayer()
        );
    }

    /**
     * Makes some checks to see if it is a valid veining event.
     *
     * @param event The break event.
     * @return Whether the veining should run.
     */
    private boolean shouldStartVeining(BreakEvent event) {
        return EnchantmentHelper.getEnchantmentLevel(Veining.veining, event.getPlayer().getHeldItemMainhand()) > 0 &&
            configAllowsBreak(event.getPlayer());
    }
}
