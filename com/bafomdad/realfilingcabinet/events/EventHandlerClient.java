package com.bafomdad.realfilingcabinet.events;

import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemFilter;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandlerClient {
	
	@SubscribeEvent
	public void renderItemFilter(RenderItemInFrameEvent event) {
		
		if (event.getEntityItemFrame().worldObj.getTotalWorldTime() % 60 == 0) {
			if (event.getItem().getItem() == RFCItems.filter) {
				EntityItemFrame frame = event.getEntityItemFrame();
				System.out.println(frame.getDisplayedItem());
				TileEntity tile = frame.worldObj.getTileEntity(new BlockPos(frame.posX, frame.posY - 1, frame.posZ));
				if (tile != null && tile instanceof TileEntityRFC) {
					TileEntityRFC tileRFC = (TileEntityRFC)tile;
					ItemStack toDisplay = tileRFC.getFilter();
					if (toDisplay != null) {
						frame.setDisplayedItem(toDisplay);
					}
					else
						frame.setDisplayedItem(new ItemStack(RFCItems.filter));
				}
			}
		}
	}
}
