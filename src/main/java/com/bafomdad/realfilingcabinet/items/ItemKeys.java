package com.bafomdad.realfilingcabinet.items;

import java.util.List;
import java.util.UUID;

import com.bafomdad.realfilingcabinet.api.ISubModel;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemKeys extends Item implements ISubModel {
	
	public final String[] KEYTYPES = new String[] { "master", "copy" };

	@Override
	public String getTranslationKey(ItemStack stack) {
		
		return getTranslationKey() + "_" + KEYTYPES[stack.getItemDamage()];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		
		if (isInCreativeTab(tab)) {
			list.add(new ItemStack(this, 1, 0));
			list.add(new ItemStack(this, 1, 1));
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag whatisthis) {
		
		if (stack.getItemDamage() == 1 && (stack.hasTagCompound() && stack.getTagCompound().hasKey(StringLibs.RFC_COPY))) {
			EntityPlayer onlinePlayer = world.getPlayerEntityByUUID(UUID.fromString(NBTUtils.getString(stack, StringLibs.RFC_COPY, "")));
			if (onlinePlayer != null)
				list.add("Original Owner: " + onlinePlayer.getName());
			else
				list.add("Original Ower: " + NBTUtils.getString(stack, StringLibs.RFC_FALLBACK, ""));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerSubModels(Item item) {
		
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName() + "_" + KEYTYPES[0], "inventory"));
		ModelLoader.setCustomModelResourceLocation(item, 1, new ModelResourceLocation(item.getRegistryName() + "_" + KEYTYPES[1], "inventory"));
	}
}
