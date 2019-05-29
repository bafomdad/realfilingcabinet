package com.bafomdad.realfilingcabinet.items.capabilities;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilityProviderFolder implements ICapabilitySerializable<NBTTagCompound> {

	@CapabilityInject(CapabilityFolder.class)
	public static Capability<CapabilityFolder> FOLDER_CAP = null;
	public static final ResourceLocation FOLDER_ID = new ResourceLocation(RealFilingCabinet.MOD_ID, "folder");
	
	private final CapabilityFolder folder;
	
	public CapabilityProviderFolder(ItemStack stack) {
		
		folder = new CapabilityFolder(stack);
	}
	
	public CapabilityProviderFolder(ItemStack stack, NBTTagCompound tag) {
		
		folder = new CapabilityFolder(stack);
		folder.deserializeNBT(tag.getCompoundTag(FOLDER_ID.toString()));
	}
	
	public static void register() {
		
		CapabilityManager.INSTANCE.register(CapabilityFolder.class, new Capability.IStorage<CapabilityFolder>() {
			@Override
			public NBTBase writeNBT(Capability<CapabilityFolder> cap, CapabilityFolder instance, EnumFacing facing) {
				
				return instance.serializeNBT();
			}
			
			@Override
			public void readNBT(Capability<CapabilityFolder> cap, CapabilityFolder instance, EnumFacing facing, NBTBase nbt) {
				
				if (nbt instanceof NBTTagCompound)
					instance.deserializeNBT((NBTTagCompound)nbt);
			}
		}, () -> new CapabilityFolder(ItemStack.EMPTY));
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {

		return capability == FOLDER_CAP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

		return capability == FOLDER_CAP ? FOLDER_CAP.cast(folder) : null;
	}

	@Override
	public NBTTagCompound serializeNBT() {

		return folder.serializeNBT();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		
		folder.deserializeNBT(nbt);
	}
}
