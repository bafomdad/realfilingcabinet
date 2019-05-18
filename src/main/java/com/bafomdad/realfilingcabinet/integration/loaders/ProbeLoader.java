package com.bafomdad.realfilingcabinet.integration.loaders;

import java.util.List;

import javax.annotation.Nullable;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.bafomdad.realfilingcabinet.api.IBlockCabinet;
import com.bafomdad.realfilingcabinet.api.IEntityCabinet;
import com.google.common.base.Function;

public class ProbeLoader {
	
	public static class GetTheOneProbe implements Function<ITheOneProbe, Void> {

		private static ITheOneProbe probe;
		
		@Nullable
		@Override
		public Void apply(ITheOneProbe input) {

			this.probe = input;
			probe.registerProvider(new IProbeInfoProvider() {

				@Override
				public String getID() {

					return "realfilingcabinet:default";
				}

				@Override
				public void addProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, IBlockState state, IProbeHitData data) {

					if (state.getBlock() instanceof IBlockCabinet) {
						TileEntity tile = world.getTileEntity(data.getPos());
						List<String> strList = ((IBlockCabinet)state.getBlock()).getInfoOverlay(tile);
						if (!strList.isEmpty())
							strList.stream().forEach(s -> info.horizontal().text(s));
					}
				}
			});
			probe.registerEntityProvider(new IProbeInfoEntityProvider() {
				
				@Override
				public String getID() {
					
					return "realfilingcabinet:entity";
				}
				
				@Override
				public void addProbeEntityInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {
					
					if (entity instanceof IEntityCabinet) {
						List<String> strList = ((IEntityCabinet)entity).getInfoOverlay(entity);
						if (!strList.isEmpty())
							strList.stream().forEach(s -> info.horizontal().text(s));
					}
				}
			});
			return null;
		}
	}
}
