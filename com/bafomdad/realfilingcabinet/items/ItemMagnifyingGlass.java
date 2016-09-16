package com.bafomdad.realfilingcabinet.items;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;

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
	
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        
    	TileEntity tile = world.getTileEntity(pos);
    	if (player.isSneaking() && (tile != null && tile instanceof IInventory))
    	{
    		IInventory tileInv = (IInventory)tile;
    		for (int i = 0; i < tileInv.getSizeInventory(); i++) {
    			ItemStack loopinv = tileInv.getStackInSlot(i);
    			if (loopinv != null && !world.isRemote)
    			{
    				System.out.println("Slot #" + i + ": " + loopinv.getDisplayName() + " " + loopinv.stackSize + "x");
    			}
    		}
    		return EnumActionResult.SUCCESS;
    	}
    	return EnumActionResult.PASS;
    }
}
