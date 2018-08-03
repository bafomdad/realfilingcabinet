package com.bafomdad.realfilingcabinet.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

import com.bafomdad.realfilingcabinet.entity.EntityCabinet;
import com.bafomdad.realfilingcabinet.helpers.MobUpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.inventory.InventoryEntity;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

public class EntityAISlurp extends EntityAIBase {
	
	private EntityCabinet cabinet;
	private PathNavigate pathFinder;
	private FluidStack targetFluid = null;
	private BlockPos targetPos = BlockPos.ORIGIN;
	private int range = 7;
	
	public EntityAISlurp(EntityCabinet cabinet) {
		
		this.cabinet = cabinet;
		this.pathFinder = cabinet.getNavigator();
		setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {

		if (!pathFinder.noPath() || MobUpgradeHelper.getMobUpgrade(cabinet, StringLibs.TAG_FLUID) == null)
			return false;
		
		if (cabinet.world != null) {
			FluidStack fluid = null;
			for (int x1 = -range; x1 < range; x1++) {
				for (int y1 = -2; y1 < 2; y1++) {
					for (int z1 = -range; z1 < range; z1++) {
						int x = x1 + MathHelper.floor(cabinet.posX);
						int y = y1 + MathHelper.floor(cabinet.posY);
						int z = z1 + MathHelper.floor(cabinet.posZ);
						
						BlockPos pos = new BlockPos(x, y, z);
						Block block = cabinet.world.getBlockState(pos).getBlock();
						int l = cabinet.world.getBlockState(pos).getBlock().getMetaFromState(cabinet.world.getBlockState(pos));
						if (block != null)
						{
							if (block instanceof BlockLiquid && l == 0)
							{
								if (!cabinet.world.isAirBlock(pos.up()))
									continue;
								
								if (block == Blocks.WATER)
								{
									FluidStack stack = new FluidStack(FluidRegistry.WATER, 1000);
									if (getMatchingFluidFolder(stack) != null) {
										fluid = stack;
										targetPos = pos;
										break;
									}
								}
								else if (block == Blocks.LAVA)
								{
									FluidStack stack = new FluidStack(FluidRegistry.LAVA, 1000);
									if (getMatchingFluidFolder(stack) != null) {
										fluid = stack;
										targetPos = pos;
										break;
									}
								}
							}
							else if (block instanceof IFluidBlock && l == 0)
							{
								if (!cabinet.world.isAirBlock(pos.up()))
									continue;
								
								FluidStack stack = new FluidStack(((IFluidBlock)block).getFluid(), 1000);
								if (getMatchingFluidFolder(stack) != null) {
									fluid = stack;
									targetPos = pos;
									break;
								}
							}
						}
					}
				}
			}
			if (fluid != null && targetPos != BlockPos.ORIGIN) {
				targetFluid = fluid;
				cabinet.setYay(true);
				return true;
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
		
		return cabinet.isEntityAlive() && !pathFinder.noPath() && targetFluid != null && MobUpgradeHelper.getMobUpgrade(cabinet, StringLibs.TAG_FLUID) != null;
	}
	
	@Override
	public void startExecuting() {
		
		if (targetFluid != null)
			pathFinder.tryMoveToXYZ(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 0.6F);
	}
	
	@Override
	public void updateTask() {
		
		super.updateTask();
		if (!cabinet.world.isRemote) {
			if (targetFluid != null && cabinet.getDistance(targetPos.getX(), targetPos.getY(), targetPos.getZ()) < 2.0){
				ItemStack folderToPut = getMatchingFluidFolder(targetFluid);
				if (folderToPut != null)
				{
					targetFluid = null;
					cabinet.world.setBlockToAir(targetPos);
					ItemFolder.add(folderToPut, 1000);
				}
			}
		}
	}
	
	private ItemStack getMatchingFluidFolder(FluidStack fluid) {
		
		for (ItemStack folder : cabinet.getInventory().getStacks())
		{
			if (folder != null && folder.getItemDamage() == 4) {
				if (ItemFolder.getObject(folder) != null && fluid.isFluidEqual((FluidStack)ItemFolder.getObject(folder)))
					return folder;
			}
		}
		return null;
	}
}
