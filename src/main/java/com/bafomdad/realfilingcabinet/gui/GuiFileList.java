package com.bafomdad.realfilingcabinet.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bafomdad.realfilingcabinet.api.IBlockCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.inventory.InventoryRFC;
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
			int width = scaled.getScaledWidth();
			int height = scaled.getScaledHeight();
			
			EntityPlayer player = mc.player;
			RayTraceResult mop = mc.objectMouseOver;
			
			if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
				Block block = mc.world.getBlockState(mop.getBlockPos()).getBlock();
				ItemStack mainhand = player.getHeldItemMainhand();
				ItemStack offhand = player.getHeldItemOffhand();
				boolean flag = (block instanceof IBlockCabinet && ((!mainhand.isEmpty() && mainhand.getItem() == RFCItems.MAGNIFYINGGLASS) || (!offhand.isEmpty() && offhand.getItem() == RFCItems.MAGNIFYINGGLASS)));
				
				if (flag) {
					TileEntity tile = mc.world.getTileEntity(mop.getBlockPos());
					if (tile instanceof TileEntityRFC) {
						List<String> list = getFileList(((TileEntityRFC)tile).getInventory(), player.isSneaking());
						if (!list.isEmpty()) {
							for (int i = 0; i < list.size(); i++) {
								GL11.glDisable(GL11.GL_LIGHTING);
								this.drawCenteredString(mc.fontRenderer, list.get(i), width / 2, 5 + (i * 10), Integer.parseInt("FFFFFF", 16));
							}
						}
					}
				}
			}
			profiler.endSection();
		}
	}
	
	private List getFileList(InventoryRFC inv, boolean crouching) {
		
		List<String> list = new ArrayList();
		for (int j = 0; j < inv.getSlots(); j++) {
			ItemStack folder = inv.getFolder(j);
			if (!folder.isEmpty()) {
				String name = FolderUtils.get(folder).getDisplayName();
				long count = FolderUtils.get(folder).getFileSize();
				
				list.add(name + " - " + count);
			}
		}
		return list;
	}
}
