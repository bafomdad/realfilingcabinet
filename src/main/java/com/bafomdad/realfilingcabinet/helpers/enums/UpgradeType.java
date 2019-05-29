package com.bafomdad.realfilingcabinet.helpers.enums;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.BlockRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileFilingCabinet;
import com.bafomdad.realfilingcabinet.entity.EntityCabinet;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.utils.SmeltingUtils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public enum UpgradeType {

	CREATIVE(null, StringLibs.TAG_CREATIVE),
	CRAFTING(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/craftingcabinet.png"), StringLibs.TAG_CRAFT),
	ENDER(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/endercabinet.png"), StringLibs.TAG_ENDER),
	OREDICT(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/oredictcabinet.png"), StringLibs.TAG_OREDICT),
	MOB(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/mobcabinet.png"), StringLibs.TAG_MOB),
	FLUID(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/fluidcabinet.png"), StringLibs.TAG_FLUID),
	LIFE(null, StringLibs.TAG_LIFE) {
		
		@Override
		public void tickUpgrade(TileEntity tile) {
			
			TileFilingCabinet tfc = (TileFilingCabinet)tile;
			World world = tfc.getWorld();
			BlockPos pos = tfc.getPos();
			
			if (!UpgradeHelper.getUpgrade(tfc, StringLibs.TAG_LIFE).isEmpty()) {
				if (!world.isRemote) {
					EntityCabinet cabinet = new EntityCabinet(world);
					IBlockState state = world.getBlockState(pos);
					float angle = state.getValue(BlockRFC.FACING).getHorizontalAngle();
					cabinet.setPositionAndRotation(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, angle, 0);
					
					for (int i = 0; i < tfc.getInventory().getSlots(); i++) {
						ItemStack folder = tfc.getInventory().getFolder(i);
						if (!folder.isEmpty())
							cabinet.getInventory().setStackInSlot(i, folder);
					}
					if (tfc.isCabinetLocked())
						cabinet.setOwnerId(tfc.getOwner());
					else
						cabinet.homePos = pos.toLong();
					
					cabinet.setLegit();
					world.spawnEntity(cabinet);
				}
				world.setBlockToAir(pos);
			}
		}
	},
	SMELTING(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/smeltingcabinet.png"), StringLibs.TAG_SMELT) {
		
		@Override
		public void tickUpgrade(TileEntity tile) {
			
			TileFilingCabinet tfc = (TileFilingCabinet)tile;
			if (SmeltingUtils.canSmelt(tfc)) {
				SmeltingUtils.incrementSmeltTime(tfc);
				if (tfc.getWorld().getTotalWorldTime() % 40 == 0) {
					SmeltingUtils.createSmeltingJob(tfc);
					tfc.markBlockForUpdate();
				}
				return;
			}
		}
	};
	
	final ResourceLocation res;
	final String tag;
	
	private UpgradeType(ResourceLocation res, String tag) {
		
		this.res = res;
		this.tag = tag;
	}
	
	public ResourceLocation getTexture() {
		
		return res;
	}
	
	public String getTag() {
		
		return tag;
	}
	
	public void tickUpgrade(TileEntity tile) {}
}
