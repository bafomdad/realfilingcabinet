package com.bafomdad.realfilingcabinet.utils;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Collectors;

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

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileFilingCabinet;
import com.bafomdad.realfilingcabinet.init.RFCIntegration;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.integration.CraftTweakerRFC;
import com.bafomdad.realfilingcabinet.integration.IModCompat;
import com.bafomdad.realfilingcabinet.integration.loaders.TweakerLoader;
import com.bafomdad.realfilingcabinet.inventory.InventoryRFC;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityFolder;
import com.bafomdad.realfilingcabinet.network.VanillaPacketDispatcher;

public class AutocraftingUtils {
	
	private static int outputSize;

	private static IRecipe getRecipeFor(ItemStack stack) {
		
		if (!stack.isEmpty()) {
			IRecipe recipe;
			Optional<IModCompat> opt = RFCIntegration.canLoad(RFCIntegration.CRT);
			if (opt.isPresent()) {
				recipe = TweakerLoader.getTweakedRecipe(stack);
				if (recipe != null) return recipe;
			}
			Iterator iter = CraftingManager.REGISTRY.iterator();
			while (iter.hasNext()) {
				recipe = (IRecipe)iter.next();
	    		if ((recipe instanceof ShapedRecipes || recipe instanceof ShapedOreRecipe || recipe instanceof ShapelessRecipes || recipe instanceof ShapelessOreRecipe) 
	    				&& recipe.getRecipeOutput().getItem() == stack.getItem()
	    				&& (!recipe.getRecipeOutput().getHasSubtypes() || recipe.getRecipeOutput().getItemDamage() == stack.getItemDamage())) {
	    			return recipe;
	    		}
			}
		}
		return null;
	}
	
	private static IItemHandler getFakeInv(TileFilingCabinet tile) {
		
		ItemStackHandler tempInv = new ItemStackHandler(tile.getInventory().getSlots());
		for (int i = 0; i < tile.getInventory().getSlots(); i++) {
			ItemStack stack = tile.getInventory().getFolder(i);
			if (stack != ItemStack.EMPTY)
				tempInv.setStackInSlot(i, tile.getInventory().getFolder(i).copy());
		}
		return tempInv;
	}
	
	public static int getOutputSize() {
		
		return outputSize;
	}
	
	public static boolean canCraft(ItemStack input, TileFilingCabinet tile) {
		
		if (isUncraftable(input, tile)) return false;
		IRecipe recipe = getRecipeFor(input);
		if (recipe == null) {
			setUncraftableItem(input, tile);
			return false;
		}
		return consumeRecipeIngredients(recipe, (IItemHandler)getFakeInv(tile));
	}
	
	public static void doCraft(ItemStack input, IItemHandler inv) {
		
		IRecipe recipe = getRecipeFor(input);
		consumeRecipeIngredients(recipe, inv);
	}
	
	private static void setUncraftableItem(ItemStack stack, TileFilingCabinet tile) {
		
		tile.uncraftableItem = stack;
	}
	
	private static boolean isUncraftable(ItemStack stack, TileFilingCabinet tile) {
		
		return stack.isEmpty() || ItemStack.areItemsEqualIgnoreDurability(stack, tile.uncraftableItem);
	}
	
	public static boolean consumeRecipeIngredients(IRecipe recipe, IItemHandler inv) {
		
//		IRecipe recipe = getRecipeFor(input);
//		if (recipe == null) {
//			setUncraftableItem(input, inv);
//			return false;
//		}
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
		
		if (inv instanceof InventoryRFC)
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(((InventoryRFC)inv).getTile());
		return true;
	}
	
	public static boolean consumeFromInventory(ItemStack stack, IItemHandler inv) {
		
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack folder = inv.getStackInSlot(i);
			if (inv instanceof InventoryRFC)
				folder = ((InventoryRFC)inv).getFolder(i);
			
			if (!folder.isEmpty()) {
				CapabilityFolder cap = FolderUtils.get(folder).getCap();
				if (cap == null) continue;
				
				if (cap.isItemStack()) {
					if (stack.isItemEqual(cap.getItemStack())) {
						if (cap.getCount() > 0) {		
							boolean consume = true;
							ItemStack container = cap.getItemStack().getItem().getContainerItem(cap.getItemStack());
							if (!container.isEmpty() && (inv instanceof InventoryRFC)) {
								if (container == cap.getItemStack())
									consume = false;
								if (consume && !shuntContainerItem(container, inv)) {
									shuntContainerItemOutside(container, inv);
								}
							}
							if (consume) {
								cap.setCount(cap.getCount() - 1);
								if (cap.getCount() < 0)
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
			ItemStack folder = ((InventoryRFC)inv).getFolder(i);
			if (!folder.isEmpty() && folder.getItem() == RFCItems.FOLDER) {
				if (FolderUtils.get(folder).getObject() instanceof ItemStack && container.isItemEqual((ItemStack)FolderUtils.get(folder).getObject())) {
					FolderUtils.get(folder).add(1);
					return true;
				}
			}
		}
		return false;
	}
	
	private static void shuntContainerItemOutside(ItemStack container, IItemHandler inv) {
		
		TileFilingCabinet tile = (TileFilingCabinet)((InventoryRFC)inv).getTile();
		
		EntityItem ei = new EntityItem(tile.getWorld(), tile.getPos().getX() + 0.5, tile.getPos().getY() + 1.5, tile.getPos().getZ() + 0.5, container);
		tile.getWorld().spawnEntity(ei);
	}
}
