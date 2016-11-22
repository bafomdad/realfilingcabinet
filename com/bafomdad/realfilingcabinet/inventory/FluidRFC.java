package com.bafomdad.realfilingcabinet.inventory;

import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.utils.FluidUtils;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class FluidRFC implements IFluidHandler {
	
	TileEntityRFC tile;
	
	public FluidRFC(TileEntityRFC tile) {
		
		this.tile = tile;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		
		return null;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {

		if (resource != null)
		{
			for (int i = 0; i < tile.getInventory().getSlots(); i++) {
				FluidStack loopfluid = FluidUtils.getFluidFromFolder(tile, i);
				if (loopfluid != null && loopfluid.getLocalizedName().equals(resource.getLocalizedName()))
				{
					if (doFill) {
						ItemFolder.add(tile.getInventory().getTrueStackInSlot(i), resource.amount);
					}
					return resource.amount;
				}
				else if (loopfluid != null && loopfluid.getLocalizedName().equals(resource.getLocalizedName()))
					return 0;
			}
		}
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {

		if (resource == null)
			return null;
		
		return this.drain(resource.amount, doDrain);
//		if (resource != null)
//		{
//			for (int i = 0; i < tile.getInventory().getSlots(); i++) {
//				FluidStack loopfluid = FluidUtils.getFluidFromFolder(tile, i);
//				if (loopfluid != null && loopfluid.getLocalizedName().equals(resource.getLocalizedName()))
//				{
//					if (loopfluid.amount > 0)
//					{
//						if (doDrain)
//							ItemFolder.remove(tile.getInventory().getTrueStackInSlot(i), resource.amount);
//
//						return loopfluid;
//					}
//				}
//			}
//		}
//		return null;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {

		ItemStack stack = tile.getFilter();
		if (tile.hasItemFrame() && stack == ItemStack.field_190927_a)
			return null;
		
		else if (tile.hasItemFrame() && stack != ItemStack.field_190927_a)
		{
			FluidStack fluid = FluidUtil.getFluidContained(stack);
			if (fluid == null)
				return null;
			
			for (int i = 0; i < tile.getInventory().getSlots(); i++) {
				FluidStack loopfluid = FluidUtils.getFluidFromFolder(tile, i);
				if (loopfluid != null && loopfluid.isFluidEqual(fluid))
				{
					FluidTank tank = new FluidTank(loopfluid, loopfluid.amount);
					FluidStack f = tank.drain(maxDrain, doDrain);
					if (f != null && f.amount > 0)
					{
						System.out.println("Draining: " + f.amount);
//						ItemFolder.remove(tile.getInventory().getTrueStackInSlot(i), f.amount);
					}
					return f;
//					if (loopfluid.amount > 0)
//					{
//						ItemFolder.remove(tile.getInventory().getTrueStackInSlot(i), loopfluid.amount);
//						
//						return loopfluid.copy();
//					}
				}
			}
		}
		return null;
	}
}
