package com.bafomdad.realfilingcabinet.blocks.tiles;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import com.bafomdad.realfilingcabinet.blocks.BlockRFC;
import com.bafomdad.realfilingcabinet.entity.EntityCabinet;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.UpgradeType;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.inventory.FluidRFC;
import com.bafomdad.realfilingcabinet.inventory.InventoryRFC;
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
		if (SmeltingUtils.canSmelt(this)) {
			SmeltingUtils.incrementSmeltTime(this);
			if (getWorld().getTotalWorldTime() % 40 == 0) {
				SmeltingUtils.createSmeltingJob(this);
				this.markBlockForUpdate();
			}
			return;
		}
		if (!UpgradeHelper.getUpgrade(this, StringLibs.TAG_LIFE).isEmpty()) {
			if (!world.isRemote) {
				EntityCabinet cabinet = new EntityCabinet(world);
				IBlockState state = world.getBlockState(getPos());
				float angle = state.getValue(BlockRFC.FACING).getHorizontalAngle();
				cabinet.setPositionAndRotation(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, angle, 0);
				
				for (int i = 0; i < getInventory().getSlots(); i++) {
					ItemStack folder = getInventory().getFolder(i);
					if (!folder.isEmpty())
						cabinet.getInventory().setStackInSlot(i, folder);
				}
				if (this.isCabinetLocked())
					cabinet.setOwnerId(getOwner());
				else
					cabinet.homePos = getPos().toLong();
				
				cabinet.setLegit();
				world.spawnEntity(cabinet);
			}
			world.setBlockToAir(getPos());
		}
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
