package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.api.IEmptyFolder;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.utils.MobUtils;

public class ItemEmptyFolder extends Item implements IEmptyFolder {
	
	public String[] folderType = new String[] { "normal", "dura", "mob", "fluid", "nbt" };
	
	public ItemEmptyFolder() {
		
		setRegistryName("emptyfolder");
		setUnlocalizedName(RealFilingCabinet.MOD_ID + ".emptyfolder");
		setMaxStackSize(8);
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(TabRFC.instance);
		GameRegistry.register(this);
	}
	
	public String getUnlocalizedName(ItemStack stack) {
		
		return getUnlocalizedName() + "_" + folderType[stack.getItemDamage()];
	}
	
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tabs, List list) {
		
		for (int i = 0; i < folderType.length; ++i)
			list.add(new ItemStack(item, 1, i));
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean whatisthis) {
	
		list.add(TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".emptyfolder" + stack.getItemDamage()));
	}
	
	@Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        
		ItemStack folder = player.getHeldItemMainhand();
		if (folder != null && folder.getItemDamage() != 3)
			return ActionResult.newResult(EnumActionResult.PASS, folder);
		
		RayTraceResult rtr = rayTrace(world, player, true);
		if (rtr == null)
			return ActionResult.newResult(EnumActionResult.PASS, folder);
		
		if (!MobUtils.canPlayerChangeStuffHere(world, player, folder, rtr.getBlockPos(), rtr.sideHit))
			return ActionResult.newResult(EnumActionResult.PASS, folder);
		
		else {
			if (rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
				
				BlockPos pos = rtr.getBlockPos();			
				Block block = world.getBlockState(pos).getBlock();
				int l = world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
				
				if ((block instanceof BlockLiquid || block instanceof IFluidBlock) && l == 0) {
					if (!world.isRemote) {
						ItemStack newFolder = new ItemStack(RFCItems.folder, 1, 4);
						if (ItemFolder.setObject(newFolder, block)) {
							folder.stackSize--;
							if (!player.inventory.addItemStackToInventory(newFolder))
								player.dropItem(newFolder, true);
							world.setBlockToAir(pos);
						}
					}
					return ActionResult.newResult(EnumActionResult.SUCCESS, folder);
				}
			}
		}
		return ActionResult.newResult(EnumActionResult.PASS, stack);
    }
	
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
		
		if (!player.worldObj.isRemote)
		{
			if (stack.getItemDamage() == 2)
			{
				ItemStack newFolder = new ItemStack(RFCItems.folder, 1, 3);
				if (ItemFolder.setObject(newFolder, target)) {
					if (!player.inventory.addItemStackToInventory(newFolder))
						player.dropItem(newFolder, true);
					stack.stackSize--;
					MobUtils.dropMobEquips(player.worldObj, target);
					target.setDead();
				}
			}
			return true;
		}
		return false;
	}
}
