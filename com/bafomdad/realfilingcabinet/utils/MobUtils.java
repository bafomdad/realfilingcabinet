package com.bafomdad.realfilingcabinet.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

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
		
		if (ItemFolder.getFileSize(stack) > 0)
		{
			if (player.canPlayerEdit(pos.offset(side), side, stack)) {
				
				ResourceLocation res = new ResourceLocation(ItemFolder.getFileName(stack));
				Entity entity = EntityList.createEntityByIDFromName(res, world);
				if (entity != null)
				{
					boolean spawn = false;
					if (!player.world.isRemote) {
						pos = pos.offset(side);
						if ((entity instanceof EntityVillager && !ConfigRFC.randomVillager))
						{
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
		if (list.isEmpty() || list == null)
			return;
		
		for (int i = 0; i < list.size(); i++) {
			ItemStack toDrop = list.get(i);
			if (toDrop.getCount() == 0)
			{
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
    		if (stack != ItemStack.EMPTY)
    		{
    			invList.add(stack);
    		}
    	}
    	return invList;
	}
	
	public static void addOrCreateMobFolder(EntityPlayer player, ItemStack folder, EntityLivingBase target) {
		
		if (folder.getItemDamage() == 2) {
			ItemStack newFolder = new ItemStack(RFCItems.folder, 1, 3);
			if (ItemFolder.setObject(newFolder, target)) {
				if (!player.inventory.addItemStackToInventory(newFolder))
					player.dropItem(newFolder, true);
				folder.shrink(1);
				target.setDead();
			}
		}
		else if (folder.getItemDamage() == 3) {
			if (ItemFolder.getObject(folder) != null) {
				ResourceLocation res = EntityList.getKey(target);
				if (ItemFolder.getObject(folder).equals(res.toString()))
				{
					MobUtils.dropMobEquips(player.world, target);
					target.setDead();
				}
			}
		}
	}
}
