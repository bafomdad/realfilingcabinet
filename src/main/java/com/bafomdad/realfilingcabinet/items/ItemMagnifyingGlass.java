package com.bafomdad.realfilingcabinet.items;

import java.time.LocalDate;
import java.time.Month;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IBlockCabinet;
import com.bafomdad.realfilingcabinet.blocks.BlockRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemMagnifyingGlass extends Item {

	public ItemMagnifyingGlass() {
		
		this.addPropertyOverride(new ResourceLocation("booltime"), new IItemPropertyGetter() {
			
			@Override
			public float apply(ItemStack stack, World world, EntityLivingBase entity) {
				
				LocalDate date = LocalDate.now();
				if (date.getMonth() == Month.APRIL && date.getDayOfMonth() == 1)
					return 0.1F;
				
				return 0;
			}
		});
	}
	
	@Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        
    	Block block = world.getBlockState(pos).getBlock();
    	if (player.isSneaking() && block instanceof IBlockCabinet) {
    		if (!world.isRemote)
        		world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockRFC.FACING, player.getHorizontalFacing().getOpposite()), 2);

    		player.swingArm(hand);
    		return EnumActionResult.PASS;
    	}
    	else if (!player.isSneaking() && !world.isRemote) {
    		TileEntity tile = world.getTileEntity(pos);
    		if (tile != null && tile instanceof TileEntityRFC) {
    			TileEntityRFC tileRFC = (TileEntityRFC)tile;
    			if (!tileRFC.isOpen)
    				return EnumActionResult.PASS;
    			
    			if (tileRFC.isCabinetLocked() && !tileRFC.getOwner().equals(player.getUniqueID()))
    				if (!tileRFC.hasKeyCopy(player, tileRFC.getOwner()))
    					return EnumActionResult.PASS;
    			
    			player.openGui(RealFilingCabinet.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
    		}
    	}
    	return EnumActionResult.PASS;
    }
}
