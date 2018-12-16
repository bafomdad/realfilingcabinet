package com.bafomdad.realfilingcabinet.render;


import com.bafomdad.realfilingcabinet.blocks.entity.FilingCabinetEntity;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;

public class ModelFilingCabinet extends Model {

	Cuboid Cabinetback;
	Cuboid Cabinetside1;
	Cuboid Cabinetside2;
	Cuboid Cabinettop;
	Cuboid Cabinetbottom;
	Cuboid DrawerSide1;
	Cuboid DrawerSide2;
	Cuboid DrawerFront;
	Cuboid DrawerBottom;

	public ModelFilingCabinet() {

		field_17138 = 64;
		field_17139 = 64;

		Cabinetback = new Cuboid(this, 16, 0);
		Cabinetback.addBox(-7F, -8F, 7F, 14, 16, 1);
		Cabinetback.setRotationPoint(0F, 0F, 0F);
		Cabinetback.setTextureSize(64, 64);
		Cabinetback.mirror = true;
		setRotation(Cabinetback, 0F, 0F, 0F);
		Cabinetside1 = new Cuboid(this, 0, 0);
		Cabinetside1.addBox(-8F, -8F, -8F, 1, 16, 16);
		Cabinetside1.setRotationPoint(0F, 0F, 0F);
		Cabinetside1.setTextureSize(64, 64);
		Cabinetside1.mirror = true;
		setRotation(Cabinetside1, 0F, 0F, 0F);
		Cabinetside2 = new Cuboid(this, 15, 0);
		Cabinetside2.addBox(7F, -8F, -8F, 1, 16, 16);
		Cabinetside2.setRotationPoint(0F, 0F, 0F);
		Cabinetside2.setTextureSize(64, 64);
		Cabinetside2.mirror = true;
		setRotation(Cabinetside2, 0F, 0F, 0F);
		Cabinettop = new Cuboid(this, 2, 1);
		Cabinettop.addBox(-7F, -8F, -8F, 14, 2, 15);
		Cabinettop.setRotationPoint(0F, 0F, 0F);
		Cabinettop.setTextureSize(64, 64);
		Cabinettop.mirror = true;
		setRotation(Cabinettop, 0F, 0F, 0F);
		Cabinetbottom = new Cuboid(this, 3, 0);
		Cabinetbottom.addBox(-7F, 6F, -8F, 14, 2, 15);
		Cabinetbottom.setRotationPoint(0F, 0F, 0F);
		Cabinetbottom.setTextureSize(64, 64);
		Cabinetbottom.mirror = true;
		setRotation(Cabinetbottom, 0F, 0F, 0F);
		DrawerSide1 = new Cuboid(this, 18, 0);
		DrawerSide1.addBox(-7F, -6F, -7F, 1, 12, 14);
		DrawerSide1.setRotationPoint(0F, 0F, 0F);
		DrawerSide1.setTextureSize(64, 64);
		DrawerSide1.mirror = true;
		setRotation(DrawerSide1, 0F, 0F, 0F);
		DrawerSide2 = new Cuboid(this, 18, 0);
		DrawerSide2.addBox(6F, -6F, -7F, 1, 12, 14);
		DrawerSide2.setRotationPoint(0F, 0F, 0F);
		DrawerSide2.setTextureSize(64, 64);
		DrawerSide2.mirror = true;
		setRotation(DrawerSide2, 0F, 0F, 0F);
		DrawerFront = new Cuboid(this, 16, 48);
		DrawerFront.addBox(-7F, -6F, -8F, 14, 12, 1);
		DrawerFront.setRotationPoint(0F, 0F, 0F);
		DrawerFront.setTextureSize(64, 64);
		DrawerFront.mirror = true;
		setRotation(DrawerFront, 0F, 0F, 0F);
		DrawerBottom = new Cuboid(this, 2, 2);
		DrawerBottom.addBox(-7F, 5F, -7F, 14, 1, 14);
		DrawerBottom.setRotationPoint(0F, 0F, 0F);
		DrawerBottom.setTextureSize(64, 64);
		DrawerBottom.mirror = true;
		setRotation(DrawerBottom, 0F, 0F, 0F);
	}
	//
	public void render(FilingCabinetEntity be, float f) {
		
		float prevStep = be.renderOffset;
		be.renderOffset = be.offset;
		be.renderOffset = prevStep + (be.renderOffset - prevStep) * f;
		DrawerSide1.z = prevStep;
		DrawerSide2.z = prevStep;
		DrawerFront.z = prevStep;
		DrawerBottom.z = prevStep;
		
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

	private void setRotation(Cuboid model, float x, float y, float z) {
		
		model.rotationPointX = x;
		model.rotationPointY = y;
		model.rotationPointZ = z;
	}
}