package com.bafomdad.realfilingcabinet.inventory;

import com.bafomdad.realfilingcabinet.blocks.entity.FilingCabinetEntity;
import com.bafomdad.realfilingcabinet.data.IItemDataHolder;
import com.bafomdad.realfilingcabinet.items.FolderItem;
import com.bafomdad.realfilingcabinet.utils.StorageUtils;
import net.minecraft.item.ItemStack;

/**
 * Created by bafomdad on 12/15/2018.
 */
public class InventoryRFC extends IItemDataHolder {

    final FilingCabinetEntity fe;

    public InventoryRFC(FilingCabinetEntity fe) {

        this.fe = fe;
    }

    @Override
    public ItemStack insert(int slot, ItemStack stack) {

        if (stack.isEmpty()) return ItemStack.EMPTY;
        int slotIndex = StorageUtils.simpleFolderMatch(fe, stack);
        if (slotIndex != -1) {
            int amount = stack.getAmount();
            FolderItem.add(fe.getInventory().get(slotIndex), amount);
            stack.subtractAmount(amount);
            fe.markDirty();
            return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public ItemStack extract(int slot, int amount) {

        if (fe.getFilter().isEmpty()) return ItemStack.EMPTY;

        if (!getObject(slot).isEmpty()) {
            int i = StorageUtils.simpleFolderMatch(fe, fe.getFilter());
            if (i != -1 && slot == i) {
                long filterCount = FolderItem.getFileSize(fe.getInventory().get(i));
                if (filterCount <= 0) return ItemStack.EMPTY;

                ItemStack stackToGet = fe.getStoredItem(i);
                int filterExtract = (int)Math.min(stackToGet.getMaxAmount(), filterCount);
                amount = Math.min(filterExtract, amount);
                FolderItem.remove(fe.getInventory().get(i), amount);

                return stackToGet.copy();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getObject(int slot) {

        if (!fe.getInventory().get(slot).isEmpty())
            return fe.getStoredItem(slot);

        return ItemStack.EMPTY;
    }

    @Override
    public int getInvSize() {

        return fe.getInvSize();
    }
}
