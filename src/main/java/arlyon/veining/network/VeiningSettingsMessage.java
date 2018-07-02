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

package arlyon.veining.network;

import arlyon.veining.Veining;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class VeiningSettingsMessage implements IMessage {

    private boolean disableWhenCrouched;
    private boolean disableWhenStanding;

    /**
     * A default constructor is required.
     */
    public VeiningSettingsMessage() {
    }

    /**
     * Creates a new instance of the veining settings message.
     *
     * @param disableWhenCrouched Whether the client has the option enabled.
     * @param disableWhenStanding Whether the client has the option enabled.
     */
    public VeiningSettingsMessage(boolean disableWhenCrouched, boolean disableWhenStanding) {
        this.disableWhenCrouched = disableWhenCrouched;
        this.disableWhenStanding = disableWhenStanding;
    }

    /**
     * Writes the boolean values to the buffer.
     *
     * @param byteBuf The buffer to write to.
     */
    @Override
    public void toBytes(ByteBuf byteBuf) {
        byteBuf.writeBoolean(disableWhenCrouched);
        byteBuf.writeBoolean(disableWhenStanding);
    }

    /**
     * Reads the boolean values from the buffer.
     *
     * @param byteBuf The buffer to read from.
     */
    @Override
    public void fromBytes(ByteBuf byteBuf) {
        // Reads the booleans back from the buffer (in the same order they were written to it)
        disableWhenCrouched = byteBuf.readBoolean();
        disableWhenStanding = byteBuf.readBoolean();
    }

    /**
     * Handles incoming messages.
     */
    public static class Handler implements IMessageHandler<VeiningSettingsMessage, IMessage> {

        /**
         * When receiving a message, add a new task on the world thread to handle the message.
         *
         * @param message In incoming message.
         * @param ctx     The message context.
         * @return Nothing. Can return a reply message.
         */
        @Override
        public IMessage onMessage(VeiningSettingsMessage message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(VeiningSettingsMessage message, MessageContext ctx) {
            // This code is run on the server side. So you can do server-side calculations here
            EntityPlayerMP playerEntity = ctx.getServerHandler().player;
            Veining.playerSettings.put(playerEntity.getGameProfile().hashCode(), new PlayerSettings(message.disableWhenCrouched, message.disableWhenStanding));
            Veining.log.debug(String.format("%s sent client side settings to server.", playerEntity.getName()));
        }
    }
}
