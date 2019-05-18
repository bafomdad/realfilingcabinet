package com.bafomdad.realfilingcabinet.entity.ai;

import java.util.Iterator;

import com.bafomdad.realfilingcabinet.entity.EntityCabinet;
import com.bafomdad.realfilingcabinet.helpers.MobUpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

public class EntityAISlurp extends EntityAIBase {
	
	private final EntityCabinet cabinet;
	private final PathNavigate pathFinder;
	private Fluid targetFluid = null;
	private BlockPos targetPos = BlockPos.ORIGIN;
	private int range = 7;
	
	public EntityAISlurp(EntityCabinet cabinet) {
		
		this.cabinet = cabinet;
		this.pathFinder = cabinet.getNavigator();
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		
		if (!pathFinder.noPath() || MobUpgradeHelper.getUpgrade(cabinet, StringLibs.TAG_FLUID).isEmpty()) return false;
		
		if (cabinet.world != null) {
			BlockPos cabinetPos = new BlockPos(MathHelper.floor(cabinet.posX), MathHelper.floor(cabinet.posY), MathHelper.floor(cabinet.posZ));
			Iterable<MutableBlockPos> iterable = BlockPos.getAllInBoxMutable(cabinetPos.add(-range, -2, -range), cabinetPos.add(range, 2, range));
			Iterator iter = iterable.iterator();
			while (iter.hasNext()) {
				BlockPos posit = (BlockPos)iter.next();
				if (cabinet.world.isAirBlock(posit)) {
					IBlockState loopState = cabinet.world.getBlockState(posit);
					Block loopBlock = loopState.getBlock();
					int l = loopBlock.getMetaFromState(loopState);
					if (loopBlock instanceof IFluidBlock && ((IFluidBlock)loopBlock).getFluid() != null && l == 0) {
						if (cabinet.insert(new FluidStack(((IFluidBlock)loopBlock).getFluid(), Fluid.BUCKET_VOLUME), true) != null) {
							targetFluid = ((IFluidBlock)loopBlock).getFluid();
							targetPos = posit;
							return true;
						}
					} else if (loopBlock == Blocks.LAVA && l == 0) {
						if (cabinet.insert(new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME), true) != null) {
							targetFluid = FluidRegistry.LAVA;
							targetPos = posit;
							return true;
						}
					} else if (loopBlock == Blocks.WATER && l == 0) {
						if (cabinet.insert(new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME), true) != null) {
							targetFluid = FluidRegistry.WATER;
							targetPos = posit;
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public void resetTask() {
		
		pathFinder.clearPath();
		cabinet.setYay(false);
	}
	
	@Override
	public boolean shouldContinueExecuting() {
		
		return cabinet.isEntityAlive() && !pathFinder.noPath() && targetFluid != null && !MobUpgradeHelper.getUpgrade(cabinet, StringLibs.TAG_FLUID).isEmpty();
	}
	
	@Override
	public void startExecuting() {
		
		if (targetFluid != null)
			pathFinder.tryMoveToXYZ(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 0.6);
	}
	
	@Override
	public void updateTask() {
		
		super.updateTask();
		if (!cabinet.world.isRemote) {
			if (targetFluid != null && cabinet.getDistance(targetPos.getX(), targetPos.getY(), targetPos.getZ()) < 2.0) {
				if (cabinet.insert(new FluidStack(targetFluid, 1000), false) != null) {
					targetFluid = null;
					cabinet.world.setBlockToAir(targetPos);
				}
			}
		}
	}
}
