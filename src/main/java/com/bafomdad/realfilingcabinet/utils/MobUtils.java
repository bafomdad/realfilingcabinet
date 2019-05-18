package com.bafomdad.realfilingcabinet.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityFolder;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityProviderFolder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.items.ItemHandlerHelper;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

public class MobUtils {
	
	public static boolean canPlayerChangeStuffHere(World world, EntityPlayer player, ItemStack stack, BlockPos pos, EnumFacing facing) {
		
		if (!world.isBlockModifiable(player, pos))
			return false;
		
		if (!player.canPlayerEdit(pos, facing, stack))
			return false;
		
		return true;
	}
	
	public static boolean spawnEntityFromFolder(World world, EntityPlayer player, ItemStack stack, BlockPos pos, EnumFacing side) {
		
		if (!canPlayerChangeStuffHere(world, player, stack, pos, side))
			return false;
		
		CapabilityFolder cap = FolderUtils.get(stack).getCap();
		if (cap == null) return false;
		
		long count = cap.getCount();
		if (count > 0) {
			if (player.canPlayerEdit(pos.offset(side), side, stack)) {
				EntityLivingBase entity = cap.getEntity(world);
				if (entity != null) {
					boolean spawn = false;
					if (!player.world.isRemote) {
						pos = pos.offset(side);
						if ((entity instanceof EntityVillager && !ConfigRFC.randomVillager)) {
		                    EntityVillager entityliving = (EntityVillager)entity;
		                    entity.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
		                    entityliving.rotationYawHead = entityliving.rotationYaw;
		                    entityliving.renderYawOffset = entityliving.rotationYaw;
		                    entityliving.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entityliving)), (IEntityLivingData)null);
							entityliving.setProfession(0);
		                    world.spawnEntity(entity);
		                    entityliving.playLivingSound();
		                    
		                    spawn = true;
						}
						if (entity instanceof EntitySlime) {
							EntitySlime slime = (EntitySlime)entity;
							slime.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
							NBTTagCompound tag = slime.getEntityData();
							tag.setInteger("Size", 0);
							slime.writeEntityToNBT(tag);
							slime.setHealth(1.0F);
							world.spawnEntity(slime);
							slime.playLivingSound();
							
							spawn = true;
						}
						else {
							EntityLiving entityliving = (EntityLiving)entity;
		                    entity.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
		                    entityliving.rotationYawHead = entityliving.rotationYaw;
		                    entityliving.renderYawOffset = entityliving.rotationYaw;
		                    entityliving.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entityliving)), (IEntityLivingData)null);
		                    world.spawnEntity(entity);
		                    entityliving.playLivingSound();
		                    
		                    spawn = true;
						}
						if (spawn) {
							if (!player.capabilities.isCreativeMode)
								cap.setCount(count - 1);
						}
					}
				}
			}
		}
		return true;
	}
	
	public static void dropMobEquips(World world, EntityLivingBase entity) {
		
		List<ItemStack> list = loopArmor(entity);
		if (list == null || list.isEmpty())
			return;
		
		for (int i = 0; i < list.size(); i++) {
			ItemStack toDrop = list.get(i);
			if (toDrop.getCount() == 0) {
				toDrop.setCount(1);
				EntityItem ei = new EntityItem(world, entity.posX, entity.posY, entity.posZ, toDrop);
				
				if (!world.isRemote)
					world.spawnEntity(ei);
			}
		}
	}

	public static List<ItemStack> loopArmor(EntityLivingBase entity) {
		
		List<ItemStack> invList = new ArrayList<ItemStack>();
		
    	EntityEquipmentSlot[] slots = new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET };
    	for (int i = 0; i < slots.length; i++) {
    		ItemStack stack = entity.getItemStackFromSlot(slots[i]);
    		if (!stack.isEmpty())
    			invList.add(stack);
    	}
    	return invList;
	}

	public static boolean acceptableTargets(EntityLivingBase target) {
		
		if (target instanceof EntityPlayer) return false;
		if (target.isChild() && !(target instanceof EntityZombie)) return false;
		if (target instanceof IEntityOwnable && ((IEntityOwnable)target).getOwner() != null) return false;
		
		String entityBlacklist = target.getClass().getSimpleName();
		for (String toBlacklist : ConfigRFC.mobFolderBlacklist) {
			if (toBlacklist.contains(entityBlacklist)) return false;
		}
		return true;
	}
}
