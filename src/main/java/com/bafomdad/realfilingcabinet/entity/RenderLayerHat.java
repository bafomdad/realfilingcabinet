package com.bafomdad.realfilingcabinet.entity;

import com.bafomdad.realfilingcabinet.api.upgrades.MobUpgrades;
import com.bafomdad.realfilingcabinet.helpers.MobUpgradeHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderLayerHat implements LayerRenderer<EntityCabinet> {

	@Override
	public void doRenderLayer(EntityCabinet cabinet, float limbSwing, float limbSwingAmount, float partialTicks, float age, float yaw, float pitch, float scale) {

		if (cabinet.isHatPresent()) {
			ModelBase hat = cabinet.getModelHat();
			ResourceLocation texture = new ResourceLocation(cabinet.getTexture());
			if (hat != null) {
				GlStateManager.pushMatrix();
				
				GlStateManager.translate(0.0F, -(cabinet.height / 2) - 0.1F, 0.0F);
				Minecraft.getMinecraft().renderEngine.bindTexture(texture);
				hat.render(cabinet, limbSwing, limbSwingAmount, age, yaw, pitch, scale);
				
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public boolean shouldCombineTextures() {

		return false;
	}
}
