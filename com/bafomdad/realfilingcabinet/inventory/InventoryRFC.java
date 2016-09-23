package com.bafomdad.realfilingcabinet.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.ItemStackHandler;

import com.bafomdad.realfilingcabinet.LogRFC;
import com.bafomdad.realfilingcabinet.api.common.IFolder;
import com.bafomdad.realfilingcabinet.api.helper.UpgradeHelper;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.network.VanillaPacketDispatcher;
import com.bafomdad.realfilingcabinet.utils.AutocraftingUtils;
import com.bafomdad.realfilingcabinet.utils.StorageUtils;

public class InventoryRFC extends ItemStackHandler implements IInventory {
	
	final TileEntityRFC tile;

	public InventoryRFC(TileEntityRFC tile, int size) {
		
		this.tile = tile;
		setSize(size);
	}
	
	@Override
	public void markDirty() {
		
		tile.markDirty();
	}
	
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		
		LogRFC.debug("Transfer stack: " + stack + " / Stack in slot: " + getStackInSlot(slot) + " / True stack in slot: "  + getTrueStackInSlot(slot) + " / Slot #" + slot + " / Simulating: " + simulate);
		
		if (tile.isCabinetLocked())
			return stack;
		
        if (stack == null || stack.stackSize == 0)
            return null;

        validateSlotIndex(slot);

//      if (ItemStack.areItemsEqual(stack, this.getStackFromFolder(slot)))
        if (StorageUtils.simpleFolderMatch(tile, stack) != -1)
        {
        	slot = StorageUtils.simpleFolderMatch(tile, stack);
        	if (!simulate) {
        		ItemFolder.add(stacks[slot], stack.stackSize);
        		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile.getWorld(), tile.getPos());
        		
            	return null;
        	}
        	return null;
        }
        return stack;
	}
	
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		
		LogRFC.debug("Extraction slot: " + slot + " / Extraction amount: " + amount + " / " + simulate);
		
		ItemStack stackFolder = this.getStackFromFolder(slot);
		if (stackFolder == null || tile.isCabinetLocked() || UpgradeHelper.getUpgrade(tile, StringLibs.TAG_CRAFT) != null)
			return null;
		
		if (tile.hasItemFrame() && tile.getFilter() == null)
			return null;
		
		if (tile.getFilter() != null)
		{
			int i = StorageUtils.simpleFolderMatch(tile, tile.getFilter());
			if (i != -1 && slot == i)
			{
				stackFolder = this.getStackFromFolder(i);
				long filterCount = ItemFolder.getFileSize(getTrueStackInSlot(i));
				if (filterCount == 0)
					return null;
				
				long filterExtract = Math.min(stackFolder.getMaxStackSize(), filterCount);
				amount = Math.min((int)filterExtract, amount);
				
				if (!simulate && !UpgradeHelper.isCreative(tile)) {
					ItemFolder.remove(getTrueStackInSlot(i), amount);
		    		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile.getWorld(), tile.getPos());
				}
				return new ItemStack(stackFolder.getItem(), amount, stackFolder.getItemDamage());
			}
			return null;
		}
		long count = ItemFolder.getFileSize(getTrueStackInSlot(slot));
		if (count == 0)
			return null;
		
		long extract = Math.min(stackFolder.getMaxStackSize(), count);
		amount = Math.min((int)extract, amount);
		
		if (!simulate && !UpgradeHelper.isCreative(tile)) {
			ItemFolder.remove(getTrueStackInSlot(slot), amount);
    		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile.getWorld(), tile.getPos());
		}
		return new ItemStack(stackFolder.getItem(), amount, stackFolder.getItemDamage());
	}
	
	@Override
	protected void onContentsChanged(int slot) {
		
		super.onContentsChanged(slot);
		if (tile != null)
			tile.markDirty();
	}

	@Override
	public int getSizeInventory() {

		return stacks.length;
	}
	
	public ItemStack getTrueStackInSlot(int slot) {
		
		return stacks[slot];
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		
		validateSlotIndex(slot);
		
		ItemStack stackFolder = getStackFromFolder(slot);
		if (stackFolder != null)
		{
			long count = ItemFolder.getFileSize(getTrueStackInSlot(slot));
			if (count == 0)
				return null;
			
			long extract = Math.min(stackFolder.getMaxStackSize(), count);
			stackFolder.stackSize = (int)extract;
			
			return stackFolder;
		}
		return null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int size) {

		ItemStack displayedStack;
		
		if (tile.getFilter() != null)
		{
			displayedStack = tile.getFilter();
			
			if (UpgradeHelper.getUpgrade(tile, StringLibs.TAG_CRAFT) == null)
			{
				if (StorageUtils.simpleFolderMatch(tile, displayedStack) != -1)
				{
					int index = StorageUtils.simpleFolderMatch(tile, displayedStack);
					long count = ItemFolder.getFileSize(getTrueStackInSlot(index));
					long extract = Math.min(displayedStack.getMaxStackSize(), count);
					
					if (!UpgradeHelper.isCreative(tile))
					{
						ItemFolder.remove(getTrueStackInSlot(index), size);
					}
					return new ItemStack(displayedStack.getItem(), Math.min((int)extract, size), displayedStack.getItemDamage());
				}
			}
			else
			{
				if (AutocraftingUtils.canCraft(displayedStack, tile))
				{
					ItemStack craftingOutput = new ItemStack(displayedStack.getItem(), 1, displayedStack.getItemDamage());
					if (tile.sizeStack >= AutocraftingUtils.getOutputSize()) {
						tile.sizeStack = 0;
						AutocraftingUtils.doCraft(displayedStack, this);
					} else {
						tile.sizeStack += 1;
						return craftingOutput;
					}
				}
			}
		}
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {

		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		stacks[slot] = stack;
		this.markDirty();
	}

	@Override
	public int getInventoryStackLimit() {

		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {

		return !(tile.getWorld().getTileEntity(tile.getPos()) instanceof TileEntityRFC) ? false : player.getDistanceSq(tile.getPos()) <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return ItemStack.areItemsEqual(stack, this.getStackFromFolder(slot));
	}

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

	@Override
	public String getName() {

		return "realfilingcabinet.storage";
	}

	@Override
	public boolean hasCustomName() {

		return true;
	}

	@Override
	public ITextComponent getDisplayName() {

		return null;
	}
	
	public ItemStack getStackFromFolder(int slot) {
		
		ItemStack folder = getTrueStackInSlot(slot);
		if (folder != null && folder.getItem() instanceof IFolder)
		{
			ItemStack stack = (ItemStack)ItemFolder.getObject(folder);
			if (stack != null) {
				return stack;
			}
		}
		return null;
	}
	
	public ItemStack[] getStacks() {
		
		return stacks;
	}
	
	public TileEntityRFC getTile() {
		
		return tile;
	}
}
