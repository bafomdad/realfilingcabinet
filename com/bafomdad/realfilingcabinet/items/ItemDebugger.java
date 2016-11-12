package com.bafomdad.realfilingcabinet.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.init.RFCBlocks;

public class ItemDebugger extends Item {

	public ItemDebugger() {
		
		setRegistryName("debugger");
		setUnlocalizedName(RealFilingCabinet.MOD_ID + ".debugger");
		setMaxStackSize(1);
		setCreativeTab(TabRFC.instance);
		GameRegistry.register(this);
	}
	
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        
    	Block block = world.getBlockState(pos).getBlock();
    	if (block == RFCBlocks.blockRFC && hand == EnumHand.MAIN_HAND)
    	{
    		TileEntityRFC tile = (TileEntityRFC)world.getTileEntity(pos);
    		if (tile == null)
    			return EnumActionResult.FAIL;
    		
    		String str = FMLCommonHandler.instance().getEffectiveSide().toString() + " : " + tile.upgrades;
    		player.addChatMessage(new TextComponentString(str));
    	}
    	ItemStack debugger = player.getHeldItem(EnumHand.OFF_HAND);
    	ItemStack thing = player.getHeldItem(EnumHand.MAIN_HAND);
    	if (debugger != null && debugger.getItem() == this) {
    		if (thing != null && !world.isRemote)
    		{
    			String str = "Unlocalized name for item in main hand: " + thing.getUnlocalizedName();
    			player.addChatMessage(new TextComponentString(str));
    		}
    	}
    	return EnumActionResult.PASS;
    }
}
