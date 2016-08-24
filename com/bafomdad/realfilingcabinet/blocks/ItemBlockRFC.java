package com.bafomdad.realfilingcabinet.blocks;

import java.util.List;

import org.lwjgl.input.Keyboard;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemBlockRFC extends ItemBlock {

	public ItemBlockRFC(Block block) {
		
		super(block);
	}
	
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean info) {
		
		if (stack.hasTagCompound())
		{
			list.add("<Shift for more info>");
			if ((Keyboard.isKeyDown(42)) || (Keyboard.isKeyDown(54))) {
				listItems(stack, list);
			} else if (list.size() > 2) {
				list.remove(2);
			}
		}
	}
	
	private void listItems(ItemStack stack, List list) {
		
		NBTTagList tagList = stack.stackTagCompound.getTagList("inventory", 10);
		for (int i = 0; i < tagList.tagCount(); i++)
		{
    		NBTTagCompound itemTag = tagList.getCompoundTagAt(i);
    		ItemStack folder = ItemStack.loadItemStackFromNBT(itemTag);
    		if (folder != null && folder.getItem() == RealFilingCabinet.itemFolder)
    		{
//    			String name = StatCollector.translateToLocal(ItemFolder.getStack(folder).getUnlocalizedName() + ".name");
    			String name = ItemFolder.getStack(folder).getDisplayName();
    			int quantity = ItemFolder.getFileSize(folder);
    			
    			list.add(quantity + " " + name);
    		}
		}
	}
}
