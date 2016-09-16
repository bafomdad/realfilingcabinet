package com.bafomdad.realfilingcabinet.blocks.tiles;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import com.bafomdad.realfilingcabinet.blocks.BlockRFC;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.inventory.InventoryRFC;

public class TileEntityRFC extends TileFilingCabinet implements ITickable {

	public InventoryRFC inv = new InventoryRFC(this, 10);
	
	// MISC variables
	private long lastClickTime;
	private UUID lastClickUUID;
	
	// NBT variables
	public int sizeStack = 0;
	
	// Rendering variables
	public float offset, renderOffset;
	public static final float offsetSpeed = 0.1F;
	public boolean isOpen = false;

	@Override
	public void update() {
		
		if (isOpen)
		{
			offset -= offsetSpeed;
			if (offset <= -0.75F)
				offset = -0.75F;
		} else {
			offset += offsetSpeed;
			if (offset >= 0.0F)
				offset = 0.0F;
		}
	}
	
	@Override
    public NBTTagCompound getUpdateTag() {
		
		return writeToNBT(new NBTTagCompound());
    }
	
	@Override
	public void writeCustomNBT(NBTTagCompound tag) {
		
		tag.setTag("inventory", inv.serializeNBT());
		tag.setBoolean("isOpen", this.isOpen);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound tag) {
		
		inv.deserializeNBT(tag.getCompoundTag("inventory"));
		this.isOpen = tag.getBoolean("isOpen");
	}
	
	public void readInv(NBTTagCompound nbt) {
		
		NBTTagList invList = nbt.getTagList("inventory", 10);
		for (int i = 0; i < invList.tagCount(); i++)
		{
			NBTTagCompound itemTag = invList.getCompoundTagAt(i);
			int slot = itemTag.getByte("Slot");
			if (slot >= 0 && slot < inv.getSizeInventory()) {
				ItemStack stack = inv.getTrueStackInSlot(slot);
				stack = ItemStack.loadItemStackFromNBT(itemTag);
			}
		}
	}
	
	public void writeInv(NBTTagCompound nbt, boolean toItem) {
		
		boolean write = false;
		NBTTagList invList = new NBTTagList();
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			if (inv.getTrueStackInSlot(i) != null)
			{
				if (toItem)
					write = true;
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte("Slot", (byte)i);
				inv.getTrueStackInSlot(i).writeToNBT(itemTag);
				invList.appendTag(itemTag);
			}
		}
		if (!toItem || write)
			nbt.setTag("inventory", invList);
	}
	
	public InventoryRFC getInventory() {
		
		return inv;
	}
	
	public boolean calcLastClick(EntityPlayer player) {
		
		boolean bool = false;
		
		if (worldObj.getTotalWorldTime() - lastClickTime < 10 && player.getPersistentID().equals(lastClickUUID)) {
			bool = true;
		}
		lastClickTime = worldObj.getTotalWorldTime();
		lastClickUUID = player.getPersistentID();
		
		return bool;
	}
	
	public ItemStack getFilter() {
		
		AxisAlignedBB aabb = new AxisAlignedBB(pos.add(0, 1, 0), pos.add(1, 2, 1));
		List<EntityItemFrame> frames = this.getWorld().getEntitiesWithinAABB(EntityItemFrame.class, aabb);
		for (EntityItemFrame frame : frames) {
			EnumFacing orientation = frame.getAdjustedHorizontalFacing();
			IBlockState state = worldObj.getBlockState(getPos());
			EnumFacing rfcOrientation = (EnumFacing)state.getValue(BlockRFC.FACING);
			if (frame != null && frame.getDisplayedItem() != null && (orientation == rfcOrientation)) {
				if (frame.getDisplayedItem().getItem() == RFCItems.filter)
				{
					int rotation = frame.getRotation();
					return inv.getStackFromFolder(rotation);
				}
				return frame.getDisplayedItem();
			}
		}
		return null;
	}
	
	@Override
	public boolean hasCapability(@Nonnull Capability<?> cap, @Nonnull EnumFacing side) {
		
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}
	
	@Override
	public <T> T getCapability(@Nonnull Capability<T> cap, @Nonnull EnumFacing side) {
		
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T) inv;
		
		return super.getCapability(cap, side);
	}
}
