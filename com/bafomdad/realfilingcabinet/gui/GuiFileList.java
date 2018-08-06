package com.bafomdad.realfilingcabinet.gui;

import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemStackHandler;

import org.lwjgl.opengl.GL11;

import com.bafomdad.realfilingcabinet.NewConfigRFC.ConfigRFC;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.blocks.BlockManaCabinet;
import com.bafomdad.realfilingcabinet.blocks.BlockRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileManaCabinet;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.inventory.InventoryRFC;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

public class GuiFileList extends Gui {

	private Minecraft mc;
	
	public GuiFileList(Minecraft mc) {
		
		super();
		this.mc = mc;
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
			
			if (mop != null) {
				IBlockState state = mop.typeOfHit == RayTraceResult.Type.BLOCK ? mc.world.getBlockState(mop.getBlockPos()) : null;
				Block block = state == null ? null : state.getBlock();
				ItemStack mainhand = player.getHeldItemMainhand();
				ItemStack offhand = player.getHeldItemOffhand();
				boolean flag = (!mainhand.isEmpty() && mainhand.getItem() == RFCItems.magnifyingGlass) || (!offhand.isEmpty() && offhand.getItem() == RFCItems.magnifyingGlass);
				
				if (ConfigRFC.magnifyingGlassGui && flag) {
					if (block instanceof BlockRFC) {
						TileEntity tile = player.world.getTileEntity(mop.getBlockPos());
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
					else if (block instanceof BlockManaCabinet) {
						TileEntity tile = player.world.getTileEntity(mop.getBlockPos());
						if (tile instanceof TileManaCabinet) {
							List<String> list = getManaList((TileManaCabinet)tile);
							if (!list.isEmpty())
								this.drawCenteredString(mc.fontRenderer, list.get(0), width / 2, 5 + 10, Integer.parseInt("FFFFFF", 16));
						}
					}
				}
			}
			profiler.endSection();
		}
	}
	
	private List getFileList(InventoryRFC inv, boolean crouching) {
		
		List<String> list = new ArrayList();
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack folder = inv.getTrueStackInSlot(i);
			if (folder != ItemStack.EMPTY && folder.getItem() instanceof IFolder) {
				if (ItemFolder.getObject(folder) != null) {
					String name = TextHelper.folderStr(folder);
					long count = ItemFolder.getFileSize(folder);
					
					if (!crouching) {
						if (folder.getItemDamage() == 2)
							list.add(name + " - " + TextHelper.format(count) + " [" + ItemFolder.getRemSize(folder) + " / " + ((ItemStack)ItemFolder.getObject(folder)).getMaxDamage() + "]");
						if (folder.getItemDamage() == 4)
							list.add(name + " - " + count + "mB");
						else if (folder.getItemDamage() != 2 && folder.getItemDamage() != 4)
							list.add(name + " - " + TextHelper.format(count));
					}
					else {
						if (folder.getItemDamage() == 2)
							list.add(name + " - " + count + " [" + ItemFolder.getRemSize(folder) + " / " + ((ItemStack)ItemFolder.getObject(folder)).getMaxDamage() + "]");
						if (folder.getItemDamage() == 4)
							list.add(name + " - " + count + "mB");
						else if (folder.getItemDamage() != 2 && folder.getItemDamage() != 4)
							list.add(name + " - " + count);
					}
				}
			}
		}
		return list;
	}

	private List getManaList(TileManaCabinet tile) {
		
		List<String> list = new ArrayList();
		
		double count = tile.getTotalInternalManaPool();
		double calc = 0.0;
		if (count > 0)
			calc = count / 1000000;
		NumberFormat percentFormatter = NumberFormat.getPercentInstance();
		String percentOut = percentFormatter.format(calc);
		list.add(percentOut + " of a full mana pool");
		
		return list;
	}
}
