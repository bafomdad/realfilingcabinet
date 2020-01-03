package com.bafomdad.realfilingcabinet.integration.loaders;

import java.util.List;

import javax.annotation.Nonnull;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.bafomdad.realfilingcabinet.api.IBlockCabinet;
import com.bafomdad.realfilingcabinet.api.IEntityCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.blocks.BlockRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileFilingCabinet;
import com.bafomdad.realfilingcabinet.entity.EntityCabinet;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;

public class WailaLoader {

	public static void load(IWailaRegistrar registrar) {
		
		registrar.registerBodyProvider(new WailaProvider(), BlockRFC.class);
//		registrar.registerNBTProvider(new WailaProvider(), BlockRFC.class);
		registrar.registerBodyProvider(new WailaEntityProvider(), EntityCabinet.class);
		registrar.registerNBTProvider(new WailaEntityProvider(), EntityCabinet.class);
	}
	
	public static class WailaProvider implements IWailaDataProvider {
		
		@Override
		public List<String> getWailaBody(ItemStack stack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
			
			if (accessor.getBlock() instanceof IBlockCabinet) {
				TileEntity tile = accessor.getTileEntity();
				List<String> strList = ((IBlockCabinet)accessor.getBlock()).getInfoOverlay(tile, accessor.getPlayer().isSneaking());
				if (!strList.isEmpty())
					strList.stream().forEach(s -> currenttip.add(s));
			}
			return currenttip;
		}
		
		@Nonnull
		@Override
		public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, BlockPos pos) {
			
			if (tile instanceof TileFilingCabinet) {
				TileFilingCabinet cabinet = (TileFilingCabinet)tile;
				if (!cabinet.smeltingJobs.isEmpty()) {
					NBTTagList tagList = new NBTTagList();
					for (int[] job : cabinet.smeltingJobs) {
						NBTTagCompound nbt = new NBTTagCompound();
						nbt.setIntArray(StringLibs.TAG_SMELTJOB, job);
						tagList.appendTag(nbt);
					}
					tag.setTag(StringLibs.TAG_SMELTLIST, tagList);
				}
			}
			return tag;
		}
	}
	
	public static class WailaEntityProvider implements IWailaEntityProvider {
		
		@Override
	    public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
	        
			if (accessor.getNBTData().hasKey(StringLibs.ENTITY_INV) && accessor.getEntity() instanceof IEntityCabinet) {
				currenttip.add("Currently carrying:");
				NBTTagCompound tag = accessor.getNBTData().getCompoundTag(StringLibs.ENTITY_INV);
		        NBTTagList tagList = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		        for (int i = 0; i < tagList.tagCount(); i++) {
		            NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
		            ItemStack stack = new ItemStack(itemTags);
		            if (!stack.isEmpty() && stack.getItem() instanceof IFolder) {
		            	FolderUtils.get(stack).addTooltips(currenttip, accessor.getPlayer().isSneaking());
		            }
		        }
			}
			return currenttip;
	    }
		
		@Override
	    public NBTTagCompound getNBTData(EntityPlayerMP player, Entity ent, NBTTagCompound tag, World world) {
			
			EntityCabinet cabinet = (EntityCabinet)ent;
			tag.setTag(StringLibs.ENTITY_INV, cabinet.getInventory().serializeNBT());
			
	        return tag;
	    }
	}
}
