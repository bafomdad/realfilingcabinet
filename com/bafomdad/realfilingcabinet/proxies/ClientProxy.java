package com.bafomdad.realfilingcabinet.proxies;

import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.init.*;

public class ClientProxy extends CommonProxy {

	@Override
	public void initAllModels() {
		
		RFCBlocks.initModels();
		RFCItems.initModels();
	}
}
