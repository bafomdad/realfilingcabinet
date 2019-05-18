package com.bafomdad.realfilingcabinet.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.GlStateManager;

import org.lwjgl.opengl.GL11;

public class ModelHatMob extends ModelBase {
    
	public ModelRenderer hatTop;
    public ModelRenderer hatBrim;

    public ModelHatMob() {
        
    	this.textureWidth = 64;
        this.textureHeight = 32;
        this.hatTop = new ModelRenderer(this, 0, 0);
        this.hatTop.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hatTop.addBox(-8.0F, 0.0F, -8.0F, 16, 16, 16, 0.0F);
        this.hatBrim = new ModelRenderer(this, 12, 7);
        this.hatBrim.setRotationPoint(0.0F, 9.5F, 0.0F);
        this.hatBrim.addBox(-6.0F, 0.0F, -6.0F, 12, 2, 12, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
       
    	GlStateManager.pushMatrix();
        GlStateManager.translate(this.hatTop.offsetX, this.hatTop.offsetY, this.hatTop.offsetZ);
        GlStateManager.translate(this.hatTop.rotationPointX * f5, this.hatTop.rotationPointY * f5, this.hatTop.rotationPointZ * f5);
        GlStateManager.scale(0.5D, 0.6D, 0.5D);
        GlStateManager.translate(-this.hatTop.offsetX, -this.hatTop.offsetY, -this.hatTop.offsetZ);
        GlStateManager.translate(-this.hatTop.rotationPointX * f5, -this.hatTop.rotationPointY * f5, -this.hatTop.rotationPointZ * f5);
        this.hatTop.render(f5);
        GlStateManager.popMatrix();
        this.hatBrim.render(f5);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
    
    	if (entity instanceof EntityCabinet) {
    		EntityCabinet cabinet = (EntityCabinet)entity;
    		this.hatBrim.rotateAngleX = cabinet.getRotationYawHead();
    		this.hatBrim.rotateAngleY = cabinet.getRotationYawHead();
    		this.hatBrim.rotateAngleZ = cabinet.getRotationYawHead();
    	}
    }
}
