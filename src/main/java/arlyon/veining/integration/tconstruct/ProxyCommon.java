package arlyon.veining.integration.tconstruct;

import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.tools.TinkerMaterials;

public class ProxyCommon {

    @Optional.Method(modid = "tconstruct")
    public static void preInit(FMLPreInitializationEvent e) {

        ITrait trait = new TraitVeining();
        TinkerRegistry.addTrait(trait);
        TinkerMaterials.netherrack.addTrait(trait);
    }

}
