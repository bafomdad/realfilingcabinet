package com.bafomdad.realfilingcabinet.blocks;

import javax.annotation.Nullable;

import thaumcraft.api.aspects.IEssentiaContainerItem;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.api.IFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityAC;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.utils.AspectStorageUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockAC extends Block implements IFilingCabinet {
	
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	
	public BlockAC() {
		
		super(Material.WOOD);
		setRegistryName("aspectcabinet");
		setTranslationKey(RealFilingCabinet.MOD_ID + ".aspectcabinet");
		setHardness(2.0F);
		setResistance(1000.0F);
		setCreativeTab(TabRFC.instance);
		GameRegistry.registerTileEntity(TileEntityAC.class, "tileAspectCabinet");
		
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}
	
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
    
    @Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		
		TileEntity tile = world.getTileEntity(pos);
		if (!world.isRemote && !player.capabilities.isCreativeMode) {
			if (tile instanceof TileEntityAC)
				leftClick(tile, player);
		}
	}
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    	
    	if (hand == EnumHand.MAIN_HAND) {
    		TileEntity tile = world.getTileEntity(pos);
    		if (tile instanceof TileEntityAC)
    			rightClick(tile, player, facing, hitX, hitY, hitZ);
    		
    		return true;
    	}
    	return false;
    }
    
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
		
		world.setBlockState(pos, state.withProperty(FACING, entity.getHorizontalFacing().getOpposite()), 2);
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityAC) {
			if (stack.hasTagCompound())
				((TileEntityAC)tile).readInv(stack.getTagCompound());
		}
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
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
    	
    	if (player.capabilities.isCreativeMode && !world.isRemote) {
    		this.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getActiveItemStack());
    	}
    	return super.removedByPlayer(state, world, pos, player, willHarvest);
    }
	
	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack) {
		
		if (tile instanceof TileEntityAC) {
			ItemStack s = new ItemStack(this);
			NBTTagCompound tag = new NBTTagCompound();
			((TileEntityAC)tile).writeInv(tag, true);
			if (!tag.isEmpty()) {
				s.setTagCompound(tag);
				world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), s));
				return;
			}
		}
		super.harvestBlock(world, player, pos, state, tile, stack);
	}

	@Override
	public void leftClick(TileEntity tile, EntityPlayer player) {
		
		TileEntityAC tileAC = (TileEntityAC)tile;
		AspectStorageUtils.extractAspect(tileAC, player, player.isSneaking());
	}

	@Override
	public void rightClick(TileEntity tile, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {

		TileEntityAC tileAC = (TileEntityAC)tile;
		ItemStack stack = player.getHeldItemMainhand();
		
		if (!player.isSneaking() && !stack.isEmpty()) {
			if (stack.getItem() == RFCItems.aspectFolder && tileAC.isOpen) {
				if (!tileAC.getWorld().isRemote) {
					for (int i = 0; i < tileAC.getInv().getSlots(); i++) {
						ItemStack tileStack = tileAC.getInv().getStackInSlot(i);
						if (tileStack.isEmpty()) {
							tileAC.getInv().setStackInSlot(i, stack);
							player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
							tileAC.markBlockForUpdate();
							return;
						}
					}
				}
			}
			else if (stack.getItem() instanceof IEssentiaContainerItem && !tileAC.isOpen) {
				if (!tileAC.getWorld().isRemote) {
					AspectStorageUtils.addAspect(tileAC, player, stack);
				}
			}
		}
		if (!player.isSneaking() && stack.isEmpty()) {
			if (!tileAC.getWorld().isRemote) {
				if (tileAC.isOpen)
					tileAC.isOpen = false;
				else
					tileAC.isOpen = true;
				tileAC.markDirty();
			}
			tileAC.markBlockForUpdate();
		}
		if (player.isSneaking() && stack.isEmpty() && tileAC.isOpen) {
			for (int i = tileAC.getInv().getSlots() - 1; i >= 0; i--) {
				ItemStack folder = tileAC.getInv().getStackInSlot(i);
				if (!folder.isEmpty()) {
					tileAC.getInv().setStackInSlot(i, ItemStack.EMPTY);
					player.setHeldItem(EnumHand.MAIN_HAND, folder);
					tileAC.markBlockForUpdate();
					break;
				}
			}
		}
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
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {

		return new TileEntityAC();
	}
}
