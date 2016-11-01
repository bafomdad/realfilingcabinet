package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.api.IEmptyFolder;
import com.bafomdad.realfilingcabinet.init.RFCItems;

public class ItemEmptyFolder extends Item implements IEmptyFolder {
	
	public String[] folderType = new String[] { "normal", "dura", "mob" };
	
	public ItemEmptyFolder() {
		
		setRegistryName("emptyfolder");
		setUnlocalizedName(RealFilingCabinet.MOD_ID + ".emptyfolder");
		setMaxStackSize(8);
		setCreativeTab(TabRFC.instance);
		GameRegistry.register(this);
	}
	
	public String getUnlocalizedName(ItemStack stack) {
		
		return getUnlocalizedName() + "_" + folderType[stack.getItemDamage()];
	}
	
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tabs, List list) {
		
		for (int i = 0; i < folderType.length; ++i)
			list.add(new ItemStack(item, 1, i));
	}
	
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
		
		if (!player.worldObj.isRemote)
		{
			if (stack.getItemDamage() == 2)
			{
				ItemStack newFolder = new ItemStack(RFCItems.folder, 1, 3);
				if (ItemFolder.setObject(newFolder, target)) {
					player.setHeldItem(hand, newFolder);
					target.setDead();
					return true;
				}
			}
		}
		return false;
	}
}
