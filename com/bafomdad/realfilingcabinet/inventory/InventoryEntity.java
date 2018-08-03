package com.bafomdad.realfilingcabinet.inventory;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

import com.bafomdad.realfilingcabinet.NewConfigRFC.ConfigRFC;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.entity.EntityCabinet;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.utils.MobUtils;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;
import com.bafomdad.realfilingcabinet.utils.StorageUtils;

public class InventoryEntity extends ItemStackHandler {

	final EntityCabinet cabinet;
	
	public InventoryEntity(EntityCabinet cabinet, int size) {
		
		this.cabinet = cabinet;
		setSize(size);
	}
	
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		
        if (stack == null || stack.getCount() == 0)
            return null;

        validateSlotIndex(slot);

        if (simpleFolderMatch(stack) != -1)
        {
        	slot = simpleFolderMatch(stack);
        	if (!simulate) {
        		if (getStacks().get(slot).getItemDamage() == 2)
        		{
        			int remSize = stack.getItemDamage();
        			int storedRem = ItemFolder.getRemSize(getStacks().get(slot));
        			
        			if (remSize == 0)
        				ItemFolder.add(getStacks().get(slot), 1);
        			
        			ItemFolder.addRem(getStacks().get(slot), stack.getMaxDamage() - stack.getItemDamage());
        			int newRem = ItemFolder.getRemSize(getStacks().get(slot));
        			
        			if (newRem >= stack.getMaxDamage())
        			{
        				ItemFolder.add(getStacks().get(slot), 1);
        				int newStoredRem = newRem - stack.getMaxDamage();
        				ItemFolder.setRemSize(getStacks().get(slot), newStoredRem);
        			}
        		}
        		else
        			ItemFolder.add(stacks.get(slot), stack.getCount());
        	}
        	return null;
        }
        return stack;
	}
	
	public ItemStack getStackFromFolder(int slot) {
		
		ItemStack folder = getStackInSlot(slot);
		if (ItemFolder.getObject(folder) == null)
			return ItemStack.EMPTY;
		
		if (folder != null && folder.getItem() instanceof IFolder)
		{
			if (ItemFolder.getObject(folder) instanceof ItemStack)
			{
				ItemStack stack = (ItemStack)ItemFolder.getObject(folder);
				if (!stack.isEmpty()) {
					return stack.copy();
				}
			}
		}
		return ItemStack.EMPTY;
	}
	
	public boolean canInsertItem(ItemStack stack) {
		
		return simpleFolderMatch(stack) != -1;
	}
	
	public boolean canInsertMob(EntityLivingBase elb, boolean doThing) {
		
		if (elb instanceof EntityPlayer)
			return false;
		
		if (!elb.isNonBoss() || elb.isChild())
			return false;
		
		if (elb instanceof EntityCabinet)
			return false;
		
		if (elb instanceof IEntityOwnable && ((IEntityOwnable)elb).getOwner() != null)
			return false;
		
		String entityblacklist = elb.getClass().getSimpleName();
		for (String toBlacklist : ConfigRFC.mobFolderBlacklist) {
			if (toBlacklist.contains(entityblacklist))
				return false;
		}
		
		String entityName = EntityList.getKey(elb).toString();
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
		
		if (stack.isEmpty())
			return -1;
		
		for (int i = 0; i < getSlots(); i++) {
			ItemStack loopinv = getStackFromFolder(i);
			if (!loopinv.isEmpty() && getStackInSlot(i).getItemDamage() == 2 && stack.getItem() == loopinv.getItem()) {
				if (stack.hasTagCompound() && NBTUtils.getBoolean(getStackInSlot(i), StringLibs.RFC_IGNORENBT, false))
					return i;
				else if (!stack.hasTagCompound())
					return i;
			}
			if (!loopinv.isEmpty() && (getStackInSlot(i).getItemDamage() == 5 && ItemStack.areItemStackTagsEqual(stack, loopinv)))
				return i;
			if (!loopinv.isEmpty() && ((getStackInSlot(i).getItemDamage() != 5 && getStackInSlot(i).getItemDamage() != 2) && StorageUtils.simpleMatch(stack, loopinv)))
				return i;
		}
		return -1;
	}
	
	public NonNullList<ItemStack> getStacks() {
		
		return stacks;
	}
}
