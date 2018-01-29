package com.bafomdad.realfilingcabinet.init;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.*;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityAC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileManaCabinet;
import com.bafomdad.realfilingcabinet.renders.RenderAspectCabinet;
import com.bafomdad.realfilingcabinet.renders.RenderFilingCabinet;
import com.bafomdad.realfilingcabinet.renders.RenderManaCabinet;

public class RFCBlocks {

	public static BlockRFC blockRFC;
	public static BlockManaCabinet manaCabinet;
	public static BlockAC blockAC;
	
	public static void init() {
		
		blockRFC = new BlockRFC();
		if (RealFilingCabinet.botaniaLoaded && ConfigRFC.botaniaIntegration)
			manaCabinet = new BlockManaCabinet();
		if (RealFilingCabinet.tcLoaded && ConfigRFC.tcIntegration)
			blockAC = new BlockAC();
	}
	
	@SideOnly(Side.CLIENT)
	public static void initModels() {

		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockRFC), 0, new ModelResourceLocation(blockRFC.getRegistryName(), "inventory"));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRFC.class, new RenderFilingCabinet());
		
		if (RealFilingCabinet.botaniaLoaded && ConfigRFC.botaniaIntegration) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(manaCabinet), 0, new ModelResourceLocation(manaCabinet.getRegistryName(), "inventory"));
			ClientRegistry.bindTileEntitySpecialRenderer(TileManaCabinet.class, new RenderManaCabinet());
		}
		if (RealFilingCabinet.tcLoaded && ConfigRFC.tcIntegration) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockAC), 0, new ModelResourceLocation(blockAC.getRegistryName(), "inventory"));
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAC.class, new RenderAspectCabinet());
		}
	}
}
