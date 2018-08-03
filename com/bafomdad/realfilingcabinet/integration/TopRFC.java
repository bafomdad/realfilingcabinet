package com.bafomdad.realfilingcabinet.integration;

import java.text.NumberFormat;

import javax.annotation.Nullable;

import com.bafomdad.realfilingcabinet.api.IFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileManaCabinet;
import com.bafomdad.realfilingcabinet.entity.EntityCabinet;
import com.bafomdad.realfilingcabinet.helpers.MobUpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.utils.SmeltingUtils;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
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
						TileEntity tile = world.getTileEntity(data.getPos());
						if (tile instanceof TileEntityRFC) {
							TileEntityRFC tileRFC = (TileEntityRFC)tile;
							for (int i = 0; i < tileRFC.getInventory().getSlots(); i++) {
								addFolderInfo(info, tileRFC, i);
							}
							if (player.isSneaking())
								addExtraInfo(info, tileRFC);
							if (UpgradeHelper.getUpgrade(tileRFC, StringLibs.TAG_SMELT) != null) {
								info.progress(tileRFC.fuelTime, SmeltingUtils.FUEL_TIME);
							}
						}
						else if (tile instanceof TileManaCabinet) {
							TileManaCabinet tileMana = (TileManaCabinet)tile;
							addManaInfo(info, tileMana);
						}
					}
				}
			});
			probe.registerEntityProvider(new IProbeInfoEntityProvider() {

				@Override
				public String getID() {

					return "realfilingcabinet:entity";
				}
				
				@Override
				public void addProbeEntityInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {

					if (entity instanceof EntityCabinet) {
						EntityCabinet cabinet = (EntityCabinet)entity;
						if (cabinet != null) {
							addCabinetInfo(info, cabinet);
							if (player.isSneaking())
								addMobUpgradeInfo(info, cabinet);
						}
					}
				}
			});
			return null;
		}
		
		public void addFolderInfo(IProbeInfo info, TileEntityRFC tile, int slot) {
			
			ItemStack folder = tile.getInventory().getTrueStackInSlot(slot);
			if (!folder.isEmpty()) {
				if (ItemFolder.getObject(folder) instanceof ItemStack) {
					String stackName = ((ItemStack)ItemFolder.getObject(folder)).getDisplayName();
					long storedSize = ItemFolder.getFileSize(folder);
					
					String name = stackName + " - " + storedSize;
					name += SmeltingUtils.getSmeltingPercentage(tile, slot);
					if (folder.getItemDamage() == 2) {
						int storedRem = ItemFolder.getRemSize(folder);
						int maxDamage = ((ItemStack)ItemFolder.getObject(folder)).getMaxDamage();
						name = stackName + " - " + storedSize + " [" + storedRem + " / " + maxDamage + "]";
					}
					info.horizontal().text(name);
				}
				else if (ItemFolder.getObject(folder) instanceof FluidStack) {
					String fluidName = ((FluidStack)ItemFolder.getObject(folder)).getLocalizedName();

					long storedSize = ItemFolder.getFileSize(folder);
					String name = fluidName + " - " + storedSize + " mB";
					info.horizontal().text(name);
				}
				else if (ItemFolder.getObject(folder) instanceof String) {
					if (folder.getItemDamage() == 3) {
						String mobName = (String)ItemFolder.getObject(folder);
						if (!mobName.isEmpty()) {
							long storedSize = ItemFolder.getFileSize(folder);
							String name = mobName + " - " + storedSize;
							info.horizontal().text(name);
						}
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
				info.horizontal().text(TextFormatting.GRAY + upgrade + TextFormatting.GREEN + UpgradeHelper.getUpgrade(tile, tile.upgrades));
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
		
		public void addManaInfo(IProbeInfo info, TileManaCabinet tile) {
			
			double count = tile.getTotalInternalManaPool();
			double calc = 0.0;
			if (count > 0)
				calc = count / 1000000;
			NumberFormat percentFormatter = NumberFormat.getPercentInstance();
			String percentOut = percentFormatter.format(calc);
			String name = percentOut + " of a full mana pool";
			info.horizontal().text(name);
		}
		
		public void addCabinetInfo(IProbeInfo info, EntityCabinet cabinet) {
			
			info.horizontal().text("Currently carrying:");
			for (int i = 0; i < cabinet.getInventory().getSlots(); i++) {
				ItemStack folder = cabinet.getInventory().getStackInSlot(i);
				if (folder != null && folder.getItem() == RFCItems.folder) {
					if (ItemFolder.getObject(folder) != null && ItemFolder.getObject(folder) instanceof ItemStack) {
						String name = ((ItemStack)ItemFolder.getObject(folder)).getDisplayName();
						long storedSize = ItemFolder.getFileSize(folder);
						
						info.horizontal().text(name + " - " + storedSize);
					}
					if (ItemFolder.getObject(folder) != null && ItemFolder.getObject(folder) instanceof FluidStack) {
						String name = ((FluidStack)ItemFolder.getObject(folder)).getLocalizedName();
						long storedSize = ItemFolder.getFileSize(folder);
						
						info.horizontal().text(name + " - " + storedSize);
					}
					if (ItemFolder.getObject(folder) != null && ItemFolder.getObject(folder) instanceof String) {
						String mobName = (String)ItemFolder.getObject(folder);
						long storedSize = ItemFolder.getFileSize(folder);
						
						info.horizontal().text(mobName + " - " + storedSize);
					}
				}
			}
		}
		
		public void addMobUpgradeInfo(IProbeInfo info, EntityCabinet cabinet) {
			
			String upgrade = "Upgrade: ";
			if (!MobUpgradeHelper.hasMobUpgrade(cabinet))
				info.horizontal().text(TextFormatting.GRAY + upgrade + "NONE");
			else
				info.horizontal().text(TextFormatting.GRAY + upgrade + TextFormatting.GREEN + MobUpgradeHelper.getMobUpgrade(cabinet, cabinet.upgrades));
		}
	}
}
