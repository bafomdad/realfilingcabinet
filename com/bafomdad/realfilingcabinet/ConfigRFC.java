package com.bafomdad.realfilingcabinet;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigRFC {

	// RECIPES
	public static boolean craftingUpgrade;
	public static boolean enderUpgrade;
	public static boolean oreDictUpgrade;
	public static boolean nametagRecipe;
	
	// MISC
	public static boolean magnifyingGlassGui;
	
	// DEBUG
	public static boolean debugLogger;
	
	public static void loadconfig(FMLPreInitializationEvent event) {
		
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		craftingUpgrade = config.get("recipes", "enableCraftingUpgradeRecipe", true).getBoolean();
		enderUpgrade = config.get("recipes", "enableEnderUpgradeRecipe", true).getBoolean();
		oreDictUpgrade = config.get("recipes", "enableOreDictUpgradeRecipe", true).getBoolean();
		nametagRecipe = config.get("recipes", "enableNametagRecipe", true).getBoolean();
		
		magnifyingGlassGui = config.get("misc", "enableMagnifyingGlassGUI", true, "Disable this if you want WAILA/TheOneProbe to handle the overlay instead.").getBoolean();
		
		debugLogger = config.get("debug", "enableDebugLogger", false, "Will output stuff to console for debugging purposes").getBoolean();
		
		config.save();
	}
}
