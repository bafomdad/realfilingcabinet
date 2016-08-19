package com.bafomdad.realfilingcabinet.proxies;

import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.TileEntityRFC;
import com.bafomdad.realfilingcabinet.renders.RenderFilingCabinet;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void init() {
		
		super.init();
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRFC.class, new RenderFilingCabinet());
	}
	
	@Override
	public void registerRenderers() {
		
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(RealFilingCabinet.blockRFC), new RenderFilingCabinet.RenderFilingCabinetItem());
	}
	
	@Override
	public World getClientWorld() {
		
		return FMLClientHandler.instance().getClient().theWorld;
	}
}
