package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.api.IEmptyFolder;
import com.bafomdad.realfilingcabinet.api.ISubModel;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;
import com.bafomdad.realfilingcabinet.utils.MobUtils;

public class ItemEmptyFolder extends Item implements ISubModel, IEmptyFolder {
	
	public enum EmptyFolderType {
		NORMAL,
		DURA,
		MOB,
		FLUID,
		NBT;
	}
	
	@Override
	public String getTranslationKey(ItemStack stack) {
		
		return getTranslationKey() + "_" + EmptyFolderType.values()[stack.getItemDamage()].toString().toLowerCase();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		
		if (isInCreativeTab(tab)) {
			for (int i = 0; i < EmptyFolderType.values().length; ++i)
				list.add(new ItemStack(this, 1, i));
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World player, List<String> list, ITooltipFlag whatisthis) {
		
		String str = "";
		switch (stack.getItemDamage()) {
			case 1: str = ".emptyfolder1"; break;
			case 2: str = ".emptyfolder2"; break;
			case 3: str = ".emptyfolder3"; break;
			default: str = ".emptyfolder0"; break;
		}
		list.add(new TextComponentTranslation(StringLibs.TOOLTIP + str).getFormattedText());
	}
	
	@Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        
		ItemStack stack = player.getHeldItem(hand);
		if (!stack.isEmpty() && stack.getItemDamage() != EmptyFolderType.FLUID.ordinal())
			return ActionResult.newResult(EnumActionResult.PASS, stack);
		
		RayTraceResult rtr = rayTrace(world, player, true);
		if (rtr == null || !MobUtils.canPlayerChangeStuffHere(world, player, stack, rtr.getBlockPos(), rtr.sideHit))
			return ActionResult.newResult(EnumActionResult.PASS, stack);
		
		if (rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
			BlockPos pos = rtr.getBlockPos();
			IBlockState state = world.getBlockState(pos);
			int l = state.getBlock().getMetaFromState(state);
			
			if ((state.getBlock() instanceof BlockLiquid || state.getBlock() instanceof IFluidBlock) && l == 0) {
				if (!world.isRemote) {
					Fluid fluid = FluidRegistry.lookupFluidForBlock(state.getBlock());
					if (fluid != null) {
						ItemStack newFolder = getFilledFolder(stack);
						if (FolderUtils.get(newFolder).setObject(new FluidStack(fluid, 1000))) {
							ItemHandlerHelper.giveItemToPlayer(player, newFolder);
							world.setBlockToAir(pos);
							stack.shrink(1);
						}
					}
				}
				return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
			}
		}
		return ActionResult.newResult(EnumActionResult.PASS, stack);
    }
	
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
		
		if (!player.world.isRemote && stack.getItemDamage() == EmptyFolderType.MOB.ordinal()) {
			if (!MobUtils.acceptableTargets(target)) return false;
			
			ItemStack newFolder = getFilledFolder(stack);
			if (FolderUtils.get(newFolder).setObject(target)) {
				ItemHandlerHelper.giveItemToPlayer(player, newFolder);
				stack.shrink(1);
				MobUtils.dropMobEquips(player.world, target);
				target.setDead();
			}
			return true;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerSubModels(Item item) {

		for (int i = 0; i < EmptyFolderType.values().length; ++i)
			ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(item.getRegistryName() + "_" + EmptyFolderType.values()[i].toString().toLowerCase(), "inventory"));
	}

	@Override
	public ItemStack getFilledFolder(ItemStack stack) {

		switch(stack.getItemDamage()) {
			case 0: return new ItemStack(RFCItems.FOLDER, 1, 0);
			case 1: return new ItemStack(RFCItems.FOLDER, 1, 2);
			case 2: return new ItemStack(RFCItems.FOLDER, 1, 3);
			case 3: return new ItemStack(RFCItems.FOLDER, 1, 4);
			case 4: return new ItemStack(RFCItems.FOLDER, 1, 5);
			default: return ItemStack.EMPTY;
		}	
	}
}
