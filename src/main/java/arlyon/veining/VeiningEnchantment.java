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

package arlyon.veining;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

/**
 * The enchantment class for the Veining enchantment.
 */
public class VeiningEnchantment extends net.minecraft.enchantment.Enchantment {

    /**
     * Pickaxe enchantment type.
     */
    public static EnumEnchantmentType PICKAXE = EnumHelper.addEnchantmentType("PICKAXE", item -> {
        assert item != null;
        return item.getToolClasses(new ItemStack(item)).stream().anyMatch(toolClass -> toolClass.equals("pickaxe"));
    });

    /**
     * Sets name and registry name and assigns the proper predicate.
     *
     * @param rarityIn the rarity of the enchantment
     * @param slots    the slots in which the enchantment is valid
     */
    VeiningEnchantment(Rarity rarityIn, EntityEquipmentSlot... slots) {
        super(rarityIn, PICKAXE, slots); // to be eligible for this enchantment, you must match the PICKAXE predicate
        setName("veining");
        setRegistryName("veining");
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     * - Settled on 32, which can be nullified or doubled based on the configuration.
     *
     * @param enchantmentLevel The level you want to get minimum enchantability weight for.
     */
    public int getMinEnchantability(int enchantmentLevel) {
        return (32 * Configuration.serverSide.enchantmentRarity) / 100;
    }

    /**
     * Returns the maximum value of enchantability needed on the enchantment level passed.ee
     *
     * @param enchantmentLevel The level you want to get maximum enchantability weight for.
     */
    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 50;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel() {
        return 1;
    }
}