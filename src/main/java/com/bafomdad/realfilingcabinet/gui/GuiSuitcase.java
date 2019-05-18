package com.bafomdad.realfilingcabinet.gui;

import org.lwjgl.opengl.GL11;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.inventory.InventorySuitcase;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiSuitcase extends GuiContainer {
	
	private static final ResourceLocation texture = new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/gui/guicabinet.png");

	public GuiSuitcase(InventoryPlayer player, InventorySuitcase suitcaseInv) {
		
		super(new ContainerSuitcase(player, suitcaseInv));
		this.xSize = 176;
		this.ySize = 131;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		
		String s = "Suitcase";
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(texture);
		
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}
}
