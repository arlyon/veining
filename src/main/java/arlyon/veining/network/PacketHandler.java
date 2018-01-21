package arlyon.veining.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Used to create a channel to send messages from the client to the server.
 */
public class PacketHandler {

    private static int packetId = 0;

    public static SimpleNetworkWrapper INSTANCE = null;

    public PacketHandler() {
    }

    /**
     * Gets the next packet id.
     * @return The next packet id.
     */
    private static int nextID() {
        return packetId++;
    }

    /**
     * Called to set up a client->server channel on the packet handler.
     * @param channelName The name of the channel.
     */
    public static void registerMessages(String channelName) {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
        registerMessages();
    }

    /**
     * Registers the messages which are to be sent from the client to the server.
     */
    private static void registerMessages() {
        INSTANCE.registerMessage(
                VeiningSettingsMessage.Handler.class,
                VeiningSettingsMessage.class,
                nextID(),
                Side.SERVER
        );
    }
}
