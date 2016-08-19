package com.bafomdad.realfilingcabinet.network;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.TileEntityRFC;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class RFCFolderMessage implements IMessage {

	int tileX;
	int tileY;
	int tileZ;
//	int tileDim;
	
	int index;
	int amount;
	
	public RFCFolderMessage() {}
	
	public RFCFolderMessage(int x, int y, int z, int amount, int index) {
		
		this.tileX = x;
		this.tileY = y;
		this.tileZ = z;
//		this.tileDim = dim;
		
		this.amount = amount;
		this.index = index;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {

		tileX = ByteBufUtils.readVarInt(buf, 5);
		tileY = ByteBufUtils.readVarInt(buf, 5);
		tileZ = ByteBufUtils.readVarInt(buf, 5);
//		tileDim = ByteBufUtils.readVarInt(buf, 1);
		
		amount = ByteBufUtils.readVarInt(buf, 1);
		index = ByteBufUtils.readVarInt(buf, 1);
	}

	@Override
	public void toBytes(ByteBuf buf) {

		ByteBufUtils.writeVarInt(buf, tileX, 5);
		ByteBufUtils.writeVarInt(buf, tileY, 5);
		ByteBufUtils.writeVarInt(buf, tileZ, 5);
//		ByteBufUtils.writeVarInt(buf, tileDim, 1);
		
		ByteBufUtils.writeVarInt(buf, amount, 1);
		ByteBufUtils.writeVarInt(buf, index, 1);
	}
	
	public static class Handler implements IMessageHandler<RFCFolderMessage, IMessage> {

		@Override
		public IMessage onMessage(RFCFolderMessage message, MessageContext ctx) {

			return null;
		}
	}
}
