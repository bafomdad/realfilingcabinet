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
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import com.bafomdad.realfilingcabinet.api.ILockableCabinet;
import com.bafomdad.realfilingcabinet.blocks.BlockRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.inventory.InventoryRFC;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;

public abstract class TileEntityRFC extends TileEntity implements ITickable, ILockableCabinet {
	
	private UUID owner;
	private InventoryRFC inv = new InventoryRFC(this, 8);
	public boolean isCreative = false;
	
	// last clicked variables (we don't have to save those to disk)
	private long lastClickTime;
	private UUID lastClickUUID;
	
	// rendering variables
	public static final float offsetSpeed = 0.1F;
	public boolean isOpen = false;
	public float offset, renderOffset;
	
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
	
	public InventoryRFC getInventory() {
		
		return inv;
	}
	
	public EntityItemFrame getItemFrame() {
		
		AxisAlignedBB aabb = new AxisAlignedBB(pos.add(0, 1, 0), pos.add(1, 2, 1));
		List<EntityItemFrame> frames = this.getWorld().getEntitiesWithinAABB(EntityItemFrame.class, aabb);
		for (EntityItemFrame frame : frames) {
			EnumFacing orientation = frame.getAdjustedHorizontalFacing();
			IBlockState state = getWorld().getBlockState(getPos());
			EnumFacing tileFacing = (EnumFacing)state.getValue(BlockRFC.FACING);
			if (orientation == tileFacing)
				return frame;
		}
		return null;
	}
	
	public ItemStack getFilter() {
		
		EntityItemFrame frame = getItemFrame();
		if (frame != null && !frame.getDisplayedItem().isEmpty()) {
			if (frame.getDisplayedItem().getItem() == RFCItems.FILTER) {
				int rotation = frame.getRotation();
				return getInventory().getStackFromFolder(rotation);
			}
			return frame.getDisplayedItem();
		}
		return ItemStack.EMPTY;
	}
	
	public boolean calcLastClick(EntityPlayer player) {
		
		boolean bool = false;
		if (getWorld().getTotalWorldTime() - lastClickTime < 10 && player.getPersistentID().equals(lastClickUUID)) bool = true;
		
		lastClickTime = getWorld().getTotalWorldTime();
		lastClickUUID = player.getPersistentID();
		
		return bool;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		
		super.writeToNBT(tag);
		writeCustomNBT(tag);
		return tag;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		
		super.readFromNBT(tag);
		readCustomNBT(tag);
	}
	
	public void writeCustomNBT(NBTTagCompound tag) {
		
		tag.setBoolean("isOpen", this.isOpen);
		tag.setBoolean(StringLibs.TAG_CREATIVE, this.isCreative);
		
		if (owner != null)
			tag.setString(StringLibs.RFC_OWNER, owner.toString());
		tag.setTag("inventory", inv.serializeNBT());
	}
	
	public void readCustomNBT(NBTTagCompound tag) {
		
		this.isOpen = tag.getBoolean("isOpen");
		this.isCreative = tag.getBoolean(StringLibs.TAG_CREATIVE);
		
		this.owner = null;
		if (tag.hasKey(StringLibs.RFC_OWNER))
			owner = UUID.fromString(tag.getString(StringLibs.RFC_OWNER));
		inv.deserializeNBT(tag.getCompoundTag("inventory"));
	}
	
	@Override
	public UUID getOwner() {
		
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
		
		return getOwner() != null;
	}
	
	public void doKeyStuff(EntityPlayer player, ItemStack key) {
	
		if (!this.isCabinetLocked()) {
			if (key.getItemDamage() == 0) {
				this.setOwner(player.getUniqueID());
				player.sendStatusMessage(new TextComponentString("Cabinet locked"), true);
				return;
			}
		} else {
			if (this.getOwner().equals(player.getUniqueID())) {
				if (key.getItemDamage() == 0) {
					this.setOwner(null);
					player.sendStatusMessage(new TextComponentString("Cabinet unlocked"), true);
					return;
				} else if (key.getItemDamage() == 1) {
					if (!key.hasTagCompound() || (key.hasTagCompound() && !key.getTagCompound().hasKey(StringLibs.RFC_COPY))) {
						NBTUtils.setString(key, StringLibs.RFC_COPY, player.getUniqueID().toString());
						NBTUtils.setString(key, StringLibs.RFC_FALLBACK, player.getDisplayNameString());
					}
				}
			}
		}
	}
	
	@Override
	public boolean hasKeyCopy(EntityPlayer player, UUID uuid) {
		
		for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
			ItemStack keyCopy = player.inventory.mainInventory.get(i);
			if (keyCopy.getItem() == RFCItems.KEY && keyCopy.getItemDamage() == 1) {
				if (keyCopy.hasTagCompound() && keyCopy.getTagCompound().hasKey(StringLibs.RFC_COPY))
					return uuid.equals(UUID.fromString(keyCopy.getTagCompound().getString(StringLibs.RFC_COPY)));
			}
		}
		return false;
	}
	
	@Override
	public void markBlockForUpdate() {
		
		IBlockState state = getWorld().getBlockState(pos);
		getWorld().notifyBlockUpdate(pos, state, state, 3);
	}
	
	@Override
	public void markBlockForRenderUpdate() {
		
		getWorld().markBlockRangeForRenderUpdate(pos, pos);
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
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		
		if (packet != null && packet.getNbtCompound() != null)
			readCustomNBT(packet.getNbtCompound());
		
		markBlockForRenderUpdate();
	}
	
	@Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
    	
    	return oldState.getBlock() != newState.getBlock();
    }
	
	@Override
	public boolean hasCapability(@Nonnull Capability<?> cap, EnumFacing side) {
		
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}
	
	@Override
	public <T> T getCapability(@Nonnull Capability<T> cap, EnumFacing side) {
		
		if (hasCapability(cap, side))
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inv);
			
		return super.getCapability(cap, side);
	}
	
	public void getDrops(NonNullList<ItemStack> list) {
		
		ItemStack stack = list.get(0);
		if (!stack.isEmpty()) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			NBTTagCompound tag = stack.getTagCompound();
			tag.setTag("inventory", inv.serializeNBT());
		}
	}
	
	public void onPlaced(World world, BlockPos pos, IBlockState state, ItemStack stack) {
		
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("inventory")) {
			inv.deserializeNBT(stack.getTagCompound().getCompoundTag("inventory"));
		}
	}
}
