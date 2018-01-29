package com.bafomdad.realfilingcabinet.events;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.tileentity.TileEntity;
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

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.utils.EnderUtils;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;

public class EventHandlerServer {

	@SubscribeEvent
	public void itemFrameInteract(EntityInteract event) {
		
		if (event.getEntityPlayer() != null && (event.getTarget() != null && event.getTarget() instanceof EntityItemFrame)) {
			EntityItemFrame frame = (EntityItemFrame)event.getTarget();
			if (frame.getDisplayedItem() != null && frame.getDisplayedItem().getItem() == RFCItems.filter) {
				
				int rotation = frame.getRotation() + 1;
				if (rotation > 7)
					rotation = 0;
				
				TileEntity tile = frame.world.getTileEntity(new BlockPos(frame.posX, frame.posY - 1, frame.posZ));
				if (tile != null && tile instanceof TileEntityRFC) {
					TileEntityRFC tileRFC = (TileEntityRFC)tile;
					ItemStack stack = tileRFC.getInventory().getStackFromFolder(rotation);
					if (stack != null)
						frame.getDisplayedItem().setStackDisplayName(stack.getDisplayName());
					else if (stack == null && frame.getDisplayedItem().hasDisplayName())
						frame.getDisplayedItem().clearCustomName();
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
			if (mainhand != null || !tileRFC.isOpen)
				return;
			
			if (UpgradeHelper.getUpgrade(tileRFC, StringLibs.TAG_ENDER) != null)
			{
				EnderUtils.extractEnderFolder(tileRFC, event.getEntityPlayer());
				return;
			}
			for (int i = tileRFC.getInventory().getSlots() - 1; i >= 0; i--)
			{
				ItemStack folder = tileRFC.getInventory().getTrueStackInSlot(i);
				if (folder != null)
				{
					tileRFC.getInventory().setStackInSlot(i, null);
					event.getEntityPlayer().setHeldItem(EnumHand.MAIN_HAND, folder);
					tileRFC.markBlockForUpdate();
					break;
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPickupItems(EntityItemPickupEvent event) {
		
		if (!ConfigRFC.pickupStuff)
			return;
		
		ItemStack estack = event.getItem().getEntityItem();
		
		if (estack.stackSize > 0)
		{
			for (int i = 0; i < event.getEntityPlayer().inventory.getSizeInventory(); i++) {
				if (i == event.getEntityPlayer().inventory.currentItem)
					continue;
				
				ItemStack folder = event.getEntityPlayer().inventory.getStackInSlot(i);
				if (folder != null && folder.getItem() == RFCItems.folder) {
					if (ItemFolder.getObject(folder) instanceof ItemStack)
					{
						if (folder.getItemDamage() == 2)
						{
							if (estack.hasTagCompound() && !NBTUtils.getBoolean(folder, StringLibs.RFC_IGNORENBT, false))
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
								((EntityPlayerMP)event.getEntityPlayer()).connection.sendPacket(new SPacketCollectItem(event.getItem().getEntityId(), event.getEntityPlayer().getEntityId()));
								break;
							}
						}
						ItemStack folderStack = (ItemStack)ItemFolder.getObject(folder);
						if (folderStack != null && ItemStack.areItemsEqual(folderStack, estack))
						{
							if (folder.getItemDamage() == 5 && !ItemStack.areItemStackTagsEqual(folderStack, estack)) {
								return;
							}
							if (folder.getItemDamage() == 1) {
								EnderUtils.syncToTile(EnderUtils.getTileLoc(folder), NBTUtils.getInt(folder, StringLibs.RFC_DIM, 0), NBTUtils.getInt(folder, StringLibs.RFC_SLOTINDEX, 0), estack.stackSize, false);
							}
							else
								ItemFolder.add(folder, estack.stackSize);
							
							event.setCanceled(true);
							event.getItem().setDead();
							((EntityPlayerMP)event.getEntityPlayer()).connection.sendPacket(new SPacketCollectItem(event.getItem().getEntityId(), event.getEntityPlayer().getEntityId()));
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
		
		for (int i = 0; i < event.player.inventory.mainInventory.length; i++) {
			ItemStack enderFolder = event.player.inventory.getStackInSlot(i);
			if (enderFolder == null)
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
	}
}
