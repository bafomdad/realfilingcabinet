package com.bafomdad.realfilingcabinet;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigRFC {

	// RECIPES
	public static boolean craftingUpgrade;
	public static boolean enderUpgrade;
	public static boolean oreDictUpgrade;
	
	// MISC
	public static boolean magnifyingGlassGui;
	
	public static void loadconfig(FMLPreInitializationEvent event) {
		
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		craftingUpgrade = config.get("recipes", "enableCraftingUpgradeRecipe", true).getBoolean();
		enderUpgrade = config.get("recipes", "enableEnderUpgradeRecipe", true).getBoolean();
		oreDictUpgrade = config.get("recipes", "enableOreDictUpgradeRecipe", true).getBoolean();
		
		magnifyingGlassGui = config.get("misc", "enableMagnifyingGlassGUI", true, "Disable this if you want WAILA to handle the overlay instead.").getBoolean();
		
		config.save();
	}
}
