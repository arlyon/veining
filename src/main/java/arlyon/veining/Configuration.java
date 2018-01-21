package arlyon.veining;

import arlyon.veining.network.VeiningSettingsMessage;
import arlyon.veining.network.PacketHandler;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Alexander Lyon on 30.07.2017.
 *
 * Controls the configurable options in the mod config menu.
 */
@Config(modid = Veining.MODID)
public class Configuration {

    @Config.Name("Server-side Settings")
    @Config.Comment("These settings only affect you if you are hosting the game.")
    @Config.LangKey("veining.config.server")
    public static ServerSide serverSide = new ServerSide();

    @Config.Name("Client-side Settings")
    @Config.Comment("These settings are personal to you and apply to all games.")
    @Config.LangKey("veining.config.client")
    public static ClientSide clientSide = new ClientSide();

    public static class ServerSide {
        @Config.Name("Multiple Vein Types")
        @Config.Comment("Determines whether the veining enchantment destroys other connected veins of different ores, or only of the original type mined.")
        public boolean multiOre = false;

        @Config.Name("Veining Durability Cost")
        @Config.Comment("Controls how much damage is done to the pickaxe per ore when the enchantment crumbles a vein.")
        @Config.RangeInt(min=0, max=5)
        public int durabilityDamage = 2;

        @Config.Name("Rarity (%)")
        @Config.Comment("Controls how rare the enchantment is (with 100% being as the mod was intended). It is recommended to keep it between 80% and 120%, and more statistics can be found on the wiki.")
        @Config.RangeInt(min=0, max=200)
        public int enchantmentRarity = 100;

        @Config.Name("Silk Touch")
        @Config.Comment("Determines whether the enchantment should respect silk touch.")
        public boolean silkTouch = true;
    }

    public static class ClientSide {
        @Config.Name("Disable When Crouching")
        @Config.Comment("When true, the enchantment won't take effect when crouched.")
        public boolean disableWhenCrouched = true;

        @Config.Name("Disable When Standing")
        @Config.Comment("When true, the enchantment won't take effect when stood up.")
        public boolean disableWhenStanding = false;
    }

    /**
     * Sets up some event handlers.
     */
    @Mod.EventBusSubscriber
    private static class EventHandler {

        /**
         * Saves the config locally and also sends critical values to the server when the config changes.
         * @param event The config change event
         */
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Veining.MODID)) {
                ConfigManager.sync(Veining.MODID, Config.Type.INSTANCE);

                PacketHandler.INSTANCE.sendToServer(
                        new VeiningSettingsMessage(
                                clientSide.disableWhenCrouched,
                                clientSide.disableWhenStanding
                        )
                );
            }
        }
    }
}
