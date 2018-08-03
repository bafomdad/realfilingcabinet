package com.bafomdad.realfilingcabinet.renders;

import javax.annotation.Nonnull;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileManaCabinet;
import com.bafomdad.realfilingcabinet.helpers.ResourceUpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.init.RFCBlocks;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.integration.BotaniaRFC;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderManaCabinet extends TileEntitySpecialRenderer<TileManaCabinet> {

	final ModelFilingCabinet model;
	final ResourceLocation res = new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/manacabinet.png");
	
	public RenderManaCabinet() {
		
		this.model = new ModelFilingCabinet();
	}
	
	@Override
	public void render(@Nonnull TileManaCabinet te, double x, double y, double z, float partialTicks, int digProgress, float whatever) {

		if (te != null && te.getWorld() != null)
			this.renderManaCabinet(te, x + 0.5F, y + 0.5F, z + 0.5F);
	}
	
	private void renderManaCabinet(TileManaCabinet te, double x, double y, double z) {

		float f = 0.0625F;
		
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		if (state != null && state.getBlock() == BotaniaRFC.manaCabinet)
		{
			int angle = state.getValue(BlockHorizontal.FACING).getIndex();
			
			GlStateManager.pushMatrix();
			GlStateManager.translate((float)x, (float)y, (float)z);
			GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
			
			switch (angle)
			{
				case 5: GlStateManager.rotate(270, 0, 1, 0); break;
				case 4: GlStateManager.rotate(90, 0, 1, 0); break;
				case 3: GlStateManager.rotate(0, 0, 1, 0); break;
				case 2: GlStateManager.rotate(180, 0, 1, 0); break;
			}
			
			if (te.isOpen || (!te.isOpen && te.offset != 0.0F))
			{
				GlStateManager.pushMatrix();
				GlStateManager.translate(-0.4F, 0.3F, te.renderOffset + 0.5F);
				GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
				
				for (int i = 0; i < te.getInv().getSlots(); i++) {
					ItemStack renderFolder = te.getInv().getStackInSlot(i);
					if (!renderFolder.isEmpty())
					{	
						GlStateManager.translate(0.0F, -0.0025F, 0.1F);
						this.renderFolders(renderFolder);
					}
				}
				GlStateManager.popMatrix();
			}
			bindTexture(res);

			model.render(te, f);
			GlStateManager.popMatrix();
		}
	}
	
	private void renderFolders(ItemStack stack) {
		
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)0.4F, (float)0.45F, (float)0.1F);
		GlStateManager.scale(0.75F, 0.75F, 0.75F);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
		GlStateManager.popMatrix();
	}
}
