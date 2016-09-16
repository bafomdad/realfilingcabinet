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
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.utils.EnderUtils;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;

public class FolderExtractRecipe extends ShapelessRecipes implements IRecipe {
	
	public static List<ItemStack> input = new ArrayList();
	private boolean canSync = false;
	private ItemStack foldy = null;
	
	static
	{
		input.add(new ItemStack(RFCItems.folder));
	}
	
	public FolderExtractRecipe() {
		
		super(new ItemStack(RFCItems.folder), input);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public boolean matches(InventoryCrafting ic, World world) {
		
		ArrayList list = new ArrayList(this.input);
		
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				
				ItemStack stack = ic.getStackInRowAndColumn(j, i);
				if (stack != null)
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
			if (stack != null)
			{
				folder = i;
			}
		}
		if (folder >= 0)
		{
			ItemStack stack = ic.getStackInSlot(folder);
			
			if (stack.getItem() == RFCItems.folder && ItemFolder.getObject(stack) != null)
			{
				long count = ItemFolder.getFileSize(stack);
				if (count > 0)
				{
					ItemStack folderStack = (ItemStack)ItemFolder.getObject(stack);
					int meta = folderStack.getItemDamage();
					long extract = Math.min(folderStack.getMaxStackSize(), count);
					
					if (stack.getItemDamage() == 1) {
						foldy = stack;
						canSync = true;
					}
					return new ItemStack(folderStack.getItem(), (int)extract, meta);
				}
			}
		}
		return null;
	}
	
	@SubscribeEvent
	public void enderFolderExtract(PlayerEvent.ItemCraftedEvent event) {
		
		if (!event.player.worldObj.isRemote && canSync)
		{
			if (foldy != null)
			{
				EnderUtils.syncToFolder(EnderUtils.getTileLoc(foldy), NBTUtils.getInt(foldy, StringLibs.RFC_DIM, 0), NBTUtils.getInt(foldy, StringLibs.RFC_SLOTINDEX, 0), ItemFolder.extractSize);
			}
			canSync = false;
		}
	}
	
	@SubscribeEvent
	public void mouseWheelEvent(GuiScreenEvent.MouseInputEvent.Pre event) {
		
		int wheel = Mouse.getEventDWheel();
		GuiScreen gui = event.getGui();
		
		if (gui instanceof GuiContainer)
		{
			if (wheel != 0)
			{
				Slot slot = ((GuiContainer)gui).getSlotUnderMouse();
				if (slot != null && slot.inventory instanceof InventoryCraftResult)
				{
					ItemStack stack = slot.getStack();
					if (stack != null)
					{
						if (wheel > 0 && (stack.stackSize += 8) <= 64)
							stack.stackSize += 8;
						if (wheel < 0 && (stack.stackSize -= 8) >= 8)
							stack.stackSize -= 8;
					}
				}
			}
		}
	}
}
