package com.bafomdad.realfilingcabinet.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.api.IFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.api.IUpgrades;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.ResourceUpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.items.ItemKeys;
import com.bafomdad.realfilingcabinet.utils.*;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;

@Optional.Interface(iface = "com.jaquadro.minecraft.storagedrawers.api.storage.INetworked", modid = RealFilingCabinet.STORAGEDRAWERS, striprefs = true)
public class BlockRFC extends Block implements IFilingCabinet, INetworked {
	
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
		
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		
		return new BlockStateContainer(this, FACING);
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB aabb, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean wat) {
        
    	addCollisionBoxToList(pos, aabb, collidingBoxes, BASE_AABB);
    }
	
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
    	
    	TileEntityRFC tileRFC = (TileEntityRFC)world.getTileEntity(pos);
    	if (UpgradeHelper.getUpgrade(tileRFC, StringLibs.TAG_MOB) == null)
    		return;
    	
    	if (!(entity instanceof EntityLivingBase) || entity instanceof EntityPlayer)
    		return;
    	
    	EntityLivingBase elb = (EntityLivingBase)entity;
    	if (!elb.isNonBoss() || (elb.isChild() && !(elb instanceof EntityZombie)))
    		return;
    	
    	ResourceLocation res = EntityList.getKey(elb);
    	for (int i = 0; i < tileRFC.getInventory().getSlots(); i++) {
    		ItemStack folder = tileRFC.getInventory().getTrueStackInSlot(i);
    		if (!folder.isEmpty() && folder.getItem() == RFCItems.folder) {
    			if (folder.getItemDamage() == 3 && ItemFolder.getObject(folder) != null)
    			{
    				if (ItemFolder.getObject(folder).equals(res.toString())) {
    					MobUtils.dropMobEquips(world, elb);
    					elb.setDead();
    					ItemFolder.add(folder, 1);
    					break;
    				}
    			}
    		}
    	}
    }
	
    @Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		
		TileEntity tile = world.getTileEntity(pos);
		if (!world.isRemote && !player.capabilities.isCreativeMode) {
			if (tile instanceof TileEntityRFC)
				leftClick(tile, player);
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    	
		if (hand == EnumHand.MAIN_HAND) {
			TileEntity tile = world.getTileEntity(pos);
	    	if (tile instanceof TileEntityRFC)
	    		rightClick(tile, player, facing, hitX, hitY, hitZ);
	    	
	    	return true;
		}
        return false;
    }
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
		
		world.setBlockState(pos, state.withProperty(FACING, entity.getHorizontalFacing().getOpposite()), 2);
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityRFC) {
			if (stack.hasTagCompound())
				((TileEntityRFC)tile).readInv(stack.getTagCompound());
		}
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		
		return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		
		return state.getValue(FACING).getHorizontalIndex();
	}
	
	@Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
    	
    	if (player.capabilities.isCreativeMode && !world.isRemote)
    		this.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getActiveItemStack());

    	return willHarvest || super.removedByPlayer(state, world, pos, player, false);
    }
	
	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack) {

		super.harvestBlock(world, player, pos, state, tile, stack);
		world.setBlockToAir(pos);
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityRFC) {
			ItemStack upgrade = UpgradeHelper.stackTest((TileEntityRFC)tile);
			if (!upgrade.isEmpty()) {
				if (upgrade.getCount() == 0)
					upgrade.setCount(1);
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), upgrade);
			}
		}
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		
		ItemStack s = new ItemStack(this);
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof TileEntityRFC)) return;
		
		NBTTagCompound tag = new NBTTagCompound();
		((TileEntityRFC)tile).writeInv(tag, true);
		if (!tag.hasNoTags()) {
			s.setTagCompound(tag);
		}
		drops.add(s);
		return;
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
			if (!tileRFC.getCabinetOwner().equals(player.getUniqueID())) {
				if (!tileRFC.hasKeyCopy(player, tileRFC.getCabinetOwner()))
					return;
			}
		}
		if (player.isSneaking() && !player.getHeldItem(EnumHand.MAIN_HAND).isEmpty() && player.getHeldItem(EnumHand.MAIN_HAND).getItem() == RFCItems.magnifyingGlass) {
			if (!tileRFC.getWorld().isRemote) {
				UpgradeHelper.removeUpgrade(player, tileRFC);
			}
			tileRFC.markBlockForUpdate();
			return;
		}
		if (UpgradeHelper.getUpgrade(tileRFC, StringLibs.TAG_CRAFT) == null) {
			StorageUtils.extractStackManually(tileRFC, player, player.isSneaking());
			return;
		} else {
			ItemStack toCraft = tileRFC.getFilter().copy();
			if (!tileRFC.getFilter().isEmpty() && toCraft.isItemDamaged())
				toCraft.setItemDamage(0);
				
			if (AutocraftingUtils.canCraft(tileRFC.getFilter(), tileRFC)) {
				ItemStack stack = toCraft;
				stack.setCount(AutocraftingUtils.getOutputSize());
				if (!UpgradeHelper.isCreative(tileRFC))
					AutocraftingUtils.doCraft(tileRFC.getFilter(), tileRFC.getInventory());
				if (!player.inventory.addItemStackToInventory(stack))
					player.dropItem(stack.getItem(), AutocraftingUtils.getOutputSize());
			}
		}
	}

	@Override
	public void rightClick(TileEntity tile, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {

		TileEntityRFC tileRFC = (TileEntityRFC)tile;
		ItemStack stack = player.getHeldItemMainhand();
		
		if (tileRFC.isCabinetLocked()) {
			if (!tileRFC.getCabinetOwner().equals(player.getUniqueID())) {
				if (!tileRFC.hasKeyCopy(player, tileRFC.getCabinetOwner()))
					return;
			}
		}
		if (tileRFC.calcLastClick(player))
			StorageUtils.addAllStacksManually(tileRFC, player);

		if (!player.isSneaking() && !stack.isEmpty()) {
			if (stack.getItem() instanceof ItemKeys) {
				if (!tileRFC.isCabinetLocked()) {
					if (stack.getItemDamage() == 0)
						tileRFC.setOwner(player.getUniqueID());
				}
				else {
					if (tileRFC.getCabinetOwner().equals(player.getUniqueID()) && stack.getItemDamage() == 0) {
						tileRFC.setOwner(null);
						return;
					}
					if (tileRFC.getCabinetOwner().equals(player.getUniqueID()) && stack.getItemDamage() == 1) {
						if (!stack.hasTagCompound() || (stack.hasTagCompound() && !stack.getTagCompound().hasKey(StringLibs.RFC_COPY))) {
							NBTUtils.setString(stack, StringLibs.RFC_COPY, player.getUniqueID().toString());
							NBTUtils.setString(stack, StringLibs.RFC_FALLBACK, player.getDisplayNameString());
						}
					}
				}
				return;
			}
			if (stack.getItem() instanceof IFolder && tileRFC.isOpen) {
				if (UpgradeHelper.getUpgrade(tileRFC, StringLibs.TAG_ENDER) != null) {
					if (stack.getItemDamage() == 1)
						player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
					return;
				}
				if (stack.getItemDamage() == 4 && UpgradeHelper.getUpgrade(tileRFC, StringLibs.TAG_FLUID) != null) {
					for (int i = 0; i < tileRFC.getInventory().getSlots(); i++) {
						ItemStack tileStack = tileRFC.getInventory().getTrueStackInSlot(i);
						if (tileStack.isEmpty()) {
							tileRFC.getInventory().setStackInSlot(i, stack);
							player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
							tileRFC.markBlockForUpdate();
							break;
						}
					}
					return;
				}
				else if (stack.getItemDamage() != 1 && !tileRFC.getWorld().isRemote) {
					if (UpgradeHelper.getUpgrade(tileRFC, StringLibs.TAG_FLUID) != null && !FluidUtils.canAcceptFluidContainer(stack))
						return;

					for (int i = 0; i < tileRFC.getInventory().getSlots(); i++) {
						ItemStack tileStack = tileRFC.getInventory().getTrueStackInSlot(i);
						if (tileStack.isEmpty()) {
							tileRFC.getInventory().setStackInSlot(i, stack);
							player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
							tileRFC.markBlockForUpdate();
							break;
						}
					}
				}
				return;
			}
			if (stack.getItem() instanceof IUpgrades) {
				if (!tileRFC.getWorld().isRemote) {
					UpgradeHelper.setUpgrade(player, tileRFC, stack);
				}
				tileRFC.markBlockForUpdate();
				return;
			} else {
				StorageUtils.addStackManually(tileRFC, player, stack);
			}
		}
		if (!player.isSneaking() && stack.isEmpty()) {	
			if (!tileRFC.getWorld().isRemote) {
				tileRFC.isOpen = !tileRFC.isOpen;
				tileRFC.markDirty();
			}
			tileRFC.markBlockForUpdate();
		}
		if (player.isSneaking() && stack.isEmpty() && tileRFC.isOpen) {		
			if (UpgradeHelper.getUpgrade(tileRFC, StringLibs.TAG_ENDER) != null) {
				EnderUtils.extractEnderFolder(tileRFC, player);
				return;
			}
			StorageUtils.folderExtract(tileRFC, player, side, hitX, hitY, hitZ);
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
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos) {
		
		TileEntityRFC tileRFC = (TileEntityRFC)world.getTileEntity(pos);
		if (tileRFC != null && tileRFC.isCabinetLocked()) {
			if (!tileRFC.getCabinetOwner().equals(player.getUniqueID()))
				return -1.0F;
		}
		return ForgeHooks.blockStrength(state, player, world, pos);
	}
	
	// copied from vanilla furnace
	@Override
    @SuppressWarnings("incomplete-switch")
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    	
		TileEntity tile = worldIn.getTileEntity(pos);
		if (!(tile instanceof TileEntityRFC)) return;
		
        if (SmeltingUtils.isSmelting((TileEntityRFC)tile)) {
            EnumFacing enumfacing = (EnumFacing)stateIn.getValue(FACING);
            double d0 = (double)pos.getX() + 0.5D;
            double d1 = (double)pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
            double d2 = (double)pos.getZ() + 0.5D;
            double d3 = 0.52D;
            double d4 = rand.nextDouble() * 0.6D - 0.3D;

            if (rand.nextDouble() < 0.1D)
                worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);

            switch (enumfacing) {
                case WEST:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
                    break;
                case EAST:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
                    break;
                case NORTH:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D);
                    break;
                case SOUTH:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
