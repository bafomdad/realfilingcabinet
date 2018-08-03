package com.bafomdad.realfilingcabinet.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class RFCWorldInfo extends WorldSavedData {
	
	public static final String ID = "RFCWorldInfo";
	
	boolean structureGenerated = false;

	public RFCWorldInfo(String name) {
		
		super(name);
	}
	
	public RFCWorldInfo() {
		
		this(ID);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		
		this.structureGenerated = nbt.getBoolean("structureGenerated");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		nbt.setBoolean("structureGenerated", structureGenerated);
		return nbt;
	}
	
	public boolean hasGenerated() {
		
		return structureGenerated;
	}
	
	public void setStructureGenerated(boolean generated) {
		
		if (generated != structureGenerated) {
			structureGenerated = generated;
			this.markDirty();
		}
	}
	
	public static RFCWorldInfo getInstance() {
		
		WorldServer world = FMLCommonHandler.instance().getMinecraftServerInstance().worlds[0];
		if (world != null) {
			WorldSavedData handler = world.getMapStorage().getOrLoadData(RFCWorldInfo.class, ID);
			if (handler == null) {
				handler = new RFCWorldInfo();
				world.getMapStorage().setData(ID, handler);
			}
			return (RFCWorldInfo)handler;
		}
		return null;
	}
}
