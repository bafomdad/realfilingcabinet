package com.bafomdad.realfilingcabinet.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class RFCPacketHandler {
	
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("RealFilingCabinet".toLowerCase());
	private static byte packetId = 0;
	
	public static void init() {
		
		INSTANCE.registerMessage(RFCTileMessage.Handler.class, RFCTileMessage.class, packetId++, Side.SERVER);
		INSTANCE.registerMessage(RFCFolderMessage.Handler.class, RFCFolderMessage.class, packetId++, Side.SERVER);
	}
}
