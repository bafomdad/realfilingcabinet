package com.bafomdad.realfilingcabinet.renders;

import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileFilingCabinet;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelFilingCabinet extends ModelBase {

	ModelRenderer Cabinetback;
	ModelRenderer Cabinetside1;
	ModelRenderer Cabinetside2;
	ModelRenderer Cabinettop;
	ModelRenderer Cabinetbottom;
	ModelRenderer DrawerSide1;
	ModelRenderer DrawerSide2;
	ModelRenderer DrawerFront;
	ModelRenderer DrawerBottom;

	public ModelFilingCabinet() {
		
		textureWidth = 64;
		textureHeight = 64;

		Cabinetback = new ModelRenderer(this, 16, 0);
		Cabinetback.addBox(-7F, -8F, 7F, 14, 16, 1);
		Cabinetback.setRotationPoint(0F, 0F, 0F);
		Cabinetback.setTextureSize(64, 64);
		Cabinetback.mirror = true;
		setRotation(Cabinetback, 0F, 0F, 0F);
		Cabinetside1 = new ModelRenderer(this, 0, 0);
		Cabinetside1.addBox(-8F, -8F, -8F, 1, 16, 16);
		Cabinetside1.setRotationPoint(0F, 0F, 0F);
		Cabinetside1.setTextureSize(64, 64);
		Cabinetside1.mirror = true;
		setRotation(Cabinetside1, 0F, 0F, 0F);
		Cabinetside2 = new ModelRenderer(this, 15, 0);
		Cabinetside2.addBox(7F, -8F, -8F, 1, 16, 16);
		Cabinetside2.setRotationPoint(0F, 0F, 0F);
		Cabinetside2.setTextureSize(64, 64);
		Cabinetside2.mirror = true;
		setRotation(Cabinetside2, 0F, 0F, 0F);
		Cabinettop = new ModelRenderer(this, 2, 1);
		Cabinettop.addBox(-7F, -8F, -8F, 14, 2, 15);
		Cabinettop.setRotationPoint(0F, 0F, 0F);
		Cabinettop.setTextureSize(64, 64);
		Cabinettop.mirror = true;
		setRotation(Cabinettop, 0F, 0F, 0F);
		Cabinetbottom = new ModelRenderer(this, 3, 0);
		Cabinetbottom.addBox(-7F, 6F, -8F, 14, 2, 15);
		Cabinetbottom.setRotationPoint(0F, 0F, 0F);
		Cabinetbottom.setTextureSize(64, 64);
		Cabinetbottom.mirror = true;
		setRotation(Cabinetbottom, 0F, 0F, 0F);
		DrawerSide1 = new ModelRenderer(this, 18, 0);
		DrawerSide1.addBox(-7F, -6F, -7F, 1, 12, 14);
		DrawerSide1.setRotationPoint(0F, 0F, 0F);
		DrawerSide1.setTextureSize(64, 64);
		DrawerSide1.mirror = true;
		setRotation(DrawerSide1, 0F, 0F, 0F);
		DrawerSide2 = new ModelRenderer(this, 18, 0);
		DrawerSide2.addBox(6F, -6F, -7F, 1, 12, 14);
		DrawerSide2.setRotationPoint(0F, 0F, 0F);
		DrawerSide2.setTextureSize(64, 64);
		DrawerSide2.mirror = true;
		setRotation(DrawerSide2, 0F, 0F, 0F);
		DrawerFront = new ModelRenderer(this, 16, 48);
		DrawerFront.addBox(-7F, -6F, -8F, 14, 12, 1);
		DrawerFront.setRotationPoint(0F, 0F, 0F);
		DrawerFront.setTextureSize(64, 64);
		DrawerFront.mirror = true;
		setRotation(DrawerFront, 0F, 0F, 0F);
		DrawerBottom = new ModelRenderer(this, 2, 2);
		DrawerBottom.addBox(-7F, 5F, -7F, 14, 1, 14);
		DrawerBottom.setRotationPoint(0F, 0F, 0F);
		DrawerBottom.setTextureSize(64, 64);
		DrawerBottom.mirror = true;
		setRotation(DrawerBottom, 0F, 0F, 0F);
	}
	//
	public void render(TileFilingCabinet te, float f) {
		
		float prevStep = te.renderOffset;
		te.renderOffset = te.offset;
		te.renderOffset = prevStep + (te.renderOffset - prevStep) * f;	
		DrawerSide1.offsetZ = prevStep;
		DrawerSide2.offsetZ = prevStep;
		DrawerFront.offsetZ = prevStep;
		DrawerBottom.offsetZ = prevStep;
		
		Cabinetback.render(f);
		Cabinetside1.render(f);
		Cabinetside2.render(f);
		Cabinettop.render(f);
		Cabinetbottom.render(f);
		
		DrawerSide1.render(f);
		DrawerSide2.render(f);
		DrawerFront.render(f);
		DrawerBottom.render(f);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}