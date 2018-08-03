package com.bafomdad.realfilingcabinet.crafting;

import java.util.function.BooleanSupplier;

import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import com.bafomdad.realfilingcabinet.NewConfigRFC;
import com.google.gson.JsonObject;

public class ConditionFactoryRFC implements IConditionFactory {

	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json) {

		String key = JsonUtils.getString(json, "key");
		boolean value = JsonUtils.getBoolean(json, "value", true);
		return () -> NewConfigRFC.boolean_stuff.get(key) == value;
	}
}
