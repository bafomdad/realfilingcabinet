package com.bafomdad.realfilingcabinet.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.api.common.IFilingCabinet;
import com.bafomdad.realfilingcabinet.api.common.IFolder;
import com.bafomdad.realfilingcabinet.api.common.IUpgrades;
import com.bafomdad.realfilingcabinet.api.helper.ResourceUpgradeHelper;
import com.bafomdad.realfilingcabinet.api.helper.UpgradeHelper;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemKeys;
import com.bafomdad.realfilingcabinet.utils.AutocraftingUtils;
import com.bafomdad.realfilingcabinet.utils.EnderUtils;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;
import com.bafomdad.realfilingcabinet.utils.StorageUtils;

public class BlockRFC extends Block implements IFilingCabinet {
	
	static float f = 0.0625F;
	protected static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(0.0D + f, 0.0D, 0.0D + f, 1.0D - f, 1.0D - f, 1.0D - f);

	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public BlockRFC() {
		
		super(Material.IRON);
		setRegistryName("modelcabinet");
		setUnlocalizedName(RealFilingCabinet.MOD_ID + ".filingcabinet");
		setHardness(5.0F);
		setResistance(1000.0F);
		setCreativeTab(TabRFC.instance);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlockRFC(this), getRegistryName());
		GameRegistry.registerTileEntity(TileEntityRFC.class, "tileFilingCabinet");
		
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		
		return new BlockStateContainer(this, FACING);
	}
	
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB aabb, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity) {
        
    	addCollisionBoxToList(pos, aabb, collidingBoxes, BASE_AABB);
    }
	
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
    	
    	entityCollisionInteraction(world, pos, state, entity);
    }
	
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		
		TileEntity tile = world.getTileEntity(pos);
		if (!world.isRemote && !player.capabilities.isCreativeMode)
		{
			if (tile != null && tile instanceof TileEntityRFC)
				leftClick(tile, player);
		}
	}
	
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
    	TileEntity tile = world.getTileEntity(pos);
    	if (tile != null && tile instanceof TileEntityRFC)
    		rightClick(tile, player);
    	
        return true;
    }
    
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    	
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }
	
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
		
		world.setBlockState(pos, state.withProperty(FACING, getFacingFromEntity(pos, entity)), 2);
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityRFC)
		{
			if (stack.hasTagCompound())
				((TileEntityRFC)tile).readInv(stack.getTagCompound());
		}
	}
	
	public static EnumFacing getFacingFromEntity(BlockPos pos, EntityLivingBase entity) {
		
		return EnumFacing.getFacingFromVector((float)(entity.posX - pos.getX()), (float)(entity.posY - pos.getY()), (float)(entity.posZ - pos.getZ()));
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		
		return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		
		return state.getValue(FACING).getHorizontalIndex();
	}
	
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
    	
    	if (player.capabilities.isCreativeMode && !world.isRemote) {
    		this.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getActiveItemStack());
    	}
    	return super.removedByPlayer(state, world, pos, player, willHarvest);
    }
	
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack) {
		
		if (tile instanceof TileEntityRFC)
		{
			ItemStack s = new ItemStack(this);
			NBTTagCompound tag = new NBTTagCompound();
			((TileEntityRFC)tile).writeInv(tag, true);
			if (tile.getTileData().hasKey(StringLibs.RFC_UPGRADE))
			{
				ItemStack upgrade = UpgradeHelper.stackTest(tile);
				if (upgrade != null && upgrade.stackSize == 0)
					upgrade.stackSize = 1;
				world.spawnEntityInWorld(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), upgrade));
			}
			if (!tag.hasNoTags())
			{
				s.setTagCompound(tag);
				world.spawnEntityInWorld(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), s));
				return;
			}
		}
		super.harvestBlock(world, player, pos, state, tile, stack);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {

		return new TileEntityRFC();
	}

	@Override
	public void leftClick(TileEntity tile, EntityPlayer player) {
		
		if (player.capabilities.isCreativeMode)
			return;
		
		TileEntityRFC tileRFC = (TileEntityRFC)tile;
		
		if (tileRFC.isCabinetLocked()) {
			if (!tileRFC.getOwner().equals(player.getUniqueID()))
			{
				if (!tileRFC.hasKeyCopy(player, tileRFC.getOwner()))
				{
					return;
				}
			}
		}
		if (player.isSneaking() && player.getHeldItem(EnumHand.MAIN_HAND) != null && player.getHeldItem(EnumHand.MAIN_HAND).getItem() == RFCItems.magnifyingGlass)
		{
			if (!tileRFC.getWorld().isRemote) {
				UpgradeHelper.removeUpgrade(player, tileRFC);
			}
			//TODO: update cabinet model texture when on a server
			return;
		}
		if (UpgradeHelper.getUpgrade(tileRFC, StringLibs.TAG_CRAFT) == null)
		{
			if (player.isSneaking()) {
				StorageUtils.extractStackManually(tileRFC, player, true);
			}
			else
				StorageUtils.extractStackManually(tileRFC, player, false);
		}
		else
		{
			if (AutocraftingUtils.canCraft(tileRFC.getFilter(), tileRFC))
			{
				ItemStack stack = tileRFC.getFilter();
				stack.stackSize = AutocraftingUtils.getOutputSize();
				if (!UpgradeHelper.isCreative(tileRFC))
					AutocraftingUtils.doCraft(tileRFC.getFilter(), tileRFC.getInventory());
				if (!player.inventory.addItemStackToInventory(stack))
					player.dropItem(stack.getItem(), 1);
			}
		}
	}

	@Override
	public void rightClick(TileEntity tile, EntityPlayer player) {

		TileEntityRFC tileRFC = (TileEntityRFC)tile;
		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
		
		if (tileRFC.isCabinetLocked()) {
			if (!tileRFC.getOwner().equals(player.getUniqueID()))
			{
				if (!tileRFC.hasKeyCopy(player, tileRFC.getOwner()))
				{
					return;
				}
			}
		}
		if (tileRFC.calcLastClick(player))
		{
			StorageUtils.addAllStacksManually(tileRFC, player);
		}
		if (!player.isSneaking() && stack != null)
		{
			if (stack.getItem() instanceof ItemKeys)
			{
				if (!tileRFC.isCabinetLocked()) {
					if (stack.getItemDamage() == 0)
						tileRFC.setOwner(player.getUniqueID());
				}
				else {
					if (tileRFC.getOwner().equals(player.getUniqueID()) && stack.getItemDamage() == 0) {
						tileRFC.setOwner(null);
						return;
					}
					if (tileRFC.getOwner().equals(player.getUniqueID()) && stack.getItemDamage() == 1) {
						if (!stack.hasTagCompound() || (stack.hasTagCompound() && !stack.getTagCompound().hasKey(StringLibs.RFC_COPY))) {
							NBTUtils.setString(stack, StringLibs.RFC_COPY, player.getUniqueID().toString());
							NBTUtils.setString(stack, StringLibs.RFC_FALLBACK, player.getDisplayNameString());
						}
					}
				}
				return;
			}
			if (stack.getItem() instanceof IFolder && tileRFC.isOpen)
			{
				if (stack.getItemDamage() == 1 && UpgradeHelper.getUpgrade(tileRFC, StringLibs.TAG_ENDER) != null)
				{
					player.setHeldItem(EnumHand.MAIN_HAND, null);
//					if (!stack.getTagCompound().hasKey(StringLibs.RFC_SLOTINDEX))
//						return;
//					
//					NBTTagCompound tagPos = NBTUtils.getCompound(stack, StringLibs.RFC_TILEPOS, true);
//					if (tagPos != null)
//					{
//						int xLoc = tagPos.getInteger("X");
//						int yLoc = tagPos.getInteger("Y");
//						int zLoc = tagPos.getInteger("Z");
//						
//						int dim = NBTUtils.getInt(stack, StringLibs.RFC_DIM, 0);
//						BlockPos pos = new BlockPos(xLoc, yLoc, zLoc);
//						if (pos.equals(tileRFC.getPos()) && dim == tileRFC.getWorld().provider.getDimension())
//							player.setHeldItem(EnumHand.MAIN_HAND, null);
//					}
				}
				else if (stack.getItemDamage() != 1 && !tileRFC.getWorld().isRemote)
				{
					for (int i = 0; i < tileRFC.getInventory().getSlots(); i++)
					{
						ItemStack tileStack = tileRFC.getInventory().getTrueStackInSlot(i);
						if (tileStack == null)
						{
							tileRFC.getInventory().setStackInSlot(i, stack);
							player.setHeldItem(EnumHand.MAIN_HAND, null);
							tileRFC.markBlockForUpdate();
							break;
						}
					}
				}
			}
			if (stack.getItem() instanceof IUpgrades) {
				if (!tileRFC.getWorld().isRemote) {
					UpgradeHelper.setUpgrade(player, tileRFC, stack);
				}
				//TODO: update cabinet model texture when on a server
				return;
			}
			else
			{
				StorageUtils.addStackManually(tileRFC, player, stack);
			}
		}
		if (!player.isSneaking() && stack == null)
		{	
			if (!tileRFC.getWorld().isRemote)
			{
				if (tileRFC.isOpen)
					tileRFC.isOpen = false;
				else
					tileRFC.isOpen = true;
				tileRFC.markDirty();
			}
			tileRFC.markBlockForUpdate();
		}
		if (player.isSneaking() && stack == null && tileRFC.isOpen)
		{		
			if (UpgradeHelper.getUpgrade(tileRFC, StringLibs.TAG_ENDER) != null)
			{
				EnderUtils.extractEnderFolder(tileRFC, player);
				return;
			}
			for (int i = tileRFC.getInventory().getSlots() - 1; i >= 0; i--)
			{
				ItemStack folder = tileRFC.getInventory().getTrueStackInSlot(i);
				if (folder != null)
				{
					tileRFC.getInventory().setStackInSlot(i, null);
					player.setHeldItem(EnumHand.MAIN_HAND, folder);
					tileRFC.markBlockForUpdate();
					break;
				}
			}
		}
	}
	
	@Override
	public void entityCollisionInteraction(World world, BlockPos pos, IBlockState state, Entity entity) {}
	
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
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }
	
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }
	
	@Override
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos) {
		
		TileEntityRFC tileRFC = (TileEntityRFC)world.getTileEntity(pos);
		if (tileRFC != null && tileRFC.isCabinetLocked()) {
			if (!tileRFC.getOwner().equals(player.getUniqueID()))
				return -1.0F;
		}
		return ForgeHooks.blockStrength(state, player, world, pos);
	}
}
