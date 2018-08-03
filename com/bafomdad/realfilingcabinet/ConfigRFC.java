package com.bafomdad.realfilingcabinet;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

// This class is now unused, and therefore marked for removing later
@Deprecated
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
	public static boolean waterNether;
	public static boolean pickupStuff;
	public static List<String> mobFolderBlacklist;
	
	// DEBUG
	public static boolean debugLogger;
	
	// INTEGRATION
	public static boolean botaniaIntegration;
	
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
		waterNether = config.get("misc", "enableNetherWater", false, "If enabled, will let fluid folders containing water to place water in the nether.").getBoolean();
		pickupStuff = config.get("misc", "canFoldersPickupItems", true, "If disabled, will not let folders pick up items dropped on the ground.").getBoolean();
		mobFolderBlacklist = Arrays.asList(config.getStringList("mobFolderBlacklist", "misc", new String[] {}, "Use this to blacklist certain mobs from being captured in the Mob Folders. Put the class name of the entities here."));
		
		debugLogger = config.get("debug", "enableDebugLogger", false, "Will output stuff to console for debugging purposes").getBoolean();
		
		botaniaIntegration = config.get("integration", "enableBotaniaIntegration", false, "Will add a folder and new cabinet when it detects that Botania is installed.").getBoolean();
		
		config.save();
	}
}
