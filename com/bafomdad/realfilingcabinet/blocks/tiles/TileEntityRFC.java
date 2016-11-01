package com.bafomdad.realfilingcabinet.blocks.tiles;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import com.bafomdad.realfilingcabinet.api.ILockableCabinet;
import com.bafomdad.realfilingcabinet.blocks.BlockRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.inventory.InventoryRFC;
import com.bafomdad.realfilingcabinet.utils.EnderUtils;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;

public class TileEntityRFC extends TileFilingCabinet implements ITickable, ILockableCabinet {

	private InventoryRFC inv = new InventoryRFC(this, 8);
	private UUID owner;
	
	// MISC variables
	private long lastClickTime;
	private UUID lastClickUUID;
	
	// NBT variables
	public int sizeStack = 0;
	private int rfcHash = -1;
	
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
			if (offset >= 0.05F)
				offset = 0.05F;
		}
	}
	
	@Override
    public NBTTagCompound getUpdateTag() {
		
		return writeToNBT(new NBTTagCompound());
    }
	
	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeCustomNBT(nbtTag);
		return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
	}

	@Override
	public void writeCustomNBT(NBTTagCompound tag) {
		
		tag.setTag("inventory", inv.serializeNBT());
		tag.setBoolean("isOpen", this.isOpen);
		
		if (owner != null)
			tag.setString("Own", owner.toString());
		if (rfcHash != -1)
			tag.setInteger(StringLibs.RFC_HASH, rfcHash);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound tag) {
		
		inv.deserializeNBT(tag.getCompoundTag("inventory"));
		this.isOpen = tag.getBoolean("isOpen");
		
		this.owner = null;
		if (tag.hasKey("Own"))
			owner = UUID.fromString(tag.getString("Own"));
		if (tag.hasKey(StringLibs.RFC_HASH))
			rfcHash = tag.getInteger(StringLibs.RFC_HASH);
	}
	
	public void readInv(NBTTagCompound nbt) {
		
		NBTTagList invList = nbt.getTagList("inventory", 10);
		for (int i = 0; i < invList.tagCount(); i++)
		{
			NBTTagCompound itemTag = invList.getCompoundTagAt(i);
			int slot = itemTag.getByte("Slot");
			if (slot >= 0 && slot < inv.getSlots()) {
				inv.getStacks()[slot] = ItemStack.loadItemStackFromNBT(itemTag);
			}
		}
	}
	
	public void writeInv(NBTTagCompound nbt, boolean toItem) {
		
		boolean write = false;
		NBTTagList invList = new NBTTagList();
		for (int i = 0; i < inv.getSlots(); i++) {
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
	
	public boolean hasItemFrame() {
		
		AxisAlignedBB aabb = new AxisAlignedBB(pos.add(0, 1, 0), pos.add(1, 2, 1));
		List<EntityItemFrame> frames = this.getWorld().getEntitiesWithinAABB(EntityItemFrame.class, aabb);
		for (EntityItemFrame frame : frames) {
			EnumFacing orientation = frame.getAdjustedHorizontalFacing();
			IBlockState state = worldObj.getBlockState(getPos());
			EnumFacing rfcOrientation = (EnumFacing)state.getValue(BlockRFC.FACING);
			
			return frame != null && orientation == rfcOrientation;
		}
		return false;
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
		
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inv);
		}
		return super.getCapability(cap, side);
	}

	@Override
	public UUID getOwner() {

		return owner;
	}

	@Override
	public boolean setOwner(UUID owner) {

		if ((this.owner != null && !this.owner.equals(owner)) || (owner != null && !owner.equals(this.owner)))
		{
			this.owner = owner;
			
			if (worldObj != null && !worldObj.isRemote) {
				
				markDirty();
				this.markBlockForUpdate();
			}
		}
		return true;
	}

	@Override
	public boolean isCabinetLocked() {

		return getOwner() != null;
	}
	
	public boolean hasKeyCopy(EntityPlayer player, UUID uuid) {
		
		for (int i = 0; i < player.inventory.mainInventory.length; i++) {
			ItemStack keyCopy = player.inventory.mainInventory[i];
			if (keyCopy == null)
				continue;
			if (keyCopy.getItem() == RFCItems.keys && keyCopy.getItemDamage() == 1) {
				if (keyCopy.hasTagCompound() && keyCopy.getTagCompound().hasKey(StringLibs.RFC_COPY)) {
					return uuid.equals(UUID.fromString(NBTUtils.getString(keyCopy, StringLibs.RFC_COPY, "")));
				}
			}
		}
		return false;
	}
	
	public void setHash(TileEntity tile) {
		
		this.rfcHash = EnderUtils.createHash(this);
	}
	
	public int getHash(TileEntityRFC tile) {
		
		return this.rfcHash;
	}
}
