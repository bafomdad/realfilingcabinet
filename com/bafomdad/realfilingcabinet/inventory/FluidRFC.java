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
	FluidStack snapshot = null;
	
	public FluidRFC(TileEntityRFC tile) {
		
		this.tile = tile;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		
		return null;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {

		if (resource != null && !tile.isCabinetLocked())
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
			}
		}
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {

		if (resource == null || tile.isCabinetLocked())
			return null;
		
		return this.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		
		ItemStack stack = tile.getFilter();
		if (tile.hasItemFrame() && stack == ItemStack.EMPTY)
			return null;
		
		else if (tile.hasItemFrame() && stack != ItemStack.EMPTY)
		{
			FluidStack fluid = FluidUtil.getFluidContained(stack);
			if (fluid == null)
				return null;
			
			for (int i = 0; i < tile.getInventory().getSlots(); i++) {
				FluidStack loopfluid = FluidUtils.getFluidFromFolder(tile, i);
				if (loopfluid != null && loopfluid.isFluidEqual(fluid))
				{
					this.takeFluidSnapshot(loopfluid);
					FluidTank tank = new FluidTank(loopfluid, maxDrain);
					FluidStack f = tank.drain(maxDrain, doDrain);
					if (f != null && f.amount > 0 && doDrain)
					{
						if (snapshot != null && snapshot.isFluidEqual(f) && doDrain) {
							ItemFolder.remove(tile.getInventory().getTrueStackInSlot(i), snapshot.amount - loopfluid.amount);
						}
					}
					return f;
				}
			}
		}
		return null;
	}
	
	private void takeFluidSnapshot(FluidStack fluid) {
		
		snapshot = fluid.copy();
	}
}
