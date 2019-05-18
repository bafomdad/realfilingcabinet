package com.bafomdad.realfilingcabinet.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;

public class ItemMysteryFolder extends Item {
	
	private static List<ItemStack> rando = new ArrayList();
	
	//TODO: put this in a configurable list
	static {
		rando.add(new ItemStack(Items.DIAMOND, 1, 0));
		rando.add(new ItemStack(Items.APPLE, 1, 0));
		rando.add(new ItemStack(Blocks.COBBLESTONE, 1, 0));
		rando.add(new ItemStack(Items.BLAZE_ROD, 1, 0));
		rando.add(new ItemStack(Items.SLIME_BALL, 1, 0));
		rando.add(new ItemStack(Blocks.CLAY, 1, 0));
		rando.add(new ItemStack(Blocks.PRISMARINE, 1, 0));
		rando.add(new ItemStack(Items.RABBIT_FOOT, 1, 0));
		rando.add(new ItemStack(Blocks.TORCH, 1, 0));
	}
	
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
			if (rando == null || rando.isEmpty())
				return ActionResult.newResult(EnumActionResult.PASS, stack);
			
			ItemStack newFolder = new ItemStack(RFCItems.FOLDER, 1, 0);
			ItemStack toSet = rando.get(world.rand.nextInt(rando.size()));
			if (FolderUtils.get(newFolder).setObject(toSet)) {
				FolderUtils.get(newFolder).add(world.rand.nextInt(4));
				return ActionResult.newResult(EnumActionResult.SUCCESS, newFolder);
			}
		}
		return new ActionResult(EnumActionResult.PASS, player.getHeldItem(hand));
    }

	@Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
       
		return true;
    }
}
