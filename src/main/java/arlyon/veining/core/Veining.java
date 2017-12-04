package arlyon.veining;

import arlyon.veining.core.proxy.ProxyCommon;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * The main entry point into the mod.
 */
@Mod(
        modid = Constants.MODID,
        name = Constants.MOD_NAME,
        version = Constants.VERSION,
        updateJSON="https://git.arlyon.co/minecraft/veining/snippets/17/raw",
        acceptedMinecraftVersions = "[1.12.0, 1.12.2]"
)
public class Veining {

    /**
     * The instance of the proxy.
     */
    @SidedProxy(clientSide = "arlyon.veining.core.proxy.ProxyClient", serverSide = "arlyon.veining.core.proxy.ProxyServer")
    private static ProxyCommon proxy;

    /**
     * Called on the pre-initialization event to get up the proxy.
     * @param e The pre-initialization event.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) { proxy.preInit(e); }

    /**
     * Called on the initialization event to get up the proxy.
     * @param e The initialization event.
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) { proxy.init(e); }

    /**
     * Called on the post-initialization event to get up the proxy.
     * @param e The post-initialization event.
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) { proxy.postInit(e); }

    /**
     * The enchantment registry subscriber class.
     */
    @Mod.EventBusSubscriber
    public static class EnchantmentHandler {

        /**
         * At the appropriate time to register enchantments, the subscriber registers the Veining enchantment.
         *
         * @param event The RegistryEvent.Register<FellingEnchantment> event.
         */
        @SubscribeEvent
        public static void registerEnchantment(RegistryEvent.Register<Enchantment> event) {
            event.getRegistry().register(Constants.veining);
        }
    }
}
