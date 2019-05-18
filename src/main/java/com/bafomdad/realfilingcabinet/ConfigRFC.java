package com.bafomdad.realfilingcabinet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;

@Config(modid=RealFilingCabinet.MOD_ID)
public class ConfigRFC {

	@Comment({"Configure the folder storage limit for dyed folders."})
	public static int folderSizeLimit = 1000;
	@Comment({"If enabled, will let filing cabinets use a different texture depending on the season."})
	public static boolean seasonalCabinets = true;
	@Comment({"Disable this if you want WAILA / TheOneProbe to handle the overlay insted."})
	public static boolean magnifyingGlassGui = true;
	@Comment({"If enabled, will let Mob Folders with a villager in it spawn villagers with their professions randomized."})
	public static boolean randomVillager = false;
	@Comment({"If enabled, will let Fluid Folders place water in the nether."})
	public static boolean waterNether = false;
	@Comment({"If disabled, will not let folders pick up dropped items."})
	public static boolean pickupStuff = true;
	@Comment({"If enabled, A fluid cabinet containing at least 3000mb of water will never run out of water."})
	public static boolean infiniteWaterSource = true;
	@Comment({"Use this to blacklist certain mobs from being captured in the Mob Folder. Put the class names of the entities here."})
	public static String[] mobFolderBlacklist = new String[]{ "EntityCabinet" };
	
	@Config(modid=RealFilingCabinet.MOD_ID, category="recipes")
	public static class RecipeConfig {
		
		@Ignore
		public static HashMap<String, Boolean> boolean_stuff = new HashMap<String, Boolean>();
		
		@Comment({"Enable Crafting Upgrade recipe"})
		@Tap
		public static boolean craftingUpgrade = true;
		@Comment({"Enable Ender Upgrade recipe"})
		@Tap
		public static boolean enderUpgrade = true;
		@Comment({"Enable Oredict Upgrade recipe"})
		@Tap
		public static boolean oreDictUpgrade = true;
		@Comment({"Enable Mob Upgrade recipe"})
		@Tap
		public static boolean mobUpgrade = true;
		@Comment({"Enable Fluid Upgrade recipe"})
		@Tap
		public static boolean fluidUpgrade = true;
		@Comment({"Enable Life Upgrade recipe"})
		@Tap
		public static boolean lifeUpgrade = true;
		@Comment({"Enable Smelting Upgrade recipe"})
		@Tap
		public static boolean smeltingUpgrade = true;
	}
	
	@Config(modid=RealFilingCabinet.MOD_ID, category="integration")
	public static class CompatConfig {
		
		@Comment({"If enabled, will add mana cabinets and folders for Botania."})
		public static boolean botaniaIntegration = true;
		@Comment({"If enabled, will add cabinets and folders for Thaumcraft."})
		public static boolean tcIntegration = true;
	}
	
	public static void checkTappedValues(Class clazz) {
		
		for (Field f : clazz.getDeclaredFields()) {
			Tap tapped = f.getAnnotation(Tap.class);
			if (tapped != null) {
				try {
					Class c = tapped.mapClass();
					if (c != null) {
						Field tapField = c.getDeclaredField(tapped.mapName());
						if (tapField != null) {
							Map map = (Map)tapField.get(null);
							if (map != null)
								map.put(f.getName(), f.get(null));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (f.getType().getSuperclass() == Object.class)
				checkTappedValues(f.getType());
		}
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Tap {
		
		Class mapClass() default ConfigRFC.RecipeConfig.class;
		String mapName() default "boolean_stuff";
	}
}
