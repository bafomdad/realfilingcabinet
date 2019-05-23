package com.bafomdad.realfilingcabinet.renders;

import javax.annotation.Nonnull;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.BlockRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.*;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.enums.FolderType;
import com.bafomdad.realfilingcabinet.init.RFCBlocks;
import com.bafomdad.realfilingcabinet.init.RFCItems;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderFilingCabinet extends TileEntitySpecialRenderer<TileEntityRFC> {

	final ModelFilingCabinet model;

	public RenderFilingCabinet() {
		
		this.model = new ModelFilingCabinet();
	}
	
	@Override
	public void render(@Nonnull TileEntityRFC te, double x, double y, double z, float partialTicks, int digProgress, float whatever) {

		if (te != null && te.getWorld() != null)
			this.renderFilingCabinet(te, x + 0.5F, y + 0.5F, z + 0.5F);
	}
	
	private void renderFilingCabinet(TileEntityRFC te, double x, double y, double z) {

		float f = 0.0625F;
		
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		if (state != null && state.getBlock() instanceof BlockRFC) {
			int angle = state.getValue(BlockHorizontal.FACING).getIndex();
			
			GlStateManager.pushMatrix();
			GlStateManager.translate((float)x, (float)y, (float)z);
			GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(state.getValue(BlockHorizontal.FACING).getHorizontalAngle(), 0, 1, 0);
			
			if (te.isOpen || (!te.isOpen && te.offset != 0.05F)) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(-0.4F, 0.3F, te.renderOffset + 0.5F);
				GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
				
				for (int i = 0; i < te.getInventory().getSlots(); i++) {
					ItemStack renderFolder = te.getInventory().getFolder(i);
					if (!renderFolder.isEmpty()) {	
						GlStateManager.translate(0.0F, -0.0025F, 0.1F);
						if (te instanceof TileFilingCabinet && !UpgradeHelper.getUpgrade((TileFilingCabinet)te, StringLibs.TAG_ENDER).isEmpty()) {
							this.renderFolders(new ItemStack(RFCItems.FOLDER, 1, FolderType.ENDER.ordinal()));
						} else {
							this.renderFolders(renderFolder);
						}
					}
				}
				GlStateManager.popMatrix();
			}
			bindTheTex(te);
			model.render(te, f);
			GlStateManager.popMatrix();
		}
	}
	
	public void bindTheTex(TileEntityRFC te) {
		
		bindTexture(UpgradeHelper.getTexture((TileFilingCabinet)te));
	}
	
	private void renderFolders(ItemStack stack) {
		
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)0.4F, (float)0.45F, (float)0.1F);
		GlStateManager.scale(0.75F, 0.75F, 0.75F);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
		GlStateManager.popMatrix();
	}
}
