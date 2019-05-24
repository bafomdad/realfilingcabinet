package com.bafomdad.realfilingcabinet.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bafomdad.realfilingcabinet.api.IBlockCabinet;
import com.bafomdad.realfilingcabinet.api.ILockableCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;

public abstract class BlockRFC extends Block implements IBlockCabinet {
	
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	
	public BlockRFC(Material mat) {
		
		super(mat);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}
	
	public abstract void leftClick(TileEntity tile, EntityPlayer player);
	
	public abstract void rightClick(TileEntity tile, EntityPlayer player, EnumFacing side);
	
	@Override
	protected BlockStateContainer createBlockState() {
		
		return new BlockStateContainer(this, FACING);
	}
	
    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
    	
    	if (side == EnumFacing.DOWN || side.getIndex() == state.getValue(FACING).getIndex()) {
    		return false;
    	}
    	return true;
    }
    
    private boolean isLocked(TileEntity tile, EntityPlayer player) {
    	
    	if (!(tile instanceof ILockableCabinet)) return false;
		ILockableCabinet cabinet = (ILockableCabinet)tile;
		if (cabinet.isCabinetLocked()) {
			if (!cabinet.getOwner().equals(player.getUniqueID())) {
				if (!cabinet.hasKeyCopy(player, cabinet.getOwner())) return true;
			}
		}
    	return false;
    }
	
	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		
		TileEntity tile = world.getTileEntity(pos);
		if (!isLocked(tile, player) && !world.isRemote && !player.capabilities.isCreativeMode)
			leftClick(tile, player);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		
		TileEntity tile = world.getTileEntity(pos);
		if (hand == EnumHand.MAIN_HAND && !isLocked(tile, player)) {
			rightClick(tile, player, facing);
			return true;
		}
		return false;
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		
		NonNullList<ItemStack> list = NonNullList.create();
		this.getDrops(list, world, pos, state, 0);
		return list.get(0);
	}
	
	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {}
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		
		TileEntityRFC tile = (TileEntityRFC)world.getTileEntity(pos);
		if (this.isLocked(tile, player))
			return false;
		
		if (willHarvest) {
			onBlockHarvested(world, pos, state, player);
			return true;
		} else {
			return super.removedByPlayer(state, world, pos, player, willHarvest);
		}
	}
	
	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack) {
		
		super.harvestBlock(world, player, pos, state, tile, stack);
		world.setBlockToAir(pos);
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> list, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		
		super.getDrops(list, world, pos, state, fortune);
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null) {
			((TileEntityRFC)tile).getDrops(list);
		}
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
		
		world.setBlockState(pos, state.withProperty(FACING, entity.getHorizontalFacing().getOpposite()), 2);
		((TileEntityRFC)world.getTileEntity(pos)).onPlaced(world, pos, state, stack);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		
		return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		
		return false;
	}
	
	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		
		return false;
	}
	
	@Override
    public boolean isFullCube(IBlockState state) {
		
        return false;
    }
	
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
		
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
