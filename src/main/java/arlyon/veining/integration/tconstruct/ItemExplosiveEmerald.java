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

package arlyon.veining.integration.tconstruct;

import arlyon.veining.Veining;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.tconstruct.TConstruct;

import java.util.Objects;

public class ItemExplosiveEmerald extends Item {

    public static final ItemExplosiveEmerald INSTANCE = new ItemExplosiveEmerald();

    private ItemExplosiveEmerald() {
        this.setCreativeTab(CreativeTabs.MATERIALS);
        this.setMaxStackSize(1);
        this.setUnlocalizedName("explosiveemerald");
        this.setRegistryName("explosiveemerald");
    }

    @Mod.EventBusSubscriber(modid = Veining.MOD_ID)
    public static class RegistrationHandler {

        /**
         * Registers the additional items
         * for tinkers construct.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event) {

            if (!Loader.isModLoaded(TConstruct.modID))
                return;

            final IForgeRegistry<Item> registry = event.getRegistry();
            registry.register(ItemExplosiveEmerald.INSTANCE);
        }

        /**
         * Registers the model for the emerald.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerModel(final ModelRegistryEvent event) {
            ModelLoader.setCustomModelResourceLocation(
                ItemExplosiveEmerald.INSTANCE,
                0,
                new ModelResourceLocation(
                    Objects.requireNonNull(ItemExplosiveEmerald.INSTANCE.getRegistryName()),
                    "inventory"
                )
            );
        }
    }
}
