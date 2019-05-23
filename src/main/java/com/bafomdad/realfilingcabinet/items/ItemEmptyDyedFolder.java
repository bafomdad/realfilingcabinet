package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import com.bafomdad.realfilingcabinet.api.ISubModel;
import com.bafomdad.realfilingcabinet.api.IEmptyFolder;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCItems;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEmptyDyedFolder extends Item implements ISubModel, IEmptyFolder {
	
	@Override
	public String getTranslationKey(ItemStack stack) {
		
		return getTranslationKey() + "." + EnumDyeColor.values()[stack.getItemDamage()].getName().toLowerCase();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		
		if (isInCreativeTab(tab)) {
			for (int i = 0; i < EnumDyeColor.values().length; ++i)
				list.add(new ItemStack(this, 1, i));
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World player, List<String> list, ITooltipFlag whatisthis) {
		
		list.add(new TextComponentTranslation(StringLibs.TOOLTIP + ".emptyfolder0").getFormattedText());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerSubModels(Item item) {

		for (int i = 0; i < EnumDyeColor.values().length; ++i)
			ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

	@Override
	public ItemStack getFilledFolder(ItemStack stack) {

		return new ItemStack(RFCItems.DYEDFOLDER, 1, stack.getItemDamage());
	}

	@Override
	public boolean canRecipeTakeStack(ItemStack folder, ItemStack recipeStack) {

		return !recipeStack.hasTagCompound() && !recipeStack.isItemDamaged();
	}
}
