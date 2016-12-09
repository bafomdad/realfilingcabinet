package com.bafomdad.realfilingcabinet.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderEntityCabinet extends RenderLiving<EntityCabinet> {
	
	public static final Factory FACTORY = new Factory();

	public RenderEntityCabinet(RenderManager rendermanager) {
		
		super(rendermanager, new ModelEntityCabinet(), 0.5F);
	}
	
	@Override
	public boolean canRenderName(EntityCabinet entity) {
		
		return false;
	}
	
	@Override
    public void doRender(EntityCabinet entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

	@Override
	protected ResourceLocation getEntityTexture(EntityCabinet entity) {

		return new ResourceLocation("realfilingcabinet:textures/entity/cabinetTexture.png");
	}
	
	public static class Factory implements IRenderFactory<EntityCabinet> {
		
		@Override
		public Render <? super EntityCabinet> createRenderFor(RenderManager manager) {
			
			return new RenderEntityCabinet(manager);
		}
	}
}
