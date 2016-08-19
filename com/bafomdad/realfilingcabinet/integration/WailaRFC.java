package com.bafomdad.realfilingcabinet.integration;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.bafomdad.realfilingcabinet.blocks.BlockRFC;
import com.bafomdad.realfilingcabinet.blocks.TileEntityRFC;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

import cpw.mods.fml.common.event.FMLInterModComms;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;

public class WailaRFC {
	
	public static void register() {
		
		FMLInterModComms.sendMessage("Waila", "register", "com.bafomdad.realfilingcabinet.integration.WailaRFC.load");
	}

	public static void load(IWailaRegistrar registrar) {
		
		registrar.registerBodyProvider(new WailaProvider(), BlockRFC.class);
	}
	
	public static class WailaProvider implements IWailaDataProvider {

		@Override
		public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {

			return null;
		}

		@Override
		public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

			return currenttip;
		}

		@Override
		public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

			TileEntityRFC tile = (TileEntityRFC)accessor.getTileEntity();
			for (int i = 0; i < tile.getSizeInventory() - 2; i++) {
				
				String name = "";
				
				ItemStack folder = tile.getStackInSlot(i);
				if (folder != null)
				{
					String stackName = ItemFolder.getStack(folder).getDisplayName();
					int storedSize = ItemFolder.getFileSize(folder);
				
					name = stackName + " - " + storedSize;	
				}
				if (!name.isEmpty())
					currenttip.add(name);
			}
			return currenttip;
		}

		@Override
		public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

			return currenttip;
		}

		@Override
		public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {

			return null;
		}
	}
}
