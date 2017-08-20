package com.bafomdad.realfilingcabinet.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.network.VanillaPacketDispatcher;

public class EnderUtils {

	public static void syncToFolder(TileEntityRFC tile, ItemStack stack, int index) {
		
		ItemStack folder = tile.getInventory().getTrueStackInSlot(index);
		long folderSize = ItemFolder.getFileSize(folder);
		
		if (folderSize != ItemFolder.getFileSize(stack) && hashMatches(stack, tile)) {
			ItemFolder.setFileSize(stack, folderSize);
			return;
		}
		else if (!hashMatches(stack, tile))
			ItemFolder.setFileSize(stack, 0);
	}
	
	public static void syncToTile(TileEntityRFC tile, int dim, int index, int amount, boolean subtract) {
		
		if (tile == null || UpgradeHelper.getUpgrade(tile, StringLibs.TAG_ENDER) == null)
			return;
		
		ItemStack folder = tile.getInventory().getTrueStackInSlot(index);
		if (folder.getItem() != RFCItems.folder)
			return;
		
		if (!subtract)
			ItemFolder.add(folder, amount);
		else
			ItemFolder.remove(folder, amount);
		
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile);
	}
	
	public static ItemStack createEnderFolder(TileEntityRFC tile, EntityPlayer player, ItemStack stack) {
		
		NBTTagCompound playerTag = player.getEntityData();
		if (!playerTag.hasKey(StringLibs.RFC_SLOTINDEX))
			playerTag.setInteger(StringLibs.RFC_SLOTINDEX, 0);
		
		ItemStack enderFolder = stack.copy();
		enderFolder.setItemDamage(1);
		NBTUtils.setInt(enderFolder, StringLibs.RFC_SLOTINDEX, playerTag.getInteger(StringLibs.RFC_SLOTINDEX));
		NBTUtils.setInt(enderFolder, StringLibs.RFC_HASH, tile.getHash(tile));
		setTileLoc(tile, enderFolder);
		
		return enderFolder;
	}
	
	public static void setTileLoc(TileEntityRFC tile, ItemStack stack) {
		
		BlockPos pos = tile.getPos();
		int dim = tile.getWorld().provider.getDimension();
		
		NBTUtils.setCompound(stack, StringLibs.RFC_TILEPOS, new NBTTagCompound());
		NBTTagCompound posTag = NBTUtils.getCompound(stack, StringLibs.RFC_TILEPOS, true);
		
		posTag.setInteger("X", pos.getX());
		posTag.setInteger("Y", pos.getY());
		posTag.setInteger("Z", pos.getZ());
		
		NBTUtils.setInt(stack, StringLibs.RFC_DIM, dim);
	}
	
	public static TileEntityRFC getTileLoc(ItemStack stack) {
		
		NBTTagCompound posTag = NBTUtils.getCompound(stack, StringLibs.RFC_TILEPOS, true);
		
		if (posTag != null)
		{
			int x = posTag.getInteger("X");
			int y = posTag.getInteger("Y");
			int z = posTag.getInteger("Z");
			int dim = NBTUtils.getInt(stack, StringLibs.RFC_DIM, 0);
			
			return findLoadedTileEntityInWorld(new BlockPos(x, y, z), dim);
		}
		return null;
	}
	
	public static TileEntityRFC findLoadedTileEntityInWorld(BlockPos pos, int dim) {
		
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (server == null)
			return null;
		
		if (pos.getY() == -1)
			return null;
		
		for (WorldServer world : server.worlds) {
			for (Object obj : world.loadedTileEntityList) {
				if (obj instanceof TileEntityRFC) {
					if (world.provider.getDimension() == dim && pos.equals(((TileEntityRFC)obj).getPos()) && UpgradeHelper.getUpgrade((TileEntityRFC)obj, StringLibs.TAG_ENDER) != null)
						if (world.isBlockLoaded(pos))
							return (TileEntityRFC)world.getTileEntity(pos);
				}
			}
		}
		return null;
	}
	
	public static void extractEnderFolder(TileEntityRFC tile, EntityPlayer player) {
		
		NBTTagCompound playerTag = player.getEntityData();
		if (playerTag.hasKey(StringLibs.RFC_SLOTINDEX))
		{
			int index = playerTag.getInteger(StringLibs.RFC_SLOTINDEX);
			ItemStack folder = tile.getInventory().getTrueStackInSlot(index);
			
			if (folder == ItemStack.EMPTY) {
				if (findNextFolder(tile, index) == -1)
				{
					folder = tile.getInventory().getTrueStackInSlot(0);
					playerTag.setInteger(StringLibs.RFC_SLOTINDEX, 0);
					index = 0;
				}
				index = findNextFolder(tile, index);
				folder = tile.getInventory().getTrueStackInSlot(index);
				if (folder == ItemStack.EMPTY)
					return;
				
				playerTag.setInteger(StringLibs.RFC_SLOTINDEX, index);
			}
			ItemStack newFolder = createEnderFolder(tile, player, folder);
			player.setHeldItem(EnumHand.MAIN_HAND, newFolder);
			playerTag.setInteger(StringLibs.RFC_SLOTINDEX, index += 1);
		}
		else
		{
			for (int i = 0; i < tile.getInventory().getSlots(); i++) {
				ItemStack folder = tile.getInventory().getTrueStackInSlot(i);
				if (folder != ItemStack.EMPTY)
				{
					ItemStack newFolder = createEnderFolder(tile, player, folder);
					player.setHeldItem(EnumHand.MAIN_HAND, newFolder);
					break;
				}
			}
		}
	}
	
	private static int findNextFolder(TileEntityRFC tile, int slot) {
		
		int index = 0;
		for (int i = slot; i < tile.getInventory().getSlots(); i++) {
			ItemStack stack = tile.getInventory().getStackInSlot(i);
			if (stack != ItemStack.EMPTY) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	public static int createHash(TileEntityRFC tile) {
		
		String tilepos = Long.toString(tile.getPos().toLong());
		String worldtime = Long.toString(tile.getWorld().getTotalWorldTime());
		
		String str = new String(tilepos + worldtime);
		
		return str.hashCode();
	}
	
	private static boolean hashMatches(ItemStack stack, TileEntityRFC tile) {
		
		return NBTUtils.getInt(stack, StringLibs.RFC_HASH, -1) == tile.getHash(tile);
	}
	
	public static boolean preValidateEnderFolder(ItemStack stack) {
		
		TileEntityRFC tile = getTileLoc(stack);
		if (tile == null)
			return false;
		
		if (tile != null && !hashMatches(stack, tile))
			return false;
		
		return true;
	}
}
