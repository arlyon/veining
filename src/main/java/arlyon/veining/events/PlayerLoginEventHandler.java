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

import arlyon.veining.Configuration;
import arlyon.veining.network.PacketHandler;
import arlyon.veining.network.VeiningSettingsMessage;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerLoginEventHandler {

    /**
     * Sends a packet to the server when the client connects with the client's veining settings.
     *
     * @param event The event that is called when an entity joins the world.
     */
    @SubscribeEvent
    public void registerPlayerSettings(EntityJoinWorldEvent event) {
        if (event.getEntity() == Minecraft.getMinecraft().player) {
            PacketHandler.INSTANCE.sendToServer(
                new VeiningSettingsMessage(
                    Configuration.clientSide.disableWhenCrouched,
                    Configuration.clientSide.disableWhenStanding
                )
            );
        }
    }
}
