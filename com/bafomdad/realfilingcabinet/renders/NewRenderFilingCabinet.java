package com.bafomdad.realfilingcabinet.renders;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.helper.UpgradeHelper;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCItems;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;

public class NewRenderFilingCabinet extends TileEntitySpecialRenderer<TileEntityRFC> {

	IModel model;
	IBakedModel bakedModel;
	
//	private IBakedModel getBakedModel() {
//		
//		if (bakedModel == null) {
//			try {
//				model = ModelLoaderRegistry.getModel(new ResourceLocation(RealFilingCabinet.MOD_ID, "block/modelcabinet_drawer"));
//			} catch (Exception e) {
//				throw new RuntimeException(e);
//			}
//			bakedModel = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
//		}
//		return bakedModel;
//	}
	
	private IBakedModel getModelDependingOnState(TileEntityRFC tile) {
		
		if (bakedModel == null) {
			if (UpgradeHelper.getUpgrade(tile, StringLibs.TAG_ENDER) != null) {
				try {
					model = ModelLoaderRegistry.getModel(new ResourceLocation(RealFilingCabinet.MOD_ID, "block/modelcabinet_drawer_ender"));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			if (UpgradeHelper.getUpgrade(tile, StringLibs.TAG_CRAFT) != null) {
				try {
					model = ModelLoaderRegistry.getModel(new ResourceLocation(RealFilingCabinet.MOD_ID, "block/modelcabinet_drawer_autocraft"));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			else {
				try {
					model = ModelLoaderRegistry.getModel(new ResourceLocation(RealFilingCabinet.MOD_ID, "block/modelcabinet_drawer"));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			bakedModel = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
		}
		return bakedModel;
	}
	
	@Override
	public void renderTileEntityAt(@Nonnull TileEntityRFC te, double x, double y, double z, float partialTicks, int digProgress) {
		
		GlStateManager.pushAttrib();
		GlStateManager.pushMatrix();
		
		GlStateManager.translate(x, y, z);
		GlStateManager.disableRescaleNormal();

		renderDrawer(te);
		
		GlStateManager.popMatrix();
		GlStateManager.popAttrib();
	}
	
	private void renderDrawer(TileEntityRFC te) {
		
		GlStateManager.pushMatrix();
		
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		int angle = state.getValue(BlockHorizontal.FACING).getIndex();
		
		switch (angle) {
			case 5: GlStateManager.rotate(90, 0, 1, 0); GlStateManager.translate(-1, 0, 0); break;
			case 4: GlStateManager.rotate(270, 0, 1, 0); GlStateManager.translate(0, 0, -1); break;
			case 3: GlStateManager.rotate(0, 0, 1, 0); break;
			case 2: GlStateManager.rotate(180, 0, 1, 0); GlStateManager.translate(-1, 0, -1); break;
		}
		
		float f = 0.0625F;
		float prevStep = te.renderOffset;
		te.renderOffset = te.offset;
		te.renderOffset = prevStep + (te.renderOffset - prevStep) * f;
		GlStateManager.translate(0, 0, -prevStep);
		
		if (te.isOpen || (!te.isOpen && te.offset != 0.0F))
		{
			GlStateManager.pushMatrix();
			
			for (int i = 0; i < te.getInventory().getSlots(); i++) {
				if (te.getInventory().getTrueStackInSlot(i) != null)
				{
					GlStateManager.translate(0.0F, -0.0025F, 0.1F);
					if (UpgradeHelper.getUpgrade(te, StringLibs.TAG_ENDER) == null)
						this.renderFolders(te.getInventory().getTrueStackInSlot(i));
					else
						this.renderFolders(new ItemStack(RFCItems.folder, 1, 1));
				}
			}
			GlStateManager.popMatrix();
		}
		
		RenderHelper.disableStandardItemLighting();
		this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		if (Minecraft.isAmbientOcclusionEnabled())
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		else
			GlStateManager.shadeModel(GL11.GL_FLAT);
		
		World world = te.getWorld();
		GlStateManager.translate(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());
		
		Tessellator tess = Tessellator.getInstance();
		tess.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(
				world, 
				getModelDependingOnState(te), 
				world.getBlockState(te.getPos()), 
				te.getPos(), 
				Tessellator.getInstance().getBuffer(), 
				false);
		tess.draw();
		
		RenderHelper.enableStandardItemLighting();
		GlStateManager.popMatrix();
	}
	
	private void renderFolders(ItemStack stack) {
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5F, 0.75F, 0.05F);
		GlStateManager.scale(0.75F, 0.75F, 0.75F);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
		GlStateManager.popMatrix();
	}
}
