package arlyon.veining.core.proxy;

import arlyon.veining.FellingEnchantment;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Arrays;

/**
 * Handles initialization for the client side.
 */
public class ProxyClient extends ProxyCommon {

    /**
     * Adds the enchantment to the creative tab on the client.
     * @param e The pre-initialization event.
     */
    @Override
    public void preInit(FMLPreInitializationEvent e) {

        super.preInit(e);

        // add it to the creative tab
        EnumEnchantmentType[] enchantmentTypes = CreativeTabs.TOOLS.getRelevantEnchantmentTypes();
        enchantmentTypes = Arrays.copyOf(enchantmentTypes, enchantmentTypes.length+1);
        enchantmentTypes[enchantmentTypes.length-1] = FellingEnchantment.PICKAXE;
        CreativeTabs.TOOLS.setRelevantEnchantmentTypes(enchantmentTypes);

    }

    @Override
    public void init(FMLInitializationEvent e) { super.init(e); }

    @Override
    public void postInit(FMLPostInitializationEvent e) { super.postInit(e); }

}
