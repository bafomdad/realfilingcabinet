package com.bafomdad.realfilingcabinet.items;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.blocks.BlockRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.entity.EntityCabinet;
import com.bafomdad.realfilingcabinet.init.RFCBlocks;
import com.bafomdad.realfilingcabinet.init.RFCItems;

public class ItemMagnifyingGlass extends Item {

	public ItemMagnifyingGlass() {
		
		setRegistryName("magnifyingglass");
		setUnlocalizedName(RealFilingCabinet.MOD_ID + ".magnifyingglass");
		setMaxStackSize(1);
		setCreativeTab(TabRFC.instance);
		GameRegistry.register(this);
	}
	
	@Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        
    	Block block = world.getBlockState(pos).getBlock();
		TileEntityRFC tile = (TileEntityRFC)world.getTileEntity(pos);
    	if (player.isSneaking() && block == RFCBlocks.blockRFC)
    	{
    		if (!world.isRemote && tile != null)
    		{
    			world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockRFC.FACING, player.getHorizontalFacing().getOpposite()));
        		return EnumActionResult.SUCCESS;
    		}
    	}
    	return EnumActionResult.PASS;
    }
    
	@Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
       
		if (target != null && target instanceof EntityCabinet) {
			
			EntityCabinet cabinet = (EntityCabinet)target;
			if (!player.isSneaking())
			{
				if (cabinet.getInventory() != null)
				{
					for (int i = 0; i < cabinet.getInventory().getSlots(); i++) {
						ItemStack stacky = cabinet.getInventory().getStackInSlot(i);
						if (stacky != null && stacky.getItem() == RFCItems.folder) {
							if (ItemFolder.getObject(stacky) != null && ItemFolder.getObject(stacky) instanceof ItemStack)
							{
								String name = ((ItemStack)ItemFolder.getObject(stacky)).getDisplayName();
								long storedSize = ItemFolder.getFileSize(stacky);
								
								player.addChatMessage(new TextComponentString(name + " - " + storedSize));
							}
						}
					}
					return true;
				}
			}
		}
		return false;
    }
}
