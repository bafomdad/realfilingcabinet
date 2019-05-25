package com.bafomdad.realfilingcabinet.network;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class RFCPacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(RealFilingCabinet.MOD_ID);
	private static byte packetId = 0;
	
	public static void init() {
		
		INSTANCE.registerMessage(PacketMouse.Handler.class, PacketMouse.class, packetId++, Side.SERVER);
	}
	
	public static void initClient() {
		
		// TODO: maybe re-add the containerlistener packet event stuff
	}
}
