package com.bafomdad.realfilingcabinet.blocks.tiles;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;

import com.bafomdad.realfilingcabinet.api.ILockableCabinet;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemManaFolder;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;
import com.google.common.base.Predicates;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList({
	@Optional.Interface(iface = "vazkii.botania.api.mana.IManaPool", modid = "botania"),
	@Optional.Interface(iface = "vazkii.botania.api.mana.spark.ISparkAttachable", modid = "botania")
})
public class TileManaCabinet extends TileFilingCabinet implements ITickable, ILockableCabinet, IManaPool, ISparkAttachable {

	private ItemStackHandler inv = new ItemStackHandler(8);
	private UUID owner;
	
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
	
		if (owner != null)
			tag.setString("Own", owner.toString());
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound tag) {
		
		getInv().deserializeNBT(tag.getCompoundTag("inventory"));
		this.isOpen = tag.getBoolean("isOpen");
		
		this.owner = null;
		if (tag.hasKey("Own"))
			owner = UUID.fromString(tag.getString("Own"));
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
			if (getInv().getStackInSlot(i) != ItemStack.EMPTY)
			{
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
	
	@Override
	public boolean hasCapability(@Nonnull Capability<?> cap, @Nonnull EnumFacing side) {
		
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}
	
	@Override
	public <T> T getCapability(@Nonnull Capability<T> cap, @Nonnull EnumFacing side) {
		
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getInv());
		
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
			if (world != null && !world.isRemote) {	
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
	
	public ItemStackHandler getInv() {
		
		return inv;
	}

	// BOTANIA IMPL
	long MAX_MANA_INTERNAL = ItemManaFolder.getMaxManaFolder() * 8;
//	int MAX_VANILLA_MANA_POOL = 1000000;
	
	public long getTotalInternalManaPool() {
		
		long total = 0;
		for (int i = 0; i < this.getInv().getSlots(); i++) {
			ItemStack stack = this.getInv().getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() instanceof IManaItem)
				total += ItemManaFolder.getManaSize(stack);
		}
		return total;
	}
	
	public int getManaFromFolder() {
		
		for (int i = 0; i < this.getInv().getSlots(); i++) {
			ItemStack stack = this.getInv().getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() instanceof IManaItem) {
				int manaSize = ItemManaFolder.getManaSize(stack);
				if (manaSize >= 0)
					return manaSize;
			}
		}
		return -1;
	}
	
	public void addManaToFolder(int mana) {
		
		for (int i = 0; i < this.getInv().getSlots(); i++){
			ItemStack stack = this.getInv().getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() instanceof IManaItem) {
				if (mana > 0 && ItemManaFolder.isManaFolderFull(stack))
					continue;
				
				if (mana < 0 && ItemManaFolder.getManaSize(stack) <= 0)
					continue;
				
				ItemManaFolder.addManaToFolder(stack, mana);
				break;
			}
		}
	}
	
	@Override
	public boolean canRecieveManaFromBursts() {

		return getManaFromFolder() != -1;
	}

	@Override
	public boolean isFull() {

		return getTotalInternalManaPool() == MAX_MANA_INTERNAL || getManaFromFolder() == -1;
	}

	@Override
	public void recieveMana(int mana) {

		int manaToAdd = Math.min(ItemManaFolder.getMaxManaFolder(), mana);
		this.addManaToFolder(manaToAdd);
	}

	@Override
	public int getCurrentMana() {

		return getManaFromFolder();
	}

	@Override
	public boolean areIncomingTranfersDone() {

		return false;
	}

	@Override
	public void attachSpark(ISparkEntity arg0) {}

	@Override
	public boolean canAttachSpark(ItemStack arg0) {

		return true;
	}

	@Override
	public ISparkEntity getAttachedSpark() {

		List sparks = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.up(), pos.up().add(1, 1, 1)), Predicates.instanceOf(ISparkEntity.class));
		if (sparks.size() == 1) {
			Entity e = (Entity)sparks.get(0);
			return (ISparkEntity)e;
		}
		return null;
	}

	@Override
	public int getAvailableSpaceForMana() {

		if (getTotalInternalManaPool() == this.MAX_MANA_INTERNAL)
			return 0;
		
		return 1000;
	}

	@Override
	public EnumDyeColor getColor() {

		return null;
	}

	@Override
	public boolean isOutputtingPower() {

		return false;
	}

	@Override
	public void setColor(EnumDyeColor arg0) {}
}
