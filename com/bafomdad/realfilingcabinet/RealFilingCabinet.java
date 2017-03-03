package com.bafomdad.realfilingcabinet;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.Logger;

import com.bafomdad.realfilingcabinet.events.EventHandlerServer;
import com.bafomdad.realfilingcabinet.proxies.CommonProxy;

@Mod(modid=RealFilingCabinet.MOD_ID, name=RealFilingCabinet.MOD_NAME, version=RealFilingCabinet.VERSION, dependencies="required-after:forge@[13.19.1.2188,)")
public class RealFilingCabinet {

	public static final String MOD_ID = "realfilingcabinet";
	public static final String MOD_NAME = "Real Filing Cabinet";
	public static final String VERSION = "1.2.21";
	
	@SidedProxy(clientSide="com.bafomdad.realfilingcabinet.proxies.ClientProxy", serverSide="com.bafomdad.realfilingcabinet.proxies.CommonProxy")
	public static CommonProxy proxy;
	
	@Mod.Instance(MOD_ID)
	public static RealFilingCabinet instance;
	
	public static ConfigRFC config;
	public static Logger logger;
	
	public static boolean topLoaded = Loader.isModLoaded("theoneprobe");
	public static boolean enderioLoaded = Loader.isModLoaded("enderio");
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		config = new ConfigRFC();
		config.loadconfig(event);
		logger = event.getModLog();
		
		proxy.preInit(event);
		proxy.initAllModels();
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {

		proxy.init(event);
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
		proxy.postInit(event);
		MinecraftForge.EVENT_BUS.register(new EventHandlerServer());
	}
}
