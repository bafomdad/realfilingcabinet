package com.bafomdad.realfilingcabinet.items;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityFolder;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityProviderFolder;
import com.bafomdad.realfilingcabinet.utils.EnderUtils;
import com.bafomdad.realfilingcabinet.utils.FluidUtils;
import com.bafomdad.realfilingcabinet.utils.MobUtils;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class ItemFolder extends Item implements IFolder {
	
	public static int extractSize = 0; // TODO: Figure out how to move this to CapabilityFolder
	
	public enum FolderType {
		NORMAL,
		ENDER,
		DURA,
		MOB,
		FLUID,
		NBT;
	}

	public ItemFolder() {
		
		setRegistryName("folder");
		setTranslationKey(RealFilingCabinet.MOD_ID + ".folder");
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(1);
	}
	
	@Override
	public NBTTagCompound getNBTShareTag(ItemStack stack)
	{
		if(!stack.hasCapability(CapabilityProviderFolder.FOLDER_CAP, null))
		{
			return super.getNBTShareTag(stack);
		}
		
		NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound().copy() : new NBTTagCompound();
		tag.setTag("folderCap", stack.getCapability(CapabilityProviderFolder.FOLDER_CAP, null).serializeNBT());
		return tag;
	}
	
	@Override
	public String getTranslationKey(ItemStack stack) {
		
		return getTranslationKey() + "_" + FolderType.values()[stack.getItemDamage()].toString().toLowerCase();
	}
	
	@Override
	public void addInformation(ItemStack stack, World player, List<String> list, ITooltipFlag whatisthis) {
		
		if(stack.hasCapability(CapabilityProviderFolder.FOLDER_CAP, null)) // Direction doesn't really matter here.
		{
			stack.getCapability(CapabilityProviderFolder.FOLDER_CAP, null).addTooltips(player, list, whatisthis);
		}
	}
	
	public ItemStack getContainerItem(ItemStack stack) {
		
		if(!stack.hasCapability(CapabilityProviderFolder.FOLDER_CAP, null))
		{
			return ItemStack.EMPTY;
		}
		
		CapabilityFolder cap = stack.getCapability(CapabilityProviderFolder.FOLDER_CAP, null);
		long count = cap.getCount();
		long extract = 0;
		if (count > 0 && cap.isItemStack())
			extract = Math.min(cap.getItemStack().getMaxStackSize(), count);
		
		if (NBTUtils.getBoolean(stack, StringLibs.RFC_TAPED, false)) {
			return ItemStack.EMPTY;
		}
		ItemStack copy = stack.copy();
		if (stack.getItemDamage() == 2 && count == 0) // TODO: This works with 0 items? Might want to test this later
		{
			setRemSize(copy, 0);
		}
		remove(copy, extract);
		extractSize = (int)extract;
		
		return copy;
	}
	
	public boolean hasContainerItem(ItemStack stack) {
		
		return !getContainerItem(stack).isEmpty();
	}
	
	public static String getFolderDisplayName(ItemStack stack)
	{
		if(stack.hasCapability(CapabilityProviderFolder.FOLDER_CAP, null))
		{
			return stack.getCapability(CapabilityProviderFolder.FOLDER_CAP, null).getDisplayName();
		}
		
		return "";
	}
	
	@Deprecated // Not for save/load use
	public static String getFileName(ItemStack stack) {
		if(stack.hasCapability(CapabilityProviderFolder.FOLDER_CAP, null))
		{
			return stack.getCapability(CapabilityProviderFolder.FOLDER_CAP, null).getContentID();
		}
		
		return "";
	}
	
	public static int getFileMeta(ItemStack stack) {
		if(stack.hasCapability(CapabilityProviderFolder.FOLDER_CAP, null))
		{
			CapabilityFolder cap = stack.getCapability(CapabilityProviderFolder.FOLDER_CAP, null);
			if(cap.isFluidStack())
			{
				return cap.getItemStack().getItemDamage();
			} else if(cap.isBlock())
			{
				return cap.getBlock().getBlock().getMetaFromState(cap.getBlock());
			}
		}
		return 0;
	}
	
	public static void setFileMeta(ItemStack stack, int meta) {
		if(stack.hasCapability(CapabilityProviderFolder.FOLDER_CAP, null))
		{
			CapabilityFolder cap = stack.getCapability(CapabilityProviderFolder.FOLDER_CAP, null);
			if(cap.isFluidStack())
			{
				cap.getItemStack().setItemDamage(meta);
			} else if(cap.isBlock())
			{
				cap.setContents(cap.getBlock().getBlock().getMetaFromState(cap.getBlock()));
			}
		}
	}
	
	public static void setFileSize(ItemStack stack, long count) {
		if(stack.hasCapability(CapabilityProviderFolder.FOLDER_CAP, null))
		{
			stack.getCapability(CapabilityProviderFolder.FOLDER_CAP, null).setCount(count);
		}
	}
	
	public static long getFileSize(ItemStack stack) {
		if(stack.hasCapability(CapabilityProviderFolder.FOLDER_CAP, null))
		{
			return stack.getCapability(CapabilityProviderFolder.FOLDER_CAP, null).getCount();
		}
		
		return 0;
	}
	
	public static void remove(ItemStack stack, long count) {
		
		long current = getFileSize(stack);
		setFileSize(stack, Math.max(current - count, 0));
	}
	
	public static void add(ItemStack stack, long count) {
		
		long current = getFileSize(stack);
		setFileSize(stack, current + count);
		
		System.out.println("New amount = " + (current + count));
	}
	
	public static void setRemSize(ItemStack stack, int count) {
		if(stack.hasCapability(CapabilityProviderFolder.FOLDER_CAP, null))
		{
			stack.getCapability(CapabilityProviderFolder.FOLDER_CAP, null).setRemaining(count);
		}
	}
	
	public static int getRemSize(ItemStack stack) {
		if(stack.hasCapability(CapabilityProviderFolder.FOLDER_CAP, null))
		{
			return stack.getCapability(CapabilityProviderFolder.FOLDER_CAP, null).getRemaining();
		}
		
		return 0;
	}
	
	public static void addRem(ItemStack stack, int count) {
		
		int current = getRemSize(stack);
		setRemSize(stack, current + count);
	}
	
	public static void remRem(ItemStack stack, int count) {
		
		int current = getRemSize(stack);
		setRemSize(stack, Math.max(current - count, 0));
	}
	
	public static NBTTagCompound getItemTag(ItemStack stack) {
		if(stack.hasCapability(CapabilityProviderFolder.FOLDER_CAP, null))
		{
			CapabilityFolder cap = stack.getCapability(CapabilityProviderFolder.FOLDER_CAP, null);
			
			if(cap.isItemStack())
			{
				return cap.getItemStack().getTagCompound();
			}
		}
		
		return new NBTTagCompound();
	}
	
	public static void setItemTag(ItemStack stack, NBTTagCompound tag) {
		if(stack.hasCapability(CapabilityProviderFolder.FOLDER_CAP, null))
		{
			CapabilityFolder cap = stack.getCapability(CapabilityProviderFolder.FOLDER_CAP, null);
			
			if(cap.isItemStack())
			{
				cap.getItemStack().setTagCompound(tag);
			}
		}
	}

	public static Object getObject(ItemStack folder) {

		if(folder.hasCapability(CapabilityProviderFolder.FOLDER_CAP, null))
		{
			return folder.getCapability(CapabilityProviderFolder.FOLDER_CAP, null).getContents();
		}
		
		return null;
	}

	public static boolean setObject(ItemStack folder, Object object) {
		
		if(folder.hasCapability(CapabilityProviderFolder.FOLDER_CAP, null))
		{
			return folder.getCapability(CapabilityProviderFolder.FOLDER_CAP, null).setContents(object);
		}
		
		return false;
	}
	
//	@Override
//	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
//		
//		if (target.isChild() && !(target instanceof EntityZombie))
//			return false;
//		
//		if (target instanceof EntityCabinet)
//			return false;
//		
//		if (target instanceof IEntityOwnable && ((IEntityOwnable)target).getOwner() != null)
//			return false;
//		
//		String entityblacklist = target.getClass().getSimpleName();
//		for (String toBlacklist : ConfigRFC.mobFolderBlacklist) {
//			if (toBlacklist.contains(entityblacklist))
//				return false;
//		}
//		ItemStack folder = player.getHeldItemMainhand();
//		if (!folder.isEmpty() && folder.getItem() == this && folder.getItemDamage() == FolderType.MOB.ordinal()) {
//			if (!ConfigRFC.mobUpgrade) return false;
//			
//			if (getObject(folder) != null) {
//				ResourceLocation res = EntityList.getKey(target);
//				if (getObject(folder).equals(res.toString()))
//				{
//					add(folder, 1);
//					MobUtils.dropMobEquips(player.world, target);
//					target.setDead();
//				}
//			}
//			return true;
//		}
//		return false;
//	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		
		ItemStack stack = player.getHeldItem(hand);
		if (getObject(stack) != null) {
			if (stack.getItemDamage() < 2) {
				if (((ItemStack)getObject(stack)).getItem() instanceof ItemBlock) {	
					long count = ItemFolder.getFileSize(stack);
					if (stack.getItemDamage() == 1 && !EnderUtils.preValidateEnderFolder(stack))
						return EnumActionResult.FAIL;
					
					if (count > 0) {
						ItemStack stackToPlace = new ItemStack(((ItemStack)getObject(stack)).getItem(), 1, ((ItemStack)getObject(stack)).getItemDamage());
						ItemStack savedfolder = player.getHeldItem(hand);
						
						player.setHeldItem(hand, stackToPlace);
						EnumActionResult ear = stackToPlace.onItemUse(player, world, pos, hand, side, hitX, hitY, hitZ);
						player.setHeldItem(hand, savedfolder);
						
						if (ear == EnumActionResult.SUCCESS) {
							if (!player.capabilities.isCreativeMode) {
								if (stack.getItemDamage() == 1 && !world.isRemote) {
									EnderUtils.syncToTile(EnderUtils.getTileLoc(stack), NBTUtils.getInt(stack, StringLibs.RFC_DIM, 0), NBTUtils.getInt(stack, StringLibs.RFC_SLOTINDEX, 0), 1, true);
									if (player instanceof FakePlayer)
										EnderUtils.syncToFolder(EnderUtils.getTileLoc(stack), stack, NBTUtils.getInt(stack, StringLibs.RFC_SLOTINDEX, 0));
								}
								else
									remove(stack, 1);
							}
							return EnumActionResult.SUCCESS;
						}
					}
				}
			}
			if (stack.getItemDamage() == 3) {
				if (MobUtils.spawnEntityFromFolder(world, player, stack, pos, side))
					return EnumActionResult.SUCCESS;
			}
			if (stack.getItemDamage() == 4) {
				if (!(getObject(stack) instanceof FluidStack))
					return EnumActionResult.PASS;
				
				if (FluidUtils.doPlace(world, player, stack, pos, side))
					return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;
	}
	
	@Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        
		ItemStack stack = player.getHeldItem(hand);
		if (!stack.isEmpty() && stack.getItem() != this)
			return ActionResult.newResult(EnumActionResult.PASS, stack);

		if (stack.getItemDamage() == 2) {
			NBTTagCompound tag = stack.getTagCompound();
			tag.setBoolean(StringLibs.RFC_IGNORENBT, !tag.getBoolean(StringLibs.RFC_IGNORENBT));
			return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
		}
		if (!stack.isEmpty() && stack.getItemDamage() != 4)
			return ActionResult.newResult(EnumActionResult.PASS, stack);
		
		RayTraceResult rtr = rayTrace(world, player, true);
		if (rtr == null)
			return ActionResult.newResult(EnumActionResult.PASS, stack);
		
		if (!MobUtils.canPlayerChangeStuffHere(world, player, stack, rtr.getBlockPos(), rtr.sideHit))
			return ActionResult.newResult(EnumActionResult.PASS, stack);
		
		else {
			if (rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
				BlockPos pos = rtr.getBlockPos();
				if (FluidUtils.doDrain(world, player, stack, pos, rtr.sideHit))
					return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
			}
		}
		return ActionResult.newResult(EnumActionResult.PASS, stack);
    }
	
	@Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        
		if (entityLiving instanceof EntityPlayer && entityLiving.isSneaking()) {
			if (!stack.isEmpty() && stack.getItem() == this) {
				if (stack.getItemDamage() == 4) {	
					NBTTagCompound tag = stack.getTagCompound();
					tag.setBoolean(StringLibs.RFC_PLACEMODE, !tag.getBoolean(StringLibs.RFC_PLACEMODE));
					return true;
				}
			}
		}
		return false;
    }
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldstack, ItemStack newstack, boolean slotchanged) {
		
		return oldstack.getItem() != newstack.getItem() || (oldstack.getItem() == newstack.getItem() && oldstack.getItemDamage() != newstack.getItemDamage());
	}
	
	@Override
	public ItemStack isFolderEmpty(ItemStack stack) {

		switch (stack.getItemDamage()) 
		{
			case 0: return new ItemStack(RFCItems.emptyFolder, 1, 0);
			case 2: return new ItemStack(RFCItems.emptyFolder, 1, 1);
			case 3: return new ItemStack(RFCItems.emptyFolder, 1, 2);
			case 4: return new ItemStack(RFCItems.emptyFolder, 1, 3);
			case 5: return new ItemStack(RFCItems.emptyFolder, 1, 4);
		}
		return ItemStack.EMPTY;
	}
}
