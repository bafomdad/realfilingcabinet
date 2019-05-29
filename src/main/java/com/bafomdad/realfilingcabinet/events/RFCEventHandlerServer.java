package com.bafomdad.realfilingcabinet.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandlerModifiable;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.enums.FolderType;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemSuitcase;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityProviderFolder;
import com.bafomdad.realfilingcabinet.utils.EnderUtils;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;
import com.bafomdad.realfilingcabinet.utils.StorageUtils;

@Mod.EventBusSubscriber(modid=RealFilingCabinet.MOD_ID)
public class RFCEventHandlerServer {
	
	@SubscribeEvent
	public static void rightClickWithshield(PlayerInteractEvent.RightClickBlock event) {
		
		if (event.getSide() == Side.CLIENT) return;
		
		ItemStack mainhand = event.getEntityPlayer().getHeldItemMainhand();
		TileEntity tile = event.getWorld().getTileEntity(event.getPos());
		
		if (event.getEntityPlayer().isSneaking() && tile instanceof TileEntityRFC) {
			TileEntityRFC tileRFC = (TileEntityRFC)event.getWorld().getTileEntity(event.getPos());
			if (!mainhand.isEmpty() || !tileRFC.isOpen)
				return;
			
			StorageUtils.folderExtract((TileEntityRFC)tile, event.getEntityPlayer());
		}
	}
	
	@SubscribeEvent
	public static void onPickupItems(EntityItemPickupEvent event) {
		
		if (!ConfigRFC.pickupStuff || event.getEntity().world.isRemote) return;
		
		ItemStack estack = event.getItem().getItem();
		if (estack.getCount() > 0) {
			// suitcase
			for (EnumHand hand : EnumHand.values()) {
				ItemStack suitcase = event.getEntityPlayer().getHeldItem(hand);
				if (!suitcase.isEmpty() && suitcase.getItem() == RFCItems.SUITCASE) {
					IItemHandlerModifiable inv = ((ItemSuitcase)suitcase.getItem()).getSuitcaseInv(suitcase);
					for (int j = 0; j < inv.getSlots(); j++) {
						ItemStack folder = inv.getStackInSlot(j);
						if (!folder.isEmpty() && folder.getItem() instanceof IFolder) {
							Object toInsert = FolderUtils.get(folder).insert(estack, false);
							if (toInsert instanceof ItemStack && ((ItemStack)toInsert).isEmpty()) {
								event.setCanceled(true);
								((EntityPlayerMP)event.getEntityPlayer()).connection.sendPacket(new SPacketCollectItem(event.getItem().getEntityId(), event.getEntityPlayer().getEntityId(), estack.getCount()));
								estack.setCount(((ItemStack)toInsert).getCount());
								if (folder.getItem() == RFCItems.FOLDER && folder.getItemDamage() == FolderType.ENDER.ordinal()) {
									FolderUtils.get(folder).setExtractSize(1);
									EnderUtils.syncToTile(folder);
								}
								break;
							} else {
								((EntityPlayerMP)event.getEntityPlayer()).connection.sendPacket(new SPacketCollectItem(event.getItem().getEntityId(), event.getEntityPlayer().getEntityId(), estack.getCount()));
							}
						}
					}
				}
			}
			// folder
			for (int i = 0; i < event.getEntityPlayer().inventory.getSizeInventory(); i++) {
				ItemStack stack = event.getEntityPlayer().inventory.getStackInSlot(i);
				if (!stack.isEmpty() && stack.getItem() instanceof IFolder) {
					Object toInsert = FolderUtils.get(stack).insert(estack, false);
					if (toInsert instanceof ItemStack && ((ItemStack)toInsert).isEmpty()) {
						event.setCanceled(true);
						((EntityPlayerMP)event.getEntityPlayer()).connection.sendPacket(new SPacketCollectItem(event.getItem().getEntityId(), event.getEntityPlayer().getEntityId(), estack.getCount()));
						estack.setCount(((ItemStack)toInsert).getCount());
						if (stack.getItem() == RFCItems.FOLDER && stack.getItemDamage() == FolderType.ENDER.ordinal()) {
							FolderUtils.get(stack).setExtractSize(1);
							EnderUtils.syncToTile(stack);
						}
						break;
					} else {
						((EntityPlayerMP)event.getEntityPlayer()).connection.sendPacket(new SPacketCollectItem(event.getItem().getEntityId(), event.getEntityPlayer().getEntityId(), estack.getCount()));
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onLootTableLoad(LootTableLoadEvent event) {
		
		if (RFCItems.MYSTERYFOLDER == Items.AIR) return;
		
		LootPool pool = event.getTable().getPool("main");
		if (pool == null) {
			pool = new LootPool(new LootEntry[0], new LootCondition[0], new RandomValueRange(5, 10), new RandomValueRange(0), "main");
			event.getTable().addPool(pool);
		}
		if (event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON))
		{
			pool.addEntry(new LootEntryItem(RFCItems.MYSTERYFOLDER, 50, 0, new LootFunction[0], new LootCondition[0], RealFilingCabinet.MOD_ID + ":" + RFCItems.MYSTERYFOLDER.getTranslationKey()));
		}
		if (event.getName().equals(LootTableList.CHESTS_WOODLAND_MANSION))
		{
			pool.addEntry(new LootEntryItem(RFCItems.MYSTERYFOLDER, 70, 0, new LootFunction[0], new LootCondition[0], RealFilingCabinet.MOD_ID + ":" + RFCItems.MYSTERYFOLDER.getTranslationKey()));
		}
	}
	
	@SubscribeEvent
	public static void onHitEntity(LivingAttackEvent event) {
		
		if (event.getEntityLiving() instanceof EntityPlayer || !(event.getSource().getTrueSource() instanceof EntityPlayer)) return;
		
		ItemStack folder = ((EntityPlayer)event.getSource().getTrueSource()).getHeldItemMainhand();
		if (!folder.isEmpty() && folder.getItem() instanceof IFolder) {
			Object obj = FolderUtils.get(folder).insert(event.getEntityLiving(), false);
			if (obj != null)
				event.setCanceled(true);
		}
	}
}
