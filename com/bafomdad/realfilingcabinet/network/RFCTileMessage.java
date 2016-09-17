package com.bafomdad.realfilingcabinet.network;

import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.utils.EnderUtils;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class RFCTileMessage implements IMessage {
	
	private BlockPos tileLoc;
	private int x;
	private int y;
	private int z;
	private int amount, index, dim;
	
	public RFCTileMessage() {}
	
	public RFCTileMessage(BlockPos tileLoc, int tDim, int amount, int index) {
		
		this.tileLoc = tileLoc;
		this.dim = tDim;
		this.x = tileLoc.getX();
		this.y = tileLoc.getY();
		this.z = tileLoc.getZ();
		this.amount = amount;
		this.index = index;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		
		x = ByteBufUtils.readVarInt(buf, 5);
		y = ByteBufUtils.readVarInt(buf, 5);
		z = ByteBufUtils.readVarInt(buf, 5);
		
		dim = ByteBufUtils.readVarInt(buf, 2);
		amount = ByteBufUtils.readVarInt(buf, 3);
		index = ByteBufUtils.readVarInt(buf, 2);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		
		ByteBufUtils.writeVarInt(buf, x, 5);
		ByteBufUtils.writeVarInt(buf, y, 5);
		ByteBufUtils.writeVarInt(buf, z, 5);
		
		ByteBufUtils.writeVarInt(buf, dim, 2);
		ByteBufUtils.writeVarInt(buf, amount, 3);
		ByteBufUtils.writeVarInt(buf, index, 2);
	}
	
	public static class Handler implements IMessageHandler<RFCTileMessage, IMessage> {

		@Override
		public IMessage onMessage(RFCTileMessage message, MessageContext ctx) {

			TileEntityRFC tile = EnderUtils.findLoadedTileEntityInWorld(new BlockPos(message.x, message.y, message.z), message.dim);
			if (tile != null)
			{
				ItemStack folder = tile.getInventory().getTrueStackInSlot(message.index);
				if (folder != null) {
//					System.out.println("Amount: " + message.amount);
					ItemFolder.remove(folder, message.amount);
					ItemFolder.extractSize = 0;
				}
			}
			return null;
		}
	}
}
