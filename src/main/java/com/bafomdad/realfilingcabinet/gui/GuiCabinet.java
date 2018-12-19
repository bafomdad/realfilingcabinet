package com.bafomdad.realfilingcabinet.gui;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.container.FabricContainerProvider;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.container.ContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Created by bafomdad on 12/18/2018.
 */
public class GuiCabinet extends ContainerGui {

    public static final Identifier CABINET_GUI = new Identifier(RealFilingCabinet.MODID, "textures/gui/guicabinet.png");
    public int xSize = 176;
    public int ySize = 131;

    final ContainerProvider provider;

    public GuiCabinet(FabricContainerProvider provider, PlayerEntity player) {

        super(provider.createContainer(player.inventory, player));
        this.provider = provider;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {

        this.drawBackground();
        super.draw(mouseX, mouseY, partialTicks);
        this.drawMousoverTooltip(mouseX, mouseY);
    }

    @Override
    protected void drawBackground(float v, int i, int i1) {

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        MinecraftClient.getInstance().getTextureManager().bindTexture(CABINET_GUI);
        drawTexturedRect(this.left, this.top, 0, 0, this.xSize, this.ySize);
//        drawTexturedRect(this.left + width / 2, this.height, 150 - width / 2, 0,width / 2, height / 2);
    }
}
