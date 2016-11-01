package com.bafomdad.realfilingcabinet.events;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

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
			if (frame.getDisplayedItem() != null && frame.getDisplayedItem().getItem() == RFCItems.filter)
			{
				int rotation = frame.getRotation() + 1;
				if (rotation > 7)
					rotation = 0;
				
				TileEntity tile = frame.worldObj.getTileEntity(new BlockPos(frame.posX, frame.posY - 1, frame.posZ));
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
		ItemStack offhand = event.getEntityPlayer().getHeldItemOffhand();
		
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
						ItemStack folderStack = (ItemStack)ItemFolder.getObject(folder);
						if (folderStack != null && ItemStack.areItemsEqual(folderStack, estack))
						{
							if (folder.getItemDamage() == 1) {
								EnderUtils.syncToTile(EnderUtils.getTileLoc(folder), NBTUtils.getInt(folder, StringLibs.RFC_DIM, 0), NBTUtils.getInt(folder, StringLibs.RFC_SLOTINDEX, 0), estack.stackSize, false);
							}
							else
								ItemFolder.add(folder, estack.stackSize);
							
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
}
