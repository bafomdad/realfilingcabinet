package com.bafomdad.realfilingcabinet.blocks.tiles;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockHorizontal;
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
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.CapabilityItemHandler;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.ILockableCabinet;
import com.bafomdad.realfilingcabinet.blocks.BlockRFC;
import com.bafomdad.realfilingcabinet.entity.EntityCabinet;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.integration.storagedrawers.CabinetData;
import com.bafomdad.realfilingcabinet.inventory.FluidRFC;
import com.bafomdad.realfilingcabinet.inventory.InventoryRFC;
import com.bafomdad.realfilingcabinet.utils.EnderUtils;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;
import com.bafomdad.realfilingcabinet.utils.SmeltingUtils;
import com.google.common.collect.Lists;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;

@Optional.Interface(iface = "com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup", modid = RealFilingCabinet.STORAGEDRAWERS, striprefs = true)
public class TileEntityRFC extends TileFilingCabinet implements ITickable, ILockableCabinet, IDrawerGroup {

	private InventoryRFC inv = new InventoryRFC(this, 8);
	private FluidRFC fluidinv = new FluidRFC(this);
	
	private UUID owner;
	
	// MISC variables
	private long lastClickTime;
	private UUID lastClickUUID;
	
	// NBT variables
	private int rfcHash = -1;
	public boolean isCreative = false;
	public String upgrades = "";
	public List<int[]> smeltingJobs = Lists.newArrayList();
	public int fuelTime = 0;
	
	// Rendering variables
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
		if (SmeltingUtils.canSmelt(this)) {
//			if (!world.isRemote) {
				SmeltingUtils.incrementSmeltTime(this);
				if (getWorld().getTotalWorldTime() % 40 == 0) {
					SmeltingUtils.createSmeltingJob(this);
					this.markBlockForUpdate();
				}
//			}
			return;
		}
		if (UpgradeHelper.getUpgrade(this, StringLibs.TAG_LIFE) != null) {
			if (!world.isRemote) {
				EntityCabinet cabinet = new EntityCabinet(world);
				IBlockState state = world.getBlockState(getPos());
				float angle = state.getActualState(world, pos).getValue(BlockHorizontal.FACING).getHorizontalAngle();
				cabinet.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
				cabinet.setRotationYawHead(angle);
				
				for (int i = 0; i < this.getInventory().getSlots(); i++) {
					ItemStack folder = this.getInventory().getTrueStackInSlot(i);
					if (folder != null) {
						cabinet.setInventory(i, folder.copy());
					}
				}
				if (this.isCabinetLocked()) {
					UUID uuid = this.getCabinetOwner();
					cabinet.setOwnerId(uuid);
				} else
					cabinet.homePos = getPos().toLong();
				
				if (!cabinet.isLegit())
					cabinet.setLegit();
				
				world.spawnEntity(cabinet);
			}
			world.setBlockToAir(getPos());
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
		tag.setBoolean(StringLibs.TAG_CREATIVE, this.isCreative);
		
		if (owner != null)
			tag.setString("Own", owner.toString());
		if (rfcHash != -1)
			tag.setInteger(StringLibs.RFC_HASH, rfcHash);
		
		tag.setString(StringLibs.RFC_UPGRADE, upgrades);
		
		SmeltingUtils.writeSmeltNBT(this, tag);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound tag) {
		
		inv.deserializeNBT(tag.getCompoundTag("inventory"));
		this.isOpen = tag.getBoolean("isOpen");
		this.isCreative = tag.getBoolean(StringLibs.TAG_CREATIVE);
		
		this.owner = null;
		if (tag.hasKey("Own"))
			owner = UUID.fromString(tag.getString("Own"));
		if (tag.hasKey(StringLibs.RFC_HASH))
			rfcHash = tag.getInteger(StringLibs.RFC_HASH);
		upgrades = tag.getString(StringLibs.RFC_UPGRADE);
		
		SmeltingUtils.readSmeltNBT(this, tag);
	}
	
	public void readInv(NBTTagCompound nbt) {
		
		NBTTagList invList = nbt.getTagList("inventory", 10);
		for (int i = 0; i < invList.tagCount(); i++) {
			NBTTagCompound itemTag = invList.getCompoundTagAt(i);
			int slot = itemTag.getByte("Slot");
			if (slot >= 0 && slot < inv.getSlots())
				inv.getStacks().set(slot, new ItemStack(itemTag));
		}
	}
	
	public void writeInv(NBTTagCompound nbt, boolean toItem) {
		
		boolean write = false;
		NBTTagList invList = new NBTTagList();
		for (int i = 0; i < inv.getSlots(); i++) {
			if (!inv.getTrueStackInSlot(i).isEmpty()) {
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
	
	public IFluidHandler getFluidInventory() {
		
		return fluidinv;
	}
	
	public boolean calcLastClick(EntityPlayer player) {
		
		boolean bool = false;
		
		if (getWorld().getTotalWorldTime() - lastClickTime < 10 && player.getPersistentID().equals(lastClickUUID)) {
			bool = true;
		}
		lastClickTime = getWorld().getTotalWorldTime();
		lastClickUUID = player.getPersistentID();
		
		return bool;
	}
	
	public boolean hasItemFrame() {
		
		AxisAlignedBB aabb = new AxisAlignedBB(pos.add(0, 1, 0), pos.add(1, 2, 1));
		List<EntityItemFrame> frames = this.getWorld().getEntitiesWithinAABB(EntityItemFrame.class, aabb);
		for (EntityItemFrame frame : frames) {
			EnumFacing orientation = frame.getAdjustedHorizontalFacing();
			IBlockState state = getWorld().getBlockState(getPos());
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
			IBlockState state = getWorld().getBlockState(getPos());
			EnumFacing rfcOrientation = (EnumFacing)state.getValue(BlockRFC.FACING);
			if (frame != null && !frame.getDisplayedItem().isEmpty() && (orientation == rfcOrientation)) {
				if (frame.getDisplayedItem().getItem() == RFCItems.filter) {
					int rotation = frame.getRotation();
					return inv.getStackFromFolder(rotation);
				}
				return frame.getDisplayedItem();
			}
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean hasCapability(@Nonnull Capability<?> cap, EnumFacing side) {
		
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && UpgradeHelper.getUpgrade(this, StringLibs.TAG_FLUID) != null)
			return cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
		if (cap == DRAWER_GROUP_CAPABILITY && !UpgradeHelper.hasUpgrade(this))
			return cap == DRAWER_GROUP_CAPABILITY;
		
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}
	
	@Override
	public <T> T getCapability(@Nonnull Capability<T> cap, EnumFacing side) {
		
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inv);
		}
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidinv);
		if (cap == DRAWER_GROUP_CAPABILITY)
			return (T) this;
		
		return super.getCapability(cap, side);
	}

	@Override
	public UUID getCabinetOwner() {

		return owner;
	}

	@Override
	public boolean setOwner(UUID owner) {

		if ((this.owner != null && !this.owner.equals(owner)) || (owner != null && !owner.equals(this.owner))) {
			this.owner = owner;
			
			if (getWorld() != null && !getWorld().isRemote) {
				markDirty();
				this.markBlockForUpdate();
			}
		}
		return true;
	}

	@Override
	public boolean isCabinetLocked() {

		return getCabinetOwner() != null;
	}
	
	public boolean hasKeyCopy(EntityPlayer player, UUID uuid) {
		
		for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
			ItemStack keyCopy = player.inventory.mainInventory.get(i);
			if (keyCopy.isEmpty())
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

/**
 * Storage Drawers Integration begins here
 */
	@CapabilityInject(IDrawerGroup.class)
	static Capability<IDrawerGroup> DRAWER_GROUP_CAPABILITY = null;
	
	@Override
	public int getDrawerCount() {

		return this.inv.getSlots();
	}

	@Optional.Method(modid = RealFilingCabinet.STORAGEDRAWERS)
	@Override
	public IDrawer getDrawer(int slot) {

		return new CabinetData(this, slot);
	}

	@Override
	public int[] getAccessibleDrawerSlots() {

		return new int[this.inv.getSlots()];
	}
}
