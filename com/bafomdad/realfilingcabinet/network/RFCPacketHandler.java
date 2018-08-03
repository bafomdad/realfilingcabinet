package com.bafomdad.realfilingcabinet.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class RFCPacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("RealFilingCabinet".toLowerCase());
	private static byte packetId = 0;
	
	public static void init() {
		
		INSTANCE.registerMessage(PacketMouse.Handler.class, PacketMouse.class, packetId++, Side.SERVER);
//		INSTANCE.registerMessage(RFCTileMessage.Handler.class, RFCTileMessage.class, packetId++, Side.SERVER);
	}
}
