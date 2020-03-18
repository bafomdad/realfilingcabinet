package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWhiteoutTape extends Item {

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag whatisthis) {
		
		list.add(new TextComponentTranslation("tooltip." + RealFilingCabinet.MOD_ID + ".whiteouttape").getFormattedText());
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		
		ItemStack copy = stack.copy();
		copy.setItemDamage(copy.getItemDamage() + 1);
		if (copy.getItemDamage() >= copy.getMaxDamage())
			copy = ItemStack.EMPTY;
		
		return copy;
	}
	
	@Override
	public boolean hasContainerItem(ItemStack stack) {
		
		return stack.getItem() == this;
	}
}
