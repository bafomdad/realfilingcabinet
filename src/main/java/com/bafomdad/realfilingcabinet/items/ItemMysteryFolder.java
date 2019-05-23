package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;

public class ItemMysteryFolder extends Item {
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag whatisthis) {
		
		list.add(new TextComponentTranslation(StringLibs.TOOLTIP +  ".mystery1").getFormattedText());
		list.add(new TextComponentTranslation(StringLibs.TOOLTIP + ".mystery2").getFormattedText());
	}
	
	@Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        
		ItemStack stack = player.getHeldItemMainhand();
		if (!stack.isEmpty() && stack.getItem() == this) {
			if (ConfigRFC.mysteryItems == null || ConfigRFC.mysteryItems.length <= 0)
				return ActionResult.newResult(EnumActionResult.PASS, stack);
			
			ItemStack newFolder = new ItemStack(RFCItems.FOLDER, 1, 0);
			ItemStack toSet = getMysteryItem(world);
			if (FolderUtils.get(newFolder).setObject(toSet)) {
				FolderUtils.get(newFolder).add(world.rand.nextInt(ConfigRFC.maxLootChance));
				return ActionResult.newResult(EnumActionResult.SUCCESS, newFolder);
			}
		}
		return new ActionResult(EnumActionResult.PASS, player.getHeldItem(hand));
    }
	
	private ItemStack getMysteryItem(World world) {
		
		ItemStack loot = ItemStack.EMPTY;
		
		String str = ConfigRFC.mysteryItems[world.rand.nextInt(ConfigRFC.mysteryItems.length)];
		String[] split = str.split(":");
		Item item = Items.AIR;
		if (split.length > 2)
			item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(split[0] + split[1]));
		else
			item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(str));
		if (item != null && item != Items.AIR) {
			if (split.length > 2)
				loot = new ItemStack(item, 1, Integer.parseInt(split[2]));
			else
				loot = new ItemStack(item);
		}
		Block block = Blocks.AIR;
		if (split.length > 2)
			block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(split[0] + split[1]));
		else
			block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(str));
		if (block != null && block != Blocks.AIR) {
			if (split.length > 2)
				loot = new ItemStack(block, 1, Integer.parseInt(split[2]));
			else
				loot = new ItemStack(block);
		}
		return loot;
	}

	@Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
       
		return true;
    }
}
