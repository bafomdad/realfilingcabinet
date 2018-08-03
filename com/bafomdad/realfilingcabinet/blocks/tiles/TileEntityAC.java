package com.bafomdad.realfilingcabinet.blocks.tiles;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.aspects.IEssentiaTransport;

import com.bafomdad.realfilingcabinet.blocks.BlockAC;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemAspectFolder;
import com.bafomdad.realfilingcabinet.utils.AspectStorageUtils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList({
	@Optional.Interface(iface = "thaumcraft.api.aspects.IAspectSource", modid = "thaumcraft"),
	@Optional.Interface(iface = "thaumcraft.api.aspects.IEssentiaTransport", modid = "thaumcraft")
})
public class TileEntityAC extends TileFilingCabinet implements ITickable, IAspectSource, IEssentiaTransport {

	private ItemStackHandler inv = new ItemStackHandler(8);
	
	public static final float offsetSpeed = 0.1F;
	public boolean isOpen = false;
	
	@Override
	public void update() {

		if (isOpen) {
			
			offset -= offsetSpeed;
			if (offset <= -0.75F)
				offset = -0.75F;
		} else {
			offset += offsetSpeed;
			if (offset >= 0.05F)
				offset = 0.05F;
		}
	}
	
	public ItemStack getFilter() {
		
		AxisAlignedBB aabb = new AxisAlignedBB(pos.add(0, 1, 0), pos.add(1, 2, 1));
		List<EntityItemFrame> frames = this.getWorld().getEntitiesWithinAABB(EntityItemFrame.class, aabb);
		for (EntityItemFrame frame : frames) {
			EnumFacing orientation = frame.getAdjustedHorizontalFacing();
			IBlockState state = world.getBlockState(getPos());
			EnumFacing rfcOrientation = (EnumFacing)state.getValue(BlockAC.FACING);
			if (frame != null && !frame.getDisplayedItem().isEmpty() && (orientation == rfcOrientation)) {
				if (frame.getDisplayedItem().getItem() == RFCItems.filter) {
					int rotation = frame.getRotation();
					return inv.getStackInSlot(rotation);
				}
				if (frame.getDisplayedItem().getItem() instanceof IEssentiaContainerItem)
					return frame.getDisplayedItem();
			}
		}
		return ItemStack.EMPTY;
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
		
		tag.setTag("inventory", getInv().serializeNBT());
		tag.setBoolean("isOpen", this.isOpen);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound tag) {
		
		getInv().deserializeNBT(tag.getCompoundTag("inventory"));
		this.isOpen = tag.getBoolean("isOpen");
	}
	
	public void readInv(NBTTagCompound nbt) {
		
		NBTTagList invList = nbt.getTagList("inventory", 10);
		for (int i = 0; i < invList.tagCount(); i++) {
			NBTTagCompound itemTag = invList.getCompoundTagAt(i);
			int slot = itemTag.getByte("Slot");
			if (slot >= 0 && slot < getInv().getSlots()) {
				getInv().setStackInSlot(slot, new ItemStack(itemTag));
			}
		}
	}
	
	public void writeInv(NBTTagCompound nbt, boolean toItem) {
		
		boolean write = false;
		NBTTagList invList = new NBTTagList();
		for (int i = 0; i < getInv().getSlots(); i++) {
			if (!getInv().getStackInSlot(i).isEmpty()) {
				if (toItem)
					write = true;
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte("Slot", (byte)i);
				getInv().getStackInSlot(i).writeToNBT(itemTag);
				invList.appendTag(itemTag);
			}
		}
		if (!toItem || write)
			nbt.setTag("inventory", invList);
	}
	
	public ItemStackHandler getInv() {
		
		return inv;
	}

	@Override
	public boolean isBlocked() {

		return false;
	}
/**	
*	Thaumcraft stuff start
*/	
	// IAspectSource
	
	public ItemStack getAspectFolder(Aspect tag) {
		
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack folder = inv.getStackInSlot(i);
			if (!folder.isEmpty() && folder.getItem() instanceof ItemAspectFolder) {
				Aspect asp = ItemAspectFolder.getAspectFromFolder(folder);
				if (asp == tag)
					return folder;
			}
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	public int addToContainer(Aspect tag, int amount) {
		
		if (amount == 0) return amount;
		
		int internalCount = containerContains(tag);
		if (internalCount < ItemAspectFolder.getMaxAmount() || internalCount == 0) {
			int added = Math.max(amount, internalCount - ItemAspectFolder.getMaxAmount());
			//System.out.println("Added: " + added);
			ItemAspectFolder.addAspect(getAspectFolder(tag), added);
			amount -= added;
		}
		this.markDirty();
		return amount;
	}

	@Override
	public int containerContains(Aspect tag) {

		if (!getAspectFolder(tag).isEmpty())
			return ItemAspectFolder.getAspectCount(getAspectFolder(tag));
		
		return -1;
	}

	@Override
	public boolean doesContainerAccept(Aspect tag) {

		return !getAspectFolder(tag).isEmpty();
	}

	@Deprecated
	@Override
	public boolean doesContainerContain(AspectList aspects) {

		return false;
	}

	@Override
	public boolean doesContainerContainAmount(Aspect tag, int amount) {

		ItemStack folder = getAspectFolder(tag);
		if (folder.isEmpty()) return false;
		
		int internalCount = ItemAspectFolder.getAspectCount(folder);
		Aspect internalAsp = ItemAspectFolder.getAspectFromFolder(folder);
		if ((internalCount >= amount) && (tag == internalAsp))
			return true;
		
		return false;
	}

	@Override
	public AspectList getAspects() {

		AspectList al = new AspectList();
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack folder = inv.getStackInSlot(i);
			if (!folder.isEmpty() && folder.getItem() instanceof ItemAspectFolder) {
				Aspect asp = ItemAspectFolder.getAspectFromFolder(folder);
				int amount = ItemAspectFolder.getAspectCount(folder);
				al.add(asp, amount);
			}
		}
		return al;
	}

	@Override
	public void setAspects(AspectList aspects) {

		if ((aspects != null) && (aspects.size() > 0)) {
			Aspect asp = aspects.getAspectsSortedByAmount()[0];
			ItemStack folder = getAspectFolder(asp);
			if (!folder.isEmpty()) {
				ItemAspectFolder.setAspect(folder, asp);
				ItemAspectFolder.setAspectCount(folder, aspects.getAmount(asp));
			}
		}
	}

	@Deprecated
	@Override
	public boolean takeFromContainer(AspectList aspects) {

		return false;
	}

	@Override
	public boolean takeFromContainer(Aspect tag, int amount) {

		if (this.doesContainerContainAmount(tag, amount)) {
			ItemStack folder = getAspectFolder(tag);
			ItemAspectFolder.removeAspect(folder, amount);
			this.markDirty();
			return true;
		}
		return false;
	}
	
	// IEssentiaTransport

	@Override
	public boolean isConnectable(EnumFacing facing) {

		return facing != EnumFacing.DOWN;
	}

	@Override
	public boolean canInputFrom(EnumFacing facing) {

		return facing != EnumFacing.DOWN;
	}

	@Override
	public boolean canOutputTo(EnumFacing facing) {

		return facing == EnumFacing.DOWN;
	}

	@Override
	public void setSuction(Aspect asp, int amount) {}

	@Override
	public Aspect getSuctionType(EnumFacing facing) {

		return AspectStorageUtils.getFirstAspectStored(this, getFilter()).getAspects()[0];
	}

	@Override
	public int getSuctionAmount(EnumFacing facing) {

		return 64;
	}

	@Override
	public int takeEssentia(Aspect asp, int amount, EnumFacing facing) {

		return (canOutputTo(facing)) && (takeFromContainer(asp, amount)) ? amount : 0;
	}

	@Override
	public int addEssentia(Aspect asp, int amount, EnumFacing facing) {

		return canInputFrom(facing) ? amount - addToContainer(asp, amount) : 0;
	}

	@Override
	public Aspect getEssentiaType(EnumFacing facing) {

		return getSuctionType(facing);
	}

	@Override
	public int getEssentiaAmount(EnumFacing facing) {

		return AspectStorageUtils.getFirstAspectStored(this, getFilter()).getAmount(getSuctionType(facing));
	}

	@Override
	public int getMinimumSuction() {

		return 32;
	}
}
