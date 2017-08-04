package arlyon.veining.proxy;

import arlyon.veining.Constants;
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
    }

    @Override
    public void init(FMLInitializationEvent e) { super.init(e); }

    @Override
    public void postInit(FMLPostInitializationEvent e) {

        super.postInit(e);

        // add it to the creative tab
        EnumEnchantmentType[] enchantmentTypes = CreativeTabs.TOOLS.getRelevantEnchantmentTypes();
        enchantmentTypes = Arrays.copyOf(enchantmentTypes, enchantmentTypes.length+1);
        enchantmentTypes[enchantmentTypes.length-1] = Constants.PICKAXE;
        CreativeTabs.TOOLS.setRelevantEnchantmentTypes(enchantmentTypes);

    }

}
