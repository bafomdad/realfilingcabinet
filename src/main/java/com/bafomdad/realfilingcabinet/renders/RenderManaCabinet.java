package com.bafomdad.realfilingcabinet.renders;

import net.minecraft.util.ResourceLocation;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;

public class RenderManaCabinet extends RenderFilingCabinet{
	
	private static final ResourceLocation MANA = new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/manacabinet.png");
	
	@Override
	public void bindTheTex(TileEntityRFC te) {
		
		bindTexture(MANA);
	}
}
