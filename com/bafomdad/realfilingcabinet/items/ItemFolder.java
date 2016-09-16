package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.utils.EnderUtils;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;

public class ItemFolder extends Item implements IFolder {
	
	private static final String TAG_FILE_NAME = "fileName";
	private static final String TAG_FILE_META = "fileMeta";
	private static final String TAG_FILE_SIZE = "fileSize";
	
	public static int extractSize = 0;
	
	public String[] folderTypes = new String[] { "normal", "ender" };

	public ItemFolder() {
		
		setRegistryName("folder");
		setUnlocalizedName(RealFilingCabinet.MOD_ID + ".folder");
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(1);
		GameRegistry.register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public String getUnlocalizedName(ItemStack stack) {
		
		return getUnlocalizedName() + "_" + folderTypes[stack.getItemDamage()];
	}
	
	@SubscribeEvent
	public void onMergeFolders(PlayerEvent.ItemCraftedEvent event) {
		
		if (event.crafting.getItem() == this)
		{
			for (int slot = 0; slot < event.craftMatrix.getSizeInventory(); slot++) {
				ItemStack stack = event.craftMatrix.getStackInSlot(slot);
				if (stack == null)
					continue;
				if (stack.getItem() == this && stack.getItemDamage() == 0)
					event.craftMatrix.setInventorySlotContents(slot, null);
			}
		}
	}
	
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean whatisthis) {
		
		ItemStack stacky = (ItemStack)getObject(stack);
		
		if (stacky != null)
		{
			long count = getFileSize(stack);
			list.add(TextHelper.format(count) + " " + stacky.getDisplayName());
		}
		if (stack.getItemDamage() == 1 && stack.getTagCompound().hasKey(StringLibs.RFC_SLOTINDEX))
		{
			TileEntityRFC tile = EnderUtils.getTileLoc(stack);
			if (tile == null)
				list.add("Bound tile is null");
			else if (tile != null && list.size() > 2)
				list.remove(2);
		}
	}
	
	public ItemStack getContainerItem(ItemStack stack) {
		
		long count = getFileSize(stack);
		long extract = Math.min(((ItemStack)getObject(stack)).getMaxStackSize(), count);
		
		if (extract == 0)
			return null;
		
		ItemStack copy = stack.copy();
		remove(copy, extract);
		extractSize = (int)extract;

		return copy;
	}
	
	public boolean hasContainerItem(ItemStack stack) {
		
		return getContainerItem(stack) != null;
	}
	
	public static String getFileName(ItemStack stack) {
		
		return NBTUtils.getString(stack, TAG_FILE_NAME, "");
	}
	
	public static int getFileMeta(ItemStack stack) {
		
		return NBTUtils.getInt(stack, TAG_FILE_META, 0);
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

	public static Object getObject(ItemStack folder) {

		String str = getFileName(folder);
		
		if (Item.getByNameOrId(str) != null) {
			Item item = (Item)Item.getByNameOrId(str);
			int meta = getFileMeta(folder);
			return new ItemStack(item, 1, meta);
		}
		if (Block.getBlockFromName(str) != null) {
			Block block = Block.getBlockFromName(str);
			int meta = getFileMeta(folder);
			return new ItemStack(block, 1, meta);
		}
		return null;
	}

	public static boolean setObject(ItemStack folder, Object object) {

		if (getObject(folder) == null)
		{
			ItemStack stack = (ItemStack)object;
			if (stack.getItem() instanceof Item && Item.REGISTRY.getNameForObject(stack.getItem()) != null) {
				NBTUtils.setString(folder, TAG_FILE_NAME, Item.REGISTRY.getNameForObject(stack.getItem()).toString());
			}
			else if (stack.getItem() instanceof ItemBlock && Block.REGISTRY.getNameForObject(Block.getBlockFromItem((Item)stack.getItem())) != null) {
				NBTUtils.setString(folder, TAG_FILE_NAME, Block.REGISTRY.getNameForObject(Block.getBlockFromItem(stack.getItem())).toString());
			}
			
			NBTUtils.setInt(folder, TAG_FILE_META, ((ItemStack)object).getItemDamage());
			add(folder, 1);
			return true;
		}
		return false;
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		
		if (((ItemStack)getObject(stack)).getItem() instanceof ItemBlock) {
			ItemStack stackToPlace = new ItemStack(((ItemStack)getObject(stack)).getItem());
			stackToPlace.onItemUse(player, world, pos, hand, side, hitX, hitY, hitZ);
			
			if (stackToPlace.stackSize == 0) {
				remove(stack, 1);
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean watdis) {
		
		if (stack.getItemDamage() != 1 && !stack.getTagCompound().hasKey(StringLibs.RFC_SLOTINDEX))
			return;
		
		TileEntityRFC tile = EnderUtils.getTileLoc(stack);
		if (tile != null)
			EnderUtils.syncToFolder(tile, stack, NBTUtils.getInt(stack, StringLibs.RFC_SLOTINDEX, 0));
		else
			setFileSize(stack, 0);
	}

	@Override
	public void willThisWork() {
		
		System.out.println("Yep!");
	}
}
