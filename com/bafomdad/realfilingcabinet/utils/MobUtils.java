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
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import com.bafomdad.realfilingcabinet.ConfigRFC;
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
				
				String entityName = ItemFolder.getFileName(stack);
				Entity entity = EntityList.createEntityByIDFromName(entityName, world);
				if (entity != null)
				{
					boolean spawn = false;
					if (!player.worldObj.isRemote) {
						pos = pos.offset(side);
						if ((entity instanceof EntityVillager && !ConfigRFC.randomVillager))
						{
		                    EntityVillager entityliving = (EntityVillager)entity;
		                    entity.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
		                    entityliving.rotationYawHead = entityliving.rotationYaw;
		                    entityliving.renderYawOffset = entityliving.rotationYaw;
		                    entityliving.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entityliving)), (IEntityLivingData)null);
							entityliving.setProfession(0);
		                    world.spawnEntityInWorld(entity);
		                    entityliving.playLivingSound();
		                    
		                    spawn = true;
						}
						else {
							EntityLiving entityliving = (EntityLiving)entity;
		                    entity.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
		                    entityliving.rotationYawHead = entityliving.rotationYaw;
		                    entityliving.renderYawOffset = entityliving.rotationYaw;
		                    entityliving.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entityliving)), (IEntityLivingData)null);
		                    world.spawnEntityInWorld(entity);
		                    entityliving.playLivingSound();
		                    
		                    spawn = true;
						}
					}
					if (spawn) {
						if (!player.capabilities.isCreativeMode)
							ItemFolder.remove(stack, 1);
					}
				}
			}
		}
		return true;
	}
	
	public static void dropMobEquips(World world, EntityLivingBase entity) {
		
		List<ItemStack> list = loopArmor(entity);
//		System.out.println(list);
		if (list.isEmpty() || list == null)
			return;
		
		for (int i = 0; i < list.size(); i++) {
			ItemStack toDrop = list.get(i);
			if (toDrop.stackSize == 0)
				toDrop.stackSize = 1;
			
			EntityItem ei = new EntityItem(world, entity.posX, entity.posY, entity.posZ, toDrop);
			if (!world.isRemote)
				world.spawnEntityInWorld(ei);
		}
	}

	public static List<ItemStack> loopArmor(EntityLivingBase entity) {
		
		List<ItemStack> invList = new ArrayList<ItemStack>();
		
    	EntityEquipmentSlot[] slots = new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET };
    	for (int i = 0; i < slots.length; i++) {
    		ItemStack stack = entity.getItemStackFromSlot(slots[i]);
    		if (stack != null)
    		{
    			invList.add(stack);
    		}
    	}
    	return invList;
	}
}
