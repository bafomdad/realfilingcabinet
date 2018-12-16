package com.bafomdad.realfilingcabinet.gui;

import com.bafomdad.realfilingcabinet.blocks.entity.FilingCabinetEntity;
import com.bafomdad.realfilingcabinet.init.RFCBlocks;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.FolderItem;
import com.google.common.collect.Streams;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by bafomdad on 12/15/2018.
 */
public class HudRFC {

    public void draw() {

        MinecraftClient client = MinecraftClient.getInstance();

        PlayerEntity player = client.player;
        HitResult hr = client.hitResult;
        int width = client.window.getScaledWidth();

        if (player == null) return;

        if (hr != null && hr.type == HitResult.Type.BLOCK) {
            List<ItemStack> magnifyingGlass = Streams.stream(player.getItemsHand()).filter(stack -> !stack.isEmpty() && stack.getItem() == RFCItems.MAGNIFYINGGLASS).collect(Collectors.toList());
            if (!magnifyingGlass.isEmpty()) {
                BlockState state = client.world.getBlockState(hr.getBlockPos());
                if (state.getBlock() == RFCBlocks.FILINGCABINET) {
                    BlockEntity be = client.world.getBlockEntity(hr.getBlockPos());
                    if (be instanceof FilingCabinetEntity) {
                        FilingCabinetEntity fc = (FilingCabinetEntity)be;
                        List<String> list = getFileList(fc);
                        if (!list.isEmpty()) {
                            for (int i = 0; i < list.size(); i++)
                                client.fontRenderer.draw(list.get(i), width / 2, 5 + (i * 10), 0xFFFFFF);
                        }
                    }
                }
            }
        }
    }

    private List<String> getFileList(FilingCabinetEntity fc) {

        List<String> list = new ArrayList();
        for (int i = 0; i < fc.getInvSize(); i++) {
            ItemStack folder = fc.getInventory().get(i);
            if (!folder.isEmpty() && !fc.getStoredItem(i).isEmpty()) {
                long count = FolderItem.getFileSize(folder);
                list.add((fc.getStoredItem(i).getDisplayName()).getText() + " - " + count);
            }
        }
        return list;
    }
}
