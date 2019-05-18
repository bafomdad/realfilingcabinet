package com.bafomdad.realfilingcabinet.api.upgrades;

import scala.reflect.internal.Trees.Super;
import net.minecraft.client.model.ModelBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class MobUpgrades {

	public static final MobUpgrades EMPTY = new MobUpgrades(ItemStack.EMPTY, null, null, "");
	
	final ItemStack stack;
	final String model;
	final ResourceLocation texture;
	final String tag;
	
	public MobUpgrades(ItemStack upgradeStack, String modelClassName, ResourceLocation texture, String upgradeTag) {
		
		this.stack = upgradeStack;
		this.model = modelClassName;
		this.texture = texture;
		this.tag = upgradeTag;
	}
	
	public ItemStack getUpgrade() {
		
		return stack;
	}
	
	public ResourceLocation getTexture() {
		
		return texture;
	}
	
	public String getTag() {
		
		return tag;
	}
	
	public boolean isEmpty() {
		
		return this == EMPTY;
	}
	
	public String getModelPath() {
		
		return model;
	}
	
	@Override
	public String toString() {
		
		return "[MobUpgrade]: " + getUpgrade() + " / " + model + " / " + getTag();
	}
}
