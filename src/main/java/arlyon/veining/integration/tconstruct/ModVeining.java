package arlyon.veining.integration.tconstruct;

import arlyon.veining.VeiningAlgorithm;
import arlyon.veining.events.VeiningEventHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Optional;

/**
 * Adds veining as a tinkers construct trait.
 */
@Optional.Interface(iface = "slimeknights.tconstruct.library.traits.AbstractTrait", modid = "tconstruct")
public class ModVeining extends LeveledSingleModifierTrait {

    /**
     * Creates a new modifier with 10 levels that counts only as a single modifier.
     */
    ModVeining() {
        super("veining", 0xffffff, 10, 1);
    }

    @Override
    public void beforeBlockBreak(ItemStack tool, BlockEvent.BreakEvent event) {
        if (
            VeiningEventHandler.eventIsServerSide(event) &&
            VeiningEventHandler.configAllowsBreak(event.getPlayer())
        )
            VeiningAlgorithm.veiningAlgorithm(
                event.getPos(),
                event.getWorld(),
                event.getPlayer(),
                (int) Math.ceil(Math.pow(this.getData(tool).level, 2) / 3) + 1
            );

        super.beforeBlockBreak(tool, event);
    }
}
