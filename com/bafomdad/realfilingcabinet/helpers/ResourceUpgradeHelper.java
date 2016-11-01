package com.bafomdad.realfilingcabinet.helpers;

import java.util.HashMap;
import java.util.Map;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class ResourceUpgradeHelper {

	private static final Map<ResourceLocation, String> upgradeTextures = new HashMap<ResourceLocation, String>();
	private static final ResourceLocation DEFAULT = new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/filingcabinet.png");
	
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
	
	public static ResourceLocation getTexture(TileEntity tile, String tag) {
		
		if (tag != null)
		{
			for (Map.Entry<ResourceLocation, String> entry : upgradeTextures.entrySet())
			{
				String value = entry.getValue();
				if (value.equals(tag))
					return entry.getKey();
			}
		}
		return DEFAULT;
	}
	
	public static ResourceLocation getDefault() {
		
		return DEFAULT;
	}
}
