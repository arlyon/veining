package arlyon.veining.proxy;

import arlyon.veining.events.VeiningEventHandler;
import arlyon.veining.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Handles things that should happen on both the client and the server side.
 */
public class ProxyCommon {

    /**
     * Registers the event handlers and packet channel.
     * @param e The pre-initialization event.
     */
    public void preInit(FMLPreInitializationEvent e) {
        PacketHandler.registerMessages("veining");
        MinecraftForge.EVENT_BUS.register(new VeiningEventHandler());
    }

    public void init(FMLInitializationEvent e) { }

    public void postInit(FMLPostInitializationEvent e) { }
}
