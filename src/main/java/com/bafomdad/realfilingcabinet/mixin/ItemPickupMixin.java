package com.bafomdad.realfilingcabinet.mixin;

import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.FolderItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by bafomdad on 12/16/2018.
 */

@Mixin(ItemEntity.class)
public abstract class ItemPickupMixin {

    @Shadow
    public int pickupDelay;

    @Inject(at = @At("HEAD"), method = "method_5694", cancellable = true)
    public void onPickup(PlayerEntity player, CallbackInfo ci) {

        ItemEntity ei = (ItemEntity)(Object)this;
        if (!ei.world.isRemote) {
            if (this.pickupDelay == 0) {
                ItemStack stack = ei.getStack();
                int amount = stack.getAmount();
                for (int i = 0; i < player.inventory.main.size(); i++) {
                    if (i == player.inventory.selectedSlot) continue;
                    ItemStack loopStack = player.inventory.main.get(i);
                    if (!loopStack.isEmpty() && loopStack.getItem() == RFCItems.FOLDER) {
                        if (FolderItem.getItem(loopStack).getItem() == stack.getItem()) {
                            player.method_6103(player, amount);
                            FolderItem.add(loopStack, amount);
                            stack.subtractAmount(amount);
                            if (stack.isEmpty()) {
                                ei.invalidate();
                            }
                        }
                    }
                }
            }
        }
    }
}
