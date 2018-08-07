package com.bafomdad.realfilingcabinet.items;

import java.time.LocalDate;
import java.time.Month;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.api.IFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.BlockRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.entity.EntityCabinet;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.init.RFCBlocks;
import com.bafomdad.realfilingcabinet.init.RFCItems;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemMagnifyingGlass extends Item {

	public ItemMagnifyingGlass() {
		
		setRegistryName("magnifyingglass");
		setTranslationKey(RealFilingCabinet.MOD_ID + ".magnifyingglass");
		setMaxStackSize(1);
		setCreativeTab(TabRFC.instance);
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
    	if (player.isSneaking() && block instanceof IFilingCabinet) {
    		if (!world.isRemote)
        		world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockRFC.FACING, player.getHorizontalFacing().getOpposite()), 2);

    		player.swingArm(hand);
    	}
    	else if (!player.isSneaking() && !world.isRemote) {
    		TileEntity tile = world.getTileEntity(pos);
    		if (tile != null && tile instanceof TileEntityRFC) {
    			TileEntityRFC tileRFC = (TileEntityRFC)tile;
    			if (!tileRFC.isOpen || UpgradeHelper.getUpgrade(tileRFC, StringLibs.TAG_ENDER) != null)
    				return EnumActionResult.PASS;
    			
    			if (tileRFC.isCabinetLocked() && !tileRFC.getCabinetOwner().equals(player.getUniqueID()))
    				if (!tileRFC.hasKeyCopy(player, tileRFC.getCabinetOwner()))
    					return EnumActionResult.PASS;
    			
    			player.openGui(RealFilingCabinet.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
    		}
    	}
    	return EnumActionResult.PASS;
    }
    
	@Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
       
		if (target != null && target instanceof EntityCabinet) {
			
			EntityCabinet cabinet = (EntityCabinet)target;
			if (!player.isSneaking()) {
				if (cabinet.getInventory() != null) {
					for (int i = 0; i < cabinet.getInventory().getSlots(); i++) {
						ItemStack stacky = cabinet.getInventory().getStackInSlot(i);
						if (stacky != null && stacky.getItem() == RFCItems.folder) {
							if (ItemFolder.getObject(stacky) != null) {
								String name = TextHelper.folderStr(stacky);
								long storedSize = ItemFolder.getFileSize(stacky);
								
								player.sendStatusMessage(new TextComponentString(name + " - " + storedSize), true);
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
