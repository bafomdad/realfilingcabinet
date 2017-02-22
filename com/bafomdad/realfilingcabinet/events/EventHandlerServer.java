package com.bafomdad.realfilingcabinet.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.bafomdad.realfilingcabinet.LogRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.utils.EnderUtils;
import com.bafomdad.realfilingcabinet.utils.MobUtils;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;

public class EventHandlerServer {

	@SubscribeEvent
	public void itemFrameInteract(EntityInteract event) {
		
		if (event.getEntityPlayer() != null && (event.getTarget() != null && event.getTarget() instanceof EntityItemFrame)) {
			EntityItemFrame frame = (EntityItemFrame)event.getTarget();
			if (frame.getDisplayedItem() != ItemStack.EMPTY && frame.getDisplayedItem().getItem() == RFCItems.filter)
			{
				int rotation = frame.getRotation() + 1;
				if (rotation > 7)
					rotation = 0;
				
				TileEntity tile = frame.getEntityWorld().getTileEntity(new BlockPos(frame.posX, frame.posY - 1, frame.posZ));
				if (tile != null && tile instanceof TileEntityRFC) {
					TileEntityRFC tileRFC = (TileEntityRFC)tile;
					ItemStack stack = tileRFC.getInventory().getStackFromFolder(rotation);
					if (!stack.isEmpty())
						frame.getDisplayedItem().setStackDisplayName(stack.getDisplayName());
					else if (stack == ItemStack.EMPTY && frame.getDisplayedItem().hasDisplayName())
						frame.getDisplayedItem().clearCustomName();
				}
			}
		}
		if (event.getEntityPlayer() != null && (event.getTarget() != null && event.getTarget() instanceof EntityVillager)) {
			if (!event.getEntityPlayer().world.isRemote && event.getEntityPlayer().getActiveHand() == EnumHand.MAIN_HAND) {
				ItemStack folder = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
				if (!folder.isEmpty() && (folder.getItem() == RFCItems.folder || folder.getItem() == RFCItems.emptyFolder)) {
					MobUtils.addOrCreateMobFolder(event.getEntityPlayer(), folder, (EntityLivingBase)event.getTarget());
				}
			}
		}
	}
	
	@SubscribeEvent
	public void rightClickWithShield(PlayerInteractEvent.RightClickBlock event) {
		
		if (event.getSide() == Side.CLIENT)
			return;
		
		ItemStack mainhand = event.getEntityPlayer().getHeldItemMainhand();
		
		if (event.getEntityPlayer().isSneaking() && event.getWorld().getTileEntity(event.getPos()) != null && event.getWorld().getTileEntity(event.getPos()) instanceof TileEntityRFC)
		{
			TileEntityRFC tileRFC = (TileEntityRFC)event.getWorld().getTileEntity(event.getPos());
			if (!mainhand.isEmpty() || !tileRFC.isOpen)
				return;
			
			if (UpgradeHelper.getUpgrade(tileRFC, StringLibs.TAG_ENDER) != null)
			{
				EnderUtils.extractEnderFolder(tileRFC, event.getEntityPlayer());
				return;
			}
			for (int i = tileRFC.getInventory().getSlots() - 1; i >= 0; i--)
			{
				ItemStack folder = tileRFC.getInventory().getTrueStackInSlot(i);
				if (!folder.isEmpty())
				{
					tileRFC.getInventory().setStackInSlot(i, ItemStack.EMPTY);
					event.getEntityPlayer().setHeldItem(EnumHand.MAIN_HAND, folder);
					tileRFC.markBlockForUpdate();
					break;
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPickupItems(EntityItemPickupEvent event) {
		
		ItemStack estack = event.getItem().getEntityItem();
		
		if (estack.getCount() > 0)
		{
			for (int i = 0; i < event.getEntityPlayer().inventory.getSizeInventory(); i++) {
				if (i == event.getEntityPlayer().inventory.currentItem)
					continue;
				
				ItemStack folder = event.getEntityPlayer().inventory.getStackInSlot(i);
				if (!folder.isEmpty() && folder.getItem() == RFCItems.folder) {
					if (ItemFolder.getObject(folder) instanceof ItemStack)
					{
						if (folder.getItemDamage() == 2)
						{
							if (estack.hasTagCompound())
								return;
							
							if (estack.getItem() == ((ItemStack)ItemFolder.getObject(folder)).getItem())
							{
								int remSize = estack.getItemDamage();
								int storedRem = ItemFolder.getRemSize(folder);
								
								ItemFolder.addRem(folder, estack.getMaxDamage() - estack.getItemDamage());
								int newRem = ItemFolder.getRemSize(folder);
								
								if (newRem >= estack.getMaxDamage())
								{
									ItemFolder.add(folder, 1);
									int newStoredRem = newRem - estack.getMaxDamage();
									ItemFolder.setRemSize(folder, newStoredRem);
								}
								event.setCanceled(true);
								event.getItem().setDead();
								break;
							}
						}
						ItemStack folderStack = (ItemStack)ItemFolder.getObject(folder);
						if (folderStack != ItemStack.EMPTY && ItemStack.areItemsEqual(folderStack, estack))
						{
							if (folder.getItemDamage() == 1) {
								EnderUtils.syncToTile(EnderUtils.getTileLoc(folder), NBTUtils.getInt(folder, StringLibs.RFC_DIM, 0), NBTUtils.getInt(folder, StringLibs.RFC_SLOTINDEX, 0), estack.getCount(), false);
							}
							else
								ItemFolder.add(folder, estack.getCount());
							
							event.setCanceled(true);
							event.getItem().setDead();
							break;
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void enderFolderTick(TickEvent.PlayerTickEvent event) {
		
		if (event.side == Side.CLIENT)
			return;
		
		for (int i = 0; i < event.player.inventory.mainInventory.size(); i++) {
			ItemStack enderFolder = event.player.inventory.getStackInSlot(i);
			if (enderFolder.isEmpty())
				continue;
			
			if (enderFolder.getItem() == RFCItems.folder && enderFolder.getItemDamage() == 1)
			{
				if (!enderFolder.getTagCompound().hasKey(StringLibs.RFC_SLOTINDEX))
					return;
				
				TileEntityRFC tile = EnderUtils.getTileLoc(enderFolder);
				if (tile != null) {
					EnderUtils.syncToFolder(tile, enderFolder, NBTUtils.getInt(enderFolder, StringLibs.RFC_SLOTINDEX, 0));
					break;
				}
				else if (tile == null || UpgradeHelper.getUpgrade(tile, StringLibs.TAG_ENDER) == null) {
					ItemFolder.setFileSize(enderFolder, 0);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		
		LootPool pool = event.getTable().getPool("main");
		if (pool == null) {
			pool = new LootPool(new LootEntry[0], new LootCondition[0], new RandomValueRange(5, 10), new RandomValueRange(0), "main");
			event.getTable().addPool(pool);
		}
		if (event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON))
		{
			pool.addEntry(new LootEntryItem(RFCItems.mysteryFolder, 50, 0, new LootFunction[0], new LootCondition[0], RealFilingCabinet.MOD_ID + ":" + RFCItems.mysteryFolder.getUnlocalizedName()));
		}
		if (event.getName().equals(LootTableList.CHESTS_WOODLAND_MANSION))
		{
			pool.addEntry(new LootEntryItem(RFCItems.mysteryFolder, 70, 0, new LootFunction[0], new LootCondition[0], RealFilingCabinet.MOD_ID + ":" + RFCItems.mysteryFolder.getUnlocalizedName()));
		}
	}
}
