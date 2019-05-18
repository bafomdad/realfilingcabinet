package com.bafomdad.realfilingcabinet.blocks;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileManaCabinet;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.init.RFCSounds;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class BlockManaCabinet extends BlockRFC {

	public BlockManaCabinet() {
		
		super(Material.ROCK);
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		
		return new TileManaCabinet();
	}	

	@Override
	public void leftClick(TileEntity tile, EntityPlayer player) {
		
		// TODO: make mana extraction possible while holding mana holding items
	}

	@Override
	public void rightClick(TileEntity tile, EntityPlayer player, EnumFacing side) {
		
		TileManaCabinet tmc = (TileManaCabinet)tile;
		ItemStack stack = player.getHeldItemMainhand();
		
		if (!player.isSneaking() && !stack.isEmpty()) {
			if (stack.getItem() == RFCItems.FOLDER_MANA && tmc.isOpen) {
				for (int i = 0; i < tmc.getInventory().getSlots(); i++) {
					ItemStack tileStack = tmc.getInventory().getFolder(i);
					if (tileStack.isEmpty()) {
						tmc.getInventory().setStackInSlot(i, stack);
						player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
						tmc.markBlockForUpdate();
						break;
					}
				}
			}
			return;
		}
		if (!player.isSneaking() && stack.isEmpty()) {
			if (!tmc.getWorld().isRemote) {
				tmc.isOpen = !tmc.isOpen;
				tmc.markDirty();
			}
			player.playSound(RFCSounds.DRAWER, 0.45F, 1F);
			tmc.markBlockForUpdate();
			return;
		}
		if (player.isSneaking() && stack.isEmpty() && tmc.isOpen) {
			for (int i = tmc.getInventory().getSlots() - 1; i >= 0; i--) {
				ItemStack folder = tmc.getInventory().getFolder(i);
				if (!folder.isEmpty()) {
					tmc.getInventory().setStackInSlot(i, ItemStack.EMPTY);
					player.setHeldItem(EnumHand.MAIN_HAND, folder);
					tmc.markBlockForUpdate();
					break;
				}
			}
		}
	}

	@Override
	public List<String> getInfoOverlay(TileEntity tile) {

		List<String> list = new ArrayList();
		if (tile instanceof TileManaCabinet) {
			TileManaCabinet tmc = (TileManaCabinet)tile;
			double count = tmc.getTotalInternalManaPool();
			double calc = 0.0;
			if (count > 0)
				calc = count / 1000000;
			NumberFormat percentFormatter = NumberFormat.getPercentInstance();
			String percentOut = percentFormatter.format(calc) + " of a full mana pool";
			list.add(percentOut);
		}
		return list;
	}
}
