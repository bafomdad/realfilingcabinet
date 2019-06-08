package com.bafomdad.realfilingcabinet.events;

import thaumcraft.api.aspects.Aspect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.enums.FolderType;
import com.bafomdad.realfilingcabinet.init.RFCIntegration;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemAspectFolder;
import com.bafomdad.realfilingcabinet.network.PacketMouse;
import com.bafomdad.realfilingcabinet.network.RFCPacketHandler;

@Mod.EventBusSubscriber(modid=RealFilingCabinet.MOD_ID, value=Side.CLIENT)
public class RFCEventHandlerClient {
	
	@SubscribeEvent
	public static void onRenderItemFrame(RenderItemInFrameEvent event) {
		
		if (!event.getItem().isEmpty() && event.getItem().getItem() == RFCItems.FILTER) {
			EntityItemFrame frame = event.getEntityItemFrame();
			int rotation = frame.getRotation();
			
			TileEntity tile = frame.getEntityWorld().getTileEntity(new BlockPos(frame.posX, frame.posY - 1, frame.posZ));
			if (tile instanceof TileFilingCabinet) {
				TileFilingCabinet cabinet = (TileFilingCabinet)tile;
				ItemStack stack = cabinet.getInventory().getStackFromFolder(rotation);
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
	public static void handleMouseWheel(MouseEvent event) {
		
		if (event.getDwheel() != 0) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (!player.isSneaking()) return;
			
			ItemStack suitcase = player.getHeldItemMainhand();
			if (!suitcase.isEmpty() && (suitcase.getItem() == RFCItems.SUITCASE || (suitcase.getItem() == RFCItems.FOLDER && suitcase.getItemDamage() == FolderType.ENDER.ordinal()))) {
				RFCPacketHandler.INSTANCE.sendToServer(new PacketMouse(event.getDwheel()));
				event.setCanceled(true);
				return;
			}
		}
	}
	
	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		
		if (event.getModID().equals(RealFilingCabinet.MOD_ID))
			ConfigManager.sync(RealFilingCabinet.MOD_ID, Config.Type.INSTANCE);
	}

	@SubscribeEvent
	public static void registerColors(ColorHandlerEvent.Item event) {
		
		ItemColors ic = event.getItemColors();
		ic.registerItemColorHandler(new IItemColor() {
			
			@Override
			public int colorMultiplier(ItemStack stack, int tintIndex) {
				
				if (!stack.isEmpty() && stack.getItem() == RFCItems.EMPTYDYEDFOLDER) {
					return EnumDyeColor.byMetadata(stack.getItemDamage()).getColorValue();
				}
				return 0xffffff;
			}
		}, RFCItems.EMPTYDYEDFOLDER);
		ic.registerItemColorHandler(new IItemColor() {
			
			@Override
			public int colorMultiplier(ItemStack stack, int tintIndex) {
				
				if (!stack.isEmpty() && stack.getItem() == RFCItems.DYEDFOLDER) {
					return EnumDyeColor.byMetadata(stack.getItemDamage()).getColorValue();
				}
				return 0xffffff;
			}
		}, RFCItems.DYEDFOLDER);
		RFCIntegration.canLoad(RFCIntegration.THAUMCRAFT).ifPresent(c -> {
			ic.registerItemColorHandler(new IItemColor() {
				
				@Override
				public int colorMultiplier(ItemStack stack, int tintIndex) {

					if (stack.getItem() == RFCItems.FOLDER_ASPECT) {
						if (tintIndex == 1) {
							Aspect asp = ItemAspectFolder.getAspectFromFolder(stack);
							if (asp != null)
								return asp.getColor();
						}
					}
					return 0xffffff;
				}
			}, RFCItems.FOLDER_ASPECT);
		});
	}
}
