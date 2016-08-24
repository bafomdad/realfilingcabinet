package com.bafomdad.realfilingcabinet.integration.ae2;

import com.bafomdad.realfilingcabinet.blocks.TileEntityRFC;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IExternalStorageHandler;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.StorageChannel;

public class RFCExternalStorageHandler implements IExternalStorageHandler {
	
	private IStorageBusMonitorWrapper sbmWrapper;
	
	public RFCExternalStorageHandler(IStorageBusMonitorWrapper wrapper) {
		
		sbmWrapper = wrapper;
	}

	@Override
	public boolean canHandle(TileEntity te, ForgeDirection d, StorageChannel channel, BaseActionSource mySrc) {

		return channel == StorageChannel.ITEMS && te instanceof TileEntityRFC;
	}

	@Override
	public IMEInventory getInventory(TileEntity te, ForgeDirection d, StorageChannel channel, BaseActionSource src) {

		if (sbmWrapper != null && channel == StorageChannel.ITEMS)
			return sbmWrapper.createStorageBusMonitor(new MEInventoryRFC((TileEntityRFC)te), src);
		
		return null;
	}
}
