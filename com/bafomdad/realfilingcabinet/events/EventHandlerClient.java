package com.bafomdad.realfilingcabinet.events;

import com.bafomdad.realfilingcabinet.NewConfigRFC.ConfigRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.init.*;
import com.bafomdad.realfilingcabinet.integration.BotaniaRFC;
import com.bafomdad.realfilingcabinet.items.ItemEmptyFolder;
import com.bafomdad.realfilingcabinet.items.ItemFilter;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.items.ItemUpgrades;
import com.bafomdad.realfilingcabinet.network.PacketMouse;
import com.bafomdad.realfilingcabinet.network.RFCPacketHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandlerClient {
	
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		
		if (RealFilingCabinet.botaniaLoaded && ConfigRFC.botaniaIntegration)
			BotaniaRFC.initClient(event);
		
		// BLOCKS
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RFCBlocks.blockRFC), 0, new ModelResourceLocation(RFCBlocks.blockRFC.getRegistryName(), "inventory"));
		
		// ITEMS
		for (int i = 0; i < ItemEmptyFolder.FolderType.values().length; ++i)
			ModelLoader.setCustomModelResourceLocation(RFCItems.emptyFolder, i, new ModelResourceLocation(RFCItems.emptyFolder.getRegistryName() + "_" + ItemEmptyFolder.FolderType.values()[i].toString().toLowerCase(), "inventory"));
		
		for (int i = 0; i < ItemFolder.FolderType.values().length; ++i)
			ModelLoader.setCustomModelResourceLocation(RFCItems.folder, i, new ModelResourceLocation(RFCItems.folder.getRegistryName() + "_" + ItemFolder.FolderType.values()[i].toString().toLowerCase(), "inventory"));
		
		for (int i = 0; i < ItemUpgrades.UpgradeType.values().length; ++i)
			ModelLoader.setCustomModelResourceLocation(RFCItems.upgrades, i, new ModelResourceLocation(RFCItems.upgrades.getRegistryName() + "_" + ItemUpgrades.UpgradeType.values()[i].toString().toLowerCase(), "inventory"));
		
		ModelLoader.setCustomModelResourceLocation(RFCItems.magnifyingGlass, 0, new ModelResourceLocation(RFCItems.magnifyingGlass.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(RFCItems.whiteoutTape, 0, new ModelResourceLocation(RFCItems.whiteoutTape.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(RFCItems.filter, 0, new ModelResourceLocation(RFCItems.filter.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(RFCItems.debugger, 0, new ModelResourceLocation(RFCItems.debugger.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(RFCItems.mysteryFolder, 0, new ModelResourceLocation(RFCItems.mysteryFolder.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(RFCItems.suitcase, 0, new ModelResourceLocation(RFCItems.suitcase.getRegistryName(), "inventory"));
		
		ModelLoader.setCustomModelResourceLocation(RFCItems.keys, 0, new ModelResourceLocation(RFCItems.keys.getRegistryName() + "_" + RFCItems.keys.keyTypes[0], "inventory"));
		ModelLoader.setCustomModelResourceLocation(RFCItems.keys, 1, new ModelResourceLocation(RFCItems.keys.getRegistryName() + "_" + RFCItems.keys.keyTypes[1], "inventory"));
		
		if (RealFilingCabinet.tcLoaded && ConfigRFC.tcIntegration) {
			ModelLoader.setCustomModelResourceLocation(RFCItems.aspectFolder, 0, new ModelResourceLocation(RFCItems.aspectFolder.getRegistryName(), "inventory"));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RFCBlocks.blockAC), 0, new ModelResourceLocation(RFCBlocks.blockAC.getRegistryName(), "inventory"));
		}
	}
	
	@SubscribeEvent
	public void onRenderItemFrame(RenderItemInFrameEvent event) {
		
		if (!event.getItem().isEmpty() && event.getItem().getItem() instanceof ItemFilter) {
			EntityItemFrame frame = event.getEntityItemFrame();
			int rotation = frame.getRotation();
			
			TileEntity tile = frame.getEntityWorld().getTileEntity(new BlockPos(frame.posX, frame.posY - 1, frame.posZ));
			if (tile instanceof TileEntityRFC) {
				TileEntityRFC tileRFC = (TileEntityRFC)tile;
				ItemStack stack = tileRFC.getInventory().getStackFromFolder(rotation);
				if (!stack.isEmpty()) {
					event.setCanceled(true);
					GlStateManager.pushMatrix();
					GlStateManager.enableLighting();
					GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
					GlStateManager.rotate((frame.getRotation() % 8 * 2) * 180.0F / 8.0F, 0.0F, 0.0F, 1.0F);
					if (stack.getItem() instanceof ItemBlock)
						GlStateManager.scale(0.25F, 0.25F, 0.25F);
					else
						GlStateManager.scale(0.5F, 0.5F, 0.5F);
					Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
					GlStateManager.popMatrix();
				}
			}
		}
	}
	
	@SubscribeEvent
	public void handleMouseWheel(MouseEvent event) {
		
		if (event.getDwheel() != 0) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (!player.isSneaking()) return;
			
			ItemStack suitcase = player.getHeldItemMainhand();
			if (!suitcase.isEmpty() && suitcase.getItem() == RFCItems.suitcase) {
				boolean next = (event.getDwheel() > 0) ? true : false;
				
				RFCPacketHandler.INSTANCE.sendToServer(new PacketMouse(next));
				event.setCanceled(true);
				return;
			}
		}
	}
	
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		
		if (event.getModID().equals(RealFilingCabinet.MOD_ID))
			ConfigManager.load(RealFilingCabinet.MOD_ID, Config.Type.INSTANCE);
	}
}
