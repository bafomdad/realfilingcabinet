package com.bafomdad.realfilingcabinet.data;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.InventoryUtil;

/**
 * Created by bafomdad on 12/15/2018.
 */
public abstract class IItemDataHolder extends AbstractDataHolder<ItemStack> {

    @Override
    public abstract ItemStack insert(int slot, ItemStack type);

    @Override
    public abstract ItemStack extract(int slot, int amount);

    @Override
    public abstract ItemStack getObject(int slot);

    public abstract int getInvSize();

    public CompoundTag serialize(CompoundTag tag, DefaultedList<ItemStack> inv) {

        InventoryUtil.serialize(tag, inv);
        return tag;
    }

    public void deserialize(CompoundTag tag, DefaultedList<ItemStack> inv) {

        InventoryUtil.deserialize(tag, inv);
    }
}
