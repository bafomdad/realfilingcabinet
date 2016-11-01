package com.bafomdad.realfilingcabinet.integration;

import javax.annotation.Nullable;

import com.bafomdad.realfilingcabinet.api.IFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class TopRFC {

	public static void register() {
		
		FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "com.bafomdad.realfilingcabinet.integration.TopRFC$GetTheOneProbe");
	}
	
	public static class GetTheOneProbe implements com.google.common.base.Function<ITheOneProbe, Void> {

		public static ITheOneProbe probe;
		
		@Nullable
		@Override
		public Void apply(ITheOneProbe theOneProbe) {

			probe = theOneProbe;
			probe.registerProvider(new IProbeInfoProvider() {
				@Override
				public String getID() {
					return "realfilingcabinet:default";
				}

				@Override
				public void addProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, IBlockState state, IProbeHitData data) {

					if (state.getBlock() instanceof IFilingCabinet) {
						TileEntityRFC tile = (TileEntityRFC)world.getTileEntity(data.getPos());
						if (tile != null) 
						{
							for (int i = 0; i < tile.getInventory().getSlots(); i++) {
								addFolderInfo(info, tile, i);
							}
							if (player.isSneaking())
								addExtraInfo(info, tile);
						}
					}
				}
			});
			return null;
		}
		
		public void addFolderInfo(IProbeInfo info, TileEntityRFC tile, int slot) {
			
			ItemStack folder = tile.getInventory().getTrueStackInSlot(slot);
			if (folder != null) {
				if (ItemFolder.getObject(folder) instanceof ItemStack)
				{
					String stackName = ((ItemStack)ItemFolder.getObject(folder)).getDisplayName();
					long storedSize = ItemFolder.getFileSize(folder);
					
					String name = stackName + " - " + storedSize;
					if (folder.getItemDamage() == 2)
					{
						int storedRem = ItemFolder.getRemSize(folder);
						int maxDamage = ((ItemStack)ItemFolder.getObject(folder)).getMaxDamage();
						name = stackName + " - " + storedSize + " [" + storedRem + " / " + maxDamage + "]"; 
					}
					info.horizontal().text(name);
				}
				else if (ItemFolder.getObject(folder) instanceof String)
				{
					String mobName = (String)ItemFolder.getObject(folder);
					if (!mobName.isEmpty())
					{
						long storedSize = ItemFolder.getFileSize(folder);
						String name = mobName + " - " + storedSize;
						info.horizontal().text(name);
					}
				}
			}
		}
		
		public void addExtraInfo(IProbeInfo info, TileEntityRFC tile) {
			
			addLockInfo(info, tile);
			addUpgradeInfo(info, tile);
		}
		
		public void addUpgradeInfo(IProbeInfo info, TileEntityRFC tile) {
			
			String upgrade = "Upgrade: ";
			if (!UpgradeHelper.hasUpgrade(tile))
				info.horizontal().text(TextFormatting.GRAY + upgrade + "NONE");
			else
				info.horizontal().text(TextFormatting.GRAY + upgrade + TextFormatting.GREEN + UpgradeHelper.getUpgrade(tile, tile.getTileData().getString(StringLibs.RFC_UPGRADE)));
			if (UpgradeHelper.isCreative(tile))
				info.horizontal().text(TextFormatting.DARK_PURPLE + "Creative Upgrade");
		}
		
		public void addLockInfo(IProbeInfo info, TileEntityRFC tile) {
			
			boolean locked = tile.isCabinetLocked();
			String lockFormat = "";
			if (locked)
				lockFormat = TextFormatting.RED + "Yes";
			else
				lockFormat = TextFormatting.RESET + "No";
			info.horizontal().text(TextFormatting.GRAY + "Locked: " + lockFormat);
		}
	}
}
