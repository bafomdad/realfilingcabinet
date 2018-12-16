package com.bafomdad.realfilingcabinet.mixin;

import com.bafomdad.realfilingcabinet.blocks.entity.FilingCabinetEntity;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.ItemFrameEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformations;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.block.BlockItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrameEntityRenderer.class)
public abstract class ItemFrameRendererMixin {

	@Inject(at = @At("HEAD"), method = "method_3992", cancellable = true)
	private void method_3992(ItemFrameEntity frame, CallbackInfo ci) {

		ItemStack stack = frame.getHeldItemStack();
		if (!stack.isEmpty() && stack.getItem() == RFCItems.FILTER) {
			int rotation = frame.getRotation();
			BlockEntity be = frame.getEntityWorld().getBlockEntity(frame.getPos().down());
			if (be instanceof FilingCabinetEntity) {
				ItemStack folderItem = ((FilingCabinetEntity)be).getInventory().get(rotation);
				if (!folderItem.isEmpty()) {
					GlStateManager.pushMatrix();
					GlStateManager.enableLighting();
					GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
					if (stack.getItem() instanceof BlockItem)
						GlStateManager.scalef(0.25F, 0.25F, 0.25F);
					else
						GlStateManager.scalef(0.5F, 0.5F, 0.5F);
					MinecraftClient.getInstance().getItemRenderer().renderItemWithTransformation(((FilingCabinetEntity)be).getStoredItem(rotation), ModelTransformations.Type.ORIGIN);
					GlStateManager.popMatrix();
					ci.cancel();
				}
			}
		}
	}
}
