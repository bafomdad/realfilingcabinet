package com.bafomdad.realfilingcabinet.utils;

import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityFolder;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityProviderFolder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

public class FluidUtils {

	public static boolean doPlace(World world, EntityPlayer player, ItemStack stack, BlockPos pos, EnumFacing facing) {
		
//		if (!NBTUtils.getBoolean(stack, StringLibs.RFC_PLACEMODE, false))
//			return false;
		
		if (!MobUtils.canPlayerChangeStuffHere(world, player, stack, pos, facing))
			return false;
		
		CapabilityFolder cap = FolderUtils.get(stack).getCap();
		if (cap == null) return false;
		
		long count = cap.getCount();
		if (cap.isFluidStack() && count >= Fluid.BUCKET_VOLUME) {
			pos = pos.offset(facing);
			Block hitblock = world.getBlockState(pos).getBlock();
			Fluid fluid = cap.getFluidStack().getFluid();
			int l = world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
			
			if (!hitblock.isReplaceable(world, pos))
				return false;
			
			if (fluid != null && (hitblock != fluid.getBlock() || (hitblock == fluid.getBlock() && l != 0))) {
				if (!player.world.isRemote && !player.capabilities.isCreativeMode)
					cap.setCount(count - Fluid.BUCKET_VOLUME);
				
				world.setBlockState(pos, fluid.getBlock().getDefaultState(), 3);
				return true;
			}
		}
		return false;
	}
	
	public static boolean doDrain(World world, EntityPlayer player, ItemStack stack, BlockPos pos, EnumFacing facing) {
		
//		if (NBTUtils.getBoolean(stack, StringLibs.RFC_PLACEMODE, false))
//			return false;
		
		if (!MobUtils.canPlayerChangeStuffHere(world, player, stack, pos, facing))
			return false;
		
		CapabilityFolder cap = FolderUtils.get(stack).getCap();
		
		Block block = world.getBlockState(pos).getBlock();
		int l = world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
		
		if (block instanceof BlockLiquid && l == 0) {
			if (cap.isFluidStack() && cap.getFluidStack().getFluid().getBlock() == block) {
				if (!world.isRemote) {
					cap.setCount(cap.getCount() + 1000);
					world.setBlockToAir(pos);
				}
				return true;
			}
		}
		else if (block instanceof IFluidBlock && l == 0) {
			Fluid fluid = ((IFluidBlock)block).getFluid();
			if (cap.isFluidStack() && cap.getFluidStack().getFluid() == fluid) {
				if (!world.isRemote) {
					cap.setCount(cap.getCount() + 1000);
					world.setBlockToAir(pos);
				}
				return true;
			}
		}
		return false;
	}
	
	public static boolean canAcceptFluidContainer(ItemStack stack) {
		
		return FluidUtil.getFluidContained(stack) != null;
	}
	
	public static FluidStack getFluidFromFolder(TileEntityRFC tile, int slot) {
		
		ItemStack stack = tile.getInventory().getFolder(slot);
		CapabilityFolder cap = FolderUtils.get(stack).getCap();
		if (cap != null && cap.isFluidStack()) {
			int count = (int)Math.min(Integer.MAX_VALUE, cap.getCount());
			FluidStack fluid = cap.getFluidStack();
			fluid.amount = count;
			return fluid;
		}
		return null;
	}
}
