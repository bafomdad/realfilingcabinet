package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import com.bafomdad.realfilingcabinet.NewConfigRFC.ConfigRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.api.IEmptyFolder;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.utils.MobUtils;

public class ItemEmptyFolder extends Item implements IEmptyFolder {
	
	public enum FolderType {
		NORMAL,
		DURA,
		MOB,
		FLUID,
		NBT;
	}
	
	public ItemEmptyFolder() {
		
		setRegistryName("emptyfolder");
		setTranslationKey(RealFilingCabinet.MOD_ID + ".emptyfolder");
		setMaxStackSize(8);
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(TabRFC.instance);
	}
	
	@Override
	public String getTranslationKey(ItemStack stack) {
		
		return getTranslationKey() + "_" + FolderType.values()[stack.getItemDamage()].toString().toLowerCase();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		
		if (isInCreativeTab(tab)) {
			for (int i = 0; i < FolderType.values().length; ++i)
				list.add(new ItemStack(this, 1, i));
		}
	}
	
	@Override
	public void addInformation(ItemStack stack, World player, List list, ITooltipFlag whatisthis) {
	
		switch(stack.getItemDamage())
		{
			case 1: list.add(TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".emptyfolder1")); return;
			case 2: list.add(TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".emptyfolder2")); 
			if (!ConfigRFC.mobUpgrade)
				list.add(TextHelper.localize("message." + RealFilingCabinet.MOD_ID + ".disabled")); return;
			case 3: list.add(TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".emptyfolder3")); return;
			default: list.add(TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".emptyfolder0"));
		}
	}
	
	@Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        
		ItemStack stack = player.getHeldItemMainhand();
		if (stack != ItemStack.EMPTY && stack.getItemDamage() != 3)
			return ActionResult.newResult(EnumActionResult.PASS, stack);
		
		RayTraceResult rtr = rayTrace(world, player, true);
		if (rtr == null)
			return ActionResult.newResult(EnumActionResult.PASS, stack);
		
		if (!MobUtils.canPlayerChangeStuffHere(world, player, stack, rtr.getBlockPos(), rtr.sideHit))
			return ActionResult.newResult(EnumActionResult.PASS, stack);
		
		else {
			if (rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
				
				BlockPos pos = rtr.getBlockPos();			
				Block block = world.getBlockState(pos).getBlock();
				int l = world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
				
				if ((block instanceof BlockLiquid || block instanceof IFluidBlock) && l == 0) {
					if (!world.isRemote) {
						ItemStack newFolder = new ItemStack(RFCItems.folder, 1, 4);
						if (ItemFolder.setObject(newFolder, block)) {							
							if (!player.inventory.addItemStackToInventory(newFolder))
								player.dropItem(newFolder, true);
							world.setBlockToAir(pos);
							stack.shrink(1);
						}
					}
					return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
				}
			}
		}
		return ActionResult.newResult(EnumActionResult.PASS, stack);
    }
	
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
		if (!player.world.isRemote && stack.getItemDamage() == FolderType.MOB.ordinal()) {
			if (!ConfigRFC.mobUpgrade) return false;
			
			ItemStack newFolder = new ItemStack(RFCItems.folder, 1, 3);
			if (ItemFolder.setObject(newFolder, target)) {
				if (!player.inventory.addItemStackToInventory(newFolder))
					player.dropItem(newFolder, true);
				stack.shrink(1);
				MobUtils.dropMobEquips(player.world, target);
				target.setDead();
			}
			return true;
		}
		return false;
	}
}
