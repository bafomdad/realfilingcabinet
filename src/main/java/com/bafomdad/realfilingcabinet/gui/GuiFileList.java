package com.bafomdad.realfilingcabinet.gui;

import java.util.List;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.lwjgl.opengl.GL11;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.api.IBlockCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityFolder;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;

public class GuiFileList extends Gui {

	private Minecraft mc;
	
	public GuiFileList() {
		
		this.mc = Minecraft.getMinecraft();
	}
	
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent.Post event) {
		
		Profiler profiler = mc.profiler;
		
		if (event.getType() == ElementType.ALL) {
			profiler.startSection("RFC-hud");
			
			ScaledResolution scaled = new ScaledResolution(mc);
			int width = scaled.getScaledWidth() / 2 + ConfigRFC.guiWidth;
			int height = ConfigRFC.guiHeight;
			
			EntityPlayer player = mc.player;
			RayTraceResult mop = mc.objectMouseOver;
			
			ItemStack mainhand = player.getHeldItemMainhand();
			ItemStack offhand = player.getHeldItemOffhand();
			
			if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
				Block block = mc.world.getBlockState(mop.getBlockPos()).getBlock();

				boolean flag = (block instanceof IBlockCabinet && (mainhand.getItem() == RFCItems.MAGNIFYINGGLASS || offhand.getItem() == RFCItems.MAGNIFYINGGLASS));
				
				if (flag) {
					TileEntity tile = mc.world.getTileEntity(mop.getBlockPos());
					List<String> list = ((IBlockCabinet)block).getInfoOverlay(tile, player.isSneaking());
					if (!list.isEmpty()) {
						for (int i = 0; i < list.size(); i++) {
							this.drawCenteredString(mc.fontRenderer, list.get(i), width, 5 + (i * 10), Integer.parseInt("FFFFFF", 16));
						}
					}
				}
			}
			profiler.endSection();
			if (ConfigRFC.folderHud) {
				profiler.startSection("RFC-folderHUD");
				
				final int w = scaled.getScaledWidth();
				final int h = scaled.getScaledHeight();
				
				Stream.of(mainhand, offhand).filter(f -> FolderUtils.get(f).getCap() != null).map(f -> FolderUtils.get(f).getCap()).findFirst().ifPresent(folder -> {
					if (folder.isItemStack()) {
						RenderHelper.enableGUIStandardItemLighting();
						mc.getRenderItem().renderItemAndEffectIntoGUI(folder.getItemStack(), (w - w) + 20, h - 20);
						this.drawString(mc.fontRenderer, "" + folder.getCount(), (w - w) + 40, h - 15, Integer.parseInt("FFFFFF", 16));
					}
				});
				profiler.endSection();
			}
		}
	}
}
