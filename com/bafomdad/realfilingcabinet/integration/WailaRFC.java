package com.bafomdad.realfilingcabinet.integration;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import mcp.mobius.waila.api.IWailaRegistrar;

import com.bafomdad.realfilingcabinet.blocks.BlockManaCabinet;
import com.bafomdad.realfilingcabinet.blocks.BlockRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileManaCabinet;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.inventory.InventoryRFC;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.utils.SmeltingUtils;

public class WailaRFC {
	
	public static void register() {
		
		FMLInterModComms.sendMessage("waila", "register", "com.bafomdad.realfilingcabinet.integration.WailaRFC.load");
	}

	public static void load(IWailaRegistrar registrar) {
		
		registrar.registerBodyProvider(new WailaProvider(), BlockRFC.class);
		registrar.registerBodyProvider(new WailaManaProvider(), BlockManaCabinet.class);
		//registrar.registerNBTProvider(new WailaProvider(), BlockRFC.class);
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

			TileEntityRFC tileRFC = (TileEntityRFC)accessor.getTileEntity();
			InventoryRFC inv = tileRFC.getInventory();
			for (int i = 0; i < inv.getSlots(); i++) {
				
				String name = "";
				ItemStack folder = inv.getTrueStackInSlot(i);
				if (!folder.isEmpty() && ItemFolder.getObject(folder) != null) {
					if (ItemFolder.getObject(folder) instanceof ItemStack) {
						String stackName = ((ItemStack)ItemFolder.getObject(folder)).getDisplayName();
						long storedSize = ItemFolder.getFileSize(folder);
						
						name = stackName + " - " + storedSize;
						//name += SmeltingUtils.getSmeltingPercentage(tileRFC, i);
					}
					else if (ItemFolder.getObject(folder) instanceof FluidStack) {
						String fluidName = ((FluidStack)ItemFolder.getObject(folder)).getLocalizedName();

						long storedSize = ItemFolder.getFileSize(folder);
						name = fluidName + " - " + storedSize + " mB";
					}
					else if (ItemFolder.getObject(folder) instanceof String) {
						if (folder.getItemDamage() == 3) {
							String entityName = ItemFolder.getFolderDisplayName(folder);
							long storedSize = ItemFolder.getFileSize(folder);
							
							name = entityName + " - " + storedSize;
						}
					}
					if (!name.isEmpty())
						currenttip.add(name);
				}
			}
			String owner = "";
			if (tileRFC.getCabinetOwner() != null) {
				EntityPlayer onlinePlayer = accessor.getPlayer().world.getPlayerEntityByUUID(tileRFC.getCabinetOwner());
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
		public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, BlockPos pos) {
	
			if (tile instanceof TileEntityRFC) {
				if (UpgradeHelper.getUpgrade((TileEntityRFC)tile, StringLibs.TAG_SMELT) != null) {
					TileEntityRFC te = (TileEntityRFC)tile;
					SmeltingUtils.writeSmeltNBT(te, tag);
				}
			}
			return tag;
		}
	}
	
	public static class WailaManaProvider implements IWailaDataProvider {
		
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

			double count = ((TileManaCabinet)accessor.getTileEntity()).getTotalInternalManaPool();
			double calc = 0.0;
			if (count > 0)
				calc = count / 1000000;
			NumberFormat percentFormatter = NumberFormat.getPercentInstance();
			String percentOut = percentFormatter.format(calc);
			currenttip.add(percentOut + " of a full mana pool");
			
			return currenttip;
		}
		
		@Override
		public NBTTagCompound getNBTData(EntityPlayerMP arg0, TileEntity arg1, NBTTagCompound arg2, World arg3, BlockPos arg4) {
	
			return null;
		}
	}
}
