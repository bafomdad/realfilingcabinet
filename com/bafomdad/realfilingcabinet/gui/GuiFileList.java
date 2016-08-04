package com.bafomdad.realfilingcabinet.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.opengl.GL11;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.TileEntityRFC;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GuiFileList extends Gui {

	private Minecraft mc;
	
	public GuiFileList(Minecraft mc) {
		
		super();
		this.mc = mc;
	}
	
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent.Post event) {
		
//		if (event.isCancelable())
//			return;
		Minecraft mc = Minecraft.getMinecraft();
		Profiler profiler = mc.mcProfiler;
		
		if (event.type == ElementType.ALL)
		{
			profiler.startSection("RFC-hud");
			
			ScaledResolution scaled = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
			int width = scaled.getScaledWidth();
			int height = scaled.getScaledHeight();

			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			MovingObjectPosition mop = player.rayTrace(5, 5);
			Block block = player.worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ);
			ItemStack magnifyingGlass = player.getCurrentEquippedItem();
			if (block != null && block == RealFilingCabinet.blockRFC && (magnifyingGlass != null && magnifyingGlass.getItem() == RealFilingCabinet.itemMagnifyingGlass)) {
				TileEntity tile = player.worldObj.getTileEntity(mop.blockX, mop.blockY, mop.blockZ);
				if (tile != null && tile instanceof TileEntityRFC)
				{
					List<String> list = getFileList((TileEntityRFC)tile);
					if (!list.isEmpty())
					{
						for (int i = 0; i < list.size(); i++)
						{
							GL11.glDisable(GL11.GL_LIGHTING);	
							this.drawCenteredString(mc.fontRenderer, list.get(i), width / 2, 0 + (i * 10), Integer.parseInt("FFFFFF", 16));
						}
					}
				}
			}
			profiler.endSection();
		}
	}
	
	private List getFileList(TileEntityRFC tile) {
		
		List<String> list = new ArrayList();
		for (int i = 0; i < tile.getSizeInventory() - 2; i++)
		{
			ItemStack folder = tile.getStackInSlot(i);
			if (folder != null && folder.getItem() == RealFilingCabinet.itemFolder)
			{
				ItemStack folderStack = ItemFolder.getStack(folder);
				if (folderStack != null)
				{
					int count = ItemFolder.getFileSize(folder);
					list.add(count + " " + StatCollector.translateToLocal(folderStack.getUnlocalizedName() + ".name"));
				}
			}
		}
		return list;
	}
}
