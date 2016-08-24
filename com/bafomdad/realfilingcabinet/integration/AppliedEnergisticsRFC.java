package com.bafomdad.realfilingcabinet.integration;

import java.lang.reflect.Constructor;

import com.bafomdad.realfilingcabinet.integration.ae2.IStorageBusMonitorWrapper;
import com.bafomdad.realfilingcabinet.integration.ae2.RFCExternalStorageHandler;

import appeng.api.AEApi;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;

public class AppliedEnergisticsRFC {
	
	private static class ReflectionWrapper implements IStorageBusMonitorWrapper {

		private Class classInventoryAdaptor;
		private Class classMEAdaptor;
		private Class classMonitor;
		
		private Constructor constMEAdaptor;
		private Constructor constMonitor;
		
		public boolean init() {
			try {
				classInventoryAdaptor = Class.forName("appeng.util.InventoryAdaptor");
				classMEAdaptor = Class.forName("appeng.util.inv.IMEAdaptor");
				classMonitor = Class.forName("appeng.me.storage.MEMonitorIInventory");
				
				constMEAdaptor = classMEAdaptor.getConstructor(IMEInventory.class, BaseActionSource.class);
				constMonitor = classMonitor.getConstructor(classInventoryAdaptor);
				
				return true;
			}
			catch (Throwable t) {
				return false;
			}
		}
		@Override
		public IMEMonitor<IAEItemStack> createStorageBusMonitor(IMEInventory<IAEItemStack> inventory, BaseActionSource src) {

			try {
				Object adaptor = constMEAdaptor.newInstance(inventory, src);
				Object monitor = constMonitor.newInstance(adaptor);
				
				return (IMEMonitor<IAEItemStack>) monitor;
			}
			catch (Throwable t) {
				return null;
			}
		}
	}
	
	private static IStorageBusMonitorWrapper wrapper;
	private static boolean isModLoaded = false;
	
	public static void register() throws Throwable {
		
		ReflectionWrapper rwrapper = new ReflectionWrapper();
		if (!rwrapper.init())
			throw new Exception("No valid Storage Bus Monitor wrapper");
		else
			isModLoaded = true;
		
		wrapper = rwrapper;
	}
	
	public static void postInit() {
		
		if (isModLoaded)
		{
			AEApi.instance().registries().externalStorage().addExternalStorageInterface(new RFCExternalStorageHandler(wrapper));
		}
	}
}
