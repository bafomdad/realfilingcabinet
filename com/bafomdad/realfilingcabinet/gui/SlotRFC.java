package com.bafomdad.realfilingcabinet.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.inventory.InventoryRFC;
import com.bafomdad.realfilingcabinet.items.ItemFolder.FolderType;

public class SlotRFC extends Slot {
	
	private static IInventory emptyInventory = new InventoryBasic("[Null]", true, 0);
	private final InventoryRFC inv;
	private final int index;

	public SlotRFC(InventoryRFC inv, int index, int xPosition, int yPosition) {
		
		super(emptyInventory, index, xPosition, yPosition);
		this.inv = inv;
		this.index = index;
	}
	
    @Override
    public boolean isItemValid(ItemStack stack) {
    	
    	return stack.getItem() instanceof IFolder && stack.getItemDamage() != FolderType.ENDER.ordinal();
    }
    
    @Override
    public ItemStack getStack() {

    	return inv.getTrueStackInSlot(index);
    }
    
    @Override
    public void putStack(ItemStack stack) {
    	
    	inv.setStackInSlot(index, stack);
    	this.onSlotChanged();
    }
    
    @Override
    public ItemStack decrStackSize(int amount) {
    	
    	ItemStack copystack = inv.getTrueStackInSlot(index).copy();
    	inv.setStackInSlot(index, ItemStack.EMPTY);
    	return copystack;
    }
    
    @Override
    public boolean canTakeStack(EntityPlayer player) {
    	
    	return !inv.getTrueStackInSlot(index).isEmpty();
    }
    
    public IItemHandler getItemHandler() {
       
    	return inv;
    }
    
    @Override
    public boolean isSameInventory(Slot other) {
        
    	return other instanceof SlotRFC && ((SlotRFC)other).getItemHandler() == this.inv;
    }
}
