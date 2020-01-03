package com.bafomdad.realfilingcabinet.blocks;

import java.util.List;

import thaumcraft.api.aspects.IEssentiaContainerItem;

import com.bafomdad.realfilingcabinet.blocks.tiles.TileAspectCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.init.RFCSounds;
import com.bafomdad.realfilingcabinet.items.ItemAspectFolder;
import com.bafomdad.realfilingcabinet.utils.AspectStorageUtils;
import com.google.common.collect.Lists;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class BlockAspectCabinet extends BlockRFC {

	public BlockAspectCabinet() {
		
		super(Material.WOOD);
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		
		return new TileAspectCabinet();
	}

	@Override
	public void leftClick(TileEntity tile, EntityPlayer player) {

		TileAspectCabinet tac = (TileAspectCabinet)tile;
		AspectStorageUtils.extractAspect(tac, player, player.isSneaking());
	}

	@Override
	public void rightClick(TileEntity tile, EntityPlayer player, EnumFacing side) {

		TileAspectCabinet tac = (TileAspectCabinet)tile;
		ItemStack stack = player.getHeldItemMainhand();
		
		if (!player.isSneaking() && !stack.isEmpty()) {
			if (stack.getItem() == RFCItems.FOLDER_ASPECT && !ItemAspectFolder.isAspectFolderEmpty(stack) && tac.isOpen) {
				if (!tac.getWorld().isRemote) {
					for (int i = 0; i < tac.getInventory().getSlots(); i++) {
						ItemStack tileStack = tac.getInventory().getFolder(i);
						if (tileStack.isEmpty()) {
							tac.getInventory().setStackInSlot(i, stack);
							player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
							tac.markBlockForUpdate();
							return;
						}
					}
				}
			} else if (stack.getItem() instanceof IEssentiaContainerItem) {
				if (!tac.getWorld().isRemote)
					AspectStorageUtils.addAspect(tac, player, stack);
				return;
			}
		}
		if (!player.isSneaking() && stack.isEmpty()) {
			if (!tac.getWorld().isRemote) {
				tac.isOpen = !tac.isOpen;
				tac.markDirty();
			}
			player.playSound(RFCSounds.DRAWER, 0.45F, 1F);
			tac.markBlockForUpdate();
			return;
		}
		if (player.isSneaking() && stack.isEmpty() && tac.isOpen) {
			for (int i = tac.getInventory().getSlots() - 1; i >= 0; i--) {
				ItemStack folder = tac.getInventory().getFolder(i);
				if (!folder.isEmpty()) {
					tac.getInventory().setStackInSlot(i, ItemStack.EMPTY);
					player.setHeldItem(EnumHand.MAIN_HAND, folder);
					tac.markBlockForUpdate();
					break;
				}
			}
		}
	}

	@Override
	public List<String> getInfoOverlay(TileEntity tile, boolean crouching) {

		// empty list since we'll use the goggles for showing information instead
		return Lists.newArrayList();
	}
}
