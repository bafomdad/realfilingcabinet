package com.bafomdad.realfilingcabinet.utils;

import java.util.ArrayList;
import java.util.List;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

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
					if (!player.worldObj.isRemote) {
						pos = pos.offset(side);
						if ((entity instanceof EntityVillager && !ConfigRFC.randomVillager))
						{
							EntityVillager villager = (EntityVillager)ItemMonsterPlacer.spawnCreature(world, res, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D);
							villager.setProfession(0);
						}
						else
							ItemMonsterPlacer.spawnCreature(world, res, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D);
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
		if (list.isEmpty() || list == null)
			return;
		
		for (int i = 0; i < list.size(); i++) {
			ItemStack toDrop = list.get(i);
			if (toDrop.func_190916_E() == 0)
			{
				toDrop.func_190920_e(1);
				EntityItem ei = new EntityItem(world, entity.posX, entity.posY, entity.posZ, toDrop);
				
				if (!world.isRemote)
					world.spawnEntityInWorld(ei);
			}
		}
	}

	public static List<ItemStack> loopArmor(EntityLivingBase entity) {
		
		List<ItemStack> invList = new ArrayList<ItemStack>();
		
    	EntityEquipmentSlot[] slots = new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET };
    	for (int i = 0; i < slots.length; i++) {
    		ItemStack stack = entity.getItemStackFromSlot(slots[i]);
    		if (stack != ItemStack.field_190927_a)
    		{
    			invList.add(stack);
    		}
    	}
    	return invList;
	}
}
