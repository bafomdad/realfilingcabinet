package com.bafomdad.realfilingcabinet.network;

import java.util.List;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.core.Utils;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldServer;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class RFCTileMessage implements IMessage {
	
	private int posX, posY, posZ;
	private int amount, index;
	
	public RFCTileMessage() {}
	
	public RFCTileMessage(int x, int y, int z, int amount, int index) {
		
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		
		this.amount = amount;
		this.index = index;
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		posX = ByteBufUtils.readVarInt(buf, 5);
		posY = ByteBufUtils.readVarInt(buf, 5);
		posZ = ByteBufUtils.readVarInt(buf, 5);
		
		amount = ByteBufUtils.readVarInt(buf, 5);
		index = ByteBufUtils.readVarInt(buf, 1);
	}

	@Override
	public void toBytes(ByteBuf buf) {

		ByteBufUtils.writeVarInt(buf, posX, 5);
		ByteBufUtils.writeVarInt(buf, posY, 5);
		ByteBufUtils.writeVarInt(buf, posZ, 5);
		
		ByteBufUtils.writeVarInt(buf, amount, 5);
		ByteBufUtils.writeVarInt(buf, index, 1);
	}

	public static class Handler implements IMessageHandler<RFCTileMessage, IMessage> {
	
		@Override
		public IMessage onMessage(RFCTileMessage message, MessageContext ctx) {
	
			MinecraftServer server = MinecraftServer.getServer();
			for (WorldServer world : server.worldServers)
			{
				for (Object obj : world.playerEntities) {
					if (obj != null && obj instanceof EntityPlayerMP) 
					{
						EntityPlayerMP player = (EntityPlayerMP)obj;
						if (player.inventory.hasItem(RealFilingCabinet.itemFolder))
							this.processInventory(player, message.posX, message.posY, message.posZ, message.amount, message.index);
							
					}
				}
			}
			return null;
		}
		
		private void processInventory(EntityPlayer player, int tilex, int tiley, int tilez, int amount, int index) {
			
			if (player.openContainer == null)
				return;
			
			Container cont = player.openContainer;
			
			int invSize = -1;
			if (cont == player.inventoryContainer)
				invSize = 4;
			if (cont instanceof ContainerWorkbench)
				invSize = 9;
			
			if (invSize == -1)
				return;
			
			for (int i = 0; i < invSize; i++) {
				ItemStack folder = (ItemStack)cont.getInventory().get(i);
				if (folder == null)
					continue;
				if (folder.getItem() == RealFilingCabinet.itemFolder && folder.getItemDamage() == 1)
				{
					int x = Utils.getInt(folder, "RFC_xLoc", -1);
					int y = Utils.getInt(folder, "RFC_yLoc", -1);
					int z = Utils.getInt(folder, "RFC_zLoc", -1);
					int slotindex = Utils.getInt(folder, ItemFolder.TAG_SLOTINDEX, 0);
					
					if (x == tilex && y == tiley && z == tilez && index == slotindex)
					{
						ItemFolder.setFileSize(folder, amount);
						break;
					}
				}
			}
		}
	}
}
