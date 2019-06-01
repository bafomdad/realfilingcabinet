package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import com.bafomdad.realfilingcabinet.LogRFC;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.api.ISubModel;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.enums.FolderType;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityFolder;
import com.bafomdad.realfilingcabinet.utils.EnderUtils;
import com.bafomdad.realfilingcabinet.utils.FluidUtils;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;
import com.bafomdad.realfilingcabinet.utils.MobUtils;

public class ItemFolder extends ItemAbstractFolder implements ISubModel, IFolder {
	
	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		
		ItemStack item = ItemStack.EMPTY;
		CapabilityFolder cap = FolderUtils.get(stack).getCap();
		if (cap == null) return item;
		
		long count = FolderUtils.get(stack).getFileSize();
		long extract = 0;
		if (count > 0 && cap.isItemStack())
			extract = Math.min(cap.getItemStack().getMaxStackSize(), count);
			
		item = stack.copy();
		if (stack.getItemDamage() == FolderType.DURA.ordinal() && count == 0)
			FolderUtils.get(item).setRemainingDurability(0);
			
		FolderUtils.get(item).remove(extract);
		FolderUtils.get(item).setExtractSize(-(int)extract);
		
		return item;
	}
	
	@Override
	public boolean hasContainerItem(ItemStack stack) {
		
		return !getContainerItem(stack).isEmpty();
	}
	
	@Override
	public String getTranslationKey(ItemStack stack) {
		
		return getTranslationKey() + "_" + FolderType.values()[stack.getItemDamage()].toString().toLowerCase();
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		
		ItemStack folder = player.getHeldItem(hand);
		EnumActionResult ear = this.placeObject(folder, player, world, pos, hand, side, hitX, hitY, hitZ);
		if (ear != EnumActionResult.SUCCESS) {
			RayTraceResult rtr = rayTrace(world, player, true);
			if (rtr == null)
				return ear;
			
			if (rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
				if (FluidUtils.doDrain(world, player, folder, rtr.getBlockPos(), side))
					return EnumActionResult.SUCCESS;
			}
		}
		return ear;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
		
		super.onUpdate(stack, world, entity, slot, isSelected);
		if (world.isRemote) return;
		
		if (stack.getItemDamage() == FolderType.ENDER.ordinal()) {
			if (!stack.hasTagCompound() || (stack.hasTagCompound() && !stack.getTagCompound().hasKey(StringLibs.RFC_SLOTINDEX)))
				return;
			
			TileFilingCabinet tile = EnderUtils.getCachedTile(stack);
			if (tile == null || UpgradeHelper.getUpgrade(tile, StringLibs.TAG_ENDER).isEmpty()) {
				FolderUtils.get(stack).setFileSize(0);
				return;
			} else if (tile != null) {
				EnderUtils.syncToFolder(stack);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerSubModels(Item item) {

		for (int i = 0; i < FolderType.values().length; ++i)
			ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(item.getRegistryName() + "_" + FolderType.values()[i].toString().toLowerCase(), "inventory"));
	}

	@Override
	public ItemStack getEmptyFolder(ItemStack stack) {

		switch (stack.getItemDamage()) {
			case 0: return new ItemStack(RFCItems.FOLDER, 1, 0);
			case 2: return new ItemStack(RFCItems.FOLDER, 1, 1);
			case 3: return new ItemStack(RFCItems.FOLDER, 1, 2);
			case 4: return new ItemStack(RFCItems.FOLDER, 1, 3);
			case 5: return new ItemStack(RFCItems.FOLDER, 1, 4);
			default: return ItemStack.EMPTY;
		}
	}
	
	@Override
	public void setAdditionalData(ItemStack folder, Object toSet) {
		
		if (toSet instanceof ItemStack) {
			ItemStack stack = (ItemStack)toSet;
			if (folder.getItemDamage() == FolderType.DURA.ordinal())
				FolderUtils.get(folder).setRemainingDurability(stack.getItemDamage());
			if (folder.getItemDamage() == FolderType.NBT.ordinal())
				FolderUtils.get(folder).setTagCompound(stack.getTagCompound());
		}
	}
	
	@Override
	public EnumActionResult placeObject(ItemStack folder, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		
		return FolderType.values()[folder.getItemDamage()].placeObject(folder, player, world, pos, hand, side, hitX, hitY, hitZ);
	}

	@Override
	public Object insertIntoFolder(ItemStack folder, Object toInsert, boolean simulate, boolean oreDict) {

		return FolderType.values()[folder.getItemDamage()].insert(FolderUtils.get(folder).getCap(), toInsert, simulate, oreDict);
	}

	@Override
	public Object extractFromFolder(ItemStack folder, long amount, boolean simulate) {

		return FolderType.values()[folder.getItemDamage()].extract(FolderUtils.get(folder).getCap(), amount, simulate);
	}
}
