package com.bafomdad.realfilingcabinet.helpers.enums;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.entity.ModelHatFluid;
import com.bafomdad.realfilingcabinet.entity.ModelHatMob;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;

import net.minecraft.util.ResourceLocation;

public enum MobUpgradeType {

	MOB(4, new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/entity/hatmob.png"), ModelHatMob.class.getName(), StringLibs.TAG_MOB),
	FLUID(5, new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/entity/hatfluid.png"), ModelHatFluid.class.getName(), StringLibs.TAG_FLUID);
	
	final int damage;
	final ResourceLocation tex;
	final String modelClassName;
	final String tag;
	
	private MobUpgradeType(int damage, ResourceLocation texture, String modelClassName, String tag) {
		
		this.damage = damage;
		this.tex = texture;
		this.modelClassName = modelClassName;
		this.tag = tag;
	}
	
	public int getItemDamage() {
		
		return damage;
	}
	
	public ResourceLocation getTexture() {
		
		return tex;
	}
	
	public String getModel() {
		
		return modelClassName;
	}
	
	public String getTag() {
		
		return tag;
	}
}
