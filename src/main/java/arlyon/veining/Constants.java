package arlyon.veining;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

public class Constants {
    public static final String MOD_NAME = "Veining";
    public static final String MODID = "veining";
    public static final String VERSION = "1.1.0";
    public static final Enchantment veining = new Enchantment(net.minecraft.enchantment.Enchantment.Rarity.UNCOMMON, EntityEquipmentSlot.MAINHAND);
    public static EnumEnchantmentType PICKAXE = EnumHelper.addEnchantmentType("PICKAXE", item -> {
        assert item != null;
        return item.getToolClasses(new ItemStack(item)).stream().anyMatch(toolClass -> toolClass.equals("pickaxe"));
    });
}