package com.bafomdad.realfilingcabinet.helpers;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

import com.bafomdad.realfilingcabinet.NewConfigRFC.ConfigRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.entity.EntityCabinet;

public class ResourceUpgradeHelper {

	private static final Map<ResourceLocation, String> upgradeTextures = new HashMap<ResourceLocation, String>();
	private static final Map<ResourceLocation, String> upgradeMobTextures = new HashMap<ResourceLocation, String>();
	
	private static final ResourceLocation MOB_DEFAULT = new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/entity/cabinettexture.png");
	private static final ResourceLocation DEFAULT = new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/filingcabinet.png");
	private static final ResourceLocation HALLOWEEN = new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/pumpkincabinet.png");
	private static final ResourceLocation CHRISTMAS = new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/candycanecabinet.png");
	
	public static final ResourceLocation SMELTING = new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/litfurnacecabinet.png");
	
	/**
	 * Put this in preinit phase before you've registered all your items/blocks. Registers your custom model texture for filing cabinet, along with the NBT tag you've mapped it to.
	 * Of course, if you don't use this method, it'll just use the default texture look when your upgrade has been applied to it.
	 * @param resourcelocation
	 * @param string
	 */
	public static void registerUpgradeResource(ResourceLocation resource, String str) {
		
		if (resource != null && !str.isEmpty())
			upgradeTextures.put(resource, str);
		else throw new IllegalArgumentException("[RealFilingCabinet]: Register upgrade resource: ResourceLocation is null, or the string tag is empty");
	}
	
	/**
	 * Entity version of resource registration for blocks.
	 * @param resource
	 * @param str
	 */
	public static void registerMobUpgradeResource(ResourceLocation resource, String str) {
		
		if (resource != null && !str.isEmpty())
			upgradeMobTextures.put(resource, str);
		else throw new IllegalArgumentException("[RealFilingCabinet]: Register upgrade mob resource: ResourceLocation is null, or the string tag is empty");
	}
	
	public static ResourceLocation getTexture(TileEntityRFC tile, String tag) {
		
		if (tag != null) {
			for (Map.Entry<ResourceLocation, String> entry : upgradeTextures.entrySet()) {
				String value = entry.getValue();
				if (value.equals(tag))
					return entry.getKey();
			}
		}
		return DEFAULT;
	}
	
	public static ResourceLocation getMobTexture(EntityCabinet cabinet, String tag) {
		
		if (tag != null) {
			for (Map.Entry<ResourceLocation, String> entry : upgradeMobTextures.entrySet()) {
				String value = entry.getValue();
				if (value.equals(tag))
					return entry.getKey();
			}
		}
		return MOB_DEFAULT;
	}
	
	public static ResourceLocation getDefault() {
		
		if (ConfigRFC.seasonalCabinets) {
			LocalDateTime current = LocalDateTime.now();
			if (current.getMonth() == Month.OCTOBER)
				return HALLOWEEN;
			if (current.getMonth() == Month.DECEMBER)
				return CHRISTMAS;
		}
		return DEFAULT;
	}
	
	public static ResourceLocation getMobDefault() {
		
		return MOB_DEFAULT;
	}
}
