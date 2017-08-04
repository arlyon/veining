package arlyon.veining.proxy;

import arlyon.veining.Constants;
import arlyon.veining.Enchantment;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Arrays;

public class ProxyClient extends ProxyCommon {

    @Override
    public void preInit(FMLPreInitializationEvent e) {

        super.preInit(e);

        // add it to the creative tab
        EnumEnchantmentType[] enchantmentTypes = CreativeTabs.TOOLS.getRelevantEnchantmentTypes();
        enchantmentTypes = Arrays.copyOf(enchantmentTypes, enchantmentTypes.length+1);
        enchantmentTypes[enchantmentTypes.length-1] = Enchantment.PICKAXE;
        CreativeTabs.TOOLS.setRelevantEnchantmentTypes(enchantmentTypes);

    }

    @Override
    public void init(FMLInitializationEvent e) { super.init(e); }

    @Override
    public void postInit(FMLPostInitializationEvent e) { super.postInit(e); }

}
