package com.bafomdad.realfilingcabinet.api;

import com.bafomdad.realfilingcabinet.api.helper.ResourceUpgradeHelper;
import com.bafomdad.realfilingcabinet.api.helper.UpgradeHelper;

public class RFCApi {

	private RFCApi() {
		
	}
	
	private static final UpgradeHelper upgradeRegistry = new UpgradeHelper();
	private static final ResourceUpgradeHelper resourceRegistry = new ResourceUpgradeHelper();
	
	public static final String VERSION = "1.10.2 - 0.1";
	public static final String MOD_ID = "realfilingcabinet";
	public static final String MOD_NAME = "RFCAPI";
	
	public static UpgradeHelper upgradeRegistry() {
		
		return upgradeRegistry;
	}
	
	public static ResourceUpgradeHelper resourceRegistry() {
		
		return resourceRegistry;
	}
}
