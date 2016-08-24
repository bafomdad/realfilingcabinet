package com.bafomdad.realfilingcabinet.blocks;

import java.util.List;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.core.IFilingCabinet;
import com.bafomdad.realfilingcabinet.core.StorageUtils;
import com.bafomdad.realfilingcabinet.core.UpgradeHandler;
import com.bafomdad.realfilingcabinet.core.Utils;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.storage.OreDictUtils;

public class TileEntityRFC extends TileFilingCabinet implements ISidedInventory, IFilingCabinet {
	
	// Inventory variables
	private ItemStack[] inventory;
	private ItemStack outputStack = null;
	private int inStack = -1;
	private int sizeStack = 0;
	
	// Rendering variables
	public int facing = 2;
	ForgeDirection fd = null;
	public static final float offsetSpeed = 0.1F;
	
	// Custom NBT Tags
	public boolean isOpen = false, isCreative = false, isAutoCraft = false, isEnder = false, isOreDict = false;

	public TileEntityRFC() {
		
		inventory = new ItemStack[10];
	}
	
	public void updateEntity() {
		
		if (isOpen)
		{
			offset -= offsetSpeed;
			if (offset <= -0.75F)
				offset = -0.75F;
		}
		else
		{
			offset += offsetSpeed;
			if (offset >= 0.0F)
				offset = 0.0F;
		}
	}
	
	@Override
	public void markDirty() {
		
		if (inventory[8] != null) {
			if (this.isItemValidForSlot(8, inventory[8])) {
				ItemStack folder = getStackInSlot(inStack);
				ItemFolder.add(folder, inventory[8].stackSize);
				StorageUtils.instance().syncToFolder(this, inStack);
				inventory[8] = null;
			}
		}
		if (!this.isCreative) 
		{
			if (!this.isAutoCraft) {
				
				int size = 0;
				if (inventory[9] != null && outputStack != null)
				{
					size = outputStack.stackSize - inventory[9].stackSize;
					if (size > 0)
					{
						int index = getMatchingFolder(outputStack);
						ItemFolder.remove(inventory[index], size);
						StorageUtils.instance().syncToFolder(this, index);
					}
				}
				else if (inventory[9] == null && outputStack != null) {
					if (getFilter() != null && StorageUtils.instance().simpleMatch(getFilter(), outputStack))
					{
						size = outputStack.stackSize;
						if (size > 0)
						{
							int index = getMatchingFolder(outputStack);
							ItemFolder.remove(inventory[index], size);
							StorageUtils.instance().syncToFolder(this, index);
						}
					}
				}
				outputStack = inventory[9];
			}
			if (this.isAutoCraft) {
				
				int size = 0;
				if (inventory[9] != null && outputStack != null)
				{
					size = outputStack.stackSize - inventory[9].stackSize;
					if (size > 0)
					{
						sizeStack += size;
						if (sizeStack >= StorageUtils.instance().getOutputSize()) {
							sizeStack = 0;
							StorageUtils.instance().doCraft(getFilter(), this);
						}
					}
				}
				else if (inventory[9] == null && outputStack != null) {
					if (getFilter() != null && StorageUtils.instance().simpleMatch(getFilter(), outputStack))
					{
						size = outputStack.stackSize;
						if (size > 0)
						{
							sizeStack += size;
							if (sizeStack >= StorageUtils.instance().getOutputSize()) {
								sizeStack = 0;
								StorageUtils.instance().doCraft(getFilter(), this);
							}
						}
					}
				}
				outputStack = inventory[9];
			}
		}
		super.markDirty();
	}
	
	private ItemStack getFilter() {
		
		ItemStack stack = null;
		
		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord + 1, this.zCoord, this.xCoord + 1, this.yCoord + 2, this.zCoord + 1);
		List<EntityItemFrame> frames = this.getWorldObj().getEntitiesWithinAABB(EntityItemFrame.class, aabb);
		for (EntityItemFrame frame : frames) {
			int orientation = frame.hangingDirection;
			if (frame != null && frame.getDisplayedItem() != null && (orientation == this.facing)) {
				stack = frame.getDisplayedItem();
				break;
			}
		}
		return stack;
	}
	
	private int getMatchingFolder(ItemStack stack) {
		
		if (stack != null) {
			for (int i = 0; i < this.getSizeInventory() - 2; i++) {
				ItemStack folder = getStackInSlot(i);
				if (folder != null && folder.getItem() == RealFilingCabinet.itemFolder)
				{
					if (ItemFolder.getStack(folder) != null) {
						if (isOreDict)
						{
							OreDictUtils.recreateOreDictionary(stack);
							if (OreDictUtils.hasOreDict())
							{
								if (OreDictUtils.areItemsEqual(stack, ItemFolder.getStack(folder)))
								{
									return i;
								}
							}
						}
						if (stack.getItem() == ItemFolder.getStack(folder).getItem() && stack.getItemDamage() == ItemFolder.getFileMeta(folder))
						{
							return i;
						}
					}
				}
			}
		}
		return -1;
	}
	
	public ItemStack setOutput() {
		
		ItemStack stacky = null;
		if (getFilter() == null) {
			return null;
		}
		
		stacky = getFilter();
		
		if (getMatchingFolder(stacky) != -1)
		{
			int slot = getMatchingFolder(stacky);
			int size;
			
			if (ItemFolder.getFileSize(inventory[slot]) > 64)
				size = 64;
			else
				size = ItemFolder.getFileSize(inventory[slot]);
			stacky = new ItemStack(stacky.getItem(), size, stacky.getItemDamage());
			return outputStack = stacky;
		}
		return null;
	}
	
	private ItemStack setCraftingOutput() {
		
		ItemStack stacky = null;
		if (getFilter() == null) {
			return null;
		}
		if (!StorageUtils.instance().canCraft(getFilter(), this) && inventory[9] != null) {
			inventory[9] = null;
			outputStack = null;
			return null;
		}
		if (StorageUtils.instance().canCraft(getFilter(), this)) {
			stacky = new ItemStack(getFilter().getItem(), 1, getFilter().getItemDamage());
		}
		return stacky;
	}

	// IInventory
	@Override
	public int getSizeInventory() {

		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {

		if (slot == 9)
		{
			if (inventory[9] != null && getFilter() == null)
				inventory[9] = null;
			
			if (this.isAutoCraft)
			{
				this.markDirty();
				
				if (inventory[9] == null && getFilter() != null)
					inventory[9] = setCraftingOutput();
				
				else if (inventory[9] != null && getFilter() != null && !StorageUtils.instance().simpleMatch(inventory[9], getFilter()))
					inventory[9] = setCraftingOutput();
			}
			else if (!this.isAutoCraft)
			{
				this.markDirty();
			
				if (inventory[9] == null && getFilter() != null && StorageUtils.instance().canFolderProvide(this, getFilter(), 1))
					inventory[9] = setOutput();
				
				else if (inventory[9] != null && getFilter() != null && !StorageUtils.instance().simpleMatch(inventory[9], getFilter()))
					inventory[9] = setOutput();
			}
			
			return inventory[9];
		}
		if (slot == 8)
		{
			this.markDirty();
		}
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int size) {

		ItemStack displayedStack;
		
		if (getFilter() != null)
		{
			displayedStack = getFilter();
			if (!this.isAutoCraft)
			{
				ItemStack itemFolder = StorageUtils.instance().findMatchingStack(this, displayedStack, size);
				if (itemFolder != null)
				{
					return itemFolder;
				}
			}
			else
			{
				if (StorageUtils.instance().canCraft(displayedStack, this))
				{
					ItemStack craftingOutput = new ItemStack(displayedStack.getItem(), 1, displayedStack.getItemDamage());
					
					if (sizeStack >= StorageUtils.instance().getOutputSize()) {
						sizeStack = 0;
						StorageUtils.instance().doCraft(displayedStack, this);
					}
					else
					{
						sizeStack += 1;
						return craftingOutput;
					}
				}
			}
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {

		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		
		inventory[slot] = stack;
		this.markDirty();
	}

	@Override
	public String getInventoryName() {

		return "rfc.storage";
	}

	@Override
	public boolean hasCustomInventoryName() {

		return false;
	}

	@Override
	public int getInventoryStackLimit() {

		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {

		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : player.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		if (slot == 8) 
		{
			for (int i = 0; i < this.getSizeInventory() - 2; i++) {
				ItemStack folder = inventory[i];
				if (folder != null && folder.getItem() == RealFilingCabinet.itemFolder && folder.getItemDamage() == 0) {
					if (ItemFolder.getStack(folder) != null) {
						if (isOreDict)
						{
							OreDictUtils.recreateOreDictionary(stack);
							if (OreDictUtils.hasOreDict())
							{
								if (OreDictUtils.areItemsEqual(stack, ItemFolder.getStack(folder)))
								{
									inStack = i;
									return true;
								}
							}
						}
						if (stack.getItem() == ItemFolder.getStack(folder).getItem() && stack.getItemDamage() == ItemFolder.getFileMeta(folder))
						{
							inStack = i;
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	// ISidedInventory
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return new int[] { 8, 9 };
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {

		return slot == 8 && this.isItemValidForSlot(slot, stack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {

		return slot == 9;
	}
	
	// NBT
	@Override
	public void writeCustomNBT(NBTTagCompound nbt) {
		
		NBTTagList nbttaglist = new NBTTagList();
		
		for (int i = 0; i < this.inventory.length; ++i) {
			if (this.inventory[i] != null)
			{
				NBTTagCompound nbt2 = new NBTTagCompound();
				nbt2.setByte("Slot", (byte)i);
				this.inventory[i].writeToNBT(nbt2);
				nbttaglist.appendTag(nbt2);
			}
		}
		nbt.setTag("Items", nbttaglist);
		nbt.setInteger("facing", (byte)this.facing);
		nbt.setBoolean("isOpen", this.isOpen);
		nbt.setBoolean("isCreative", this.isCreative);
		nbt.setBoolean("isAutoCraft", this.isAutoCraft);
		nbt.setBoolean("isEnder", this.isEnder);
		nbt.setBoolean("isOreDict", this.isOreDict);
		if (sizeStack > 0)
			nbt.setInteger("RFC_size", this.sizeStack);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt) {
		
		NBTTagList nbttaglist = nbt.getTagList("Items", 10);
		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbt2 = nbttaglist.getCompoundTagAt(i);
			int j = nbt2.getByte("Slot") & 255;
			
			if (j >= 0 && j < this.inventory.length)
				this.inventory[j] = ItemStack.loadItemStackFromNBT(nbt2);
		}
		this.facing = nbt.getInteger("facing");
		this.fd = ForgeDirection.getOrientation(this.facing);
		this.isOpen = nbt.getBoolean("isOpen");
		this.isCreative = nbt.getBoolean("isCreative");
		this.isAutoCraft = nbt.getBoolean("isAutoCraft");
		this.isEnder = nbt.getBoolean("isEnder");
		this.isOreDict = nbt.getBoolean("isOreDict");
		this.sizeStack = nbt.getInteger("RFC_size");
	}
	
    public void readInv(NBTTagCompound nbt) {
    	
    	NBTTagList invList = nbt.getTagList("inventory", 10);
    	for (int i = 0; i < invList.tagCount(); i++)
    	{
    		NBTTagCompound itemTag = invList.getCompoundTagAt(i);
    		int slot = itemTag.getByte("Slot");
    		if (slot >= 0 && slot < this.inventory.length)
    			this.inventory[slot] = ItemStack.loadItemStackFromNBT(itemTag);
    	}
    }
    
    public void writeInv(NBTTagCompound nbt, boolean toItem) {
    	
    	boolean write = false;
    	NBTTagList invList = new NBTTagList();
    	for (int i = 0; i < this.inventory.length; i++) {
    		if (this.inventory[i] != null)
    		{
    			if (toItem)
    				write = true;
    			NBTTagCompound itemTag = new NBTTagCompound();
    			itemTag.setByte("Slot", (byte)i);
    			this.inventory[i].writeToNBT(itemTag);
    			invList.appendTag(itemTag);
    		}
    	}
    	if (!toItem || write)
    		nbt.setTag("inventory", invList);
    }

	// IFilingCabinet
    @Override
	public void leftClick(EntityPlayer player) {
	
		if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == RealFilingCabinet.itemMagnifyingGlass) {
			UpgradeHandler.boinkOutUpgrade(this, player);
			return;
		}
    	if (getFilter() == null)
    		return;
    	
    	if (player.capabilities.isCreativeMode)
    		return;
    	
    	if (!player.isSneaking()) {
    		if (!this.isAutoCraft)
    		{
        		if (StorageUtils.instance().canFolderProvide(this, getFilter(), 1))
        		{
        			ItemStack stack = getFilter();
        			if (!this.isCreative)
        				StorageUtils.instance().decreaseFolderSize(this, stack, 1);
        			if (!player.inventory.addItemStackToInventory(stack))
        				player.dropItem(stack.getItem(), 1);
        		}
    		}
    		else
    		{
    			if (StorageUtils.instance().canCraft(getFilter(), this))
    			{
    				ItemStack stack = getFilter();
    				stack.stackSize = StorageUtils.instance().getOutputSize();
    				if (!this.isCreative)
    					StorageUtils.instance().doCraft(getFilter(), this);
    				if (!player.inventory.addItemStackToInventory(stack))
    					player.dropItem(stack.getItem(), 1);
    			}
    		}
    	}
    	else {
    		if (!this.isAutoCraft)
    		{
        		if (StorageUtils.instance().canFolderProvide(this, getFilter(), 64))
        		{
        			ItemStack stack = getFilter();
        			stack.stackSize = 64;
        			if (!this.isCreative)
        				StorageUtils.instance().decreaseFolderSize(this, stack, stack.stackSize);
        			if (!player.inventory.addItemStackToInventory(stack))
        				player.dropItem(stack.getItem(), stack.stackSize);
        		}
        		else if (!StorageUtils.instance().canFolderProvide(this, getFilter(), 64) && StorageUtils.instance().getFileSize() > 0)
        		{
        			ItemStack stack = getFilter();
        			stack.stackSize = StorageUtils.instance().getFileSize();
        			if (!this.isCreative)
        				StorageUtils.instance().decreaseFolderSize(this, stack, stack.stackSize);
        			if (!player.inventory.addItemStackToInventory(stack))
        				player.dropItem(stack.getItem(), stack.stackSize);
        		}
    		}
    		else
    		{
    			if (StorageUtils.instance().canCraft(getFilter(), this))
    			{
    				ItemStack stack = getFilter();
    				stack.stackSize = StorageUtils.instance().getOutputSize();
    				if (!this.isCreative)
    					StorageUtils.instance().doCraft(getFilter(), this);
    				if (!player.inventory.addItemStackToInventory(stack))
    					player.dropItem(stack.getItem(), 1);
    			}
    		}
    	}
	}

	@Override
	public void rightClick(EntityPlayer player) {

		if (this.worldObj.isRemote)
			return;

		ItemStack stack = player.getHeldItem();
		
		if (!player.isSneaking() && stack != null)
		{
			if (stack.getItem() == RealFilingCabinet.itemFolder)
			{
				if (!this.isOpen)
					return;
				
				if (!this.isEnder && stack.getItemDamage() != 1)
				{
					for (int i = 0; i < this.getSizeInventory() - 2; i++)
					{
						ItemStack tileStack = this.getStackInSlot(i);
						
						if (tileStack == null)
						{
							this.setInventorySlotContents(i, stack);
							player.setCurrentItemOrArmor(0, null);
							Utils.dispatchTEToNearbyPlayers(worldObj, this.xCoord, this.yCoord, this.zCoord);
							break;
						}
					}
				}
				else
				{
					if (!stack.stackTagCompound.hasKey(ItemFolder.TAG_SLOTINDEX))
						return;
					
					int xLoc = Utils.getInt(stack, "RFC_xLoc", -1);
					int yLoc = Utils.getInt(stack, "RFC_yLoc", -1);
					int zLoc = Utils.getInt(stack, "RFC_zLoc", -1);
					int dim = Utils.getInt(stack, "RFC_dim", 0);
					
					if (this.xCoord == xLoc && this.yCoord == yLoc && this.zCoord == zLoc && this.worldObj.provider.dimensionId == dim)
					{
						player.setCurrentItemOrArmor(0, null);
					}
				}
			}
			else if (stack.getItem() != RealFilingCabinet.itemFolder)
			{
				if (stack.getItem() == RealFilingCabinet.itemUpgrades) {
					UpgradeHandler.handleUpgrade(this, stack, player);
				}
				StorageUtils.instance().addStackManually(this, stack, player);
			}
		}
		else
		{
			if (!player.isSwingInProgress)
			{
				if (!this.isOpen)
					this.isOpen = true;
				else
					this.isOpen = false;
				this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);	
			}
			else
				StorageUtils.instance().addAllStacksManually(this, player);
		}
	}
}
