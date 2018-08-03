package com.bafomdad.realfilingcabinet.inventory;

import javax.annotation.Nonnull;

import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class InventorySuitcase implements IItemHandlerModifiable {

	private final IItemHandlerModifiable suitcaseInv;
	final ItemStack suitcase;
	
	public InventorySuitcase(ItemStack suitcase) {
		
		this.suitcase = suitcase;
		suitcaseInv = (IItemHandlerModifiable)suitcase.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
	}
	
	public ItemStack getCase() {
		
		return suitcase;
	}
	
	@Override
	public void setStackInSlot(int slot, ItemStack stack) {

		suitcaseInv.setStackInSlot(slot, stack);
	}
	
	@Override
	public int getSlots() {

		return suitcaseInv.getSlots();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {

		return suitcaseInv.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {

		return suitcaseInv.insertItem(slot, stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {

		return suitcaseInv.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {

		return suitcaseInv.getSlotLimit(slot);
	}

	public static class InvProvider implements ICapabilitySerializable<NBTBase> {

		private final IItemHandler inv = new ItemStackHandler(8) {
			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack toInsert, boolean simulate) {
				if (!toInsert.isEmpty() && toInsert.getItem() instanceof IFolder && toInsert.getItemDamage() != ItemFolder.FolderType.ENDER.ordinal()) {
					return super.insertItem(slot, toInsert, simulate);
				}
				return toInsert;
			}
		};
		
		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {

			return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

			if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inv);
			
			return null;
		}

		@Override
		public NBTBase serializeNBT() {

			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inv, null);
		}

		@Override
		public void deserializeNBT(NBTBase nbt) {

			CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inv, null, nbt);
		}
	}
}
