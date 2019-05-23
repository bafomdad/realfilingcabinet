package com.bafomdad.realfilingcabinet.utils;

public class TestUtils {

	// test stuff here
	
	public static void main(String[] args) {
		
		String test1 = "minecraft:diamond";
		String test2 = "minecraft:cobble:0";
		
		System.out.println(test1.split(":")[1]);
		String[] split = test2.split(":");
		if (split.length > 2)
			System.out.println(split[2]);
	}
}
