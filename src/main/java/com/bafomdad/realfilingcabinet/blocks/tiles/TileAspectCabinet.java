package com.bafomdad.realfilingcabinet.blocks.tiles;

import com.bafomdad.realfilingcabinet.items.ItemAspectFolder;
import com.bafomdad.realfilingcabinet.utils.AspectStorageUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TileAspectCabinet extends TileEntityRFC implements IAspectSource, IEssentiaTransport {

	@Override
	public ItemStack getFilter() {

		return (super.getFilter().getItem() instanceof IEssentiaContainerItem) ? super.getFilter() : ItemStack.EMPTY;
	}
	
	public ItemStack getAspectFolder(Aspect tag) {
		
		for (int i = 0; i < getInventory().getSlots(); i++) {
			ItemStack folder = getInventory().getFolder(i);
			if (!folder.isEmpty() && folder.getItem() instanceof ItemAspectFolder) {
				Aspect asp = ItemAspectFolder.getAspectFromFolder(folder);
				if (asp == tag)
					return folder;
			}
		}
		return ItemStack.EMPTY;
	}

	/**
	 * Thaucraft stuff start
	 */
	@Override
	public int addToContainer(Aspect tag, int amount) {
		
		if (amount == 0) return amount;
		
		int internalCount = containerContains(tag);
		if (internalCount < ItemAspectFolder.getMaxAmount() || internalCount == 0) {
			int added = Math.max(amount, internalCount - ItemAspectFolder.getMaxAmount());
			//System.out.println("Added: " + added);
			ItemAspectFolder.incrementAspect(getAspectFolder(tag), added);
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
		for (int i = 0; i < getInventory().getSlots(); i++) {
			ItemStack folder = getInventory().getFolder(i);
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
			ItemAspectFolder.decrementAspect(folder, amount);
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

	@Override
	public boolean isBlocked() {
		
		return false;
	}
}
