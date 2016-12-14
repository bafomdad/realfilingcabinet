package com.bafomdad.realfilingcabinet.entity;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.MobUpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.ResourceUpgradeHelper;

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

		int state = entity.getTextureState();
		switch (state) {
			case 1: return new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/entity/cabinetMobTexture.png".toLowerCase());
			case 2: return new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/entity/cabinetFluidTexture.png".toLowerCase());
			default: return ResourceUpgradeHelper.getMobDefault();
		}
	}
	
	public static class Factory implements IRenderFactory<EntityCabinet> {
		
		@Override
		public Render <? super EntityCabinet> createRenderFor(RenderManager manager) {
			
			return new RenderEntityCabinet(manager);
		}
	}
}
