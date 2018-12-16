package com.bafomdad.realfilingcabinet.utils;

import com.bafomdad.realfilingcabinet.blocks.entity.FilingCabinetEntity;
import com.bafomdad.realfilingcabinet.items.FolderItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * Created by bafomdad on 12/12/2018.
 */
public class StorageUtils {

    public static int simpleFolderMatch(FilingCabinetEntity be, ItemStack stack) {

        for (int i = 0; i < be.getInvSize(); i++) {
            ItemStack loopInv = be.getInventory().get(i);
            if (!loopInv.isEmpty() && stack.getItem() == be.getStoredItem(i).getItem())
                return i;
        }
        return -1;
    }

    public static void addStackManually(FilingCabinetEntity be, PlayerEntity player, ItemStack stack) {

        if (be.getWorld().isRemote) return;

        for (int i = 0; i < be.getInvSize(); i++) {
            ItemStack loopInv = be.getInventory().get(i);
            if (!loopInv.isEmpty() && stack.getItem() == be.getStoredItem(i).getItem()) {
                FolderItem.add(loopInv, stack.getAmount());
                player.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
                be.markBlockForUpdate();
            }
        }
    }

    public static void extractStackManually(FilingCabinetEntity be, PlayerEntity player) {

        ItemStack stack = be.getFilter();
        if (!stack.isEmpty()) {
            for (int i = 0; i < be.getInvSize(); i++) {
                ItemStack loopInv = be.getInventory().get(i);
                if (!loopInv.isEmpty() && stack.getItem() == be.getStoredItem(i).getItem()) {
                    long count = FolderItem.getFileSize(loopInv);
                    if (count <= 0) continue;

                    long extract = Math.min((player.isSneaking()) ? be.getStoredItem(i).getMaxAmount() : 1, count);
                    ItemStack stackExtract = new ItemStack(be.getStoredItem(i).getItem(), (int)extract);
                    boolean flag = player.inventory.insertStack(stackExtract);
                    if (flag) {
                        FolderItem.remove(loopInv, extract);
                        be.markBlockForUpdate();
                    }
                    break;
                }
            }
        }
    }

    public static void folderExtract(FilingCabinetEntity be, PlayerEntity player) {

        for (int i = be.getInvSize() - 1; i >= 0; i--) {
            ItemStack folder = be.getInventory().get(i);
            if (!folder.isEmpty()) {
                be.getInventory().set(i, ItemStack.EMPTY);
                player.setEquippedStack(EquipmentSlot.HAND_MAIN, folder);
                be.markBlockForUpdate();
                break;
            }
        }
    }
}
