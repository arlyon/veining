package arlyon.veining;

import net.minecraft.inventory.EntityEquipmentSlot;

/**
 * The enchantment class for the Veining enchantment.
 */
public class Enchantment extends net.minecraft.enchantment.Enchantment {

    /**
     * Sets name and registry name and assigns the proper predicate.
     *
     * @param rarityIn the rarity of the enchantment
     * @param slots the slots in which the enchantment is valid
     */
    Enchantment(Rarity rarityIn, EntityEquipmentSlot... slots) {
        super(rarityIn, Constants.PICKAXE, slots); // to be eligible for this enchantment, you must match the PICKAXE predicate
        setName("veining");
        setRegistryName("veining");
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     *   - Settled on 32, which can be nullified or doubled based on the configuration.
     *
     * @param enchantmentLevel The level you want to get minimum enchantability weight for.
     */
    public int getMinEnchantability(int enchantmentLevel) { return (32 * Configuration.enchantmentRarity)/100; }

    /**
     * Returns the maximum value of enchantability needed on the enchantment level passed.ee
     *
     * @param enchantmentLevel The level you want to get maximun enchantability weight for.
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