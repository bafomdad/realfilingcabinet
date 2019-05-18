package com.bafomdad.realfilingcabinet.blocks;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.api.IUpgrade;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.FolderType;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.init.RFCSounds;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;
import com.bafomdad.realfilingcabinet.utils.StorageUtils;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
		StorageUtils.extractStackManually(tileRFC, player);
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
		return list;
	}
}
