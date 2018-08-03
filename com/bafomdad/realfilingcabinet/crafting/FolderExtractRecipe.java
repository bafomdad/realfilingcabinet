package com.bafomdad.realfilingcabinet.crafting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.utils.EnderUtils;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;
import com.bafomdad.realfilingcabinet.utils.StorageUtils;

public class FolderExtractRecipe extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	
	public static List<ItemStack> input = new ArrayList();
	private boolean canSync = false;
	private ItemStack foldy = ItemStack.EMPTY;
	final String name;
	
	static
	{
		input.add(new ItemStack(RFCItems.folder));
	}
	
	public FolderExtractRecipe(String name) {
		
		this.name = name;
		this.setRegistryName(new ResourceLocation(RealFilingCabinet.MOD_ID, name));
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public boolean matches(InventoryCrafting ic, World world) {
		
		ArrayList list = new ArrayList(this.input);
		
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				
				ItemStack stack = ic.getStackInRowAndColumn(j, i);
				if (!stack.isEmpty())
				{
					boolean flag = false;
					Iterator iter = list.iterator();
					
					while (iter.hasNext())
					{
						ItemStack stack1 = (ItemStack)iter.next();
						
						if (stack.getItem() == stack1.getItem())
						{
							flag = true;
							list.remove(stack1);
							break;
						}
					}
					if (!flag)
						return false;
				}
			}
		}
		return list.isEmpty();
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting ic) {
		
		int folder = -1;
		
		for (int i = 0; i < ic.getSizeInventory(); i++) {
			
			ItemStack stack = ic.getStackInSlot(i);
			if (!stack.isEmpty())
				folder = i;
		}
		if (folder >= 0) {
			ItemStack stack = ic.getStackInSlot(folder);
			
			if (stack.getItem() == RFCItems.folder && ItemFolder.getObject(stack) != null) {
				StorageUtils.checkTapeNBT(ic.getStackInSlot(folder), false);
				if (ItemFolder.getObject(stack) instanceof ItemStack) {
					long count = ItemFolder.getFileSize(stack);
					if (count > 0) {
						ItemStack folderStack = (ItemStack)ItemFolder.getObject(stack);
						int meta = folderStack.getItemDamage();
						long extract = Math.min(folderStack.getMaxStackSize(), count);
						
						if (stack.getItemDamage() == 1) {
							foldy = stack;
							canSync = true;
						}
						if (stack.getItemDamage() == 5) {
							NBTTagCompound itemtag = ItemFolder.getItemTag(stack);
							ItemStack copystack = new ItemStack(folderStack.getItem(), (int)extract, meta);
							copystack.setTagCompound(itemtag);
							return copystack;
						}
						return new ItemStack(folderStack.getItem(), (int)extract, meta);
					}
					else if (count == 0 && stack.getItemDamage() == 2) {
						if (ItemFolder.getRemSize(stack) > 0) {
							ItemStack folderStack = (ItemStack)ItemFolder.getObject(stack);
							int damage = folderStack.getMaxDamage() - ItemFolder.getRemSize(stack);
							
							return new ItemStack(folderStack.getItem(), 1, damage);
						}
					}
				}
			}
		}
		return ItemStack.EMPTY;
	}
	
	@SubscribeEvent
	public void enderFolderExtract(PlayerEvent.ItemCraftedEvent event) {
		
		if (event.crafting.getItem() instanceof IFolder) {
			for (int i = 0; i < event.craftMatrix.getSizeInventory(); i++) {
				ItemStack container = event.craftMatrix.getStackInSlot(i);
				if (!container.isEmpty() && !(container.getItem() instanceof IFolder) && container.getItem().hasContainerItem(container))
					event.craftMatrix.setInventorySlotContents(i, ItemStack.EMPTY);
			}
		}
		if (!event.player.getEntityWorld().isRemote && canSync)
		{
			if (!foldy.isEmpty())
			{
				EnderUtils.syncToTile(EnderUtils.getTileLoc(foldy), NBTUtils.getInt(foldy, StringLibs.RFC_DIM, 0), NBTUtils.getInt(foldy, StringLibs.RFC_SLOTINDEX, 0), ItemFolder.extractSize, true);
			}
			canSync = false;
		}
	}

	@Override
	public boolean canFit(int width, int height) {

		return width >= 3 && height >= 3;
	}

	@Override
	public ItemStack getRecipeOutput() {

		return new ItemStack(RFCItems.folder);
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting ic) {

        NonNullList<ItemStack> ret = NonNullList.withSize(ic.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < ret.size(); i++) {
        	if (ic.getStackInSlot(i).getItem() == RFCItems.folder)
        		ret.set(i, ic.getStackInSlot(i).getItem().getContainerItem(ic.getStackInSlot(i)));
        	else
        		ret.set(i, ItemStack.EMPTY);
        }
        return ret;
	}
}