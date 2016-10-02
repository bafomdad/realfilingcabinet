package com.bafomdad.realfilingcabinet.integration;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import com.bafomdad.realfilingcabinet.blocks.BlockRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.inventory.InventoryRFC;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

public class WailaRFC {
	
	public static void register() {
		
		FMLInterModComms.sendMessage("Waila", "register", "com.bafomdad.realfilingcabinet.integration.WailaRFC.load");
	}

	public static void load(IWailaRegistrar registrar) {
		
		registrar.registerBodyProvider(new WailaProvider(), BlockRFC.class);
	}
	
	public static class WailaProvider implements IWailaDataProvider {
		
		@Override
		public ItemStack getWailaStack(IWailaDataAccessor arg0, IWailaConfigHandler arg1) {

			return null;
		}
		
		@Override
		public List<String> getWailaHead(ItemStack arg0, List<String> currenttip, IWailaDataAccessor arg2, IWailaConfigHandler arg3) {

			return currenttip;
		}

		@Override
		public List<String> getWailaBody(ItemStack stack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

			InventoryRFC inv = ((TileEntityRFC)accessor.getTileEntity()).getInventory();
			for (int i = 0; i < inv.getSlots(); i++) {
				
				String name = "";
				ItemStack folder = inv.getTrueStackInSlot(i);
				if (folder != null && ItemFolder.getObject(folder) != null && ItemFolder.getObject(folder) instanceof ItemStack) 
				{
					String stackName = ((ItemStack)ItemFolder.getObject(folder)).getDisplayName();
					long storedSize = ItemFolder.getFileSize(folder);
					
					name = stackName + " - " + storedSize;
				}
				if (!name.isEmpty())
					currenttip.add(name);
			}
			String owner = "";
			TileEntityRFC tileRFC = (TileEntityRFC)accessor.getTileEntity();
			if (tileRFC.getOwner() != null)
			{
				EntityPlayer onlinePlayer = accessor.getPlayer().worldObj.getPlayerEntityByUUID(tileRFC.getOwner());
				if (onlinePlayer != null)
					owner = "Locked, owned by: " + onlinePlayer.getName();
				else
					owner = "Locked";
			}
			if (!owner.isEmpty())
				currenttip.add(owner);
			
			return currenttip;
		}

		@Override
		public List<String> getWailaTail(ItemStack arg0, List<String> currenttip, IWailaDataAccessor arg2, IWailaConfigHandler arg3) {

			return currenttip;
		}
		
		@Override
		public NBTTagCompound getNBTData(EntityPlayerMP arg0, TileEntity arg1, NBTTagCompound arg2, World arg3, BlockPos arg4) {
	
			return null;
		}
	}
}
