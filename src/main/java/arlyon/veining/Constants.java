package arlyon.veining;

import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;

public class Constants {
    public static final String MOD_NAME = "Veining";
    public static final String MODID = "veining";
    public static final String VERSION = "1.1.0";

    public static final FellingEnchantment veining = new FellingEnchantment(Rarity.UNCOMMON, EntityEquipmentSlot.MAINHAND);
}