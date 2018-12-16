package com.bafomdad.realfilingcabinet.mixin;

import com.bafomdad.realfilingcabinet.data.EnumDataType;
import com.bafomdad.realfilingcabinet.data.IDataHooks;
import com.bafomdad.realfilingcabinet.data.IItemDataHolder;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sortme.Hopper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created by bafomdad on 12/15/2018.
 */

@Mixin(HopperBlockEntity.class)
public abstract class HopperMixin {

    @Inject(at = @At("HEAD"), method = "tryInsert", cancellable = true)
    private void tryInsert(CallbackInfoReturnable cir) {

        HopperBlockEntity hopper = (HopperBlockEntity)(Object)this;
        Direction dir = hopper.getCachedState().get(HopperBlock.field_11129);
        BlockEntity be = hopper.getWorld().getBlockEntity(hopper.getPos().offset(dir));
        if (be instanceof IDataHooks) {
            IItemDataHolder holder = ((IDataHooks)be).getDataHolder(EnumDataType.ITEM);
            if (holder != null) {
                for (int i = 0; i < hopper.getInvSize(); i++) {
                    if (!hopper.getInvStack(i).isEmpty()) {
                        ItemStack hopperItem = hopper.getInvStack(i).copy();
                        ItemStack toInsert = holder.insert(i, hopperItem);
                        if (toInsert.isEmpty()) {
                            hopper.markDirty();
                        }
                        hopper.setInvStack(i, hopperItem);
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "tryExtract", cancellable = true)
    private static void tryExtract(Hopper hopper, CallbackInfoReturnable cir) {

        BlockEntity be = hopper.getWorld().getBlockEntity(new BlockPos(hopper.getHopperX(), hopper.getHopperY() + 1, hopper.getHopperZ()));
        if (be instanceof IDataHooks) {
            IItemDataHolder holder = ((IDataHooks)be).getDataHolder(EnumDataType.ITEM);
            if (holder != null) {
                boolean flag = false;
                for (int i = 0; i < holder.getInvSize(); i++) {
                    ItemStack toExtract = holder.extract(i, 1);
                    if (!toExtract.isEmpty()) {
                        for (int j = 0; j < hopper.getInvSize(); j++) {
                            ItemStack hopperItem = hopper.getInvStack(j);
                            if (hopperItem.isEmpty()) {
                                hopper.setInvStack(j, toExtract);
                                flag = true;
                                break;
                            } else if (!hopperItem.isEmpty() && canMergeItems(hopperItem, toExtract)) {
                                int var8 = toExtract.getMaxAmount() - hopperItem.getAmount();
                                int var9 = Math.min(toExtract.getAmount(), var8);
//                                toExtract.addAmount(var9);
//                                hopperItem.addAmount(var9);
                                hopperItem.addAmount(1);
                                flag = var9 > 0;
                                break;
                            }
                        }
                        if (flag) {
                            hopper.markDirty();
                            cir.setReturnValue(true);
                        }
                    }
                }
            }
        }
    }

    private static boolean canMergeItems(ItemStack var0, ItemStack var1) {

        return var0.getItem() != var1.getItem()?false:(var0.getDamage() != var1.getDamage()?false:(var0.getAmount() > var0.getMaxAmount()?false:ItemStack.areTagsEqual(var0, var1)));
    }
}
