package arlyon.veining.integration.tconstruct;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;

/**
 * A modifier that can be leveled but only uses a single modifier slot.
 */
public class LeveledSingleModifierTrait extends ModifierTrait {

    /**
     * Creates a new LeveledSingleModifierTrait
     * @param identifier The string identifier.
     * @param color The color in the tooltip.
     * @param maxLevel The max level of the modifier.
     * @param ingredientPerLevel The number of ingredients per level.
     */
    public LeveledSingleModifierTrait(String identifier, int color, int maxLevel, int ingredientPerLevel) {
        super(identifier, color, maxLevel, ingredientPerLevel);
        TinkerRegistry.addTrait(this);
        this.aspects.clear();
        addAspects(new SingleSlotMultiAspect(this, color, maxLevel, ingredientPerLevel));
    }
}
