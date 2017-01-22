package com.bafomdad.realfilingcabinet.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.inventory.FakeInventory;
import com.bafomdad.realfilingcabinet.inventory.InventoryRFC;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.network.VanillaPacketDispatcher;

public class AutocraftingUtils {
	
	private static int outputSize;

	private static IRecipe getRecipeFor(ItemStack stack) {
		
		if (stack != null) {
			for (IRecipe recipe : (List<IRecipe>)(CraftingManager.getInstance().getRecipeList())) {
	    		if ((recipe instanceof ShapedRecipes || recipe instanceof ShapedOreRecipe || recipe instanceof ShapelessRecipes || recipe instanceof ShapelessOreRecipe) 
	    				&& recipe.getRecipeOutput().getItem() == stack.getItem()
	    				&& (!recipe.getRecipeOutput().getHasSubtypes() || recipe.getRecipeOutput().getItemDamage() == stack.getItemDamage())) {
	    			return recipe;
	    		}
			}
		}
		return null;
	}
	
	private static IInventory getFakeInv(TileEntityRFC tile) {
		
		FakeInventory tempInv = new FakeInventory(tile.getInventory().getSlots());
		tempInv.copyFrom(tile.getInventory());
		return tempInv;
	}
	
	public static int getOutputSize() {
		
		return outputSize;
	}
	
	public static boolean canCraft(ItemStack input, TileEntityRFC tile) {
		
		if (input == null)
			return false;
		
		return consumeRecipeIngredients(input, (IItemHandler)getFakeInv(tile));
	}
	
	public static void doCraft(ItemStack input, IItemHandler inv) {
		
		consumeRecipeIngredients(input, inv);
	}
	
	public static boolean consumeRecipeIngredients(ItemStack input, IItemHandler inv) {
		
		IRecipe recipe = getRecipeFor(input);
		if (recipe != null) 
		{
			if (getRecipeItems(recipe) != null) {
				ItemStack[] recipeList = getRecipeItems(recipe);
//				System.out.println(Arrays.asList(recipeList).toString() + " / " + recipe.getRecipeOutput());
				for (int i = 0; i < recipeList.length; i++) {
					ItemStack ingredient = recipeList[i];
//					System.out.println(ingredient.getDisplayName());
					if (ingredient != null && ingredient.stackSize > 1)
						ingredient.stackSize = 1;
					if (ingredient != null && (ingredient.getItemDamage() == -1 || ingredient.getItemDamage() == Short.MAX_VALUE))
						ingredient.setItemDamage(0);
					if (ingredient != null && !consumeFromInventory(ingredient, inv))
						return false;
				}
			}
			if (getShapelessRecipeItems(recipe) != null) {
				List<ItemStack> recipeList = getShapelessRecipeItems(recipe);
				for (int i = 0; i < recipeList.size(); i++) {
					ItemStack ingredient = recipeList.get(i);
					if (ingredient != null && ingredient.stackSize > 1)
						ingredient.stackSize = 1;
					if (ingredient != null && (ingredient.getItemDamage() == -1 || ingredient.getItemDamage() == Short.MAX_VALUE))
						ingredient.setItemDamage(0);
					if (ingredient != null && !consumeFromInventory(ingredient, inv))
						return false;
				}
			}
			outputSize = recipe.getRecipeOutput().stackSize;
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
			
			if (folder != null && folder.getItem() == RFCItems.folder) {
				if (ItemFolder.getObject(folder) instanceof ItemStack)
				{
					if (ItemFolder.getObject(folder) != null && (stack.isItemEqual((ItemStack)ItemFolder.getObject(folder)))) {
						if (ItemFolder.getFileSize(folder) > 0)
						{
							boolean consume = true;
							
							ItemStack container = ((ItemStack)ItemFolder.getObject(folder)).getItem().getContainerItem((ItemStack)ItemFolder.getObject(folder));
							if (container != null && (inv instanceof InventoryRFC)) {
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
			if (folder != null && folder.getItem() == RFCItems.folder) {
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
		tile.getWorld().spawnEntityInWorld(ei);
	}

	public static ItemStack[] getRecipeItems(IRecipe recipe) {
		
		if (recipe instanceof ShapedRecipes)
		{
			return getRecipeItemsShaped((ShapedRecipes)recipe);
		}
		if (recipe instanceof ShapedOreRecipe)
		{
			return getRecipeItemsOre((ShapedOreRecipe)recipe);
		}
		return null;
	}
	
	public static ItemStack[] getRecipeItemsOre(ShapedOreRecipe shaped) {
		
		try 
		{
			Object[] objects = ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shaped, 3);
			ItemStack[] items = new ItemStack[objects.length];
			
			for (int i = 0; i < objects.length; i++)
			{
				if (objects[i] instanceof ItemStack)
				{
					items[i] = (ItemStack)objects[i];
				}
				if (objects[i] instanceof ArrayList && ((ArrayList<ItemStack>)objects[i]).size() > 0)
				{
					items[i] = ((ArrayList<ItemStack>)objects[i]).get(0);
				}
				else if (objects[i] instanceof List)
				{
					Iterator<ItemStack> itr = ((List<ItemStack>)objects[i]).iterator();
					while (itr.hasNext())
					{
						items[i] = itr.next();
						break;
					}
				}
			}
			return items;
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ItemStack[] getRecipeItemsShaped(ShapedRecipes shaped) {
		
		return shaped.recipeItems;
	}
	
	public static List getShapelessRecipeItems(IRecipe recipe) {
		
		if (recipe instanceof ShapelessRecipes)
		{
			return getRecipeItemsShapeless((ShapelessRecipes)recipe);
		}
		if (recipe instanceof ShapelessOreRecipe)
		{
			return getRecipeItemsShapelessOre((ShapelessOreRecipe)recipe);
		}
		return null;
	}
	
	public static List getRecipeItemsShapeless(ShapelessRecipes shapeless) {
		
		return shapeless.recipeItems;
	}
	
	public static List getRecipeItemsShapelessOre(ShapelessOreRecipe shapeless) {
		
		try {
			ArrayList<Object> objects = ObfuscationReflectionHelper.getPrivateValue(ShapelessOreRecipe.class, shapeless, 1);
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			
			for (int i = 0; i < objects.size(); i++)
			{
				if (objects.get(i) instanceof ItemStack)
				{
					items.add((ItemStack)objects.get(i));
				}
			}
			return items;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
}
