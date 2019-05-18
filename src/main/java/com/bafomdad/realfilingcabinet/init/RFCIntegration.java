package com.bafomdad.realfilingcabinet.init;

import java.util.Optional;

import net.minecraftforge.fml.common.Loader;

import com.bafomdad.realfilingcabinet.integration.*;

public final class RFCIntegration {
	
	public static final IModCompat BOTANIA = new BotaniaRFC();
	public static final IModCompat THAUMCRAFT = new ThaumcraftRFC();
	public static final IModCompat WAILA = new WailaRFC();
	public static final IModCompat TOP = new TopRFC();
	public static final IModCompat CRT = new CraftTweakerRFC();

	public static Optional<IModCompat> canLoad(IModCompat compat) {
		
		return (Loader.isModLoaded(compat.getModID()) && compat.isConfigEnabled()) ? Optional.of(compat) : Optional.empty();
	}
}
