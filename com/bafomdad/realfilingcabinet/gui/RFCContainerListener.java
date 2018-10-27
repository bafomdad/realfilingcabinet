package com.bafomdad.realfilingcabinet.gui;

import com.bafomdad.realfilingcabinet.network.PacketRFC;
import com.bafomdad.realfilingcabinet.network.RFCPacketHandler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class RFCContainerListener implements IContainerListener {
	
	private final EntityPlayerMP player;
	
	public RFCContainerListener(EntityPlayerMP player) {
		
		this.player = player;
	}

	@Override
	public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {

		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("windowId", containerToSend.windowId);
		
		for (int i = 0; i < itemsList.size(); i++)
			tag.setBoolean("updateAll", true);
		
		RFCPacketHandler.INSTANCE.sendTo(new PacketRFC(tag), player);
	}

	@Override
	public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {

		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("windowId", containerToSend.windowId);
		tag.setBoolean("updateAll", false);
		tag.setInteger("slotId", slotInd);
		
		RFCPacketHandler.INSTANCE.sendTo(new PacketRFC(tag), player);
	}

	@Override
	public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {}

	@Override
	public void sendAllWindowProperties(Container containerIn, IInventory inventory) {}
}
