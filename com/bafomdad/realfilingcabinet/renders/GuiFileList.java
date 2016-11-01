package com.bafomdad.realfilingcabinet.renders;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.blocks.BlockRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.inventory.InventoryRFC;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
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

public class GuiFileList extends Gui {

	private Minecraft mc;
	
	public GuiFileList(Minecraft mc) {
		
		super();
		this.mc = mc;
	}
	
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent.Post event) {
		
		Profiler profiler = mc.mcProfiler;
		
		if (event.getType() == ElementType.ALL && ConfigRFC.magnifyingGlassGui)
		{
			profiler.startSection("RFC-hud");
			
			ScaledResolution scaled = new ScaledResolution(mc);
			int width = scaled.getScaledWidth();
			int height = scaled.getScaledHeight();
			
			EntityPlayer player = mc.thePlayer;
			RayTraceResult mop = mc.objectMouseOver;
			
			if (mop != null) {
				IBlockState state = mop.typeOfHit == RayTraceResult.Type.BLOCK ? mc.theWorld.getBlockState(mop.getBlockPos()) : null;
				Block block = state == null ? null : state.getBlock();
				ItemStack magnifyingGlass = player.getHeldItemMainhand();
				
				if (block instanceof BlockRFC && magnifyingGlass != null && (magnifyingGlass.getItem() == RFCItems.magnifyingGlass))
				{
					TileEntity tile = player.worldObj.getTileEntity(mop.getBlockPos());
					if (tile != null && tile instanceof TileEntityRFC)
					{
						List<String> list = getFileList(((TileEntityRFC)tile).getInventory(), player.isSneaking());
						if (!list.isEmpty())
						{
							for (int i = 0; i < list.size(); i++)
							{
								GL11.glDisable(GL11.GL_LIGHTING);
								this.drawCenteredString(mc.fontRendererObj, list.get(i), width / 2, 0 + (i * 10), Integer.parseInt("FFFFFF", 16));
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
		for (int i = 0; i < inv.getSlots(); i++)
		{
			ItemStack folder = inv.getTrueStackInSlot(i);
			if (folder != null && folder.getItem() instanceof IFolder)
			{
				if (ItemFolder.getObject(folder) instanceof ItemStack)
				{
					ItemStack stack = (ItemStack)ItemFolder.getObject(folder);
					if (stack != null)
					{
						long count = ItemFolder.getFileSize(folder);
						if (folder.getItemDamage() == 2) {
							if (!crouching)
								list.add(TextHelper.format(count) + " " + stack.getDisplayName() + " [" + ItemFolder.getRemSize(folder) + " / " + stack.getMaxDamage() + "]");
							else
								list.add(count + " " + stack.getDisplayName() + " [" + ItemFolder.getRemSize(folder) + " / " + stack.getMaxDamage() + "]");
						}
						else {
							if (!crouching)
								list.add(TextHelper.format(count) + " " + stack.getDisplayName());
							else
								list.add(count + " " + stack.getDisplayName());
						}
					}
				}
				else if (ItemFolder.getObject(folder) instanceof String)
				{
					String str = (String)ItemFolder.getObject(folder);
					if (!str.isEmpty())
					{
						long count = ItemFolder.getFileSize(folder);
						if (!crouching)
							list.add(TextHelper.format(count) + " " + str);
						else
							list.add(count + " " + str);
					}
				}
			}
		}
		return list;
	}
}
