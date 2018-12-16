package com.bafomdad.realfilingcabinet.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by bafomdad on 12/16/2018.
 */

@Mixin(ItemEntity.class)
public abstract class ItemPickupMixin {

    @Inject(at = @At("HEAD"), method = "method_5694", cancellable = true)
    public void onPickup(PlayerEntity player, CallbackInfo ci) {

    }
}
