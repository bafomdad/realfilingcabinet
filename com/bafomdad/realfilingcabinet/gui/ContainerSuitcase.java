package com.bafomdad.realfilingcabinet.gui;

import com.bafomdad.realfilingcabinet.inventory.InventorySuitcase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerSuitcase extends Container {

	private final InventorySuitcase suitcaseInv;
	
	public ContainerSuitcase(InventoryPlayer player, InventorySuitcase suitcaseInv) {
		
		this.suitcaseInv = suitcaseInv;
		
		for (int i = 0; i < 8; i++)
			addSlotToContainer(new SlotItemHandler(suitcaseInv, i, 16 + i * 18, 18));

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++)
				addSlotToContainer(new Slot(player, k + j * 9 + 9, 8 + k * 18, 50 + j * 18));
		}
		for (int l = 0; l < 9; l++)
			addSlotToContainer(new Slot(player, l, 8 + l * 18, 108));
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {

		return player.getHeldItemMainhand() == suitcaseInv.getCase();
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = (Slot)this.inventorySlots.get(slotIndex);
		
		if (slot != null && slot.getHasStack()) {
			ItemStack stack1 = slot.getStack();
			stack = stack1.copy();
			
			if (slotIndex < 8) {
				if (!this.mergeItemStack(stack1, 8, this.inventorySlots.size(), true))
					return ItemStack.EMPTY;
			}
			else if (!this.mergeItemStack(stack1, 0, 8, false))
				return ItemStack.EMPTY;
			
			if (stack1.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
			
			slot.onTake(player, stack1);
		}
		return stack;
	}
}
