package com.bafomdad.realfilingcabinet.renders;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.helper.ResourceUpgradeHelper;
import com.bafomdad.realfilingcabinet.api.helper.UpgradeHelper;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCItems;

public class OldRenderFilingCabinet extends TileEntitySpecialRenderer<TileEntityRFC> {

	final ModelFilingCabinet model;

//	private static final ResourceLocation filingcabinet = new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/filingcabinet.png");
//	private static final ResourceLocation endercabinet = new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/endercabinet.png");
//	private static final ResourceLocation craftingcabinet = new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/craftingcabinet.png");
//	private static final ResourceLocation oredictcabinet = new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/oredictcabinet.png");
	
	public OldRenderFilingCabinet() {
		
		this.model = new ModelFilingCabinet();
	}
	
	@Override
	public void renderTileEntityAt(@Nonnull TileEntityRFC te, double x, double y, double z, float partialTicks, int digProgress) {

		this.renderFilingCabinet(te, x + 0.5F, y + 0.5F, z + 0.5F);
	}
	
	private void renderFilingCabinet(TileEntityRFC te, double x, double y, double z) {

		float f = 0.0625F;
		
		if (te != null && te.getWorld() != null)
		{
			IBlockState state = te.getWorld().getBlockState(te.getPos());
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
				
				for (int i = 0; i < te.getInventory().getSizeInventory() - 2; i++) {
					if (te.getInventory().getTrueStackInSlot(i) != null)
					{	
						GlStateManager.translate(0.0F, -0.0025F, 0.1F);
						
						if (UpgradeHelper.getUpgrade(te, StringLibs.TAG_ENDER) != null)
							this.renderFolders(new ItemStack(RFCItems.folder, 1, 1));
						else
							this.renderFolders(new ItemStack(RFCItems.folder, 1, 0));
					}
				}
				GlStateManager.popMatrix();
			}
			if (UpgradeHelper.isCreative(te))
			{
				GlStateManager.color(0.65F, 0.3F, 0.65F);
			}
//			if (UpgradeHelper.getUpgrade(te, StringLibs.TAG_CRAFT) != null)
//				bindTexture(craftingcabinet);
//			if (UpgradeHelper.getUpgrade(te, StringLibs.TAG_ENDER) != null)
//				bindTexture(endercabinet);
//			if (UpgradeHelper.getUpgrade(te, StringLibs.TAG_OREDICT) != null)
//				bindTexture(oredictcabinet);
//			if (!UpgradeHelper.hasUpgrade(te))
//				bindTexture(filingcabinet);
			if (UpgradeHelper.hasUpgrade(te))
				bindTexture(ResourceUpgradeHelper.getTexture(te, UpgradeHelper.getUpgrade(te, te.getTileData().getString(StringLibs.RFC_UPGRADE))));
			else
				bindTexture(ResourceUpgradeHelper.getDefault())
				;
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
