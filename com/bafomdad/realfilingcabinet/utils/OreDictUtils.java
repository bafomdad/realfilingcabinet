package com.bafomdad.realfilingcabinet.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictUtils {

	private static List<ItemStack> oreDictMatches;
	
	private static void reset() {
		
		oreDictMatches = null;
	}

	public static void recreateOreDictionary(ItemStack stack) {
		
		int[] oreIDs = OreDictionary.getOreIDs(stack);
		if (oreIDs.length == 0)
			oreDictMatches = null;
		else {
			oreDictMatches = new ArrayList<ItemStack>();
			for (int id : oreIDs) {
				String oreName = OreDictionary.getOreName(id);
				
				List<ItemStack> list = OreDictionary.getOres(oreName);
				for (int i = 0; i < list.size(); i++) {
//					if (list.get(i).getItemDamage() == OreDictionary.WILDCARD_VALUE) {
//						continue;
//					}
					oreDictMatches.add(list.get(i));
				}
			}
			if (oreDictMatches.size() == 0)
				oreDictMatches = null;
		}
	}
	
	public static boolean hasOreDict() {
		
		return oreDictMatches != null;
	}
	
	public static boolean areItemsEqual(ItemStack stack1, ItemStack stack2) {
		
		if (stack1.isEmpty()|| stack2.isEmpty())
			return false;
		
		if (!stack1.isItemEqual(stack2) || (stack1.isItemEqual(stack2) && stack1.getItemDamage() != stack2.getItemDamage()))
		{
			if (stack1.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
				return false;
			}
			
//			if (stack1.getItem() == stack2.getItem())
//				return false;
			
			int[] ids1 = OreDictionary.getOreIDs(stack1);
			int[] ids2 = OreDictionary.getOreIDs(stack2);
			if (ids1.length == 0 || ids2.length == 0)
				return false;
			
			boolean oreMatch = false;
			for (int id1 : ids1) {
				for (int id2 : ids2) {
					if (id1 != id2)
						continue;
					
					String name = OreDictionary.getOreName(id1);
					if (isEntryValid(name)) {
						oreMatch = true;
						break;
					}
				}
				if (oreMatch)
					break;
			}
			if (!oreMatch)
				return false;
		}
		return ItemStack.areItemStackTagsEqual(stack1, stack2);
	}
	
	private static boolean isEntryValid(String name) {
		
		if (OreDictRegistry.instance().getWhitelist().contains(name))
			return true;
		
		return true;
	}
	
	public static class OreDictRegistry {
		
		private static final OreDictRegistry INSTANCE = new OreDictRegistry();
		
		private Set<String> whitelist = new HashSet<String>();
		
		public void init() {
			
			for (String item : new String[] { "oreIron", "oreGold", "oreAluminum", "oreAluminium", "oreTin", "oreCopper", "oreLead", "oreSilver", "orePlatinum", "oreNickel" })
				addWhitelist(item);
			
	        for (String item : new String[] { "blockIron", "blockGold", "blockAluminum", "blockAluminium", "blockTin", "blockCopper", "blockLead", "blockSilver", "blockPlatinum", "blockNickel" })
	            addWhitelist(item);

	        for (String item : new String[] { "ingotIron", "ingotGold", "ingotAluminum", "ingotAluminium", "ingotTin", "ingotCopper", "ingotLead", "ingotSilver", "ingotPlatinum", "ingotNickel" })
	            addWhitelist(item);

	        for (String item : new String[] { "nuggetIron", "nuggetGold", "nuggetAluminum", "nuggetAluminium", "nuggetTin", "nuggetCopper", "nuggetLead", "nuggetSilver", "nuggetPlatinum", "nuggetNickel" })
	            addWhitelist(item);
		}
		
		public static OreDictRegistry instance() {
			
			return INSTANCE;
		}
		
		public Set getWhitelist() {
			
			return whitelist;
		}
		
		private boolean addWhitelist(String entry) {
			
			if (entry == null)
				return false;
			
			return whitelist.add(entry);
		}
	}
}
