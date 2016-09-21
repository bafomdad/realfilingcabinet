package com.bafomdad.realfilingcabinet;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.Logger;

import com.bafomdad.realfilingcabinet.events.EventHandlerServer;
import com.bafomdad.realfilingcabinet.proxies.CommonProxy;
import com.bafomdad.realfilingcabinet.world.RFCWorldInfo;

@Mod(modid=RealFilingCabinet.MOD_ID, name=RealFilingCabinet.MOD_NAME, version=RealFilingCabinet.VERSION, dependencies="after:waila")
public class RealFilingCabinet {

	public static final String MOD_ID = "realfilingcabinet";
	public static final String MOD_NAME = "Real Filing Cabinet";
	public static final String VERSION = "0.4.8";
	
	@SidedProxy(clientSide="com.bafomdad.realfilingcabinet.proxies.ClientProxy", serverSide="com.bafomdad.realfilingcabinet.proxies.ServerProxy")
	public static CommonProxy proxy;
	
	@Mod.Instance(MOD_ID)
	public static RealFilingCabinet instance;
	
	public static ConfigRFC config;
	public static Logger logger;
	
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
	
//	@Mod.EventHandler
//	public void serverStarted(FMLServerStartedEvent event) {
//		
//		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
//		{
//			World world = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld();
//			if (!world.isRemote)
//			{
//				LogRFC.info("WorldData loading");
//				RFCWorldInfo saveData = (RFCWorldInfo)world.loadItemData(RFCWorldInfo.class, RFCWorldInfo.ID);
//				if (saveData == null)
//				{
//					LogRFC.info("WorldData not found");
//					saveData = new RFCWorldInfo(RFCWorldInfo.ID);
//					world.setItemData(RFCWorldInfo.ID, saveData);
//				}
//				else
//					LogRFC.info("WorldData retrieved");
//			}
//		}
//	}
}
