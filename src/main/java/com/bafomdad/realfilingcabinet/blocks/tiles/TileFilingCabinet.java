package com.bafomdad.realfilingcabinet.blocks.tiles;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.bafomdad.realfilingcabinet.api.IUpgrade;
import com.bafomdad.realfilingcabinet.api.upgrades.Upgrades;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.inventory.FluidRFC;
import com.bafomdad.realfilingcabinet.utils.EnderUtils;
import com.bafomdad.realfilingcabinet.utils.SmeltingUtils;
import com.google.common.collect.Lists;

public class TileFilingCabinet extends TileEntityRFC {
	
	private FluidRFC fluidInv = new FluidRFC(this);
	
	public String upgrade = "";
	public List<int[]> smeltingJobs = Lists.newArrayList();
	public int fuelTime = 0;
	public ItemStack uncraftableItem = ItemStack.EMPTY;
	public boolean isCreative = false;
	private int rfcHash = -1;
	
	@Override
	public void update() {
		
		super.update();

		Upgrades upgrade = UpgradeHelper.getUpgrade(this);
		if (!upgrade.isEmpty())
			((IUpgrade)upgrade.getUpgrade().getItem()).tickUpgrade(upgrade.getUpgrade(), this);
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound tag) {
	
		super.writeCustomNBT(tag);
		
		tag.setTag(StringLibs.RFC_CRAFTABLE, uncraftableItem.serializeNBT());
		tag.setString(StringLibs.RFC_UPGRADE, upgrade);
		tag.setBoolean(StringLibs.TAG_CREATIVE, this.isCreative);
		if (rfcHash != -1)
			tag.setInteger(StringLibs.RFC_HASH, rfcHash);
		
		SmeltingUtils.writeSmeltNBT(this, tag);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound tag) {
		
		super.readCustomNBT(tag);
		
		if (tag.hasKey(StringLibs.RFC_CRAFTABLE))
			this.uncraftableItem = new ItemStack((NBTTagCompound)tag.getTag(StringLibs.RFC_CRAFTABLE));
		this.upgrade = tag.getString(StringLibs.RFC_UPGRADE);
		this.isCreative = tag.getBoolean(StringLibs.TAG_CREATIVE);
		if (tag.hasKey(StringLibs.RFC_HASH))
			rfcHash = tag.getInteger(StringLibs.RFC_HASH);
		
		SmeltingUtils.readSmeltNBT(this, tag);
	}
	
	public IFluidHandler getFluidInventory() {
		
		return fluidInv;
	}
	
	public void setHash() {
		
		this.rfcHash = EnderUtils.createHash(this);
	}
	
	public int getHash() {
		
		return this.rfcHash;
	}
	
	@Override
	public boolean hasCapability(@Nonnull Capability<?> cap, EnumFacing side) {
		
		return (!UpgradeHelper.getUpgrade(this, StringLibs.TAG_FLUID).isEmpty()) ? cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY : super.hasCapability(cap, side);
	}
	
	@Override
	public <T> T getCapability(@Nonnull Capability<T> cap, EnumFacing side) {
		
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidInv);
			
		return super.getCapability(cap, side);
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> list) {
		
		super.getDrops(list);
		if (UpgradeHelper.hasUpgrade(this)) {
			list.add(UpgradeHelper.getUpgrade(this).getUpgrade());
		}
	}
}
