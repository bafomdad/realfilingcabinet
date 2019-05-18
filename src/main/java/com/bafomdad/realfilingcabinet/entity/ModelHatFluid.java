package com.bafomdad.realfilingcabinet.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class ModelHatFluid extends ModelBase {
    
	public ModelRenderer hatMain;
    public ModelRenderer hatBill;

    public ModelHatFluid() {
        
    	this.textureWidth = 64;
        this.textureHeight = 32;
        this.hatMain = new ModelRenderer(this, 0, 0);
        this.hatMain.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hatMain.addBox(-8.0F, 0.0F, -8.0F, 16, 16, 16, 0.0F);
        this.hatBill = new ModelRenderer(this, 32, 0);
        this.hatBill.setRotationPoint(4.0F, 5.5F, 0.0F);
        this.hatBill.addBox(0.0F, 0.0F, -2.0F, 4, 1, 4, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 

    	GlStateManager.translate(0, 0.30, 0);
    	GlStateManager.pushMatrix();
        GlStateManager.translate(this.hatMain.offsetX, this.hatMain.offsetY, this.hatMain.offsetZ);
        GlStateManager.translate(this.hatMain.rotationPointX * f5, this.hatMain.rotationPointY * f5, this.hatMain.rotationPointZ * f5);
        GlStateManager.scale(0.5D, 0.4D, 0.5D);
        GlStateManager.translate(-this.hatMain.offsetX, -this.hatMain.offsetY, -this.hatMain.offsetZ);
        GlStateManager.translate(-this.hatMain.rotationPointX * f5, -this.hatMain.rotationPointY * f5, -this.hatMain.rotationPointZ * f5);
        this.hatMain.render(f5);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.hatBill.offsetX, this.hatBill.offsetY, this.hatBill.offsetZ);
        GlStateManager.translate(this.hatBill.rotationPointX * f5, this.hatBill.rotationPointY * f5, this.hatBill.rotationPointZ * f5);
        GlStateManager.scale(1.7D, 1.0D, 1.7D);
        GlStateManager.translate(-this.hatBill.offsetX, -this.hatBill.offsetY, -this.hatBill.offsetZ);
        GlStateManager.translate(-this.hatBill.rotationPointX * f5, -this.hatBill.rotationPointY * f5, -this.hatBill.rotationPointZ * f5);
        this.hatBill.render(f5);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
    
    	if (entity instanceof EntityCabinet) {
    		EntityCabinet cabinet = (EntityCabinet)entity;
    		this.hatBill.rotateAngleX = 0;
    		this.hatBill.rotateAngleY = 0;
    		this.hatBill.rotateAngleZ = 0;
    	}
    }
}
