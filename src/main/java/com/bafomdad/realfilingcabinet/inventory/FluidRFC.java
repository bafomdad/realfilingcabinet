package com.bafomdad.realfilingcabinet.inventory;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityFolder;
import com.bafomdad.realfilingcabinet.utils.FluidUtils;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;

public class FluidRFC implements IFluidHandler {
	
	TileFilingCabinet tile;
	FluidStack snapshot = null;
	
	public FluidRFC(TileFilingCabinet tile) {
		
		this.tile = tile;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		
		FluidTankProperties[] props = new FluidTankProperties[8];
		for (int i = 0; i < tile.getInventory().getSlots(); i++) {
			ItemStack folder = tile.getInventory().getFolder(i);
			CapabilityFolder cap = FolderUtils.get(folder).getCap();
			if (cap != null && cap.isFluidStack()) {
				props[i] = new FluidTankProperties(cap.getFluidStack(), Math.max(Integer.MAX_VALUE - 1, (int)cap.getCount()));
			}
			else
				props[i] = (FluidTankProperties)EmptyFluidHandler.EMPTY_TANK_PROPERTIES;
		}
		return props;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {

		if (resource != null && !tile.isCabinetLocked()) {
			for (int i = 0; i < tile.getInventory().getSlots(); i++) {
				Object obj = FolderUtils.get(tile.getInventory().getFolder(i)).insert(resource, doFill);
				if (obj instanceof FluidStack) {
					return ((FluidStack)obj).amount;
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
		if (stack.isEmpty()) return null;
		
		FluidStack fluid = FluidUtil.getFluidContained(stack);
		if (fluid == null) return null;
		
		for (int i = 0; i < tile.getInventory().getSlots(); i++) {
			CapabilityFolder cap = FolderUtils.get(tile.getInventory().getFolder(i)).getCap();
			if (cap != null && cap.isFluidStack()) {
				FluidStack loopFluid = cap.getFluidStack();
				if (loopFluid.isFluidEqual(fluid)) {
					loopFluid.amount = (int)Math.min(Integer.MAX_VALUE - 1, cap.getCount());
					this.takeFluidSnapshot(loopFluid);
					FluidTank tank = new FluidTank(loopFluid, maxDrain);
					FluidStack f = tank.drain(maxDrain, doDrain);
					if (f != null && f.amount > 0 && doDrain) {
						if (snapshot != null && snapshot.isFluidEqual(f) && doDrain && !UpgradeHelper.isCreative(tile)) {
							if (snapshot.getFluid() == FluidRegistry.WATER && snapshot.amount >= 3000 && ConfigRFC.infiniteWaterSource)
								return f;
							
							FolderUtils.get(tile.getInventory().getFolder(i)).remove(snapshot.amount - loopFluid.amount);
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
