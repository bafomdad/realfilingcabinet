package com.bafomdad.realfilingcabinet.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.TileEntityRFC;
import com.bafomdad.realfilingcabinet.gui.FakeInventory;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.network.RFCPacketHandler;
import com.bafomdad.realfilingcabinet.network.RFCTileMessage;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class StorageUtils {
	
	private static final StorageUtils INSTANCE = new StorageUtils();
	private int outSize;
	private int outputSize;
	
	public static StorageUtils instance() {
		
		return INSTANCE;
	}
	
	public void addStackManually(TileEntityRFC tile, ItemStack stack, EntityPlayer player) {
		
		for (int i = 0; i < tile.getSizeInventory() - 2; i++)
		{
			ItemStack validFolder = tile.getStackInSlot(i);
			
			if (validFolder != null && validFolder.getItem() == RealFilingCabinet.itemFolder)
			{
				if (stack.getItem() == ItemFolder.getStack(validFolder).getItem() && stack.getItemDamage() == ItemFolder.getFileMeta(validFolder))
				{
					ItemFolder.add(validFolder, stack.stackSize);
					player.setCurrentItemOrArmor(0, null);
					tile.markDirty();
					break;
				}
			}
		}
	}
	
	public void addAllStacksManually(TileEntityRFC tile, EntityPlayer player) {
		
		boolean consume = false;
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack loopinv = player.inventory.getStackInSlot(i);
			if (loopinv != null && (loopinv.getItem() != RealFilingCabinet.itemFolder || loopinv.getItem() != RealFilingCabinet.itemEmptyFolder))
			{
				for (int j = 0; j < tile.getSizeInventory() - 2; j++) {
					ItemStack folder = tile.getStackInSlot(j);
					if (folder != null && folder.getItem() == RealFilingCabinet.itemFolder) {
						if (ItemFolder.getStack(folder) != null && ItemFolder.getStack(folder).getItem() == loopinv.getItem() && ItemFolder.getFileMeta(folder) == loopinv.getItemDamage())
						{
							ItemFolder.add(folder, loopinv.stackSize);
							player.inventory.setInventorySlotContents(i, null);
							consume = true;
							break;
						}
					}
				}
			}
		}
		if (consume)
		{
			RealFilingCabinet.proxy.updatePlayerInventory(player);
			tile.markDirty();
		}
	}
	
	public void extractStackManually(TileEntityRFC tile, ItemStack stack, EntityPlayer player) {
		
		for (int i = 0; i < tile.getSizeInventory() - 2; i++)
		{
			ItemStack validFolder = tile.getStackInSlot(i);
			
			if (validFolder != null && validFolder.getItem() == RealFilingCabinet.itemFolder)
			{
				if (stack.getItem() == ItemFolder.getStack(validFolder).getItem() && stack.getItemDamage() == ItemFolder.getFileMeta(validFolder))
				{
					int size = 1;
					if (player.isSneaking())
					{
						if (ItemFolder.getFileSize(validFolder) >= 64)
							size = 64;
						else
							size = ItemFolder.getFileSize(validFolder);
					}
					ItemFolder.remove(validFolder, size);
					player.setCurrentItemOrArmor(0, new ItemStack(ItemFolder.getStack(validFolder).getItem(), size, ItemFolder.getFileMeta(validFolder)));
					tile.markDirty();
					break;
				}
			}
		}
	}
	
	public void addFolderManually(TileEntityRFC tile, ItemStack stack, EntityPlayer player) {
		
		for (int i = 0; i < tile.getSizeInventory() - 2; i++)
		{
			ItemStack tileStack = tile.getStackInSlot(i);
			if (tileStack == null)
			{
				tile.setInventorySlotContents(i, stack);
				player.setCurrentItemOrArmor(0, null);
				Utils.dispatchTEToNearbyPlayers(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
				break;
			}
		}
	}
	
	public void decreaseFolderSize(TileEntityRFC tile, ItemStack stack, int size) {
		
		for (int i = 0; i < tile.getSizeInventory() - 2; i++) {
			ItemStack folder = tile.getStackInSlot(i);
			if (folder != null && folder.getItem() == RealFilingCabinet.itemFolder)
			{
				if (ItemFolder.getStack(folder) != null)
				{
					if (stack.getItem() == ItemFolder.getStack(folder).getItem() && stack.getItemDamage() == ItemFolder.getFileMeta(folder))
					{
						ItemFolder.remove(folder, size);
						break;
					}
				}
			}
		}
	}
	
	public ItemStack findMatchingStack(TileEntityRFC tile, ItemStack stack, int size) {
		
		ItemStack matchStack = null;
		
		for (int i = 0; i < tile.getSizeInventory() - 2; i++)
		{
			ItemStack loopitem = tile.getStackInSlot(i);
			if (loopitem != null)
			{
				if (ItemFolder.getStack(loopitem) != null) {
					ItemStack extractedStack = new ItemStack(ItemFolder.getStack(loopitem).getItem(), size, ItemFolder.getFileMeta(loopitem));
					if (stack.getItem() == ItemFolder.getStack(loopitem).getItem() && stack.getItemDamage() == ItemFolder.getFileMeta(loopitem) && ItemFolder.getFileSize(loopitem) >= size)
					{
						matchStack = extractedStack;
						if (!tile.isCreative)
						{
							ItemFolder.remove(loopitem, size);
						}
						break;
					}
				}
			}
		}
		return matchStack;
	}
	
	public boolean canFolderProvide(TileEntityRFC tile, ItemStack stack, int size) {
		
		for (int i = 0; i < tile.getSizeInventory() - 2; i++) {
			ItemStack folder = tile.getStackInSlot(i);
			if (folder != null && folder.getItem() == RealFilingCabinet.itemFolder)
			{
				if (ItemFolder.getStack(folder) != null)
				{
					if (stack.getItem() == ItemFolder.getStack(folder).getItem() && stack.getItemDamage() == ItemFolder.getFileMeta(folder))
					{
						if (ItemFolder.getFileSize(folder) > 0 && ItemFolder.getFileSize(folder) >= size) {
							if (ItemFolder.getFileSize(folder) < 64)
								outSize = ItemFolder.getFileSize(folder);
							else if (ItemFolder.getFileSize(folder) > 64)
								outSize = 64;
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public int getFileSize() {
	
		return outSize;
	}
	
	public boolean simpleMatch(ItemStack stack1, ItemStack stack2) {
		
		return stack1.getItem().equals(stack2.getItem()) && stack1.getItemDamage() == stack2.getItemDamage();
	}
	
	//CraftingUtils begin
	
	private IRecipe getRecipeFor(ItemStack stack) {
		
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
	
	public IInventory getFakeInv(TileEntityRFC tile) {
		
		FakeInventory tempInv = new FakeInventory("temp", false, tile.getSizeInventory() - 2);
		tempInv.copyFrom(tile);
		return tempInv;
	}
	
	public int getOutputSize() {
		
		return outputSize;
	}
	
	public boolean canCraft(ItemStack input, TileEntityRFC tile) {
		
		return consumeRecipeIngredients(input, getFakeInv(tile));
	}
	
	public void doCraft(ItemStack input, IInventory inv) {
		
		consumeRecipeIngredients(input, inv);
	}
	
	public boolean consumeRecipeIngredients(ItemStack input, IInventory inv) {
		
		IRecipe recipe = getRecipeFor(input);
		if (recipe != null) 
		{
			if (getRecipeItems(recipe) != null) {
				ItemStack[] recipeList = getRecipeItems(recipe);
				for (int i = 0; i < recipeList.length; i++) {
					ItemStack ingredient = recipeList[i];
					if (ingredient != null && ingredient.stackSize > 1)
						ingredient.stackSize = 1;
					if (ingredient != null && (ingredient.getItemDamageForDisplay() == -1 || ingredient.getItemDamageForDisplay() == Short.MAX_VALUE))
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
					if (ingredient != null && (ingredient.getItemDamageForDisplay() == -1 || ingredient.getItemDamageForDisplay() == Short.MAX_VALUE))
						ingredient.setItemDamage(0);
					if (ingredient != null && !consumeFromInventory(ingredient, inv))
						return false;
				}
			}
			outputSize = recipe.getRecipeOutput().stackSize;
		}
		return true;
	}
	
	public boolean consumeFromInventory(ItemStack stack, IInventory inv) {
		
		for (int i = 0; i < inv.getSizeInventory() - 2; i++) {
			ItemStack folder = inv.getStackInSlot(i);
			if (folder != null && folder.getItem() == RealFilingCabinet.itemFolder) {
				if (ItemFolder.getStack(folder) != null && stack.isItemEqual(ItemFolder.getStack(folder))) {
					if (ItemFolder.getFileSize(folder) > 0)
					{
						boolean consume = true;
						
						ItemStack container = ItemFolder.getStack(folder).getItem().getContainerItem(ItemFolder.getStack(folder));
						if (container != null && !shuntContainerItem(container, inv))
							shuntContainerItemOutside(container, inv);
						
						if (consume) {
							ItemFolder.remove(folder, 1);
							if (ItemFolder.getFileSize(folder) == 0)
								consume = false;
						}
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private boolean shuntContainerItem(ItemStack container, IInventory inv) {
		
		for (int i = 0; i < inv.getSizeInventory() - 2; i++) {
			ItemStack folder = inv.getStackInSlot(i);
			if (folder != null && folder.getItem() == RealFilingCabinet.itemFolder) {
				if (ItemFolder.getStack(folder) != null && container.isItemEqual(ItemFolder.getStack(folder))) {
					ItemFolder.add(folder, 1);
					return true;
				}
			}
		}
		return false;
	}
	
	private void shuntContainerItemOutside(ItemStack container, IInventory inv) {
		
		if (inv instanceof TileEntityRFC)
		{
			TileEntityRFC tile = (TileEntityRFC)inv;
			
			EntityItem ei = new EntityItem(tile.getWorldObj(), tile.xCoord, tile.yCoord + 1, tile.zCoord, container);
			tile.getWorldObj().spawnEntityInWorld(ei);
		}
	}

	public ItemStack[] getRecipeItems(IRecipe recipe) {
		
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
	
	public ItemStack[] getRecipeItemsOre(ShapedOreRecipe shaped) {
		
		try {
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
			}
			return items;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ItemStack[] getRecipeItemsShaped(ShapedRecipes shaped) {
		
		return shaped.recipeItems;
	}
	
	public List getShapelessRecipeItems(IRecipe recipe) {
		
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
	
	public List getRecipeItemsShapeless(ShapelessRecipes shapeless) {
		
		return shapeless.recipeItems;
	}
	
	public List getRecipeItemsShapelessOre(ShapelessOreRecipe shapeless) {
		
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
	
	//EnderUtils begin
	
	public void syncToFolder(TileEntityRFC tile, ItemStack stack, int index) {
		
		ItemStack folder = tile.getStackInSlot(index);
		int folderSize = ItemFolder.getFileSize(folder);
		
		if (folderSize != ItemFolder.getFileSize(stack))
		{
			ItemFolder.setFileSize(stack, folderSize);
		}
	}
	
	public void syncToFolder(TileEntityRFC tile, int index) {
		
		ItemStack folder = tile.getStackInSlot(index);
		if (folder.getItem() != RealFilingCabinet.itemFolder)
			return;
		
		int x = tile.xCoord;
		int y = tile.yCoord;
		int z = tile.zCoord;
		
		int currAmount = ItemFolder.getFileSize(folder);
		
		RFCPacketHandler.INSTANCE.sendToServer(new RFCTileMessage(x, y, z, currAmount, index));
	}
	
	public void syncBackToTile(World world, ItemStack folder, int amount) {
		
		if (folder.getItem() != RealFilingCabinet.itemFolder)
			return;
		
		int x = Utils.getInt(folder, "RFC_xLoc", -1);
		int y = Utils.getInt(folder, "RFC_yLoc", -1);
		int z = Utils.getInt(folder, "RFC_zLoc", -1);
		
		int index = Utils.getInt(folder, ItemFolder.TAG_SLOTINDEX, 0);
		
		TileEntityRFC tile = (TileEntityRFC)world.getTileEntity(x, y, z);
		if (tile != null)
		{
			ItemStack tileStack = tile.getStackInSlot(index);
			if (tileStack != null)
			{
				ItemFolder.remove(tileStack, amount);
//				updateTileOutput(tile, folder, index);
			}
		}
	}
	
	public int syncRecipeOutput(ItemStack folder, ItemStack output) {
		
		int size = ItemFolder.getFileSize(folder);
		
		return output.stackSize = Math.min(64, size);
	}
	
	public void syncBackToTile(TileEntityRFC tile, ItemStack stack, int index) {
		
		ItemStack folder = tile.getStackInSlot(index);
		int folderSize = ItemFolder.getFileSize(stack);
		
		if (folderSize != ItemFolder.getFileSize(folder))
		{
			ItemFolder.setFileSize(folder, folderSize);
//			updateTileOutput(tile, folder, index);
		}
	}
	
	private void updateTileOutput(TileEntityRFC tile, int index) {
		
		ItemStack folder = tile.getStackInSlot(index);
		int folderSize = ItemFolder.getFileSize(folder);
		
		if (folderSize >= 64)
			return;
		
		else if (folderSize < 64)
		{
			if (folderSize == 0)
			{
				tile.setInventorySlotContents(9, null);
				return;
			}
			ItemStack newStack = new ItemStack(ItemFolder.getStack(folder).getItem(), folderSize, ItemFolder.getStack(folder).getItemDamage());
			tile.setInventorySlotContents(9, newStack);
		}
	}
	
	public void syncRecipes(TileEntityRFC tile, ItemStack folder, ItemStack stack) {
		
		decreaseFolderSize(tile, stack, stack.stackSize);
		updateTileOutput(tile, Utils.getInt(folder, ItemFolder.TAG_SLOTINDEX, 0));
	}
}
