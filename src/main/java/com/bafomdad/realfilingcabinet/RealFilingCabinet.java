package com.bafomdad.realfilingcabinet;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import org.apache.logging.log4j.Logger;

import com.bafomdad.realfilingcabinet.commands.CommandsRFC;
import com.bafomdad.realfilingcabinet.gui.GuiHandlerRFC;
import com.bafomdad.realfilingcabinet.init.RFCDataFixer;
import com.bafomdad.realfilingcabinet.init.RFCIntegration;
import com.bafomdad.realfilingcabinet.integration.*;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityProviderFolder;
import com.bafomdad.realfilingcabinet.proxies.CommonProxy;

@Mod(modid=RealFilingCabinet.MOD_ID, name=RealFilingCabinet.MOD_NAME, version=RealFilingCabinet.VERSION)
public class RealFilingCabinet {

	public static final String MOD_ID = "realfilingcabinet";
	public static final String MOD_NAME = "Real Filing Cabinet";
	public static final String VERSION = "@VERSION@";
	
	@SidedProxy(clientSide="com.bafomdad.realfilingcabinet.proxies.ClientProxy", serverSide="com.bafomdad.realfilingcabinet.proxies.CommonProxy")
	public static CommonProxy proxy;
	
	@Mod.Instance(MOD_ID)
	public static RealFilingCabinet instance;
	
	public static Logger LOGGER;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		LOGGER = event.getModLog();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandlerRFC());
		ConfigRFC.checkTappedValues(ConfigRFC.RecipeConfig.class);
		CapabilityProviderFolder.register();
		proxy.preInit(event);
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {

		RFCIntegration.canLoad(RFCIntegration.WAILA).ifPresent(w -> w.register());
		RFCIntegration.canLoad(RFCIntegration.TOP).ifPresent(t -> t.register());
		RFCDataFixer.init();
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
		proxy.postInit(event);
	}
	
	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		
		event.registerServerCommand(new CommandsRFC());
	}
}
