package com.bafomdad.realfilingcabinet;

public class LogRFC {

	public static void debug(String msg) {
		
		RealFilingCabinet.LOGGER.debug(msg);
	}
	
	public static void info(String msg) {
		
		RealFilingCabinet.LOGGER.info(msg);
	}
	
	public static void error(String msg) {
		
		RealFilingCabinet.LOGGER.error(msg);
	}
}
