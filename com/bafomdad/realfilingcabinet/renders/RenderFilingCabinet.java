package com.bafomdad.realfilingcabinet.renders;

import java.nio.FloatBuffer;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.TileEntityRFC;
import com.bafomdad.realfilingcabinet.blocks.TileFilingCabinet;

public class RenderFilingCabinet extends TileEntitySpecialRenderer {
	
	final ModelFilingCabinet model;

	private static final ResourceLocation filingcabinet = new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/filingcabinet.png");
	private static final ResourceLocation endercabinet = new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/endercabinet.png");
	private static final ResourceLocation craftingcabinet = new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/craftingcabinet.png");
	
	public RenderFilingCabinet() {
		
		this.model = new ModelFilingCabinet();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {

		if (te instanceof TileEntityRFC)
			this.renderFilingCabinet((TileEntityRFC)te, x + 0.5F, y + 0.5F, z + 0.5F);
	}
	
	private void renderFilingCabinet(TileEntityRFC te, double x, double y, double z) {

		float f = 0.0625F;
		float rotation = 0.0f;
		
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y, (float)z);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		
		if (te != null && te.getWorldObj() != null)
		{
			switch (te.facing)
			{
				case 0: rotation = 0.0f; break;
				case 2: rotation = 180.0f; break;
				case 3: rotation = 270.0f; break;
				case 1: rotation = 90.0f; break;
			}
			GL11.glRotatef(rotation, 0.0f, 1.0f, 0.0f);
			
			if (te.isOpen || (!te.isOpen && te.offset != 0.0F))
			{
				GL11.glPushMatrix();
				GL11.glTranslatef(-0.4F, 0.3F, te.renderOffset + 0.5F);
				GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
				
				for (int i = 0; i < te.getSizeInventory() - 2; i++) {
					if (te.getStackInSlot(i) != null)
					{
						GL11.glTranslatef((float)0.0F, (float)0.0F, (float)0.1F);
						if (te.isEnder)
							Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/items/enderfolder.png"));
						else
							Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/items/folder.png"));
						
						Tessellator tess = Tessellator.instance;
						tess.startDrawingQuads();
						
						float h = 0.75F;
						
						tess.addVertexWithUV(0, 0, 0, 0, 1);
						tess.addVertexWithUV(0, h, 0, 0, 0);
						tess.addVertexWithUV(h, h, 0, 1, 0);
						tess.addVertexWithUV(h, 0, 0, 1, 1);
						
						tess.addVertexWithUV(0, 0, 0, 1, 1);
						tess.addVertexWithUV(h, 0, 0, 0, 1);
						tess.addVertexWithUV(h, h, 0, 0, 0);
						tess.addVertexWithUV(0, h, 0, 1, 0);
						
						tess.draw();
					}
				}
				GL11.glPopMatrix();
			}
			if (te.isCreative)
			{
				GL11.glColor3f(0.65F, 0.3F, 0.65F);
			}
		}
		if (te.isAutoCraft)
			bindTexture(craftingcabinet);
		if (te.isEnder)
			bindTexture(endercabinet);
		if (!te.isEnder && !te.isAutoCraft)
			bindTexture(filingcabinet);
		model.render(te, f);
		
		GL11.glPopMatrix();
	}
	
	public static class RenderFilingCabinetItem implements IItemRenderer {

		private final ModelFilingCabinet model;
		
		public RenderFilingCabinetItem() {
			
			this.model = new ModelFilingCabinet();
		}
		
		@Override
		public boolean handleRenderType(ItemStack item, ItemRenderType type) {

			return true;
		}

		@Override
		public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {

			return true;
		}

		@Override
		public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
			
			if (!(type == type.EQUIPPED_FIRST_PERSON))
				TileEntityRendererDispatcher.instance.renderTileEntityAt(new TileEntityRFC(), 0.0D, 0.0D, 0.0D, 0.5F);
			else
				TileEntityRendererDispatcher.instance.renderTileEntityAt(new TileEntityRFC(), 0.0D, 0.1D, 0.0D, 0.5F);
		}
	}
}
