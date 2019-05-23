package com.bafomdad.realfilingcabinet.utils;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.AdvancementsRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.enums.FolderType;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityFolder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemHandlerHelper;

public class StorageUtils {
	
	public static void addStackManually(TileEntityRFC tile, EntityPlayer player, ItemStack stack) {
		
		if (tile.getWorld().isRemote) return;
		
		if (testOredict(tile, stack)) return;
		
		for (int i = 0; i < tile.getInventory().getSlots(); i++) {
			ItemStack folderStack = tile.getInventory().getFolder(i);
			if (!folderStack.isEmpty()) {
				testAdvancement(tile.getInventory().getFolder(i), player);
				FolderUtils.get(tile.getInventory().getFolder(i)).insert(stack, false);
				if (stack.isEmpty())
					break;
			}
		}
		tile.markBlockForUpdate();
	}
	
	public static void addAllStacksManually(TileEntityRFC tile, EntityPlayer player) {
		
		if (tile.getWorld().isRemote) return;
		
		boolean consume = false;
		for (ItemStack loopInv : player.inventory.mainInventory) {
			if (!loopInv.isEmpty()) {
				for (int i = 0; i < tile.getInventory().getSlots(); i++) {
					ItemStack folderStack = tile.getInventory().getStackFromFolder(i);
					if (!folderStack.isEmpty()) {
						testAdvancement(tile.getInventory().getFolder(i), player);
						FolderUtils.get(tile.getInventory().getFolder(i)).insert(loopInv, false);
						consume = true;
					}
				}
			}
		}
		if (consume) {
			if (player instanceof EntityPlayerMP)
				((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
			tile.markDirty();
		}
	}

	public static void extractStackManually(TileEntityRFC tile, EntityPlayer player) {
		
		ItemStack stack = tile.getFilter();
		if (!stack.isEmpty()) {
			for (int i = 0; i < tile.getInventory().getSlots(); i++) {
				ItemStack folder = tile.getInventory().getFolder(i);
				CapabilityFolder cap = FolderUtils.get(folder).getCap();
				if (cap != null && cap.isItemStack() && ItemStack.areItemsEqual(cap.getItemStack(), stack) && cap.getCount() > 0) {
					if (folder.getItemDamage() == FolderType.NBT.ordinal() && !ItemStack.areItemStackTagsEqual(cap.getItemStack(), stack))
						return;
					ItemHandlerHelper.giveItemToPlayer(player, (ItemStack)cap.extract((player.isSneaking()) ? stack.getMaxStackSize() : 1, false));
					tile.markBlockForUpdate();
					return;
				}
			}
		}
	}
	
	public static void folderExtract(TileEntityRFC tile, EntityPlayer player) {
		
		if (tile instanceof TileFilingCabinet && !UpgradeHelper.getUpgrade((TileFilingCabinet)tile, StringLibs.TAG_ENDER).isEmpty()) {
			ItemStack enderFolder = new ItemStack(RFCItems.FOLDER, 1, FolderType.ENDER.ordinal());
			
			NBTUtils.setInt(enderFolder, StringLibs.RFC_SLOTINDEX, 0);
			NBTUtils.setInt(enderFolder, StringLibs.RFC_HASH, ((TileFilingCabinet)tile).getHash());
			EnderUtils.setTileLoc((TileFilingCabinet)tile, enderFolder);
			
			player.setHeldItem(EnumHand.MAIN_HAND, enderFolder);
			return;
		}
		for (int i = tile.getInventory().getSlots() - 1; i >= 0; i--) {
			ItemStack folder = tile.getInventory().getFolder(i);
			if (!folder.isEmpty()) {
				tile.getInventory().setStackInSlot(i, ItemStack.EMPTY);
				player.setHeldItem(EnumHand.MAIN_HAND, folder);
				tile.markBlockForUpdate();
				break;
			}
		}
	}
	
	public static void cycleIndex(ItemStack stack, int wheel) {
		
		if (wheel > 0) wheel = 1;
		if (wheel < 0) wheel = -1;

		int index = getIndex(stack);
		NBTUtils.setInt(stack, StringLibs.RFC_SLOTINDEX, Math.floorMod(index + wheel, 8));
	}
	
	public static int getIndex(ItemStack stack) {

		return NBTUtils.getInt(stack, StringLibs.RFC_SLOTINDEX, 0);
	}
	
	public static void testAdvancement(ItemStack dyedFolder, EntityPlayer player) {
		
		if (player instanceof EntityPlayerMP && dyedFolder.getItem() == RFCItems.DYEDFOLDER) {
			if (FolderUtils.get(dyedFolder).getFileSize() >= ConfigRFC.folderSizeLimit)
				AdvancementsRFC.advance((EntityPlayerMP)player, new ResourceLocation(RealFilingCabinet.MOD_ID, "limit_reach"), "code_triggered");
		}
	}
	
	public static boolean testOredict(TileEntityRFC tile, ItemStack toInsert) {
		
		if (!(tile instanceof TileFilingCabinet)) return false;
		if (UpgradeHelper.getUpgrade((TileFilingCabinet)tile, StringLibs.TAG_OREDICT).isEmpty()) return false;
		
		OreDictUtils.recreateOreDictionary(toInsert);
		if (OreDictUtils.hasOreDict()) {
			for (int i = 0; i < tile.getInventory().getSlots(); i++) {
				ItemStack stackInFolder = tile.getInventory().getStackFromFolder(i);
				if (!stackInFolder.isEmpty() && OreDictUtils.areItemsEqual(toInsert, stackInFolder)) {
					FolderUtils.get(tile.getInventory().getFolder(i)).insert(toInsert, false);
					tile.markBlockForUpdate();
					return true;
				}
			}
		}
		return false;
	}
}
