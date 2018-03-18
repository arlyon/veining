package arlyon.veining.proxy;

import arlyon.veining.events.VeiningEventHandler;
import arlyon.veining.network.PacketHandler;
import net.minecraft.init.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Handles things that should happen on both the client and the server side.
 */
public class ProxyCommon {

    /**
     * Handles the pre-initialization event.
     *
     * Registers the event handlers and packet channel.
     * Registers Items.COAL into the OreDict to simplify the algorithm.
     * @param e The pre-initialization event.
     */
    public void preInit(FMLPreInitializationEvent e) {
        PacketHandler.registerMessages("veining");
        MinecraftForge.EVENT_BUS.register(new VeiningEventHandler());

        OreDictionary.registerOre("gemCoal", Items.COAL);

        if (Loader.isModLoaded("tconstruct")) arlyon.veining.integration.tconstruct.ProxyCommon.preInit(e);
    }

    public void init(FMLInitializationEvent e) { }

    public void postInit(FMLPostInitializationEvent e) { }
}
