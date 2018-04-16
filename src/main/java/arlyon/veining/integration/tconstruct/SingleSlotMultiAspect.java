package arlyon.veining.integration.tconstruct;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.modifiers.*;
import slimeknights.tconstruct.library.utils.TinkerUtil;

/**
 * A modifier that takes a single slot but can be levelled up.
 *
 * Modified from MultiAspect, released under MIT
 */
public class SingleSlotMultiAspect extends ModifierAspect {

    private final int ingredientsPerLevel;

    private DataAspect dataAspect;
    private LevelAspect levelAspect;
    private FreeFirstModifierAspect freeModifierAspect;

    /**
     * Creates a new SingleSlotMultiAspect
     * @param parent The parent modifier.
     * @param color The color.
     * @param maxLevel The max level.
     * @param ingredientsPerLevel The number of ingredients to level each level.
     */
    public SingleSlotMultiAspect(IModifier parent, int color, int maxLevel, int ingredientsPerLevel) {
        super(parent);
        this.ingredientsPerLevel = ingredientsPerLevel;

        dataAspect = new DataAspect(parent, color);
        freeModifierAspect = new FreeFirstModifierAspect(parent, 1);
        levelAspect = new LevelAspect(parent, maxLevel);
    }

    protected int getMaxForLevel(int level) {
        return ingredientsPerLevel * level;
    }

    @Override
    public boolean canApply(ItemStack stack, ItemStack original) throws TinkerGuiException {
        // check if the threshold has been reached
        NBTTagCompound modifierTag = TinkerUtil.getModifierTag(stack, parent.getIdentifier());
        ModifierNBT.IntegerNBT data = getData(modifierTag);

        // the current level is full / level is 0
        if(data.current >= getMaxForLevel(data.level)) {
            // can we even apply a new level?
            if(!levelAspect.canApply(stack, original)) {
                return false;
            }

            // enough modifiers for another level?
            return freeModifierAspect.canApply(stack, original);
        }

        // we have not maxed out this level OR we have enough modifiers and can add a new level
        return true;
    }

    @Override
    public void updateNBT(NBTTagCompound root, NBTTagCompound modifierTag) {
        // simple data
        dataAspect.updateNBT(root, modifierTag);

        // increase the current level progress
        ModifierNBT.IntegerNBT data = getData(modifierTag);

        // new level?
        if(data.current >= getMaxForLevel(data.level)) {
            // remove modifiers
            freeModifierAspect.updateNBT(root, modifierTag);
            // add a level
            levelAspect.updateNBT(root, modifierTag);

            // update max. but to do so, we have to re-read the changed data again
            data = getData(modifierTag);
        }

        // always update max in case it changed since it got saved
        data.max = getMaxForLevel(data.level);

        // increase the level progress
        data.current++;
        data.write(modifierTag);
    }

    private ModifierNBT.IntegerNBT getData(NBTTagCompound tag) {
        ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(tag);

        if(data.max == 0) {
            data.max = getMaxForLevel(data.level);
        }

        return data;
    }
}
