package com.bafomdad.realfilingcabinet.api.upgrades;

import com.bafomdad.realfilingcabinet.api.ITileCabinet;
import com.bafomdad.realfilingcabinet.api.IUpgrade;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class Upgrades {
	
	public static final Upgrades EMPTY = new Upgrades(ItemStack.EMPTY, null, "");
	
	final ItemStack stack;
	final ResourceLocation res;
	final String tag;

	public Upgrades(ItemStack upgradeStack, ResourceLocation upgradeTexture, String upgradeTag) {
		
		this.stack = upgradeStack;
		this.res = upgradeTexture;
		this.tag = upgradeTag;
	}
	
	public ItemStack getUpgrade() {
		
		return stack;
	}
	
	public ResourceLocation getTexture() {
		
		return res;
	}
	
	public String getTag() {
		
		return tag;
	}
	
	public boolean isEmpty() {
		
		return this == EMPTY;
	}
	
	@Override
	public String toString() {
		
		return "[Upgrade]: " + getUpgrade() + " / " + getTexture() + " / " + getTag();
	}
}
