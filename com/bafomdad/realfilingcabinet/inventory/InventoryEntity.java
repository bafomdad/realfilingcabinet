package com.bafomdad.realfilingcabinet.inventory;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.entity.EntityCabinet;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.utils.MobUtils;
import com.bafomdad.realfilingcabinet.utils.StorageUtils;

public class InventoryEntity extends ItemStackHandler {

	final EntityCabinet cabinet;
	
	public InventoryEntity(EntityCabinet cabinet, int size) {
		
		this.cabinet = cabinet;
		setSize(size);
	}
	
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		
        if (stack == null || stack.stackSize == 0)
            return null;

        validateSlotIndex(slot);

        if (simpleFolderMatch(stack) != -1)
        {
        	slot = simpleFolderMatch(stack);
        	if (!simulate) {
        		ItemFolder.add(stacks[slot], stack.stackSize);
        	}
        	return null;
        }
        return stack;
	}
	
	public ItemStack getStackFromFolder(int slot) {
		
		ItemStack folder = getStackInSlot(slot);
		if (ItemFolder.getObject(folder) == null)
			return null;
		
		if (folder != null && folder.getItem() instanceof IFolder)
		{
			if (ItemFolder.getObject(folder) instanceof ItemStack)
			{
				ItemStack stack = (ItemStack)ItemFolder.getObject(folder);
				if (stack != null) {
					return stack.copy();
				}
			}
		}
		return null;
	}
	
	public boolean canInsertItem(ItemStack stack) {
		
		return simpleFolderMatch(stack) != -1;
	}
	
	public boolean canInsertMob(EntityLivingBase elb, boolean doThing) {
		
		if (elb instanceof EntityPlayer)
			return false;
		
		if (!elb.isNonBoss() || elb.isChild())
			return false;
		
    	String entityName = EntityList.getEntityString(elb);
    	for (int i = 0; i < getSlots(); i++) {
    		ItemStack folder = getStackInSlot(i);
    		if (folder != null && folder.getItem() == RFCItems.folder) {
    			if (folder.getItemDamage() == 3 && ItemFolder.getObject(folder) != null) {
    				if (ItemFolder.getObject(folder).equals(entityName)) {
    					if (doThing)
    					{
    						ItemFolder.add(folder, 1);
    					}
    					return true;
    				}
    			}
    		}
    	}
		return false;
	}
	
	public int simpleFolderMatch(ItemStack stack) {
		
		if (stack == null)
			return -1;
		
		for (int i = 0; i < getSlots(); i++) {
			ItemStack loopinv = getStackFromFolder(i);
			if (loopinv != null && StorageUtils.simpleMatch(stack, loopinv))
				return i;
		}
		return -1;
	}
	
	public ItemStack[] getStacks() {
		
		return stacks;
	}
}
