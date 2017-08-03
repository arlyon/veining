package arlyon.veining;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

import java.util.Arrays;

public class Enchantment extends net.minecraft.enchantment.Enchantment {

    // creates a new enchantment type called axe that can be applied on any tool with the class axe.
    private static EnumEnchantmentType PICKAXE = EnumHelper.addEnchantmentType("PICKAXE", item -> item.getToolClasses(new ItemStack(item)).stream().anyMatch(toolClass -> toolClass.equals("pickaxe")));

    public Enchantment(Rarity rarityIn, EntityEquipmentSlot... slots) {
        super(rarityIn, PICKAXE, slots);
        setName("veining");
        setRegistryName("veining");

        // add it to the creative tab
        EnumEnchantmentType[] enchantmentTypes = CreativeTabs.TOOLS.getRelevantEnchantmentTypes();
        enchantmentTypes = Arrays.copyOf(enchantmentTypes, enchantmentTypes.length+1);
        enchantmentTypes[enchantmentTypes.length-1] = PICKAXE;

        CreativeTabs.TOOLS.setRelevantEnchantmentTypes(enchantmentTypes);
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(int enchantmentLevel) { return 35; }

    /**
     * Returns the maximum value of enchantability needed on the enchantment level passed.
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