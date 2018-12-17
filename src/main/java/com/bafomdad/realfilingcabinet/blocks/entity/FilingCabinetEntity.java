package com.bafomdad.realfilingcabinet.blocks.entity;

import com.bafomdad.realfilingcabinet.blocks.FilingCabinetBlock;
import com.bafomdad.realfilingcabinet.data.AbstractDataHolder;
import com.bafomdad.realfilingcabinet.data.EnumDataType;
import com.bafomdad.realfilingcabinet.data.IDataHooks;
import com.bafomdad.realfilingcabinet.init.RFCEntities;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.inventory.InventoryRFC;
import com.bafomdad.realfilingcabinet.items.FolderItem;
import net.fabricmc.fabric.block.entity.ClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.List;

/**
 * Created by bafomdad on 12/11/2018.
 */
public class FilingCabinetEntity extends BlockEntity implements Tickable, ClientSerializable, IDataHooks {

    public float offset, renderOffset;
    public static final float offsetSpeed = 0.1F;

    public boolean isOpen = false;

    private DefaultedList<ItemStack> inventory;
    private InventoryRFC data = new InventoryRFC(this);

    public FilingCabinetEntity() {

        super(RFCEntities.FILINGCABINET_BE);
        this.inventory = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
    }

    @Override
    public void tick() {

        if (isOpen) {
            offset -= offsetSpeed;
            if (offset <= -0.75F);
            offset = -0.75F;
        } else {
            offset += offsetSpeed;
            if (offset >= 0.05F)
                offset = 0.05F;
        }
    }

    // write
    @Override
    public CompoundTag toTag(CompoundTag tag) {

        tag = super.toTag(tag);
        tag.putBoolean("isOpen", this.isOpen);
        data.serialize(tag, this.inventory);

        return tag;
    }

    // read
    @Override
    public void fromTag(CompoundTag tag) {

        super.fromTag(tag);
        this.isOpen = tag.getBoolean("isOpen");
        this.inventory = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
        if (tag.containsKey("Items", 9))
            data.deserialize(tag, this.inventory);
    }

    public void markBlockForUpdate() {

        this.markDirty();
        this.world.updateListeners(pos, this.getCachedState(), this.getCachedState(), 3);
    }

    @Override
    public void fromClientTag(CompoundTag tag) {

        this.isOpen = tag.getBoolean("isOpen");
        data.deserialize(tag, inventory);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {

        tag.putBoolean("isOpen", this.isOpen);
        data.serialize(tag, inventory);
        return tag;
    }

    public DefaultedList<ItemStack> getInventory() {

        return inventory;
    }

    public ItemStack getFilter() {

        VoxelShape aabb = VoxelShapes.cube(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
        List<ItemFrameEntity> frames = world.getVisibleEntities(ItemFrameEntity.class, aabb.getBoundingBox());
        for (ItemFrameEntity frame : frames) {
            Direction dir = frame.getHorizontalFacing();
            Direction facing = getCachedState().get(FilingCabinetBlock.FACING);
            if (frame != null && !frame.getHeldItemStack().isEmpty() && (dir == facing)) {
                if (frame.getHeldItemStack().getItem() == RFCItems.FILTER) {
                    return getStoredItem(frame.getRotation());
                }
                return frame.getHeldItemStack();
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getStoredItem(int slot) {

        return FolderItem.getItem(inventory.get(slot));
    }

    public int getInvSize() {

        return 8;
    }

    @Override
    public boolean hasDataHolder(EnumDataType type) {

        return type == EnumDataType.ITEM;
    }

    @Override
    public AbstractDataHolder getDataHolder(EnumDataType type) {

        if (hasDataHolder(type)) {
            return data;
        }
        return null;
    }
}
