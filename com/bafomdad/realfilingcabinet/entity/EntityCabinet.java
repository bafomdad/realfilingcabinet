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
import net.minecraftforge.items.ItemHandlerHelper;

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
	private boolean isRealEntity = false;
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
		
		if (source == DamageSource.OUT_OF_WORLD)
			return false;
		
		if (source.getTrueSource() instanceof EntityPlayer && source.getTrueSource().isSneaking()) {
			EntityPlayer player = (EntityPlayer)source.getTrueSource();
			if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() == RFCItems.magnifyingGlass)
				return false;
		}
		return true;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		
		if (this.isEntityInvulnerable(source))
			return false;
		
		if (!this.world.isRemote && !this.isLegit()) {
			this.setDead();
			return false;
		}
		if (!upgrades.isEmpty()) {
			if (source.getTrueSource() instanceof EntityPlayer)
				MobUpgradeHelper.removeUpgrade((EntityPlayer)source.getTrueSource(), this);
			return false;
		}
		this.setTile(source);
		return true;
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		
		ItemStack stack = player.getHeldItemMainhand();
		if (hand == EnumHand.MAIN_HAND && !this.world.isRemote) {
			if (!stack.isEmpty() && stack.getItem() instanceof IUpgrades) {
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
        for (int i = 0; i < nbttaglist.tagCount(); ++i){
        	ItemStack itemstack = new ItemStack(nbttaglist.getCompoundTagAt(i));
            if (!itemstack.isEmpty())
                this.inventory.setStackInSlot(i, itemstack.copy());
        }
        isRealEntity = tag.getBoolean("legitEntity");
        upgrades = tag.getString(StringLibs.RFC_MOBUPGRADE);
        homePos = tag.getLong("homePos");
        this.setTextureState(tag.getInteger("varTex"));
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		
		super.writeEntityToNBT(tag);
		
		NBTTagList tagList = new NBTTagList();
        for (int i = 0; i < this.inventory.getSlots(); ++i){
            ItemStack itemstack = this.inventory.getStackInSlot(i);
            if (!itemstack.isEmpty())
                tagList.appendTag(itemstack.writeToNBT(new NBTTagCompound()));
        }
        tag.setTag("Inventory", tagList);
        tag.setBoolean("legitEntity", isRealEntity);
        tag.setString(StringLibs.RFC_MOBUPGRADE, upgrades);
        tag.setLong("homePos", homePos);
        tag.setInteger("varTex", this.getTextureState());
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
	
	public void setTextureState(int val) {
		
		this.dataManager.set(STATE, Integer.valueOf(val));
	}
	
	public int getTextureState() {
		
		return (Integer)this.dataManager.get(STATE).intValue();
	}
	
	public boolean isLegit() {
		
		return this.isRealEntity;
	}
	
	public void setLegit() {
		
		this.isRealEntity = true;
	}
	
	private void setTile(DamageSource source) {
		
		if (source.getTrueSource() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)source.getTrueSource();
			IBlockState state = RFCBlocks.blockRFC.getDefaultState().withProperty(BlockRFC.FACING, this.getHorizontalFacing().getOpposite());
			BlockPos pos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.posY), MathHelper.floor(this.posZ));
			if (!world.isRemote) {
				if (world.isAirBlock(pos)) {
					world.setBlockState(pos, state);
					TileEntityRFC tile = (TileEntityRFC)world.getTileEntity(pos);
					if (tile != null) {
						for (int i = 0; i < inventory.getSlots(); i++) {
							ItemStack folder = inventory.getStackInSlot(i);
							if (!folder.isEmpty())
								this.setInventory(tile, i, folder);
						}
						if (this.getOwner() != null)
							tile.setOwner(this.getOwnerId());
					}
					ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(RFCItems.upgrades, 1, 6));
					this.setDead();
				}
				else
					player.sendStatusMessage(new TextComponentString(TextHelper.localize("message." + RealFilingCabinet.MOD_ID + ".notAir")), true);
			}
		}
	}
	
	@Override
	public int getMaxFallHeight() {
		
		return 10;
	}
	
	@Override
	public float getPathPriority(PathNodeType nodeType) {
		
		if (nodeType.getPriority() != 0.0F && (nodeType == PathNodeType.LAVA || nodeType == PathNodeType.DANGER_OTHER))
			return 0.0F;
			
		return super.getPathPriority(nodeType);
	}
}
