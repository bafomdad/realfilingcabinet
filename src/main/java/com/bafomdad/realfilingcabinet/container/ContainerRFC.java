package com.bafomdad.realfilingcabinet.container;

import com.bafomdad.realfilingcabinet.api.IFolder;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

/**
 * Created by bafomdad on 12/18/2018.
 */
public class ContainerRFC extends Container {

    final DefaultedList<ItemStack> list;

    public ContainerRFC(PlayerEntity player, DefaultedList<ItemStack> list) {

        this.list = list;
        for (int i = 0; i < list.size(); i++)
            addSlot(new SlotRFC(list, i, 16 + i * 18, 18));
        for (int j = 0; j < 3; j++) {
            for (int k = 0; k < 9; k++)
                addSlot(new Slot(player.inventory, k + j * 9 + 9, 8 + k * 18, 50 + j * 18));
        }
        for (int l = 0; l < 9; l++)
            addSlot(new Slot(player.inventory, l, 8 + l * 18, 108));
        // should this even be here?
        this.syncId = 120;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {

        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slotList.get(index);

        if (slot != null && slot.hasStack()) {
            ItemStack stack1 = slot.getStack();
            stack = stack1.copy();

            if (index < list.size()) {
                if (!this.insertItem(stack1, list.size(), this.slotList.size(), true))
                    return ItemStack.EMPTY;
            } else if (!this.insertItem(stack1, 0, list.size(), false))
                return ItemStack.EMPTY;

            if (stack1.getAmount() == 0)
                slot.setStack(ItemStack.EMPTY);
        }
        return stack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {

        return true;
    }

    private class SlotRFC extends Slot {

        private final int index;
        final DefaultedList<ItemStack> items;

        public SlotRFC(DefaultedList<ItemStack> items, int index, int xPosition, int yPosition) {

            super(null, index, xPosition, yPosition);
            this.items = items;
            this.index = index;
        }

        @Override
        public boolean canInsert(ItemStack stack) {

            return stack.getItem() instanceof IFolder;
        }

        @Override
        public ItemStack getStack() {

            return this.items.get(this.index);
        }

        @Override
        public void setStack(ItemStack stack) {

            this.items.set(this.index, stack);
        }

        @Override
        public ItemStack takeStack(int amount) {

            return this.items.get(index);
        }

        @Override
        public void markDirty() {}

        @Override
        public int getMaxStackAmount() {

            return 1;
        }
    }
}
