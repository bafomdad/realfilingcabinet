package com.bafomdad.realfilingcabinet.integration;

public class CraftTweakerRFC implements IModCompat {
	
	@Override
	public String getModID() {

		return "crafttweaker";
	}

	@Override
	public boolean isConfigEnabled() {

		return true;
	}
}
