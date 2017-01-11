package com.bafomdad.realfilingcabinet.entity;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
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
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IUpgrades;
import com.bafomdad.realfilingcabinet.blocks.BlockRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.entity.ai.*;
import com.bafomdad.realfilingcabinet.helpers.MobUpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.ResourceUpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.init.RFCBlocks;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.inventory.InventoryEntity;

public class EntityCabinet extends EntityTameable {
	
	private final InventoryEntity inventory;
	private static final DataParameter<Boolean> YAY = EntityDataManager.createKey(EntityCabinet.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> STATE = EntityDataManager.createKey(EntityCabinet.class, DataSerializers.VARINT);
	private int variant = 0;
	public String upgrades = "";
	public long homePos;
	
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
		this.dataManager.register(STATE, Integer.valueOf(0));
	}
	
	protected void initEntityAI() {
		
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIFollowOwner(this, 0.6F, 10.0F, 2.0F));
		this.tasks.addTask(4, new EntityAIEatItem(this));
		this.tasks.addTask(5, new EntityAIHugMob(this));
		this.tasks.addTask(6, new EntityAISlurp(this));
		this.tasks.addTask(7, new EntityAIMoveBackHome(this));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(9, new EntityAILookIdle(this));
	}
	
	protected void applyEntityAttributes() {
		
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(30.0D);
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
		
		if (!upgrades.isEmpty()) {
			if (source.getEntity() instanceof EntityPlayer)
				MobUpgradeHelper.removeUpgrade((EntityPlayer)source.getEntity(), this);
			return false;
		}
		this.setTile(source);
		return true;
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack) {
		
		if (hand == EnumHand.MAIN_HAND && !this.worldObj.isRemote) {
			if (stack != null && stack.getItem() instanceof IUpgrades)
			{
				MobUpgradeHelper.setUpgrade(player, this, stack);
				return true;
			}
		}
		return false;
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
        upgrades = tag.getString(StringLibs.RFC_MOBUPGRADE);
        homePos = tag.getLong("homePos");
        this.setTextureState(tag.getInteger("varTex"));
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
        tag.setString(StringLibs.RFC_MOBUPGRADE, upgrades);
        tag.setLong("homePos", homePos);
        tag.setInteger("varTex", this.getTextureState());
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {

		return null;
	}
	
	@Override
	public int getMaxFallHeight() {
		
		return 10;
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
	
	public void setTextureState(int val) {
		
		this.dataManager.set(STATE, Integer.valueOf(val));
	}
	
	public int getTextureState() {
		
		return (Integer)this.dataManager.get(STATE).intValue();
	}
	
	private void setTile(DamageSource source) {
		
		if (source.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)source.getEntity();
			IBlockState state = RFCBlocks.blockRFC.getDefaultState().withProperty(BlockRFC.FACING, this.getHorizontalFacing().getOpposite());
			BlockPos pos = new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
			if (!worldObj.isRemote)
			{
				if (worldObj.isAirBlock(pos)) {
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
				else
					player.addChatMessage(new TextComponentString(TextHelper.localize("message." + RealFilingCabinet.MOD_ID + ".notAir")));
			}
		}
	}
	
	@Override
    public float getPathPriority(PathNodeType nodeType) {
    	
    	if (nodeType.getPriority() != 0.0F && (nodeType == PathNodeType.LAVA || nodeType == PathNodeType.DANGER_OTHER)) {
    		
    		return 0.0F;
    	}
    	return super.getPathPriority(nodeType);
    }
}
