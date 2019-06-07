package com.bafomdad.realfilingcabinet.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IFolder {

	public ItemStack getEmptyFolder(ItemStack stack);
	
	public default void setAdditionalData(ItemStack folder, Object toSet) {}
	
	public default EnumActionResult placeObject(ItemStack folder, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		
		return EnumActionResult.PASS;
	}
	
	public Object insertIntoFolder(ItemStack folder, Object toInsert, boolean simulate, boolean oreDict);
	
	public Object extractFromFolder(ItemStack folder, long amount, boolean simulate, boolean creative);
}
