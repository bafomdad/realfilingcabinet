package com.bafomdad.realfilingcabinet;

import com.bafomdad.realfilingcabinet.api.IModAddon;
import com.bafomdad.realfilingcabinet.blocks.entity.FilingCabinetEntity;
import com.bafomdad.realfilingcabinet.init.*;
import com.bafomdad.realfilingcabinet.utils.StorageUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.events.PlayerInteractionEvent;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.List;

public class RealFilingCabinet implements ModInitializer {

	public static final String MODID = "realfilingcabinet";
	public static List<IModAddon> addons = new ArrayList();

	@Override
	public void onInitialize() {

		RFCBlocks.init();
		RFCEntities.init();
		RFCItems.init();
		RFCRecipes.init();
		setAddons();
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

	private void setAddons() {

		addons.add(new InternalAddon());
	}

	private class InternalAddon implements IModAddon {

		@Override
		public void register() {

			System.out.println("Internally registered API from within RFC");
		}
	}
}
