package com.bafomdad.realfilingcabinet.helpers;

import java.util.Locale;

import net.minecraft.util.IStringSerializable;

public enum FilingCabinetVariant implements IStringSerializable {
	
	DEFAULT(0),
	AUTOCRAFT(1),
	ENDER(2);
	
	private static final FilingCabinetVariant[] TYPE_LOOKUP = new FilingCabinetVariant[values().length];
	private final int meta;
	
	private FilingCabinetVariant(int meta) {
		
		this.meta = meta;
	}
	
	public int getMetadata() {
		
		return this.meta;
	}

	@Override
	public String getName() {

		return name().toLowerCase(Locale.ROOT);
	}
	
    public static FilingCabinetVariant byMetadata(int meta)
    {
        if (meta < 0 || meta >= TYPE_LOOKUP.length)
        {
            meta = 0;
        }
        return TYPE_LOOKUP[meta];
    }
	
    static
    {
        for (FilingCabinetVariant variantTypes : values())
        {
            TYPE_LOOKUP[variantTypes.getMetadata()] = variantTypes;
        }
    }
}
