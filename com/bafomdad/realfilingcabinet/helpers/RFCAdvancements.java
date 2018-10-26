package com.bafomdad.realfilingcabinet.helpers;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class RFCAdvancements {
	
	public static void advance(EntityPlayerMP player, ResourceLocation id, String condition) {
		
		PlayerAdvancements advancements = player.getAdvancements();
		AdvancementManager manager = player.getServerWorld().getAdvancementManager();
		Advancement advancement = manager.getAdvancement(id);
		if (advancement != null)
			advancements.grantCriterion(advancement, condition);
	}
}
