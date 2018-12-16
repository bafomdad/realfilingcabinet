package com.bafomdad.realfilingcabinet.render;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.entity.FilingCabinetEntity;
import com.bafomdad.realfilingcabinet.init.RFCBlocks;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformations;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

public class RenderFilingCabinet extends BlockEntityRenderer<FilingCabinetEntity> {

	final ModelFilingCabinet model;
	public static final Identifier CABINET_TEX = new Identifier(RealFilingCabinet.MODID, "textures/entity/filingcabinet.png");
	
	public RenderFilingCabinet() {
		
		this.model = new ModelFilingCabinet();
	}
	
	@Override
	public void render(FilingCabinetEntity be, double x, double y, double z, float partialTicks, int digProgress) {

		this.renderFilingCabinet(be, x + 0.5F, y + 0.5F, z + 0.5F);
	}
	
	private void renderFilingCabinet(FilingCabinetEntity be, double x, double y, double z) {

		float f = 0.0625F;
		
		BlockState state = be.getCachedState();
		if (state.getBlock() == RFCBlocks.FILINGCABINET) {
			int angle = state.get(Properties.FACING_HORIZONTAL).ordinal();
			
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)x, (float)y, (float)z);
			GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(state.get(Properties.FACING_HORIZONTAL).asRotation(), 0, 1, 0);
			
			if (be.isOpen || (!be.isOpen && be.offset != 0.05F)) {
				GlStateManager.pushMatrix();
				GlStateManager.translatef(-0.4F, 0.3F, be.renderOffset + 0.5F);
				GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
				
				for (int i = 0; i < be.getInvSize(); i++) {
					ItemStack renderFolder = be.getInventory().get(i);
					if (!renderFolder.isEmpty()) {	
						GlStateManager.translatef(0.0F, -0.0025F, 0.1F);
						this.renderFolders(renderFolder);
					}
				}
				GlStateManager.popMatrix();
			}
			this.bindTexture(CABINET_TEX);

			model.render(be, f);
			GlStateManager.popMatrix();
		}
	}
	
	private void renderFolders(ItemStack stack) {
		
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)0.4F, (float)0.45F, (float)0.1F);
		GlStateManager.scalef(0.75F, 0.75F, 0.75F);
		MinecraftClient.getInstance().getItemRenderer().renderItemWithTransformation(stack, ModelTransformations.Type.ORIGIN);
		GlStateManager.popMatrix();
	}
}
