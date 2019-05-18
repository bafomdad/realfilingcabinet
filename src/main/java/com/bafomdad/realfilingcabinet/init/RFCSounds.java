package com.bafomdad.realfilingcabinet.init;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class RFCSounds {

	public static final SoundEvent SQUEAK = createSound("squeak");
	public static final SoundEvent DRAWER = createSound("wooden");
	
	private static SoundEvent createSound(String name) {
		
		ResourceLocation res = new ResourceLocation(RealFilingCabinet.MOD_ID, name);
		return new SoundEvent(res).setRegistryName(res);
	}
}
