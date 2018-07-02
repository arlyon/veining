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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.traits.AbstractTrait;

public class ProxyCommon {
    @Optional.Method(modid = TConstruct.modID)
    public static void preInit(FMLPreInitializationEvent e) {
        AbstractTrait modifier = new ModVeining();
        modifier.addRecipeMatch(RecipeMatch.of(ItemExplosiveEmerald.INSTANCE));
        MinecraftForge.EVENT_BUS.register(modifier);
    }

    @Optional.Method(modid = TConstruct.modID)
    public static void init(FMLInitializationEvent e) {
        GameRegistry.addShapedRecipe(
            new ResourceLocation(Veining.MOD_ID, "explosiveemerald.recipe"),
            new ResourceLocation(Veining.MOD_ID, "items"),
            new ItemStack(ItemExplosiveEmerald.INSTANCE),
            " T ",
            "EDE",
            " T ",
            'T', Item.getItemFromBlock(Blocks.TNT),
            'E', Items.EMERALD,
            'D', Items.DIAMOND
        );
    }
}
