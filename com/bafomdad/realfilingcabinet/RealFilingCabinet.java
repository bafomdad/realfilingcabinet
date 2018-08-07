package com.bafomdad.realfilingcabinet;

import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityProviderFolder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import org.apache.logging.log4j.Logger;

import com.bafomdad.realfilingcabinet.commands.CommandRFC;
import com.bafomdad.realfilingcabinet.events.EventHandlerServer;
import com.bafomdad.realfilingcabinet.gui.GuiHandlerRFC;
import com.bafomdad.realfilingcabinet.proxies.CommonProxy;

@Mod(modid=RealFilingCabinet.MOD_ID, name=RealFilingCabinet.MOD_NAME, version=RealFilingCabinet.VERSION, dependencies = "after:forge@[" + RealFilingCabinet.FORGE_VER + ",);")
public class RealFilingCabinet {

	public static final String MOD_ID = "realfilingcabinet";
	public static final String MOD_NAME = "Real Filing Cabinet";
	public static final String VERSION = "@VERSION@";
	public static final String FORGE_VER = "14.21.0.2363";
	
	public static final String STORAGEDRAWERS = "storageDrawers";
	
	@SidedProxy(clientSide="com.bafomdad.realfilingcabinet.proxies.ClientProxy", serverSide="com.bafomdad.realfilingcabinet.proxies.CommonProxy")
	public static CommonProxy proxy;
	
	@Mod.Instance(MOD_ID)
	public static RealFilingCabinet instance;
	
	public static Logger logger;
	
	public static boolean botaniaLoaded = Loader.isModLoaded("botania");
	public static boolean topLoaded = Loader.isModLoaded("theoneprobe");
	public static boolean wailaLoaded = Loader.isModLoaded("waila");
	public static boolean tcLoaded = Loader.isModLoaded("thaumcraft");
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandlerRFC());
		NewConfigRFC.preInit(event);
		logger = event.getModLog();
		
		proxy.preInit(event);
		proxy.initAllModels();
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {

		proxy.init(event);
		proxy.registerColors();
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
		proxy.postInit(event);
		MinecraftForge.EVENT_BUS.register(new EventHandlerServer());
		CapabilityProviderFolder.register();
	}
	
	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		
		event.registerServerCommand(new CommandRFC());
	}
}
