package com.bafomdad.realfilingcabinet.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;

import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;

public class ContainerRFC extends Container {
	
	private TileEntityRFC te;
	
	public ContainerRFC(EntityPlayer player, TileEntityRFC te) {
		
		this.te = te;
		
		for (int i = 0; i < te.getInventory().getSlots(); i++)
			addSlotToContainer(new SlotRFC(te.getInventory(), i, 16 + i * 18, 18));
		
		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++)
				addSlotToContainer(new Slot(player.inventory, k + j * 9 + 9, 8 + k * 18, 50 + j * 18));
		}
		for (int l = 0; l < 9; l++)
			addSlotToContainer(new Slot(player.inventory, l, 8 + l * 18, 108));
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {
		
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = (Slot)this.inventorySlots.get(i);
		
		if (slot != null && slot.getHasStack()) {
			ItemStack stack1 = slot.getStack();
			stack = stack1.copy();
			
			if (i < te.getInventory().getSlots()) {
				if (!this.mergeItemStack(stack1, te.getInventory().getSlots(), this.inventorySlots.size(), true))
					return ItemStack.EMPTY;
			}
			else if (!this.mergeItemStack(stack1, 0, te.getInventory().getSlots(), false))
				return ItemStack.EMPTY;
			
			if (stack1.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
		}
		return stack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {

		return !(player instanceof FakePlayer);
	}
}
