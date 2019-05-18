package com.bafomdad.realfilingcabinet.utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.FolderType;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityFolder;
import com.bafomdad.realfilingcabinet.network.VanillaPacketDispatcher;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class EnderUtils {
	
	private static LoadingCache<ItemStack, TileFilingCabinet> cache;
	private static TileFilingCabinet loadedTile;
	
	static {
		cache = CacheBuilder.newBuilder()
				.concurrencyLevel(2)
				.maximumSize(10)
				.expireAfterWrite(10, TimeUnit.SECONDS)
				.build(
					new CacheLoader<ItemStack, TileFilingCabinet>() {
						@Override
						public TileFilingCabinet load(ItemStack stack) throws Exception {
							
							setTile(stack);
							return loadedTile;
						}
					});
	}
	
	public static void setTile(ItemStack stack) {
		
		if (!stack.isEmpty() && stack.getItem() == RFCItems.FOLDER && stack.getItemDamage() == FolderType.ENDER.ordinal()) {
			TileFilingCabinet tile = getTileLoc(stack);
			if (tile != null)
				loadedTile = tile;
		}
	}
	
	private static LoadingCache<ItemStack, TileFilingCabinet> getCache() {
		
		return cache;
	}
	
	public static TileFilingCabinet getCachedTile(ItemStack stack) {
		
		if (getCache() == null || loadedTile == null) {
			return getTileLoc(stack);
		}
		try {
			return cache.get(stack);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * Syncs information from the tile entity to the folder stack
	 * Deprecated! Use the method right below this one
	 */
	@Deprecated
	public static void syncToFolder(TileFilingCabinet tile, ItemStack stack, int index) {

		CapabilityFolder cap = FolderUtils.get(tile.getInventory().getFolder(index)).getCap();
		if (cap == null || !cap.isItemStack() || !hashMatches(stack, tile)) {
			FolderUtils.get(stack).setObject(ItemStack.EMPTY);
			FolderUtils.get(stack).setFileSize(0);
			return;
		}
		long folderSize = cap.getCount();
		if (hashMatches(stack, tile)) {
			if (!(FolderUtils.get(stack).getObject() instanceof ItemStack))
				FolderUtils.get(stack).setObject(cap.getContents());
			if (folderSize != FolderUtils.get(stack).getFileSize())
				FolderUtils.get(stack).setFileSize(folderSize);
			return;
		}
	}
	
	public static void syncToFolder(ItemStack folder) {
		
		TileFilingCabinet tile = getCachedTile(folder);
		CapabilityFolder cap = FolderUtils.get(tile.getInventory().getFolder(NBTUtils.getInt(folder, StringLibs.RFC_SLOTINDEX, 0))).getCap();
		if (cap == null || !cap.isItemStack() || !hashMatches(folder, tile)) {
			FolderUtils.get(folder).setObject(ItemStack.EMPTY);
			FolderUtils.get(folder).setFileSize(0);
			return;
		}
		long folderSize = cap.getCount();
		if (!(FolderUtils.get(folder).getObject() instanceof ItemStack))
			FolderUtils.get(folder).setObject(cap.getContents());
		if (folderSize != FolderUtils.get(folder).getFileSize())
			FolderUtils.get(folder).setFileSize(folderSize);
	}
	
	/*
	 * Syncs information from the folder stack to the tile entity
	 * Deprecated! Use the method right below this one
	 */
	@Deprecated
	public static void syncToTile(TileFilingCabinet tile, int dim, int index, int amount) {
		
		if (tile == null || UpgradeHelper.getUpgrade(tile, StringLibs.TAG_ENDER).isEmpty())
			return;
		
		ItemStack folder = tile.getInventory().getFolder(index);
		if (folder.getItem() != RFCItems.FOLDER)
			return;
		
		FolderUtils.get(folder).add(amount);		
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile);
	}
	
	public static void syncToTile(ItemStack folder) {
		
		TileFilingCabinet tile = getCachedTile(folder);
		if (tile == null || UpgradeHelper.getUpgrade(tile, StringLibs.TAG_ENDER).isEmpty()) return;
		
		int index = NBTUtils.getInt(folder, StringLibs.RFC_SLOTINDEX, 0);
		ItemStack tileFolder = tile.getInventory().getFolder(index);
		CapabilityFolder cap = FolderUtils.get(tileFolder).getCap();
		if (cap == null || !cap.isItemStack()) return;

		cap.setCount(cap.getCount() + FolderUtils.get(folder).getExtractSize());
		FolderUtils.get(folder).setExtractSize(0);
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile);
	}
	
	public static ItemStack createEnderFolder(TileFilingCabinet tile, EntityPlayer player, ItemStack stack) {
		
		NBTTagCompound playerTag = player.getEntityData();
		if (!playerTag.hasKey(StringLibs.RFC_SLOTINDEX))
			playerTag.setInteger(StringLibs.RFC_SLOTINDEX, 0);
		
		ItemStack enderFolder = stack.copy();
		enderFolder.setItemDamage(1);
		NBTUtils.setInt(enderFolder, StringLibs.RFC_SLOTINDEX, playerTag.getInteger(StringLibs.RFC_SLOTINDEX));
		NBTUtils.setInt(enderFolder, StringLibs.RFC_HASH, tile.getHash());
		setTileLoc(tile, enderFolder);
		
		return enderFolder;
	}
	
	public static void setTileLoc(TileFilingCabinet tile, ItemStack stack) {
		
		BlockPos pos = tile.getPos();
		int dim = tile.getWorld().provider.getDimension();

		NBTTagCompound posTag = NBTUtils.getCompound(stack, StringLibs.RFC_TILEPOS, false);
		
		posTag.setInteger("X", pos.getX());
		posTag.setInteger("Y", pos.getY());
		posTag.setInteger("Z", pos.getZ());
		
		NBTUtils.setCompound(stack, StringLibs.RFC_TILEPOS, posTag);
		NBTUtils.setInt(stack, StringLibs.RFC_DIM, dim);
		
		setTile(stack);
	}
	
	public static TileFilingCabinet getTileLoc(ItemStack stack) {
		
		NBTTagCompound posTag = NBTUtils.getCompound(stack, StringLibs.RFC_TILEPOS, true);
		
		if (posTag != null) {
			int x = posTag.getInteger("X");
			int y = posTag.getInteger("Y");
			int z = posTag.getInteger("Z");
			int dim = NBTUtils.getInt(stack, StringLibs.RFC_DIM, 0);
			
			return findLoadedTileEntityInWorld(new BlockPos(x, y, z), dim);
		}
		return null;
	}
	
	public static TileFilingCabinet findLoadedTileEntityInWorld(BlockPos pos, int dim) {
		
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (server == null)
			return null;
		
		if (pos.getY() == -1)
			return null;
		
		for (WorldServer world : server.worlds) {
			for (Object obj : world.loadedTileEntityList) {
				if (obj instanceof TileFilingCabinet) {
					if (world.provider.getDimension() == dim && pos.equals(((TileFilingCabinet)obj).getPos()) && !UpgradeHelper.getUpgrade((TileFilingCabinet)obj, StringLibs.TAG_ENDER).isEmpty())
						if (world.isBlockLoaded(pos))
							return (TileFilingCabinet)world.getTileEntity(pos);
				}
			}
		}
		return null;
	}
	
	public static int createHash(TileFilingCabinet tile) {
		
		String str = new String("" + tile.getPos().toLong() + tile.getWorld().getTotalWorldTime());
		return str.hashCode();
	}
	
	private static boolean hashMatches(ItemStack stack, TileFilingCabinet tile) {
		
		if (tile == null) return false;
		return NBTUtils.getInt(stack, StringLibs.RFC_HASH, -1) == tile.getHash();
	}
	
	public static boolean preValidateEnderFolder(ItemStack stack) {
		
		TileFilingCabinet tile = getTileLoc(stack);
		if (tile == null)
			return false;
		
		if (tile != null && !hashMatches(stack, tile))
			return false;
		
		return true;
	}
}
