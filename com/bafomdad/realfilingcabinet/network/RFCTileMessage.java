package com.bafomdad.realfilingcabinet.network;

import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RFCTileMessage implements IMessage {
	
	private long pos;
	private boolean open;
	
	public RFCTileMessage() {}
	
	public RFCTileMessage(TileEntityRFC tile, boolean open) {
		
		this.pos = tile.getPos().toLong();
		this.open = open;
	}

	@Override
	public void toBytes(ByteBuf buf) {

		buf.writeLong(pos);
		buf.writeBoolean(open);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		
		pos = buf.readLong();
		open = buf.readBoolean();
	}
	
	public BlockPos getPos() {
		
		return BlockPos.fromLong(pos);
	}
	
	public static class Handler implements IMessageHandler<RFCTileMessage, IMessage> {

		@Override
		public IMessage onMessage(RFCTileMessage message, MessageContext ctx) {

			if (ctx.getServerHandler().player.world.isBlockLoaded(message.getPos())) return null;
			
			TileEntityRFC tile = (TileEntityRFC)ctx.getServerHandler().player.world.getTileEntity(message.getPos());
			if (tile != null)
				tile.isOpen = message.open;
			
			return null;
		}

	}
}
