package com.bafomdad.realfilingcabinet.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class FakeInventory implements IInventory {
	
	protected String inventoryTitle;
	protected int slotsCount;
	protected ItemStack[] inventoryContents;
	protected boolean isInvNameLocalized;
	
	public FakeInventory(String name, boolean isInvNameLocalized, int size) {
		
		this.isInvNameLocalized = isInvNameLocalized;
		slotsCount = size;
		inventoryTitle = name;
		inventoryContents = new ItemStack[size];
	}

	@Override
	public int getSizeInventory() {

		return slotsCount;
	}

	@Override
	public ItemStack getStackInSlot(int i) {

		return inventoryContents[i];
	}

	@Override
	public ItemStack decrStackSize(int par1, int par2) {
		
		if (inventoryContents[par1] != null) {
			ItemStack itemstack;
			
			if (inventoryContents[par1].stackSize <= par2) {
				itemstack = inventoryContents[par1];
				inventoryContents[par1] = null;
				return itemstack;
			}
			itemstack = inventoryContents[par1].splitStack(par2);
			if (inventoryContents[par1].stackSize == 0)
				inventoryContents[par1] = null;
			
			return itemstack;
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {

		inventoryContents[i] = itemstack;
		
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
			itemstack.stackSize = getInventoryStackLimit();
	}

	@Override
	public int getInventoryStackLimit() {

		return 64;
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {

		return true;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {

		return true;
	}
	
	public void copyFrom(IInventory inventory) {
		
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (i < getSizeInventory()) {
				ItemStack stack = inventory.getStackInSlot(i);
				if (stack != null)
					setInventorySlotContents(i, stack.copy());
				else setInventorySlotContents(i, null);
			}
		}
	}

	@Override
	public String getName() {

		return null;
	}

	@Override
	public boolean hasCustomName() {

		return false;
	}

	@Override
	public ITextComponent getDisplayName() {

		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {

		return null;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public int getField(int id) {

		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {

		return 0;
	}

	@Override
	public void clear() {}
}
