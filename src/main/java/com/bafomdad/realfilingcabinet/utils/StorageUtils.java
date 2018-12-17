package com.bafomdad.realfilingcabinet.utils;

import com.bafomdad.realfilingcabinet.blocks.entity.FilingCabinetEntity;
import com.bafomdad.realfilingcabinet.items.FolderItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Created by bafomdad on 12/12/2018.
 */
public class StorageUtils {

    public static int simpleFolderMatch(FilingCabinetEntity be, ItemStack stack) {

        if (stack.isEmpty()) return -1;

        for (int i = 0; i < be.getInvSize(); i++) {
            ItemStack loopInv = be.getInventory().get(i);
            if (!loopInv.isEmpty() && stack.getItem() == be.getStoredItem(i).getItem())
                return i;
        }
        return -1;
    }

    public static void addStackManually(FilingCabinetEntity be, PlayerEntity player, ItemStack stack) {

        if (be.getWorld().isRemote) return;

        if (be.calcLastClick(player)) {
            addAllStacksManually(be, player);
            return;
        }
        for (int i = 0; i < be.getInvSize(); i++) {
            ItemStack loopInv = be.getInventory().get(i);
            if (!loopInv.isEmpty() && stack.getItem() == be.getStoredItem(i).getItem()) {
                FolderItem.add(loopInv, stack.getAmount());
                player.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
                be.markBlockForUpdate();
            }
        }
    }

    private static void addAllStacksManually(FilingCabinetEntity be, PlayerEntity player) {

        boolean consume = false;
        for (int i = 0; i < player.inventory.main.size(); i++) {
            ItemStack loopInv = player.inventory.main.get(i);
            int slot = simpleFolderMatch(be, loopInv);
            if (!loopInv.isEmpty() && slot != -1) {
                FolderItem.add(be.getInventory().get(slot), loopInv.getAmount());
                player.inventory.main.set(i, ItemStack.EMPTY);
                consume = true;
            }
        }
        if (consume) {
//            if (player instanceof ServerPlayerEntity)
//                ((ServerPlayerEntity)player).container
            be.markDirty();
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
                        break;
                    }
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
