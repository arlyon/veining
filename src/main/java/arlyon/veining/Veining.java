package arlyon.veining;

import arlyon.veining.network.PlayerSettings;
import arlyon.veining.proxy.ProxyCommon;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * The main entry point into the mod.
 */
@Mod(
        modid = Veining.MOD_ID,
        name = Veining.MOD_NAME,
        version = Veining.MOD_VERSION,
        updateJSON = Veining.UPDATE_JSON,
        acceptedMinecraftVersions = Veining.MINECRAFT_VERSIONS,
        dependencies = "required-after:forge@[14.23.1,);after:tconstruct@[1.12-2.9,);after:mantle@[1.12-1.3.1,)"
)
public class Veining {

    static final String MOD_NAME = "Veining";
    public static final String MOD_ID = "veining";
    static final String MOD_VERSION = "1.2.2";
    static final String UPDATE_JSON = "https://raw.githubusercontent.com/arlyon/veining/1.12.x/update.json";
    static final String MINECRAFT_VERSIONS = "[1.12.0, 1.12.2]"; // starting with 1.12, up to 1.12.2

    public static final VeiningEnchantment veining = new VeiningEnchantment(
            net.minecraft.enchantment.Enchantment.Rarity.UNCOMMON,
            EntityEquipmentSlot.MAINHAND
    );

    public static final Map<Integer, PlayerSettings> playerSettings = new HashMap<>();
    public static Logger log;

    @SidedProxy(clientSide = "arlyon.veining.proxy.ProxyClient", serverSide = "arlyon.veining.proxy.ProxyServer")
    private static ProxyCommon proxy;

    /**
     * Passes the pre-initialization event onwards to the proxy.
     * @param e The pre-initialization event.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        proxy.preInit(e);
        log = e.getModLog();
    }

    /**
     * Passes the initialization event onwards to the proxy.
     * @param e The initialization event.
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) { proxy.init(e); }

    /**
     * Passes the post-initialization event onwards to the proxy.
     * @param e The post-initialization event.
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) { proxy.postInit(e); }

    /**
     * Sets up some event handlers.
     */
    @Mod.EventBusSubscriber
    public static class EnchantmentHandler {

        /**
         * Registers the veining enchantment when the VeiningEnchantment register event fires.
         * @param event The enchantment register event.
         */
        @SubscribeEvent
        public static void registerEnchantment(RegistryEvent.Register<net.minecraft.enchantment.Enchantment> event) {
            event.getRegistry().register(Veining.veining);
        }
    }
}
