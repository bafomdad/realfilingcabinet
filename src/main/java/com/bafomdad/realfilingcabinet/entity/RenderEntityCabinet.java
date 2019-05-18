package com.bafomdad.realfilingcabinet.entity;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderEntityCabinet extends RenderLiving<EntityCabinet> {

	public static final Factory FACTORY = new Factory();
	
	public RenderEntityCabinet(RenderManager manager) {
		
		super(manager, new ModelEntityCabinet(), 0.5F);
		this.addLayer(new RenderLayerHat());
	}
	
	@Override
	public boolean canRenderName(EntityCabinet entity) {
		
		return false;
	}
	
	@Override
	public ResourceLocation getEntityTexture(EntityCabinet entity) {
		
		return new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/entity/cabinettexture.png");
	}
	
	public static class Factory implements IRenderFactory<EntityCabinet> {
		
		@Override
		public Render<? super EntityCabinet> createRenderFor(RenderManager manager) {
			
			return new RenderEntityCabinet(manager);
		}
	}
}
