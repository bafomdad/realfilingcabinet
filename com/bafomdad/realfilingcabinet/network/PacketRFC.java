package com.bafomdad.realfilingcabinet.network;

import com.bafomdad.realfilingcabinet.LogRFC;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityFolder;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityProviderFolder;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketRFC implements IMessage {
	
	private NBTTagCompound tags = new NBTTagCompound();
	
	public PacketRFC() {}
	
	public PacketRFC(NBTTagCompound tags) {
		
		this.tags = tags;
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		tags = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {

		ByteBufUtils.writeTag(buf, tags);
	}
	
	public static class ServerHandler implements IMessageHandler<PacketRFC, PacketRFC> {

		@Override
		public PacketRFC onMessage(PacketRFC message, MessageContext ctx) {

			return null;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static class ClientHandler implements IMessageHandler<PacketRFC, PacketRFC> {

		@Override
		public PacketRFC onMessage(PacketRFC message, MessageContext ctx) {

			if (!message.tags.hasKey("windowId", 3))
				return null;
			
			int wid = message.tags.hasKey("windowId", 3) ? message.tags.getInteger("windowId") : -1;
			Container cont = Minecraft.getMinecraft().player.openContainer;
			
			if (cont == null || cont.windowId != wid)
				return null;
			
			for (Slot slot : cont.inventorySlots) {
				ItemStack stack = slot.getStack();
				CapabilityFolder cap;
				if (!stack.hasTagCompound()) continue;
				
				else if (stack.hasCapability(CapabilityProviderFolder.FOLDER_CAP, null) && stack.getTagCompound().hasKey("folderCap")) {
					cap = stack.getCapability(CapabilityProviderFolder.FOLDER_CAP, null);
					LogRFC.debug("Packeting :" + stack.getTagCompound().getCompoundTag("folderCap"));
					cap.deserializeNBT(stack.getTagCompound().getCompoundTag("folderCap"));
					stack.getTagCompound().removeTag("folderCap");
					
				} else continue;
				
				if (stack.getTagCompound().getSize() <= 0)
					stack.setTagCompound(null);
			}
			return null;
		}
	}
}
