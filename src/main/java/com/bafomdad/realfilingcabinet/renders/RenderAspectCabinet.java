package com.bafomdad.realfilingcabinet.renders;

import net.minecraft.util.ResourceLocation;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;

public class RenderAspectCabinet extends RenderFilingCabinet {

	private static final ResourceLocation ASPECT = new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/aspectcabinet.png");
	
	@Override
	public void bindTheTex(TileEntityRFC te) {
		
		bindTexture(ASPECT);
	}
}
