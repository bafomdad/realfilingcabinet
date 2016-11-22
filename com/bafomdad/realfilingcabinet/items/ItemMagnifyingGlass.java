package com.bafomdad.realfilingcabinet.items;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.blocks.BlockRFC;
import com.bafomdad.realfilingcabinet.init.RFCBlocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemMagnifyingGlass extends Item {

	public ItemMagnifyingGlass() {
		
		setRegistryName("magnifyingglass");
		setUnlocalizedName(RealFilingCabinet.MOD_ID + ".magnifyingglass");
		setMaxStackSize(1);
		setCreativeTab(TabRFC.instance);
		GameRegistry.register(this);
	}
	
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        
    	Block block = world.getBlockState(pos).getBlock();
    	if (player.isSneaking() && block == RFCBlocks.blockRFC)
    	{
    		if (!world.isRemote)
    		{
        		world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockRFC.FACING, player.getHorizontalFacing().getOpposite()));
        		return EnumActionResult.SUCCESS;
    		}
    	}
    	return EnumActionResult.PASS;
    }
}
