package com.bafomdad.realfilingcabinet.utils;

import java.util.Iterator;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.inventory.InventoryRFC;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.network.VanillaPacketDispatcher;

public class AutocraftingUtils {
	
	private static int outputSize;

	private static IRecipe getRecipeFor(ItemStack stack) {
		
		if (!stack.isEmpty()) {
			Iterator iter = CraftingManager.REGISTRY.iterator();
			while (iter.hasNext()) {
				IRecipe recipe = (IRecipe)iter.next();
	    		if ((recipe instanceof ShapedRecipes || recipe instanceof ShapedOreRecipe || recipe instanceof ShapelessRecipes || recipe instanceof ShapelessOreRecipe) 
	    				&& recipe.getRecipeOutput().getItem() == stack.getItem()
	    				&& (!recipe.getRecipeOutput().getHasSubtypes() || recipe.getRecipeOutput().getItemDamage() == stack.getItemDamage())) {
	    			return recipe;
	    		}
			}
		}
		return null;
	}
	
	private static IItemHandler getFakeInv(TileEntityRFC tile) {
		
		ItemStackHandler tempInv = new ItemStackHandler(tile.getInventory().getSlots());
		for (int i = 0; i < tile.getInventory().getSlots(); i++) {
			ItemStack stack = tile.getInventory().getTrueStackInSlot(i);
			if (stack != ItemStack.EMPTY)
				tempInv.setStackInSlot(i, tile.getInventory().getTrueStackInSlot(i).copy());
		}
		return tempInv;
	}
	
	public static int getOutputSize() {
		
		return outputSize;
	}
	
	public static boolean canCraft(ItemStack input, TileEntityRFC tile) {
		
		if (input.isEmpty()) return false;
		
		return consumeRecipeIngredients(input, (IItemHandler)getFakeInv(tile));
	}
	
	public static void doCraft(ItemStack input, IItemHandler inv) {
		
		consumeRecipeIngredients(input, inv);
	}
	
	public static boolean consumeRecipeIngredients(ItemStack input, IItemHandler inv) {
		
		IRecipe recipe = getRecipeFor(input);
		if (recipe != null) {
			NonNullList<Ingredient> ingredients = recipe.getIngredients();
			for (int i = 0; i < ingredients.size(); i++) {
				Ingredient ingredient = ingredients.get(i);
				boolean flag = ingredient.getMatchingStacks().length > 0;
				if (flag) {
					ItemStack stack = ingredient.getMatchingStacks()[0];
					if (stack.isEmpty())
						return false;
					if (stack.getCount() > 1)
						stack.setCount(1);
					if (stack.getItemDamage() == -1 || stack.getItemDamage() == Short.MAX_VALUE)
						stack.setItemDamage(0);
					if (!consumeFromInventory(stack, inv))
						return false;
				}
			}
			outputSize = recipe.getRecipeOutput().getCount();
		}
		if (inv instanceof InventoryRFC)
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(((InventoryRFC)inv).getTile());
		return true;
	}
	
	public static boolean consumeFromInventory(ItemStack stack, IItemHandler inv) {
		
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack folder = inv.getStackInSlot(i);
			if (inv instanceof InventoryRFC)
				folder = ((InventoryRFC)inv).getTrueStackInSlot(i);
			
			if (!folder.isEmpty() && folder.getItem() == RFCItems.folder) {
				if (ItemFolder.getObject(folder) instanceof ItemStack) {
					if (ItemFolder.getObject(folder) != null && stack.isItemEqual((ItemStack)ItemFolder.getObject(folder))) {
						if (ItemFolder.getFileSize(folder) > 0) {
							
							boolean consume = true;
							ItemStack container = ((ItemStack)ItemFolder.getObject(folder)).getItem().getContainerItem((ItemStack)ItemFolder.getObject(folder));
							if (container != ItemStack.EMPTY && (inv instanceof InventoryRFC)) {
								if (container == (ItemStack)ItemFolder.getObject(folder))
									consume = false;
								if (consume && !shuntContainerItem(container, inv)) {
									shuntContainerItemOutside(container, inv);
								}
							}
							if (consume) {
								ItemFolder.remove(folder, 1);
								if (ItemFolder.getFileSize(folder) < 0)
									consume = false;
							}
							return consume;
						}
					}
				}
			}
		}
		return false;
	}
	
	private static boolean shuntContainerItem(ItemStack container, IItemHandler inv) {
		
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack folder = ((InventoryRFC)inv).getTrueStackInSlot(i);
			if (folder != ItemStack.EMPTY && folder.getItem() == RFCItems.folder) {
				if (ItemFolder.getObject(folder) != null && container.isItemEqual((ItemStack)ItemFolder.getObject(folder))) {
					ItemFolder.add(folder, 1);
					return true;
				}
			}
		}
		return false;
	}
	
	private static void shuntContainerItemOutside(ItemStack container, IItemHandler inv) {
		
		TileEntityRFC tile = ((InventoryRFC)inv).getTile();
		
		EntityItem ei = new EntityItem(tile.getWorld(), tile.getPos().getX() + 0.5, tile.getPos().getY() + 1.5, tile.getPos().getZ() + 0.5, container);
		tile.getWorld().spawnEntity(ei);
	}
}
