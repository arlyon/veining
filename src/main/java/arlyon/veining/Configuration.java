package arlyon.veining;

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
@Config(modid = Constants.MODID)
public class Configuration {

    @Config.Name("Multiple Vein Types")
    @Config.Comment("Determines whether the veining enchantment destroys other connected veins of different ores, or only of the original type mined.")
    public static boolean multiOre = false;

    @Config.Name("Disable When Crouching")
    @Config.Comment("When true, the enchantment won't take effect when crouched.")
    public static boolean disableWhenCrouched = true;

    @Config.Name("Disable When Standing")
    @Config.Comment("When true, the enchantment won't take effect when stood up.")
    public static boolean disableWhenStanding = false;

    @Config.Name("Veining Durability Cost")
    @Config.Comment("Controls how much damage is done to the pickaxe per ore when the enchantment crumbles a vein.")
    @Config.RangeInt(min=0, max=5)
    public static int durabilityDamage = 2;

    @Config.Name("Rarity (%)")
    @Config.Comment("Controls how rare the enchantment is (with 100% being as the mod was intended). It is recommended to keep it between 80% and 120%, and more statistics can be found on the wiki.")
    @Config.RangeInt(min=0, max=200)
    public static int enchantmentRarity = 100;

    @Mod.EventBusSubscriber
    private static class EventHandler {

        /**
         * Inject the new values and save to the config file when the config has been changed from the GUI.
         *
         * @param event The config change event
         */
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Constants.MODID)) {
                ConfigManager.sync(Constants.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
