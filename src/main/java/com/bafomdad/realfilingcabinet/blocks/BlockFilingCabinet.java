package com.bafomdad.realfilingcabinet.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.api.IUpgrade;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.enums.FolderType;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.init.RFCSounds;
import com.bafomdad.realfilingcabinet.utils.AutocraftingUtils;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;
import com.bafomdad.realfilingcabinet.utils.SmeltingUtils;
import com.bafomdad.realfilingcabinet.utils.StorageUtils;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

public class BlockFilingCabinet extends BlockRFC {
	
	static float f = 0.0625F;
	protected static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(0.0D + f, 0.0D, 0.0D + f, 1.0D - f, 1.0D - f, 1.0D - f);
	
	public BlockFilingCabinet() {
		
		super(Material.IRON);
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB aabb, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean wat) {
        
    	addCollisionBoxToList(pos, aabb, collidingBoxes, BASE_AABB);
    }
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		
		return new TileFilingCabinet();
	}
	
	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
		
		if (!(entity instanceof EntityLivingBase)) return;
		
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileFilingCabinet) {
			TileFilingCabinet tfc = (TileFilingCabinet)tile;
			if (UpgradeHelper.getUpgrade(tfc, StringLibs.TAG_MOB).isEmpty()) return;
			
			for (int i = 0; i < tfc.getInventory().getSlots(); i++) {
				Object obj = FolderUtils.get(tfc.getInventory().getFolder(i)).insert((EntityLivingBase)entity, false);
				if (obj != null) {
					tfc.markBlockForUpdate();
					break;
				}
			}
		}
	}

	@Override
	public void leftClick(TileEntity tile, EntityPlayer player) {

		if (player.capabilities.isCreativeMode) return;
		
		TileFilingCabinet tileRFC = (TileFilingCabinet)tile;
		if (player.isSneaking() && player.getHeldItemMainhand().getItem() == RFCItems.MAGNIFYINGGLASS) {
			if (!tileRFC.getWorld().isRemote)
				UpgradeHelper.removeUpgrade(player, tileRFC);
			tileRFC.markBlockForUpdate();
			return;
		}
		if (UpgradeHelper.getUpgrade(tileRFC, StringLibs.TAG_CRAFT).isEmpty()) {
			StorageUtils.extractStackManually(tileRFC, player);
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
				ItemHandlerHelper.giveItemToPlayer(player, stack);
			}
		}
	}

	@Override
	public void rightClick(TileEntity tile, EntityPlayer player, EnumFacing side) {

		TileFilingCabinet tileRFC = (TileFilingCabinet)tile;
		ItemStack stack = player.getHeldItemMainhand();
		
		if (tileRFC.calcLastClick(player)) {
			StorageUtils.addAllStacksManually(tileRFC, player);
			tileRFC.markBlockForUpdate();
			return;
		}
		if (!player.isSneaking() && stack.isEmpty()) {
			if (!tileRFC.getWorld().isRemote) {
				tileRFC.isOpen = !tileRFC.isOpen;
				tileRFC.markDirty();
			}
			player.playSound(RFCSounds.SQUEAK, 0.75F, 1F);
			tileRFC.markBlockForUpdate();
			return;
		}
		if (player.isSneaking() && stack.isEmpty()) {
			StorageUtils.folderExtract(tileRFC, player);
			return;
		}
		if (!stack.isEmpty() && tileRFC.isOpen) {
			if (stack.getItem() instanceof IFolder) {
				if (stack.getItem() == RFCItems.FOLDER && stack.getItemDamage() == FolderType.ENDER.ordinal()) {
					player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
					return;
				}
				for (int i = 0; i < tileRFC.getInventory().getSlots(); i++) {
					if (tileRFC.getInventory().getFolder(i).isEmpty()) {
						tileRFC.getInventory().setStackInSlot(i, stack);
						player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
						tileRFC.markBlockForUpdate();
						return;
					}
				}
			}
		}
		if (!player.isSneaking() && !stack.isEmpty()) {
			if (stack.getItem() == RFCItems.KEY) {
				tileRFC.doKeyStuff(player, stack);
				return;
			}
			if (stack.getItem() instanceof IUpgrade)
				UpgradeHelper.setUpgrade(player, tileRFC, stack);
			else
				StorageUtils.addStackManually(tileRFC, player, stack);
			tileRFC.markBlockForUpdate();
			return;
		}
	}

	@Override
	public List<String> getInfoOverlay(TileEntity tile) {

		List<String> list = new ArrayList();
		if (tile instanceof TileFilingCabinet) {
			TileFilingCabinet tfc = (TileFilingCabinet)tile;
			for (int i = 0; i < tfc.getInventory().getSlots(); i++) {
				ItemStack folder = tfc.getInventory().getFolder(i);
				FolderUtils.get(folder).addTooltips(list);
			}
		}
		if (((TileEntityRFC)tile).isCabinetLocked())
			list.add(TextFormatting.YELLOW + "[LOCKED]");
		return list;
	}
	
	// copied from vanilla furnace
	@Override
	@SideOnly(Side.CLIENT)
    @SuppressWarnings("incomplete-switch")
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    	
		TileEntity tile = worldIn.getTileEntity(pos);
		if (!(tile instanceof TileFilingCabinet)) return;
		
        if (SmeltingUtils.isSmelting((TileFilingCabinet)tile)) {
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
