package com.bafomdad.realfilingcabinet.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ModelEntityCabinet extends ModelBase {

	ModelRenderer body1;
	ModelRenderer armR1;
	ModelRenderer armL1;
	ModelRenderer legR1;
	ModelRenderer legL1;
	ModelRenderer legR2;
	ModelRenderer legL2;

	public ModelEntityCabinet() {
		
		textureWidth = 64;
		textureHeight = 32;

		body1 = new ModelRenderer(this, 0, 0);
		body1.addBox(-8F, -8F, -8F, 16, 16, 16);
		body1.setRotationPoint(0F, 0F, 0F);
		body1.setTextureSize(64, 32);
		body1.mirror = true;
		setRotation(body1, 0F, 0F, 0F);
		armR1 = new ModelRenderer(this, 17, 1);
		armR1.addBox(-1F, -1F, -2F, 2, 12, 4);
		armR1.setRotationPoint(-9F, -1F, 0F);
		armR1.setTextureSize(64, 32);
		armR1.mirror = true;
		setRotation(armR1, 0F, 0F, 0F);
		armL1 = new ModelRenderer(this, 17, 1);
		armL1.addBox(-1F, -1F, -2F, 2, 12, 4);
		armL1.setRotationPoint(9F, -1F, 0F);
		armL1.setTextureSize(64, 32);
		armL1.mirror = true;
		setRotation(armL1, 0F, 0F, 0F);
		legR1 = new ModelRenderer(this, 1, 17);
		legR1.addBox(-1.5F, 0F, -1.5F, 3, 2, 3);
		legR1.setRotationPoint(-3F, 8F, 0F);
		legR1.setTextureSize(64, 32);
		legR1.mirror = true;
		setRotation(legR1, 0F, 0F, 0F);
		legL1 = new ModelRenderer(this, 1, 17);
		legL1.addBox(-1.5F, 0F, -1.5F, 3, 2, 3);
		legL1.setRotationPoint(3F, 8F, 0F);
		legL1.setTextureSize(64, 32);
		legL1.mirror = true;
		setRotation(legL1, 0F, 0F, 0F);
		legR2 = new ModelRenderer(this, 19, 2);
		legR2.addBox(-3F, 0F, -3F, 6, 2, 6);
		legR2.setRotationPoint(-3F, 10F, 0F);
		legR2.setTextureSize(64, 32);
		legR2.mirror = true;
		setRotation(legR2, 0F, 0F, 0F);
		legL2 = new ModelRenderer(this, 19, 2);
		legL2.addBox(-3F, 0F, -3F, 6, 2, 6);
		legL2.setRotationPoint(3F, 10F, 0F);
		legL2.setTextureSize(64, 32);
		legL2.mirror = true;
		setRotation(legL2, 0F, 0F, 0F);
		legL2.mirror = false;
		
//		legL1.addChild(legL2);
//		legR1.addChild(legR2);
	}

	public void render(Entity entity, float f, float f1, float age, float f3, float f4, float f5) {
		
		float speed = (float)((new Vec3d(entity.motionX, 0, entity.motionZ)).length() * 3.0F);
		GlStateManager.translate(0, 0.15, 0);
		super.render(entity, f, f1, age, f3, f4, f5);
		setRotationAngles(f, f1, age, f3, f4, f5, entity);
		body1.render(f5);
		
	    GlStateManager.rotate(speed*60f*(float) Math.sin(Math.toRadians(age % 360)*24F), 1, 0, 0);
	    armR1.render(f5);
	    GlStateManager.rotate(-120f*speed*(float) Math.sin(Math.toRadians(age % 360)*24F), 1, 0, 0);
	    armL1.render(f5);
	    GlStateManager.rotate(speed*60f*(float) Math.sin(Math.toRadians(age % 360)*24F), 1, 0, 0);
	    GlStateManager.rotate(speed*60f*(float) Math.sin(Math.toRadians(age % 360)*24F), 1, 0, 0);
	    legL2.offsetX = 0.1F;
	    legL2.offsetY = 0.6F;
	    legL2.render(f5);
	    GlStateManager.rotate(-120f*speed*(float) Math.sin(Math.toRadians(age % 360)*24F), 1, 0, 0);
	    legR2.offsetX = -0.1F;
	    legR2.offsetY = 0.6F;
	    legR2.render(f5);
	    
	    GlStateManager.rotate(30F*(float)Math.abs(Math.sin(Math.toRadians(age % 360)*15F)), 0, 1, 0);
	    GlStateManager.translate(0, -0.15, 0);
		
//		legR2.render(f5);
//		legL2.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		body1.setRotationPoint(0F, 5F + MathHelper.cos(f * 1.4F) * 2.5F * f1, 0F);
		
		boolean flag = entity instanceof EntityCabinet && ((EntityCabinet)entity).isYaying();
		float yay = flag ? 3F : 0F;

		armL1.rotateAngleX = yay;
		armR1.rotateAngleX = yay;

	}
}