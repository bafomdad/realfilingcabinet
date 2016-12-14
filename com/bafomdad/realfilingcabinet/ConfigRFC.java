package com.bafomdad.realfilingcabinet;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigRFC {

	// RECIPES
	public static boolean craftingUpgrade;
	public static boolean enderUpgrade;
	public static boolean oreDictUpgrade;
	public static boolean mobUpgrade;
	public static boolean fluidUpgrade;
	public static boolean lifeUpgrade;
	
	// MISC
	public static boolean magnifyingGlassGui;
	public static boolean randomVillager;
	public static boolean seasonalCabinets;
	
	// DEBUG
	public static boolean debugLogger;
	
	public static void loadconfig(FMLPreInitializationEvent event) {
		
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		craftingUpgrade = config.get("recipes", "enableCraftingUpgradeRecipe", true).getBoolean();
		enderUpgrade = config.get("recipes", "enableEnderUpgradeRecipe", true).getBoolean();
		oreDictUpgrade = config.get("recipes", "enableOreDictUpgradeRecipe", true).getBoolean();
		mobUpgrade = config.get("recipes", "enableMobUpgradeRecipe", true, "Disabling this also disables the mob folder recipe.").getBoolean();
		fluidUpgrade = config.get("recipes", "enableFluidUpgradeRecipe", true).getBoolean();
		lifeUpgrade = config.get("recipes", "enableLifeUpgradeRecipe", true).getBoolean();
		
		magnifyingGlassGui = config.get("misc", "enableMagnifyingGlassGUI", true, "Disable this if you want TheOneProbe to handle the overlay instead.").getBoolean();
		randomVillager = config.get("misc", "enableSpawnRandomVillager", false, "If enabled, will let mob folders with a villager in it spawn villagers with their professions randomized.").getBoolean();
		seasonalCabinets = config.get("misc", "enableSeasonalCabinets", true, "If enabled, the normal filing cabinets will use a different texture depending on the season").getBoolean();
		
		debugLogger = config.get("debug", "enableDebugLogger", false, "Will output stuff to console for debugging purposes").getBoolean();
		
		config.save();
	}
}
