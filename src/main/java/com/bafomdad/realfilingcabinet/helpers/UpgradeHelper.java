package com.bafomdad.realfilingcabinet.helpers;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.LinkedHashSet;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemHandlerHelper;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IUpgrade;
import com.bafomdad.realfilingcabinet.api.RealFilingCabinetAPI;
import com.bafomdad.realfilingcabinet.api.upgrades.Upgrades;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.enums.UpgradeType;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.utils.EnderUtils;

public class UpgradeHelper {

	private static final LinkedHashSet<Upgrades> UPGRADES = RealFilingCabinetAPI.UPGRADES;
	
	private static final ResourceLocation DEFAULT = new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/filingcabinet.png");
	private static final ResourceLocation HALLOWEEN = new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/pumpkincabinet.png");
	private static final ResourceLocation CHRISTMAS = new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/candycanecabinet.png");
	
	/**
	 * Conditional checking for whether the tile already has an upgrade installed, excluding creative upgrade
	 * @param tile
	 * @return
	 */
	public static boolean hasUpgrade(TileFilingCabinet tile) {
		
		if (tile == null) return false;
		return !tile.upgrade.isEmpty();
	}
	
	/**
	 * Conditional checking for whether the tile has an creative upgrade installed
	 * @param tile
	 * @return
	 */
	public static boolean isCreative(TileEntityRFC tile) {
		
		return tile.isCreative;
	}
	
	/**
	 * Gets the upgrade, from the string tag it is registered with. Returns EMPTY if none is found
	 * @param tile
	 * @return Upgrades
	 */
	public static Upgrades getUpgrade(TileFilingCabinet tile, String tag) {
		
		if (!hasUpgrade(tile)) return Upgrades.EMPTY;
		
		Upgrades upgrade = getUpgrade(tile);
		return (!upgrade.isEmpty() && upgrade.getTag().equals(tag)) ? upgrade : Upgrades.EMPTY;
	}
	
	/**
	 * Gets any upgrade found. Returns EMPTY if none is found
	 * @param tile
	 * @return Upgrades
	 */
	public static Upgrades getUpgrade(TileFilingCabinet tile) {
		
		Optional<Upgrades> opt = UPGRADES.stream().filter(u -> u.getTag().equals(tile.upgrade)).findAny();
		return (opt.isPresent()) ? opt.get() : Upgrades.EMPTY;
	}
	
	/**
	 * Sets the upgrade into the tile, if it allows for it, including creative upgrade
	 * @param player
	 * @param tile
	 * @param upgrade
	 */
	public static void setUpgrade(EntityPlayer player, TileFilingCabinet tile, ItemStack upgrade) {
		
		if (tile.getWorld().isRemote || !(upgrade.getItem() instanceof IUpgrade)) return;
		
		if (!((IUpgrade)upgrade.getItem()).canApply(upgrade, player)) return;
		
		UPGRADES.stream().filter(u -> ItemStack.areItemsEqual(upgrade, u.getUpgrade())).findAny().ifPresent(
				u -> {
					if (u.getTag().equals(StringLibs.TAG_CREATIVE)) {
						if (isCreative(tile)) return;
						
						tile.isCreative = true;
						if (!player.capabilities.isCreativeMode)
							upgrade.shrink(1);
						tile.markDirty();
						return;
					}
					if (hasUpgrade(tile)) return;
					
					tile.upgrade = u.getTag();
					if (!player.capabilities.isCreativeMode)
						upgrade.shrink(1);
					if (upgrade.getItem() == RFCItems.UPGRADE && upgrade.getItemDamage() == UpgradeType.ENDER.ordinal())
						tile.setHash();
					tile.markDirty();
					return;
				});
	}
	
	/**
	 * Removes an upgrade from the tile, including creative upgrade
	 * @param player
	 * @param tile
	 */
	public static void removeUpgrade(EntityPlayer player, TileFilingCabinet tile) {
		
		if ((!hasUpgrade(tile)) && !isCreative(tile)) return;
		
		UPGRADES.stream().filter(u -> tile.isCreative || u.getTag().equals(tile.upgrade)).findAny().ifPresent(
				u -> {
					if (tile.isCreative) {
						tile.isCreative = false;
						ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(RFCItems.UPGRADE, 1, 0));
						tile.markDirty();
						return;
					}
					ItemHandlerHelper.giveItemToPlayer(player, u.getUpgrade().copy());
					tile.upgrade = "";
					tile.markDirty();
					return;
				});
	}
	
	public static ResourceLocation getTexture(TileFilingCabinet tile) {
		
		Optional<Upgrades> opt = UPGRADES.stream().filter(u -> u.getTag().equals(tile.upgrade)).findAny();
		return (opt.isPresent() && opt.get().getTexture() != null) ? opt.get().getTexture() : getDefault();
	}
	
	private static ResourceLocation getDefault() {
		
		if (ConfigRFC.seasonalCabinets) {
			LocalDateTime current = LocalDateTime.now();
			if (current.getMonth() == Month.OCTOBER)
				return HALLOWEEN;
			if (current.getMonth() == Month.DECEMBER)
				return CHRISTMAS;
		}
		return DEFAULT;
	}
}
