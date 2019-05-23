package com.bafomdad.realfilingcabinet.helpers;

import java.util.LinkedHashSet;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import com.bafomdad.realfilingcabinet.api.IUpgrade;
import com.bafomdad.realfilingcabinet.api.RealFilingCabinetAPI;
import com.bafomdad.realfilingcabinet.api.upgrades.MobUpgrades;
import com.bafomdad.realfilingcabinet.entity.EntityCabinet;

public class MobUpgradeHelper {

	private static final LinkedHashSet<MobUpgrades> MOBUPGRADES = RealFilingCabinetAPI.MOBUPGRADES;
	
	public static boolean hasUpgrade(EntityCabinet entity) {
		
		if (entity == null) return false;
		return !entity.upgrades.isEmpty();
	}
	
	public static MobUpgrades getUpgrade(EntityCabinet entity, String tag) {
		
		if (!hasUpgrade(entity)) return MobUpgrades.EMPTY;
		
		MobUpgrades upgrade = getUpgrade(entity);
		return (!upgrade.isEmpty() && upgrade.getTag().equals(tag)) ? upgrade : MobUpgrades.EMPTY;
	}
	
	public static MobUpgrades getUpgrade(EntityCabinet entity) {
		
		Optional<MobUpgrades> opt = MOBUPGRADES.stream().filter(u -> u.getTag().equals(entity.upgrades)).findAny();
		return (opt.isPresent()) ? opt.get() : MobUpgrades.EMPTY;
	}
	
	public static void setMobUpgrade(EntityPlayer player, EntityCabinet entity, ItemStack upgrade) {
		
		if (entity.world.isRemote || !(upgrade.getItem() instanceof IUpgrade)) return;
		
		if (hasUpgrade(entity)) return;
		
		MOBUPGRADES.stream().filter(u -> ItemStack.areItemsEqual(upgrade, u.getUpgrade())).findAny().ifPresent(
				u -> {
					entity.upgrades = u.getTag();
					if (!player.capabilities.isCreativeMode)
						upgrade.shrink(1);
					entity.setTexture(u.getTexture().toString());
					entity.setModel(u.getModelPath());
					return;
				});
	}
	
	public static void removeMobUpgrade(EntityPlayer player, EntityCabinet entity) {
		
		if (!hasUpgrade(entity)) return;
		
		MOBUPGRADES.stream().filter(u -> u.getTag().equals(entity.upgrades)).findAny().ifPresent(
				u -> {
					ItemHandlerHelper.giveItemToPlayer(player, u.getUpgrade().copy());
					entity.upgrades = "";
					entity.setTexture("");
					entity.setModel("");
					return;
				});
	}
}
