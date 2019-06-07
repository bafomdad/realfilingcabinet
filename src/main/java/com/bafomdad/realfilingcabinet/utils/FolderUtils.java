package com.bafomdad.realfilingcabinet.utils;

import java.util.List;
import java.util.Optional;

import com.bafomdad.realfilingcabinet.api.IBlockCabinet;
import com.bafomdad.realfilingcabinet.api.IEmptyFolder;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.helpers.enums.FolderType;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityFolder;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityProviderFolder;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public final class FolderUtils {
	
	final Optional<CapabilityFolder> cap;
	
	private FolderUtils(ItemStack stack) {
		
		CapabilityFolder folderCap = stack.getCapability(CapabilityProviderFolder.FOLDER_CAP, null);
		cap = (folderCap != null) ? Optional.of(folderCap) : Optional.empty();
	}
	
	public static FolderUtils get(ItemStack stack) {
		
		return new FolderUtils(stack);
	}
	
	public CapabilityFolder getCap() {
		
		return (cap.isPresent()) ? cap.get() : null;
	}
	
	public void addTooltips(List<String> list) {
		
		cap.ifPresent(c -> c.addTooltips(list));
	}
	
	public long getFileSize() {
		
		return (cap.isPresent()) ? cap.get().getCount() : 0;
	}

	public void setFileSize(long count) {
		
		cap.ifPresent(s -> s.setCount(count));
	}
	
	public int getExtractSize() {
		
		return (cap.isPresent()) ? cap.get().getExtractSize() : 0;
	}
	
	public void setExtractSize(int size) {
		
		cap.ifPresent(e -> e.setExtractSize(size));
	}
	
	public void setRemainingDurability(int size) {
		
		cap.ifPresent(r -> r.setRemainingDurability(size));
	}
	
	public void setTagCompound(NBTTagCompound tag) {
		
		cap.ifPresent(t -> t.setTagCompound(tag));
	}
	
	public void add(long count) {
		
		long current = getFileSize();
		setFileSize(current + count);
	}
	
	public void remove(long count) {
		
		long current = getFileSize();
		setFileSize(Math.max(current - count, 0));
	}
	
	public int getDamageSize() {
		
		return (cap.isPresent()) ? cap.get().getRemainingDurability() : 0;
	}
	
	public void setDamageSize(int damage) {
		
		cap.ifPresent(d -> d.setRemainingDurability(damage));
	}
	
	public String getDisplayName() {
		
		return (cap.isPresent()) ? cap.get().getDisplayName() : "";
	}
	
	public Object getObject() {
		
		return (cap.isPresent()) ? cap.get().getContents() : null;
	}
	
	public boolean setObject(Object obj) {
		
		return (cap.isPresent()) ? cap.get().setContents(obj) : false;
	}

	public Object insert(Object objects, boolean simulate) {
		
		return this.insert(objects, simulate, false);
	}
	
	public Object insert(Object objects, boolean simulate, boolean oreDict) {
		
		return (cap.isPresent()) ? cap.get().insert(objects, simulate, oreDict) : objects;
	}
	
	public Object extract(long amount, boolean simulate, boolean creative) {
		
		return (cap.isPresent()) ? cap.get().extract(amount, simulate, creative) : null;
	}
	
	public static boolean areContentsEqual(ItemStack folder1, ItemStack folder2) {
		
		CapabilityFolder cap1 = FolderUtils.get(folder1).getCap();
		CapabilityFolder cap2  = FolderUtils.get(folder2).getCap();
		if (cap1 == null || cap2 == null) return false;
		
		if (cap1.isItemStack() && cap2.isItemStack()) {
			if (folder1.getItemDamage() == FolderType.NBT.ordinal())
				return ItemStack.areItemStackTagsEqual(cap1.getItemStack(), cap2.getItemStack());
			return ItemStack.areItemsEqual(cap1.getItemStack(), cap2.getItemStack());
		}
		if (cap1.isFluidStack() && cap2.isFluidStack())
			return FluidStack.areFluidStackTagsEqual(cap1.getFluidStack(), cap2.getFluidStack());
		if (cap1.isEntity() && cap2.isEntity())
			return cap1.getEntityClass().equals(cap2.getEntityClass());
			
		return false;
	}
	
	public static boolean allowableIngredient(ItemStack stack) {
		
		return !(stack.getItem() instanceof IEmptyFolder 
				|| stack.getItem() instanceof IFolder 
				|| Block.getBlockFromItem(stack.getItem()) instanceof IBlockCabinet
				|| stack.getItem() == RFCItems.WHITEOUTTAPE)
				|| stack.getItem() == RFCItems.MAGNIFYINGGLASS;
	}
}
