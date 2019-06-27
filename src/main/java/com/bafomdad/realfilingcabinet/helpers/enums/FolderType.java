package com.bafomdad.realfilingcabinet.helpers.enums;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityFolder;
import com.bafomdad.realfilingcabinet.utils.EnderUtils;
import com.bafomdad.realfilingcabinet.utils.FluidUtils;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;
import com.bafomdad.realfilingcabinet.utils.MobUtils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public enum FolderType {
	
	NORMAL {

		@Override
		public ItemStack insert(CapabilityFolder cap, Object toInsert, boolean sim, boolean oreDict) {

			if (!(toInsert instanceof ItemStack) || !cap.isItemStack()) return null;
			if (!ItemStack.areItemsEqual((ItemStack)toInsert, cap.getItemStack()) && !oreDict) return null;

			ItemStack stack = (ItemStack)toInsert;
			if (!sim) {
				cap.setCount(cap.getCount() + stack.getCount());
				stack.setCount(0);
			}
			return ItemStack.EMPTY;
		}

		@Override
		public ItemStack extract(CapabilityFolder cap, long amount, boolean sim, boolean creative) {

			ItemStack items = cap.getItemStack().copy();
			items.setCount((int)Math.min(cap.getCount(), amount));
			
			if (!sim && !creative) {
				cap.setCount(cap.getCount() - items.getCount());
			}
			return items;
		}
		
		@Override
		public EnumActionResult placeObject(ItemStack folder, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
			
			Object obj = FolderUtils.get(folder).getObject();
			if (obj instanceof ItemStack) {
				if (((ItemStack)obj).getItem() instanceof ItemBlock) {
					long count = FolderUtils.get(folder).getFileSize();
					
					if (count > 0) {
						ItemStack stackToPlace = new ItemStack(((ItemStack)obj).getItem(), 1, ((ItemStack)obj).getItemDamage());
						ItemStack savedFolder = player.getHeldItem(hand);
						
						player.setHeldItem(hand, stackToPlace);
						EnumActionResult ear = stackToPlace.onItemUse(player, world, pos, hand, side, hitX, hitY, hitZ);
						player.setHeldItem(hand, savedFolder);
						
						if (ear == EnumActionResult.SUCCESS) {
							if (!player.capabilities.isCreativeMode)
								FolderUtils.get(folder).remove(1);
							
							return EnumActionResult.SUCCESS;
						}
					}
				}
			}
			return EnumActionResult.PASS;
		}
	},
	ENDER {

		@Override
		public ItemStack insert(CapabilityFolder cap, Object toInsert, boolean sim, boolean oreDict) {

			if (!(toInsert instanceof ItemStack) || !cap.isItemStack()) return null;
			if (!ItemStack.areItemsEqual((ItemStack)toInsert, cap.getItemStack()) && !oreDict) return null;

			ItemStack stack = (ItemStack)toInsert;
			if (!sim) {
				cap.setCount(cap.getCount() + stack.getCount());
				stack.setCount(0);
			}
			return ItemStack.EMPTY;
		}

		@Override
		public ItemStack extract(CapabilityFolder cap, long amount, boolean sim, boolean creative) {

			ItemStack items = cap.getItemStack().copy();
			items.setCount((int)Math.min(cap.getCount(), amount));
			
			if (!sim && !creative)
				cap.setCount(cap.getCount() - items.getCount());

			return items;
		}
		
		@Override
		public EnumActionResult placeObject(ItemStack folder, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
			
			Object obj = FolderUtils.get(folder).getObject();
			if (obj instanceof ItemStack) {
				if (((ItemStack)obj).getItem() instanceof ItemBlock) {
					long count = FolderUtils.get(folder).getFileSize();
					if (!EnderUtils.preValidateEnderFolder(folder))
						return EnumActionResult.PASS;
					
					if (count > 0) {
						ItemStack stackToPlace = new ItemStack(((ItemStack)obj).getItem(), 1, ((ItemStack)obj).getItemDamage());
						ItemStack savedFolder = player.getHeldItem(hand);
						
						player.setHeldItem(hand, stackToPlace);
						EnumActionResult ear = stackToPlace.onItemUse(player, world, pos, hand, side, hitX, hitY, hitZ);
						player.setHeldItem(hand, savedFolder);
						
						if (ear == EnumActionResult.SUCCESS) {
							if (!player.capabilities.isCreativeMode && !world.isRemote) {
								FolderUtils.get(folder).setExtractSize(-1);
								EnderUtils.syncToTile(folder);
								if (player instanceof FakePlayer)
									EnderUtils.syncToFolder(folder);
							}
							return EnumActionResult.SUCCESS;
						}
					}
				}
			}
			return EnumActionResult.PASS;
		}
	},
	DURA {

		@Override
		public ItemStack insert(CapabilityFolder cap, Object toInsert, boolean sim, boolean oreDict) {

			if (!(toInsert instanceof ItemStack) || !cap.isItemStack()) return null;
			if (!ItemStack.areItemsEqualIgnoreDurability((ItemStack)toInsert, cap.getItemStack()) && !oreDict) return null;

			ItemStack stack = (ItemStack)toInsert;
			if (!sim) {
				cap.setRemainingDurability(cap.getRemainingDurability() + (stack.getMaxDamage() - stack.getItemDamage()));
				int newRem = cap.getRemainingDurability();

				if (newRem >= stack.getMaxDamage()) {
					cap.setCount(cap.getCount() + 1);
					int newStoredRem = newRem - stack.getMaxDamage();
					cap.setRemainingDurability(newStoredRem);
				}
				stack.shrink(1);
			}
			return ItemStack.EMPTY;
		}

		@Override
		public ItemStack extract(CapabilityFolder cap, long amount, boolean sim, boolean creative) {

			ItemStack items = cap.getItemStack().copy();
			items.setCount((int)Math.min(cap.getCount(), amount));
			
			if (!sim && !creative)
				cap.setCount(cap.getCount() - items.getCount());

			return items;	
		}
	},
	MOB {

		@Override
		public EntityLivingBase insert(CapabilityFolder cap, Object toInsert, boolean sim, boolean oreDict) {

			if (!(toInsert instanceof EntityLivingBase)) return null;
			
			EntityLivingBase target = (EntityLivingBase)toInsert;
			if (MobUtils.acceptableTargets(target)) {
				if (cap.getContents().equals(target.getClass()) && !sim) {
					cap.setCount(cap.getCount() + 1);
					MobUtils.dropMobEquips(target.world, target);
					target.setDead();
				}
			}
			return target;
		}

		@Override
		public EntityLivingBase extract(CapabilityFolder cap, long amount, boolean sim, boolean creative) {
			// NO-OP
			return null;
		}
		
		@Override
		public EnumActionResult placeObject(ItemStack folder, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
			
			if (MobUtils.spawnEntityFromFolder(world, player, folder, pos, side))
				return EnumActionResult.SUCCESS;
			
			return EnumActionResult.PASS;
		}
			
	},
	FLUID {

		@Override
		public FluidStack insert(CapabilityFolder cap, Object toInsert, boolean sim, boolean oreDict) {
			
			if (!(toInsert instanceof FluidStack) || !cap.isFluidStack()) return null;
			
			FluidStack fluid = (FluidStack)toInsert;
			if (!fluid.isFluidEqual(cap.getFluidStack())) return null;
			
			if (sim)
				cap.setCount(cap.getCount() + fluid.amount);
			
			return fluid;
		}

		@Override
		public FluidStack extract(CapabilityFolder cap, long amount, boolean sim, boolean creative) {

			if (!cap.isFluidStack()) return null;
			
			return cap.getFluidStack();
		}
		
		@Override
		public EnumActionResult placeObject(ItemStack folder, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
			
			if (FluidUtils.doPlace(world, player, folder, pos, side))
				return EnumActionResult.SUCCESS;

			return EnumActionResult.PASS;
		}
	},
	NBT {

		@Override
		public ItemStack insert(CapabilityFolder cap, Object toInsert, boolean sim, boolean oreDict) {

			if (!(toInsert instanceof ItemStack) || !cap.isItemStack()) return null;
			if (!ItemStack.areItemStackTagsEqual((ItemStack)toInsert, cap.getItemStack()) && !oreDict) return null;

			ItemStack stack = (ItemStack)toInsert;
			if (!sim) {
				cap.setCount(cap.getCount() + stack.getCount());
				stack.setCount(0);
			}
			return ItemStack.EMPTY;
		}

		@Override
		public ItemStack extract(CapabilityFolder cap, long amount, boolean sim, boolean creative) {

			ItemStack items = cap.getItemStack().copy();
			items.setCount((int)Math.min(cap.getCount(), amount));
			
			if (!sim && !creative)
				cap.setCount(cap.getCount() - items.getCount());

			return items;	
		}
	};
	
	public abstract <T> T insert(CapabilityFolder cap, Object toInsert, boolean sim, boolean oreDict);
	
	public abstract <T> T extract(CapabilityFolder cap, long amount, boolean sim, boolean creative);
	
	public EnumActionResult placeObject(ItemStack folder, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		
		return EnumActionResult.PASS;
	}
}
