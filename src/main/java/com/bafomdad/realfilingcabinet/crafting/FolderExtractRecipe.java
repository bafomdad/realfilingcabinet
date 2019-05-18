package com.bafomdad.realfilingcabinet.crafting;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.helpers.FolderType;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.utils.EnderUtils;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class FolderExtractRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	
	public FolderExtractRecipe() {
		
		super();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public boolean matches(InventoryCrafting ic, World world) {

		boolean foundFolder = false;
		for (int i = 0; i < ic.getSizeInventory(); i++) {
			ItemStack stack = ic.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof IFolder && !foundFolder)
					foundFolder = true;
				else return false;
			}
		}
		return foundFolder;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting ic) {

		ItemStack item = ItemStack.EMPTY;
		int folder = -1;
		
		for (int i = 0; i < ic.getSizeInventory(); i++) {
			ItemStack stack = ic.getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() instanceof IFolder)
				folder = i;
		}
		if (folder >= 0) {
			ItemStack stack1 = ic.getStackInSlot(folder);
			if (FolderUtils.get(stack1).getObject() instanceof ItemStack) {
				long count = FolderUtils.get(stack1).getFileSize();
				if (count > 0) {
					ItemStack folderStack = (ItemStack)FolderUtils.get(stack1).getObject();
					long extract = Math.min(folderStack.getMaxStackSize(), count);
					if (stack1.getItem() == RFCItems.FOLDER && stack1.getItemDamage() == FolderType.ENDER.ordinal()) {
						foldy = stack1;
						canSync = true;
					}
					if (folderStack.hasTagCompound())
						return folderStack.copy();
					
					return new ItemStack(folderStack.getItem(), (int)extract, folderStack.getItemDamage());
				}
			}
		}
		return item;
	}

	@Override
	public boolean canFit(int width, int height) {

		return width * height >= 2;
	}

	@Override
	public ItemStack getRecipeOutput() {

		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean isDynamic() {
		
		return true;
	}
	
	private boolean canSync = false;
	private ItemStack foldy = ItemStack.EMPTY;
	
	@SubscribeEvent
	public void enderFolderExtract(PlayerEvent.ItemCraftedEvent event) {
		
		if (!event.player.getEntityWorld().isRemote && canSync) {
			if (!foldy.isEmpty()) {
				EnderUtils.syncToTile(foldy);
				foldy = ItemStack.EMPTY;
			}
			canSync = false;
		}
	}
}
