package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.bafomdad.realfilingcabinet.NewConfigRFC.ConfigRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.entity.EntityCabinet;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.utils.EnderUtils;
import com.bafomdad.realfilingcabinet.utils.FluidUtils;
import com.bafomdad.realfilingcabinet.utils.MobUtils;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;

public class ItemFolder extends Item implements IFolder {
	
	private static final String TAG_FILE_NAME = "fileName";
	private static final String TAG_FILE_META = "fileMeta";
	private static final String TAG_FILE_SIZE = "fileSize";
	
	private static final String TAG_REM_SIZE = "leftoverSize";
	private static final String TAG_ITEMTAG = "itemTagCompound";
	
	private static ItemStack ITEM_STORED = ItemStack.EMPTY;
	
	public static int extractSize = 0;
	
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
		setUnlocalizedName(RealFilingCabinet.MOD_ID + ".folder");
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(1);
	}
	
	public String getUnlocalizedName(ItemStack stack) {
		
		return getUnlocalizedName() + "_" + FolderType.values()[stack.getItemDamage()].toString().toLowerCase();
	}
	
	@Override
	public void addInformation(ItemStack stack, World player, List list, ITooltipFlag whatisthis) {
		
		String name = getFileName(stack);
		if (!name.isEmpty()) {
			long count = getFileSize(stack);
			if (stack.getItemDamage() == FolderType.FLUID.ordinal()) {
				
				if (getObject(stack) instanceof FluidStack)
					name = ((FluidStack)getObject(stack)).getLocalizedName();
				list.add(count + "mb " + name);
				
				boolean bool = NBTUtils.getBoolean(stack, StringLibs.RFC_PLACEMODE, false);
				String placeMode = bool ? TextFormatting.GREEN + TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".placemode.on") : TextFormatting.RED + TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".placemode.off");
				list.add(placeMode);
				
				return;
			}
			if (stack.getItemDamage() == FolderType.MOB.ordinal()) {
				
				ResourceLocation res = new ResourceLocation(ItemFolder.getFileName(stack));
				Entity entity = EntityList.createEntityByIDFromName(res, player);
				if (entity != null)
					list.add(count + " " + entity.getName());
				list.add(TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mobinteract"));
				
				if (!ConfigRFC.mobUpgrade)
					list.add(TextHelper.localize("message." + RealFilingCabinet.MOD_ID + ".disabled"));
				return;
			}
			if (getObject(stack) instanceof ItemStack)
				name = ((ItemStack)getObject(stack)).getDisplayName();
			
			list.add((Keyboard.isKeyDown(42)) || (Keyboard.isKeyDown(54)) ? count + " " + name : TextHelper.format(count) + " " + name);
			
			if (stack.getItemDamage() == FolderType.DURA.ordinal()) {
				
				list.add("Durability: " + ItemFolder.getRemSize(stack) + " / " + ((ItemStack)getObject(stack)).getMaxDamage());
				boolean bool = NBTUtils.getBoolean(stack, StringLibs.RFC_IGNORENBT, false);
				String ignoreNBT = bool ? TextFormatting.GREEN + TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".ignorenbt.true") : TextFormatting.RED + TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".ignorenbt.false");
				list.add(ignoreNBT);
				
				return;
			}
		}
	}
	
	public ItemStack getContainerItem(ItemStack stack) {
		
		long count = getFileSize(stack);
		long extract = 0;
		if (count > 0 && getObject(stack) instanceof ItemStack)
			extract = Math.min(((ItemStack)getObject(stack)).getMaxStackSize(), count);
		
		if (stack.getTagCompound().hasKey(StringLibs.RFC_TAPED) && NBTUtils.getBoolean(stack, StringLibs.RFC_TAPED, true)) {
			return ItemStack.EMPTY;
		}
		ItemStack copy = stack.copy();
		if (stack.getItemDamage() == 2 && count == 0)
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
	
	public static String getFileName(ItemStack stack) {
		
		return NBTUtils.getString(stack, TAG_FILE_NAME, "");
	}
	
	public static int getFileMeta(ItemStack stack) {
		
		return NBTUtils.getInt(stack, TAG_FILE_META, 0);
	}
	
	public static void setFileMeta(ItemStack stack, int meta) {
		
		NBTUtils.setInt(stack, TAG_FILE_META, meta);
	}
	
	public static void setFileSize(ItemStack stack, long count) {

		NBTUtils.setLong(stack, TAG_FILE_SIZE, count);
	}
	
	public static long getFileSize(ItemStack stack) {
		
		return NBTUtils.getLong(stack, TAG_FILE_SIZE, 0);
	}
	
	public static void remove(ItemStack stack, long count) {
		
		long current = getFileSize(stack);
		setFileSize(stack, Math.max(current - count, 0));
	}
	
	public static void add(ItemStack stack, long count) {
		
		long current = getFileSize(stack);
		setFileSize(stack, current + count);
	}
	
	public static void setRemSize(ItemStack stack, int count) {
		
		NBTUtils.setInt(stack, TAG_REM_SIZE, count);
	}
	
	public static int getRemSize(ItemStack stack) {
		
		return NBTUtils.getInt(stack, TAG_REM_SIZE, 0);
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
		
		return NBTUtils.getCompound(stack, TAG_ITEMTAG, true);
	}
	
	public static void setItemTag(ItemStack stack, NBTTagCompound tag) {
		
		NBTUtils.setCompound(stack, TAG_ITEMTAG, tag);
	}
	
	public static ItemStack getItem(ItemStack stack) {
		
		return ITEM_STORED;
	}
	
	public static void setItem(ItemStack stack, ItemStack toStore, boolean copy) {
		
		if (copy)
			ITEM_STORED = toStore.copy();
		
		ITEM_STORED = toStore;
		setFileMeta(stack, toStore.getItemDamage());
		setFileSize(stack, 1);
	}

	public static Object getObject(ItemStack folder) {

		String str = getFileName(folder);
		
		if (folder.getItemDamage() == 3) {
			if (!str.isEmpty())
				return str;
		}
		if (folder.getItemDamage() == FolderType.FLUID.ordinal()) {
			if (!str.isEmpty() && FluidRegistry.getFluid(str) != null) {
				long extract = Math.min(Integer.MAX_VALUE - 1, getFileSize(folder));
				return new FluidStack(FluidRegistry.getFluid(str), (int)extract);
			}
			else if (!str.isEmpty() && Block.getBlockFromName(str) != null) {
				long extract = Math.min(Integer.MAX_VALUE - 1, getFileSize(folder));
				if (Block.getBlockFromName(str) == Blocks.WATER)
					return new FluidStack(FluidRegistry.WATER, (int)extract);
				else if (Block.getBlockFromName(str) == Blocks.LAVA)
					return new FluidStack(FluidRegistry.LAVA, (int)extract);
			}
		}
		ItemStack copystack = null;
		if (Item.getByNameOrId(str) != null) {
			Item item = (Item)Item.getByNameOrId(str);
			int meta = getFileMeta(folder);
			copystack = new ItemStack(item, 1, meta);
			if (folder.getItemDamage() == 5)
				copystack.setTagCompound(getItemTag(folder));
			return copystack;
		}
		if (Block.getBlockFromName(str) != null) {
			Block block = Block.getBlockFromName(str);
			int meta = getFileMeta(folder);
			copystack = new ItemStack(block, 1, meta);
			if (folder.getItemDamage() == 5)
				copystack.setTagCompound(getItemTag(folder));
			return copystack;
		}
		return null;
	}

	public static boolean setObject(ItemStack folder, Object object) {

		if (getObject(folder) == null) {
			if (object instanceof ItemStack) {
				ItemStack stack = (ItemStack)object;
				if (stack.getItem() instanceof Item && Item.REGISTRY.getNameForObject(stack.getItem()) != null) {
					NBTUtils.setString(folder, TAG_FILE_NAME, Item.REGISTRY.getNameForObject(stack.getItem()).toString());
				}
				else if (stack.getItem() instanceof ItemBlock && Block.REGISTRY.getNameForObject(Block.getBlockFromItem((Item)stack.getItem())) != null) {
					NBTUtils.setString(folder, TAG_FILE_NAME, Block.REGISTRY.getNameForObject(Block.getBlockFromItem(stack.getItem())).toString());
				}
				NBTUtils.setInt(folder, TAG_FILE_META, ((ItemStack)object).getItemDamage());
				add(folder, 1);
				if (folder.getItemDamage() == FolderType.DURA.ordinal())
					addRem(folder, 0);
				
				if (folder.getItemDamage() == FolderType.NBT.ordinal())
					setItemTag(folder, ((ItemStack)object).getTagCompound());
				
				return true;
			}
			if (object instanceof BlockLiquid || object instanceof IFluidBlock) {
				Block bl = (Block)object;
				String fluidname = bl.getLocalizedName();
				if (object instanceof IFluidBlock)
					fluidname = ((IFluidBlock)object).getFluid().getName();
				
				NBTUtils.setString(folder, TAG_FILE_NAME, fluidname);
				add(folder, 1000);
				
				return true;
			}
			if (object instanceof EntityLivingBase) {
				if (object instanceof EntityCabinet)
					return false;
				
				if (object instanceof IEntityOwnable && ((IEntityOwnable)object).getOwner() != null)
					return false;
				
				String entityblacklist = ((EntityLivingBase)object).getClass().getSimpleName();
				for (String toBlacklist : ConfigRFC.mobFolderBlacklist) {
					if (toBlacklist.contains(entityblacklist))
						return false;
				}
				
				if (!(object instanceof EntityPlayer) && ((EntityLivingBase)object).isNonBoss() && (!((EntityLivingBase)object).isChild() || (EntityLivingBase)object instanceof EntityZombie && ((EntityLivingBase)object).isChild())) {
					ResourceLocation entityName = EntityList.getKey((Entity)object);
					NBTUtils.setString(folder, TAG_FILE_NAME, entityName.toString());
					add(folder, 1);
					return true;
				}
			}
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
