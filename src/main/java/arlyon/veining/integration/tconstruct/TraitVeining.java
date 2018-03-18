package arlyon.veining.integration.tconstruct;

import arlyon.veining.events.VeiningEventHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Optional;
import slimeknights.tconstruct.library.traits.AbstractTrait;

/**
 * Adds veining as a tinkers construct trait.
 */
@Optional.Interface(iface = "slimeknights.tconstruct.library.traits.AbstractTrait", modid = "tconstruct")
public class TraitVeining extends AbstractTrait {

    public TraitVeining() {
        super("veiner", 0xffffff);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void beforeBlockBreak(ItemStack tool, BlockEvent.BreakEvent event) {
        if
        (
            VeiningEventHandler.eventIsServerSide(event) &&
            VeiningEventHandler.configAllowsBreak(event.getPlayer()) &&
            VeiningEventHandler.getOreType(event.getState(), event.getPos(), event.getPlayer()) != null
        )
            VeiningEventHandler.veiningAlgorithm(
                event.getPos(),
                event.getWorld(),
                event.getPlayer(),
                VeiningEventHandler.getOreType(event.getState(), event.getPos(), event.getPlayer())
            );

        super.beforeBlockBreak(tool, event);
    }
}
