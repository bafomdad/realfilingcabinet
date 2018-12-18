package com.bafomdad.realfilingcabinet;

import com.bafomdad.realfilingcabinet.blocks.entity.FilingCabinetEntity;
import com.bafomdad.realfilingcabinet.init.*;
import com.bafomdad.realfilingcabinet.utils.StorageUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.events.PlayerInteractionEvent;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;

import net.minecraft.util.ActionResult;

public class RealFilingCabinet implements ModInitializer {

	public static final String MODID = "realfilingcabinet";

	@Override
	public void onInitialize() {

		RFCBlocks.init();
		RFCEntities.init();
		RFCItems.init();
		RFCRecipes.init();
		PlayerInteractionEvent.INTERACT_BLOCK.register((player, world, hand, pos, facing, hitX, hitY, hitZ) -> {

			ItemStack stack = player.getMainHandStack();
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof FilingCabinetEntity && player.isSneaking() && stack.isEmpty()) {
				FilingCabinetEntity fe = (FilingCabinetEntity)be;
				if (fe.isOpen) {
					StorageUtils.folderExtract(fe, player);
					return ActionResult.SUCCESS;
				}
			}
			return ActionResult.PASS;
		});
		System.out.println("Real Filing Cabinets loaded. Let's get kraken");
	}
}
