package com.bafomdad.realfilingcabinet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class NewConfigRFC {

	public static HashMap<String, Boolean> boolean_stuff = new HashMap<String, Boolean>();
	
	@Config(modid=RealFilingCabinet.MOD_ID)
	public static class ConfigRFC {
		
		// RECIPES
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
		
		// MISC
		@Comment({"Disable this if you want TheOneProbe to handle the overlay instead."})
		@Tap
		public static boolean magnifyingGlassGui = true;
		@Comment({"If enabled, will let Mob Folders with a villager in it spawn villagers with their professions randomized."})
		@Tap
		public static boolean randomVillager = false;
		@Comment({"If enabled, will let filing cabinets use a different texture depending on the season."})
		@Tap
		public static boolean seasonalCabinets = true;
		@Comment({"If enabled, will let Fluid Folders place water in the nether."})
		@Tap
		public static boolean waterNether = false;
		@Comment({"If disabled, will not let folders pick up dropped items."})
		@Tap
		public static boolean pickupStuff = true;
		@Comment({"Will output stuff to console for debugging purposes."})
		@Tap
		public static boolean debugLogger = false;
		@Comment({"Use this to blacklist certain mobs from being captured in the Mob Folder. Put the class names of the entities here."})
		public static String[] mobFolderBlacklist = new String[]{};
		@Comment({"If enabled, A fluid cabinet filled with more than 3000mb of water will never run out of water."})
		@Tap
		public static boolean infiniteWaterSource = true;
		
		// INTEGRATION
		@Comment({"If enabled, will add mana cabinets and folders for Botania"})
		@Tap
		public static boolean botaniaIntegration = true;
		@Comment({"If enabled, will add a folder and a cabinet when it detects that Thaumcraft is installed."})
		@Tap
		public static boolean tcIntegration = true;
	}
	
//	static Configuration config;
	public static void preInit(FMLPreInitializationEvent event) {
		
		checkTappedValues(ConfigRFC.class);
	}
	
	public static void checkTappedValues(Class clazz) {
		
		for (Field f : clazz.getDeclaredFields()) {
			Tap tapped = f.getAnnotation(Tap.class);
			if (tapped != null) {
				try 
				{
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
			}
			else if (f.getType().getSuperclass() == Object.class)
				checkTappedValues(f.getType());
		}
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Tap {
		
		Class mapClass() default NewConfigRFC.class;
		String mapName() default "boolean_stuff";
	}
}
