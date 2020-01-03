package com.bafomdad.realfilingcabinet.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IEntityCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.blocks.BlockRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileFilingCabinet;
import com.bafomdad.realfilingcabinet.entity.ai.*;
import com.bafomdad.realfilingcabinet.helpers.MobUpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.enums.UpgradeType;
import com.bafomdad.realfilingcabinet.init.RFCBlocks;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;

public class EntityCabinet extends EntityTameable implements IEntityCabinet {
	
	private final ItemStackHandler inventory;
	private static final DataParameter<Boolean> YAY = EntityDataManager.createKey(EntityCabinet.class, DataSerializers.BOOLEAN);

	private static final DataParameter<String> MODEL = EntityDataManager.createKey(EntityCabinet.class, DataSerializers.STRING);
	private static final DataParameter<String> TEXTURE = EntityDataManager.createKey(EntityCabinet.class, DataSerializers.STRING);
	
	public long homePos;
	private boolean isRealEntity = false;
	public String upgrades = "";
	
	public EntityCabinet(World world) {
		
		super(world);
		this.setSize(0.9F, 1.3F);
		this.inventory = new ItemStackHandler(8);
	}
	
	@Override
	protected void entityInit() {
		
		super.entityInit();
		this.isImmuneToFire = true;
		this.setTamed(true);
		this.dataManager.register(YAY, Boolean.valueOf(false));
		
		this.dataManager.register(MODEL, String.valueOf(""));
		this.dataManager.register(TEXTURE, String.valueOf(""));
	}
	
	@Override
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
	
	@Override
	protected void applyEntityAttributes() {
		
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(30.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.55D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
	}
	
	@Override
	public boolean isEntityInvulnerable(DamageSource source) {
		
		if (source == DamageSource.OUT_OF_WORLD) return false;
		
		if (source.getTrueSource() instanceof EntityPlayer && source.getTrueSource().isSneaking()) {
			EntityPlayer player = (EntityPlayer)source.getTrueSource();
			if (player.getHeldItemMainhand().getItem() == RFCItems.MAGNIFYINGGLASS)
				return false;
		}
		return true;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		
		if (this.isEntityInvulnerable(source)) return false;
		
		if (!this.world.isRemote && !this.isLegit()) {
			this.setDead();
			return false;
		}
		if (getPosition().getY() < 0) {
			BlockPos toHome = BlockPos.fromLong(homePos);
			if (!world.isRemote)
				this.setPosition(toHome.getX(), toHome.getY(), toHome.getZ());
			
			return false;
		}
		if (source.getTrueSource() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)source.getTrueSource();
			if (MobUpgradeHelper.hasUpgrade(this)) {
				MobUpgradeHelper.removeMobUpgrade(player, this);
				return false;
			}
			this.setTile(player);
			return false;
		}
		return true;
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		
		if (hand == EnumHand.MAIN_HAND) {
			ItemStack stack = player.getHeldItemMainhand();
			if (!stack.isEmpty() && !world.isRemote) {
				MobUpgradeHelper.setMobUpgrade(player, this, stack);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		
		super.readEntityFromNBT(tag);
	
		this.inventory.deserializeNBT(tag.getCompoundTag(StringLibs.ENTITY_INV));
		this.isRealEntity = tag.getBoolean("legitEntity");
		this.homePos = tag.getLong("homePos");
		this.upgrades = tag.getString(StringLibs.RFC_MOBUPGRADE);
		
		this.setTexture(tag.getString("hatTexture"));
		this.setModel(tag.getString("hatModel"));
		
		// legacy conversion of old inventory system to the new one
		if (tag.hasKey("Inventory")) {
			NBTTagList tagList = tag.getTagList("Inventory", 10);
			for (int i = 0; i < tagList.tagCount(); ++i) {
				ItemStack stack = new ItemStack(tagList.getCompoundTagAt(i));
				if (!stack.isEmpty())
					getInventory().setStackInSlot(i, stack);
			}
			tag.removeTag("Inventory");
		}
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		
		super.writeEntityToNBT(tag);
		
		tag.setTag(StringLibs.ENTITY_INV, inventory.serializeNBT());
		tag.setBoolean("legitEntity", isRealEntity);
		tag.setLong("homePos", homePos);
		tag.setString(StringLibs.RFC_MOBUPGRADE, upgrades);
		
		tag.setString("hatTexture", this.getTexture());
		tag.setString("hatModel", this.getModel());
	}
	
	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		
		super.notifyDataManagerChange(key);
	}
	
	public void setModel(String modelClassName) {
		
		this.dataManager.set(MODEL, String.valueOf(modelClassName));
	}
	
	public String getModel() {
		
		return (String)this.dataManager.get(MODEL);
	}
	
	public void setTexture(String resourcePath) {
		
		this.dataManager.set(TEXTURE, String.valueOf(resourcePath));
	}
	
	public String getTexture() {
		
		return (String)this.dataManager.get(TEXTURE);
	}
	
	public boolean isHatPresent() {
		
		return !getModel().isEmpty() && !getTexture().isEmpty();
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {

		return null;
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
	
	@Override
	public boolean hasCapability(@Nonnull Capability<?> cap, EnumFacing side) {
		
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}
	
	@Override
	public <T> T getCapability(@Nonnull Capability<T> cap, EnumFacing side) {
		
		if (hasCapability(cap, side))
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
		
		return super.getCapability(cap, side);
	}
	
	public ItemStackHandler getInventory() {
		
		return inventory;
	}
	
	public Object insert(Object toInsert, boolean simulate) {
		
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack folder = inventory.getStackInSlot(i);
			if (!folder.isEmpty() && folder.getItem() instanceof IFolder) {
				return FolderUtils.get(folder).insert(toInsert, simulate);
			}
		}
		return null;
	}
	
	public void setYay(boolean bool) {
		
		this.dataManager.set(YAY, Boolean.valueOf(bool));
	}
	
	public boolean isYaying() {
		
		return this.dataManager.get(YAY).booleanValue();
	}
	
	public boolean isLegit() {
		
		return this.isRealEntity;
	}
	
	public void setLegit() {
		
		this.isRealEntity = true;
	}
	
	private void setTile(EntityPlayer player) {
		
		IBlockState state = RFCBlocks.MODELCABINET.getDefaultState().withProperty(BlockRFC.FACING, this.getHorizontalFacing());
		BlockPos pos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.posY), MathHelper.floor(this.posZ));
		if (!world.isRemote) {
			if (world.isAirBlock(pos)) {
				world.setBlockState(pos, state, 2);
				TileFilingCabinet tile = (TileFilingCabinet)world.getTileEntity(pos);
				if (tile != null) {
					for (int i = 0; i < inventory.getSlots(); i++) {
						ItemStack folder = inventory.getStackInSlot(i);
						if (!folder.isEmpty())
							tile.getInventory().setStackInSlot(i, folder);
						if (this.getOwner() != null)
							tile.setOwner(this.getOwnerId());
					}
				}
				ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(RFCItems.UPGRADE, 1, UpgradeType.LIFE.ordinal()));
				this.setDead();
			} else {
				player.sendStatusMessage(new TextComponentTranslation("message." + RealFilingCabinet.MOD_ID + ".notAir"), true);
			}
		}
	}
	
	@Override
	public List<String> getInfoOverlay(Entity entity, boolean crouching) {
	
		List<String> list = new ArrayList();
		list.add("Currently carrying:");
		if (entity instanceof EntityCabinet) {
			for (int i = 0; i < getInventory().getSlots(); i++) {
				ItemStack folder = getInventory().getStackInSlot(i);
				if (!folder.isEmpty() && folder.getItem() instanceof IFolder) {
					FolderUtils.get(folder).addTooltips(list, crouching);
				}
			}
		}
		return list;
	}
	
	@SideOnly(Side.CLIENT)
	public ModelBase getModelHat() {

		try {
			Class clazz = Class.forName(getModel());
			return (ModelBase)clazz.newInstance();
		} catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
