package com.bafomdad.realfilingcabinet.helpers.enums;

import net.minecraft.item.ItemStack;

public enum EmptyFolderType {

	NORMAL {
		@Override
		public boolean canRecipeTakeStack(ItemStack recipeStack) {

			return !recipeStack.hasTagCompound() && !recipeStack.isItemDamaged();
		}
	},
	DURA {
		@Override
		public boolean canRecipeTakeStack(ItemStack recipeStack) {

			return !recipeStack.hasTagCompound() && recipeStack.isItemStackDamageable() && !recipeStack.isItemDamaged();
		}
	},
	MOB {
		@Override
		public boolean canRecipeTakeStack(ItemStack recipeStack) {

			return false;
		}
	},
	FLUID {
		@Override
		public boolean canRecipeTakeStack(ItemStack recipeStack) {

			return false;
		}
	},
	NBT {
		@Override
		public boolean canRecipeTakeStack(ItemStack recipeStack) {

			return recipeStack.hasTagCompound() && !recipeStack.isItemDamaged();
		}
	};
	
	public abstract boolean canRecipeTakeStack(ItemStack recipeStack);
}
