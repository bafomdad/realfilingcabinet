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
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySlime;
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

import com.bafomdad.realfilingcabinet.NewConfigRFC.ConfigRFC;
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
		
		if (ItemFolder.getFileSize(stack) > 0) {
			if (player.canPlayerEdit(pos.offset(side), side, stack)) {
				if(!stack.hasCapability(CapabilityProviderFolder.FOLDER_CAP, null))
					return false;
				CapabilityFolder cap = stack.getCapability(CapabilityProviderFolder.FOLDER_CAP, null);
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
								ItemFolder.remove(stack, 1);
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
	
	public static void addOrCreateMobFolder(EntityPlayer player, ItemStack folder, EntityLivingBase target) {

		if (folder.getItemDamage() == 2) {
			ItemStack newFolder = new ItemStack(RFCItems.folder, 1, 3);
			if (ItemFolder.setObject(newFolder, target)) {
				ItemHandlerHelper.giveItemToPlayer(player, newFolder);
				folder.shrink(1);
				target.setDead();
			}
		}
		else if (folder.getItemDamage() == 3) {
			Object obj = ItemFolder.getObject(folder);
			if (obj != null && obj.equals(target.getClass())) {
				ItemFolder.add(folder, 1);
				dropMobEquips(player.world, target);
				target.setDead();
			}
		}
	}
}
