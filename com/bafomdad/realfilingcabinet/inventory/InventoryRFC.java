package com.bafomdad.realfilingcabinet.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.ItemStackHandler;

import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.api.IInventoryRFC;
import com.bafomdad.realfilingcabinet.api.UpgradeHelper;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.utils.AutocraftingUtils;
import com.bafomdad.realfilingcabinet.utils.StorageUtils;

public class InventoryRFC extends ItemStackHandler implements IInventoryRFC {
	
	final TileEntityRFC tile;
	
	private ItemStack inStack;
	private ItemStack outStack;
	private int outIndex;

	public InventoryRFC(TileEntityRFC tile, int size) {
		
		this.tile = tile;
		setSize(size);
	}
	
	@Override
	public void markDirty() {

		if (stacks[8] != null) {
			if (this.isItemValidForSlot(8, stacks[8])) {
				int index = StorageUtils.simpleFolderMatch(tile, stacks[8]);
				ItemStack folder = getTrueStackInSlot(index);
				ItemFolder.add(folder, stacks[8].stackSize);
				stacks[8] = null;
			}
		}
		if (!UpgradeHelper.isCreative(tile)) {
			if (UpgradeHelper.getUpgrade(tile, StringLibs.TAG_CRAFT) == null)
			{
				int size = 0;
				if (stacks[9] != null && outStack != null)
				{
					size = outStack.stackSize - stacks[9].stackSize;
					if (size > 0)
					{
						ItemFolder.remove(getTrueStackInSlot(outIndex), size);
					}
				}
				else if (stacks[9] == null && outStack != null) {
					
					size = outStack.stackSize;
					if (size > 0)
					{
						ItemFolder.remove(getTrueStackInSlot(outIndex), size);
					}
				}
//				setOutput();
			}
			else
			{
				int size = 0;
				if (stacks[9] != null && outStack != null)
				{
					size = outStack.stackSize - stacks[9].stackSize;
					if (size > 0)
					{
						tile.sizeStack += size;
						if (tile.sizeStack >= AutocraftingUtils.getOutputSize())
						{
							tile.sizeStack = 0;
							AutocraftingUtils.doCraft(tile.getFilter(), this);
						}
					}
				}
				else if (stacks[9] == null && outStack != null) {
					
					size = outStack.stackSize;
					if (size > 0)
					{
						tile.sizeStack += size;
						if (tile.sizeStack >= AutocraftingUtils.getOutputSize())
						{
							tile.sizeStack = 0;
							AutocraftingUtils.doCraft(tile.getFilter(), this);
						}
					}
				}
//				setCraftingOutput();
			}
		}
	}
	
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		
		if (getTrueStackInSlot(slot) != null)
		{
			if (ItemStack.areItemsEqual(stack, getStackFromFolder(slot))) {
				ItemFolder.add(getTrueStackInSlot(slot), stack.stackSize);
				return null;
			}
		}
		return stack;
	}
	
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		
		ItemStack stackFolder = getStackFromFolder(slot);
		long count = ItemFolder.getFileSize(getTrueStackInSlot(slot));
		long extract = Math.min(stackFolder.getMaxStackSize(), count);
		amount = Math.min((int)extract, amount);
		
		ItemFolder.remove(getTrueStackInSlot(slot), amount);
		return new ItemStack(stackFolder.getItem(), amount, stackFolder.getItemDamage());
	}
	
	@Override
	public void onContentsChanged(int slot) {
		
		System.out.println("changed");
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		return new int[] { 8, 9 };
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing direction) {

		return slot == 8 && this.isItemValidForSlot(slot, stack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing direction) {

		return slot == 9 && tile.getFilter() != null;
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
		
		if (slot == 9 || slot == 8)
		{
//			markDirty();
		}
		ItemStack stackFolder = getStackFromFolder(slot);
		if (stackFolder != null)
		{
			long count = ItemFolder.getFileSize(getTrueStackInSlot(slot));
			if (count == 0)
				return null;
			
			long extract = Math.min(stackFolder.getMaxStackSize(), count);
			stackFolder.stackSize = (int)extract;
		}
		return stackFolder;
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

		return (slot == 8 && StorageUtils.simpleFolderMatch(tile, stack) != -1);
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

	@Override
	public int getFolderInventory() {

		return getSizeInventory() - 2;
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
}
