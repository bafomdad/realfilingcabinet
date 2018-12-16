package com.bafomdad.realfilingcabinet;

import com.bafomdad.realfilingcabinet.api.IModAddon;
import com.bafomdad.realfilingcabinet.data.EnumDataType;
import com.bafomdad.realfilingcabinet.data.IDataHooks;
import com.bafomdad.realfilingcabinet.init.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.events.PlayerInteractionEvent;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

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
		/**
		PlayerInteractionEvent.INTERACT_BLOCK.register((player, world, hand, pos, facing, hitX, hitY, hitZ) -> {

			if (!(world instanceof ServerWorld)) return ActionResult.PASS;

			ItemStack stack = player.getMainHandStack();
			if (stack.isEmpty() || (!stack.isEmpty() && stack.getItem() != RFCItems.MAGNIFYINGGLASS)) return ActionResult.PASS;

			BlockEntity be = world.getBlockEntity(pos);
			if (be != null) {
				System.out.println(((IDataHooks)be).getDataHolder(EnumDataType.CUSTOM));
				System.out.println(((IDataHooks)be).getDataHolder(EnumDataType.ITEM));
				return ActionResult.SUCCESS;
			}
			return ActionResult.PASS;
		});
		*/
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
