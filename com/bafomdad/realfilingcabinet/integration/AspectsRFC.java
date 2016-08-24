package com.bafomdad.realfilingcabinet.integration;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AspectsRFC {

	public static void register() {
		
		registerBlockMeta(RealFilingCabinet.blockRFC, new int[] { 0 }, new Object[] { Aspect.METAL, Integer.valueOf(4), Aspect.ORDER, Integer.valueOf(4), Aspect.VOID, Integer.valueOf(4) });
		registerItemMeta(RealFilingCabinet.itemEmptyFolder, new int[] { 0 }, new Object[] { Aspect.VOID, Integer.valueOf(4), Aspect.MAGIC, Integer.valueOf(4) });
		registerItemMeta(RealFilingCabinet.itemUpgrades, new int[] { 1 }, new Object[] { Aspect.CRAFT, Integer.valueOf(2) });
		registerItemMeta(RealFilingCabinet.itemUpgrades, new int[] { 2 }, new Object[] { Aspect.ELDRITCH, Integer.valueOf(2), Aspect.TRAVEL, Integer.valueOf(2) });
		registerItemMeta(RealFilingCabinet.itemUpgrades, new int[] { 3 }, new Object[] { Aspect.MIND, Integer.valueOf(2) });
		registerItemMeta(RealFilingCabinet.itemFolder, new int[] { 0 }, new Object[] { Aspect.CLOTH, Integer.valueOf(2), Aspect.MAGIC, Integer.valueOf(4) });
		registerItemMeta(RealFilingCabinet.itemFolder, new int[] { 1 }, new Object[] { Aspect.MAGIC, Integer.valueOf(4), Aspect.ELDRITCH, Integer.valueOf(4) });
		registerItemMeta(RealFilingCabinet.itemWhiteoutTape, new int[] { 0 }, new Object[] { Aspect.SLIME, Integer.valueOf(3) });
		registerItemMeta(RealFilingCabinet.itemMagnifyingGlass, new int[] { 0 }, new Object[] { Aspect.MIND, Integer.valueOf(2), Aspect.CRYSTAL, Integer.valueOf(2) });
	}
	
	public static void registerBlockMeta(Block block, int[] meta, Object... aspects) {
		
		if (block != null)
			ThaumcraftApi.registerObjectTag(new ItemStack(block), meta, genList(aspects));
	}
	
	public static void registerItemMeta(Item item, int[] meta, Object... aspects) {
		
		if (item != null)
			ThaumcraftApi.registerObjectTag(new ItemStack(item), meta, genList(aspects));
	}
	
	public static AspectList genList(Object[] aspects) {
		
		AspectList list = new AspectList();
		Aspect curAspect = null;
		for (int i = 0; i < aspects.length; i++) {
			if ((curAspect == null) && (aspects[i] instanceof Aspect))
			{
				curAspect = (Aspect)aspects[i];
			}
			else if ((curAspect != null) && (aspects[i] instanceof Integer))
			{
				list.add(curAspect, ((Integer)aspects[i]).intValue());
				curAspect = null;
			}
		}
		return list;
	}
}
