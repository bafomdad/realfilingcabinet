package com.bafomdad.realfilingcabinet.items;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.init.RFCBlocks;
import com.bafomdad.realfilingcabinet.inventory.FluidRFC;

public class ItemDebugger extends Item {

	public ItemDebugger() {
		
		setRegistryName("debugger");
		setTranslationKey(RealFilingCabinet.MOD_ID + ".debugger");
		setMaxStackSize(1);
		setCreativeTab(TabRFC.instance);
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        
    	Block block = world.getBlockState(pos).getBlock();
    	if (block == RFCBlocks.blockRFC && hand == EnumHand.MAIN_HAND) {
    		TileEntityRFC tile = (TileEntityRFC)world.getTileEntity(pos);
    		if (tile == null)
    			return EnumActionResult.FAIL;
    		
    		String str = FMLCommonHandler.instance().getEffectiveSide().toString() + " : " + tile.upgrades;
    		player.sendMessage(new TextComponentString(str));
    		return EnumActionResult.SUCCESS;
    	}
    	ItemStack debugger = player.getHeldItem(EnumHand.OFF_HAND);
    	ItemStack thing = player.getHeldItem(EnumHand.MAIN_HAND);
    	if (!debugger.isEmpty() && debugger.getItem() == this) {
    		if (!thing.isEmpty() && !world.isRemote) {
    			String str = TextHelper.localize("message." + RealFilingCabinet.MOD_ID + ".debugger") + ": " + thing.getTranslationKey();
    			player.sendMessage(new TextComponentString(str));
    		}
    	}
    	return EnumActionResult.PASS;
    }
	
	@Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        
		if (!(target instanceof EntityPlayer)) {
			ResourceLocation res = EntityList.getKey(target);
			Entity entity = EntityList.createEntityByIDFromName(res, player.world);
			if (!player.world.isRemote)
				System.out.println("Entity: " + res.toString() + " / " + entity);
			return true;
		}
		return false;
    }
}
