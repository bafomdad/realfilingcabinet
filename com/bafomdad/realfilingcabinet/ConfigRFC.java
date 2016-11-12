package com.bafomdad.realfilingcabinet;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigRFC {
	
	// BLOCKS
	public static boolean binBlock;

	// RECIPES
	public static boolean craftingUpgrade;
	public static boolean enderUpgrade;
	public static boolean oreDictUpgrade;
	public static boolean mobUpgrade;
	public static boolean nametagRecipe;
	
	public static String binRecipe = "item.diamond/9/tile.blockDiamond";
	
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
		mobUpgrade = config.get("recipes", "enableMobUpgradeRecipe", true, "Disabling this also disables the mob folder recipe.").getBoolean();
		
		nametagRecipe = config.get("recipes", "enableNametagRecipe", true).getBoolean();
		binRecipe = config.getString("recipes", "garbageBinRecipes", binRecipe, "Add custom recipes here where you can dump the contents of a folder in a garbage bin to get something back. PARAMS: input/quantity of input/output and separate recipes with commas.");
		
		magnifyingGlassGui = config.get("misc", "enableMagnifyingGlassGUI", true, "Disable this if you want WAILA to handle the overlay instead.").getBoolean();
		binBlock = config.get("block", "enableGarbageBin", true, "Disable this to remove the block from the mod. This also disables the recipe.").getBoolean();
		
		debugLogger = config.get("debug", "enableDebugLogger", false, "Will output stuff to console for debugging purposes").getBoolean();
		
		config.save();
	}
}
