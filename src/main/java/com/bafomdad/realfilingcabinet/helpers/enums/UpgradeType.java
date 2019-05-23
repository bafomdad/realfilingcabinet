package com.bafomdad.realfilingcabinet.helpers.enums;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;

import net.minecraft.util.ResourceLocation;

public enum UpgradeType {

	CREATIVE(null, StringLibs.TAG_CREATIVE),
	CRAFTING(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/craftingcabinet.png"), StringLibs.TAG_CRAFT),
	ENDER(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/endercabinet.png"), StringLibs.TAG_ENDER),
	OREDICT(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/oredictcabinet.png"), StringLibs.TAG_OREDICT),
	MOB(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/mobcabinet.png"), StringLibs.TAG_MOB),
	FLUID(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/fluidcabinet.png"), StringLibs.TAG_FLUID),
	LIFE(null, StringLibs.TAG_LIFE),
	SMELTING(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/smeltingcabinet.png"), StringLibs.TAG_SMELT);
	
	final ResourceLocation res;
	final String tag;
	
	private UpgradeType(ResourceLocation res, String tag) {
		
		this.res = res;
		this.tag = tag;
	}
	
	public ResourceLocation getTexture() {
		
		return res;
	}
	
	public String getTag() {
		
		return tag;
	}
}
