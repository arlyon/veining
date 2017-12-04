package arlyon.veining.core.proxy;

import arlyon.veining.events.VeiningEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ProxyCommon {

    public void preInit(FMLPreInitializationEvent e) {
        // PacketHandler.registerMessages("veining"); - to be used later when splitting client and server config
        MinecraftForge.EVENT_BUS.register(new VeiningEventHandler());
    }

    public void init(FMLInitializationEvent e) { }

    public void postInit(FMLPostInitializationEvent e) { }
}
