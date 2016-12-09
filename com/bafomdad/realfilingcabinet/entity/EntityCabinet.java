package com.bafomdad.realfilingcabinet.entity;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import com.bafomdad.realfilingcabinet.blocks.BlockRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.entity.ai.EntityAIEatItem;
import com.bafomdad.realfilingcabinet.entity.ai.EntityAIHugMob;
import com.bafomdad.realfilingcabinet.entity.ai.IOriginPoint;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCBlocks;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.inventory.InventoryEntity;

public class EntityCabinet extends EntityTameable implements IOriginPoint {
	
	private final InventoryEntity inventory;
	private static final DataParameter<Boolean> YAY = EntityDataManager.createKey(EntityCabinet.class, DataSerializers.BOOLEAN);
	private long originPos;
	
	public EntityCabinet(World world) {
		
		super(world);
		this.setSize(0.9F, 1.3F);
		this.inventory = new InventoryEntity(this, 8);
	}
	
	@Override
	protected void entityInit() {
		
		super.entityInit();
		this.isImmuneToFire = true;
		this.setTamed(true);
		this.dataManager.register(YAY, Boolean.valueOf(false));
	}
	
	protected void initEntityAI() {
		
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIFollowOwner(this, 0.6F, 10.0F, 2.0F));
		this.tasks.addTask(4, new EntityAIEatItem(this));
		this.tasks.addTask(5, new EntityAIHugMob(this));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
	}
	
	protected void applyEntityAttributes() {
		
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.55D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
	}
	
	@Override
	public void onUpdate() {
		
		super.onUpdate();
	}
	
	@Override
	public boolean isEntityInvulnerable(DamageSource source) {
		
		if (source == DamageSource.outOfWorld)
			return false;
		
		if (source.getEntity() instanceof EntityPlayer && source.getEntity().isSneaking()) {
			EntityPlayer player = (EntityPlayer)source.getEntity();
			if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() == RFCItems.magnifyingGlass)
				return false;
		}
		return true;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		
		if (this.isEntityInvulnerable(source))
			return false;
		
//		amount = 999.0F;
		this.setTile(source);

		return super.attackEntityFrom(source, amount);
	}
	
	@Override
	public void onDeath(DamageSource source) {
		
		super.onDeath(source);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		
		super.readEntityFromNBT(tag);
		
        NBTTagList nbttaglist = tag.getTagList("Inventory", 10);
        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttaglist.getCompoundTagAt(i));

            if (itemstack != null)
            {
                this.inventory.setStackInSlot(i, itemstack.copy());
            }
        }
        this.originPos = tag.getLong(StringLibs.RFC_ORIGIN);
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		
		super.writeEntityToNBT(tag);
		
		NBTTagList tagList = new NBTTagList();
        for (int i = 0; i < this.inventory.getSlots(); ++i)
        {
            ItemStack itemstack = this.inventory.getStackInSlot(i);

            if (itemstack != null)
            {
                tagList.appendTag(itemstack.writeToNBT(new NBTTagCompound()));
            }
        }
        tag.setTag("Inventory", tagList);
        tag.setLong(StringLibs.RFC_ORIGIN, originPos);
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {

		return null;
	}
	
	public InventoryEntity getInventory() {
		
		return inventory;
	}
	
	public void setInventory(int slot, ItemStack stack) {
		
		if (inventory == null)
			return;
		
		this.inventory.setStackInSlot(slot, stack);
	}
	
	public void setInventory(TileEntityRFC tile, int slot, ItemStack stack) {
		
		tile.getInventory().setStackInSlot(slot, stack);
	}
	
	public void setYay(boolean bool) {
		
		this.dataManager.set(YAY, Boolean.valueOf(bool));
	}
	
	public boolean isYaying() {
		
		return (Boolean)this.dataManager.get(YAY).booleanValue();
	}
	
	private void setTile(DamageSource source) {
		
		if (source.getEntity() instanceof EntityPlayer) {
			IBlockState state = RFCBlocks.blockRFC.getDefaultState().withProperty(BlockRFC.FACING, this.getHorizontalFacing().getOpposite());
			BlockPos pos = new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
			if (!worldObj.isRemote)
			{
				worldObj.setBlockState(pos, state);
				TileEntityRFC tile = (TileEntityRFC)worldObj.getTileEntity(pos);
				if (tile != null)
				{
					for (int i = 0; i < inventory.getSlots(); i++) {
						ItemStack folder = inventory.getStackInSlot(i);
						if (folder != null)
							this.setInventory(tile, i, folder);
					}
					if (this.getOwner() != null)
					{
						tile.setOwner(this.getOwnerId());
					}
				}
				if (!((EntityPlayer)source.getEntity()).inventory.addItemStackToInventory(new ItemStack(RFCItems.upgrades, 1, 6)))
					((EntityPlayer)source.getEntity()).dropItem(new ItemStack(RFCItems.upgrades, 1, 6), true);
				
				this.setDead();
			}
		}
	}

	@Override
	public BlockPos getOriginPoint() {

		return BlockPos.fromLong(originPos);
	}

	@Override
	public void setOriginPoint(BlockPos pos) {

		this.originPos = pos.toLong();
	}
}
