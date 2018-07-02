/*
 * veining (c) by Alexander Lyon
 *
 * veining is licensed under a
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by-nc-sa/4.0/>
 */

/*
 * veining (c) by arlyon
 *
 * veining is licensed under a
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by-nc-sa/4.0/>
 */

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
        if (VeiningEventHandler.configAllowsBreak(event.getPlayer()))
            VeiningAlgorithm.getInstance().veiningAlgorithm(
                event.getPos(),
                event.getWorld(),
                event.getPlayer(),
                (int) Math.ceil(Math.pow(this.getData(tool).level, 2) / 3) + 1
            );

        super.beforeBlockBreak(tool, event);
    }
}
