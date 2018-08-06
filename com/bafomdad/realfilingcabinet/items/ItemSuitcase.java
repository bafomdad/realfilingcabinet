package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import javax.annotation.Nonnull;

import org.lwjgl.input.Keyboard;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.inventory.InventorySuitcase;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;

import vazkii.botania.api.item.IBlockProvider;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

@Optional.Interface(iface = "vazkii.botania.api.item.IBlockProvider", modid = "botania")
public class ItemSuitcase extends Item implements IBlockProvider {
	
	private final String TAG_INDEX = "RFC:placeIndex";

	public ItemSuitcase() {
	
		setRegistryName("suitcase");
		setTranslationKey(RealFilingCabinet.MOD_ID + ".suitcase");
		setMaxStackSize(1);
		setCreativeTab(TabRFC.instance);
	}
	
	@Override
	public void addInformation(ItemStack stack, World player, List<String> list, ITooltipFlag whatisthis) {
		
		list.add(TextFormatting.GOLD + "Current place index: " + TextFormatting.RESET + getIndex(stack));
		IItemHandlerModifiable suitcaseInv = getInventory(stack);
		if (suitcaseInv != null) {
			if (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54))
				listItems(suitcaseInv, list);
			else if (list.size() > 2)
				list.remove(2);
		}
	}
	
	@Override
	public boolean getShareTag() {
		
		return false;
	}
	
	private void listItems(IItemHandlerModifiable inv, List<String> list) {
		
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack folder = inv.getStackInSlot(i);
			if (!folder.isEmpty() && folder.getItem() == RFCItems.folder) {
				String name = TextHelper.folderStr(folder);
				long count = ItemFolder.getFileSize(folder);
				
				list.add(TextHelper.format(count) + " " + name);
			}
		}
	}
	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		
		if (hand == EnumHand.MAIN_HAND) {
			player.openGui(RealFilingCabinet.instance, 1, world, 0, 0, 0);
			return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}
		return ActionResult.newResult(EnumActionResult.PASS, player.getHeldItem(hand));
	}
	
	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float x, float y, float z) {
		
		ItemStack heldStack = player.getHeldItem(hand);
		IItemHandlerModifiable suitcaseInv = getInventory(heldStack);
		TileEntity tile = world.getTileEntity(pos);
		
		if (tile instanceof TileEntityRFC) {
			if (!world.isRemote) {
				TileEntityRFC tileRFC = (TileEntityRFC)tile;
				
				if (!tileRFC.isOpen)
					return EnumActionResult.FAIL;
				
				boolean flag = ItemHandlerHelper.calcRedstoneFromInventory(suitcaseInv) > 0;
				if (flag) {
					if (ItemHandlerHelper.calcRedstoneFromInventory(tileRFC.getInventory()) > 0) 
						return EnumActionResult.FAIL;
					
					for (int i = 0; i < suitcaseInv.getSlots(); i++) {
						ItemStack stack = suitcaseInv.getStackInSlot(i);
						if (!stack.isEmpty()) {
							tileRFC.getInventory().setStackInSlot(i, stack);
							suitcaseInv.extractItem(i, 1, false);
						}
					}
				}
				else {
					for (int i = 0; i < tileRFC.getInventory().getSlots(); i++) {
						ItemStack stack = tileRFC.getInventory().getTrueStackInSlot(i);
						if (!stack.isEmpty()) {
							suitcaseInv.insertItem(i, stack, false);
							tileRFC.getInventory().setStackInSlot(i, ItemStack.EMPTY);
						}
					}
				}
				tileRFC.markBlockForUpdate();
			}
			return EnumActionResult.SUCCESS;
		}
		ItemStack stackToPlace = getStoredItem(heldStack, suitcaseInv, false);
		if (!stackToPlace.isEmpty()) {
			if (!world.isRemote) {
				player.setHeldItem(hand, stackToPlace);
				EnumActionResult ear = stackToPlace.onItemUse(player, world, pos, hand, side, x, y, z);
				player.setHeldItem(hand, heldStack);
				if (ear == EnumActionResult.SUCCESS) {
					if (!player.capabilities.isCreativeMode)
						getStoredItem(heldStack, suitcaseInv, true);
				}
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}
	
	@Nonnull
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound tag) {
		
		return new InventorySuitcase.InvProvider();
	}
	
	public static IItemHandlerModifiable getInventory(ItemStack stack) {
		
		return (IItemHandlerModifiable)stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
	}
	
	public static ItemStack getFolder(ItemStack suitcase, ItemStack toPickup) {
		
		IItemHandlerModifiable suitcaseInv = getInventory(suitcase);
		for (int i = 0; i < suitcaseInv.getSlots(); i++) {
			ItemStack folder = suitcaseInv.getStackInSlot(i);
			if (!folder.isEmpty() && folder.getItem() == RFCItems.folder) {
				Object obj = ItemFolder.getObject(folder);
				if (obj instanceof ItemStack) {
					if (ItemStack.areItemsEqual((ItemStack)obj, toPickup)) {
						return folder;
					}
				}
			}
		}
		return ItemStack.EMPTY;
	}
	
	private ItemStack getStoredItem(ItemStack suitcase, IItemHandlerModifiable inv, boolean subtract) {
		
		ItemStack folder = inv.getStackInSlot(getIndex(suitcase));
		if (!folder.isEmpty()) {
			Object obj = ItemFolder.getObject(folder);
			if (obj instanceof ItemStack && ((ItemStack)obj).getItem() instanceof ItemBlock) {
				if (subtract)
					ItemFolder.remove(folder, 1);
				
				return (ItemStack)obj;
			}
		}
		return ItemStack.EMPTY;
	}
	
	private int getStoredBlock(ItemStack stack, Block block, int meta) {
		
		IItemHandlerModifiable suitcaseInv = getInventory(stack);
		for (int i = 0; i < suitcaseInv.getSlots(); i++) {
			ItemStack folder = suitcaseInv.getStackInSlot(i);
			if (!folder.isEmpty() && folder.getItem() == RFCItems.folder) {
				Object obj = ItemFolder.getObject(folder);
				if (obj instanceof ItemStack && ((ItemStack)obj).getItem() instanceof ItemBlock) {
					if (Block.getBlockFromItem(((ItemStack)obj).getItem()) == block && meta == ItemFolder.getFileMeta(folder)) {
						return i;
					}
				}
			}
		}
		return -1;
	}
	
	private int getStoredCount(ItemStack stack, int slot) {
		
		IItemHandlerModifiable suitcaseInv = getInventory(stack);
		return (int)Math.min(Integer.MAX_VALUE, ItemFolder.getFileSize(suitcaseInv.getStackInSlot(slot)));
	}
	
	private void setStoredCount(ItemStack stack, int slot) {
		
		IItemHandlerModifiable suitcaseInv = getInventory(stack);
		ItemFolder.remove(suitcaseInv.getStackInSlot(slot), 1);
	}
	
	public int getIndex(ItemStack stack) {
		
		return NBTUtils.getInt(stack, TAG_INDEX, 0);
	}
	
	public void cycleIndex(ItemStack stack, boolean add) {
		
		int i = getIndex(stack);
		if (add && (i + 1) < 8)
			NBTUtils.setInt(stack, TAG_INDEX, i + 1);
		else if (add && (i + 1) >= 8)
			NBTUtils.setInt(stack, TAG_INDEX, 0);
		if (!add && i > 0)
			NBTUtils.setInt(stack, TAG_INDEX, i - 1);
		else if (!add && (i - 1) <= 0)
			NBTUtils.setInt(stack, TAG_INDEX, 8 - 1);
	}
	
	// Botania implementation start
	@Override
	public boolean provideBlock(EntityPlayer player, ItemStack requestor, ItemStack stack, Block block, int meta, boolean doit) {

		if (player.world.isRemote) return false;
		
		int slot = getStoredBlock(stack, block, meta);
		if (slot >= 0) {
			int count = getStoredCount(stack, slot);
			if (count > 0) {
				if (doit)
					setStoredCount(stack, slot);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int getBlockCount(EntityPlayer player, ItemStack requestor, ItemStack stack, Block block, int meta) {

		int slot = getStoredBlock(stack, block, meta);
		if (slot >= 0)
			return getStoredCount(stack, slot);
			
		return 0;
	}
}
