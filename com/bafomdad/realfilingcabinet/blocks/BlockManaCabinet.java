package com.bafomdad.realfilingcabinet.blocks;

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
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.mana.IManaItem;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.api.IFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileManaCabinet;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCIntegration;
import com.bafomdad.realfilingcabinet.integration.BotaniaRFC;
import com.bafomdad.realfilingcabinet.items.ItemKeys;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;

public class BlockManaCabinet extends Block implements IFilingCabinet {
	
	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockManaCabinet() {
		
		super(Material.ROCK);
		setRegistryName("manacabinet");
		setTranslationKey(RealFilingCabinet.MOD_ID + ".manacabinet");
		setHardness(5.0F);
		setResistance(1000.0F);
		setCreativeTab(TabRFC.instance);
		
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
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    	
    	if (hand == EnumHand.MAIN_HAND) {
        	TileEntity tile = world.getTileEntity(pos);
        	if (tile != null && tile instanceof TileManaCabinet)
        		rightClick(tile, player, facing, hitX, hitY, hitZ);
        	
            return true;
    	}
    	return false;
    }
    
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
		
		world.setBlockState(pos, state.withProperty(FACING, entity.getHorizontalFacing().getOpposite()), 2);
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileManaCabinet)
		{
			if (stack.hasTagCompound())
				((TileManaCabinet)tile).readInv(stack.getTagCompound());
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
    	return willHarvest || super.removedByPlayer(state, world, pos, player, false);
    }
	
	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack) {
		
		super.harvestBlock(world, player, pos, state, tile, stack);
		world.setBlockToAir(pos);
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		
		ItemStack s = new ItemStack(this);
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof TileManaCabinet)) return;
		
		NBTTagCompound tag = new NBTTagCompound();
		((TileManaCabinet)tile).writeInv(tag, true);
		if (!tag.isEmpty()) {
			s.setTagCompound(tag);
			drops.add(s);
			return;
		}
	}

	@Override
	public void leftClick(TileEntity tile, EntityPlayer player) {}

	@Override
	public void rightClick(TileEntity tile, EntityPlayer player, EnumFacing side, float hitX, float hity, float hitZ) {

		TileManaCabinet tileMana = (TileManaCabinet)tile;
		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
		
		if (tileMana.isCabinetLocked()) {
			if (!tileMana.getCabinetOwner().equals(player.getUniqueID()) && !tileMana.hasKeyCopy(player, tileMana.getCabinetOwner()))
				return;
		}
		if (!player.isSneaking() && !stack.isEmpty())
		{
			if (stack.getItem() instanceof ItemKeys)
			{
				if (!tileMana.isCabinetLocked()) {
					if (stack.getItemDamage() == 0)
						tileMana.setOwner(player.getUniqueID());
				}
				else {
					if (tileMana.getCabinetOwner().equals(player.getUniqueID()) && stack.getItemDamage() == 0) {
						tileMana.setOwner(null);
						return;
					}
					if (tileMana.getCabinetOwner().equals(player.getUniqueID()) && stack.getItemDamage() == 1) {
						if (!stack.hasTagCompound() || (stack.hasTagCompound() && !stack.getTagCompound().hasKey(StringLibs.RFC_COPY))) {
							NBTUtils.setString(stack, StringLibs.RFC_COPY, player.getUniqueID().toString());
							NBTUtils.setString(stack, StringLibs.RFC_FALLBACK, player.getDisplayNameString());
						}
					}
				}
				return;
			}
			if (stack.getItem() == BotaniaRFC.manaFolder && tileMana.isOpen) {
				for (int i = 0; i < tileMana.getInv().getSlots(); i++) {
					ItemStack tileStack = tileMana.getInv().getStackInSlot(i);
					if (tileStack.isEmpty()) {
						tileMana.getInv().setStackInSlot(i, stack);
						player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
						tileMana.markBlockForRenderUpdate();
						break;
					}
				}
				return;
			}
		}
		if (!player.isSneaking() && stack.isEmpty())
		{
			if (!tileMana.getWorld().isRemote)
			{
				if (tileMana.isOpen)
					tileMana.isOpen = false;
				else
					tileMana.isOpen = true;
				tileMana.markDirty();
			}
			tileMana.markBlockForUpdate();
		}
		if (player.isSneaking() && stack.isEmpty() && tileMana.isOpen)
		{
			for (int i = tileMana.getInv().getSlots() - 1; i >= 0; i--)
			{
				ItemStack folder = tileMana.getInv().getStackInSlot(i);
				if (!folder.isEmpty())
				{
					tileMana.getInv().setStackInSlot(i, ItemStack.EMPTY);
					player.setHeldItem(EnumHand.MAIN_HAND, folder);
					tileMana.markBlockForUpdate();
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
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }
	
	@Override
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos) {
		
		TileManaCabinet tileMana = (TileManaCabinet)world.getTileEntity(pos);
		if (tileMana != null && tileMana.isCabinetLocked()) {
			if (!tileMana.getCabinetOwner().equals(player.getUniqueID()))
				return -1.0F;
		}
		return ForgeHooks.blockStrength(state, player, world, pos);
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {

		return new TileManaCabinet();
	}
}
