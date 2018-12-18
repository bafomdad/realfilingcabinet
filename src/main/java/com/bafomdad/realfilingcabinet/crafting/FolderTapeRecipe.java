package com.bafomdad.realfilingcabinet.crafting;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.init.RFCRecipes;
import com.bafomdad.realfilingcabinet.items.FolderItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bafomdad on 12/16/2018.
 */
public class FolderTapeRecipe extends AbstractRecipe {

    public FolderTapeRecipe(String name) {

        super(new Identifier(RealFilingCabinet.MODID, name));
    }

    @Override
    public boolean matches(Inventory inv, World world) {

        List<ItemStack> list = new ArrayList();
        list.add(new ItemStack(RFCItems.TAPE));
        for (int i = 0; i < inv.getInvSize(); ++i) {
            ItemStack stack = inv.getInvStack(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() == RFCItems.FOLDER)
                    list.add(stack);

                boolean flag = false;
                Iterator iter = list.iterator();

                while (iter.hasNext()) {
                    ItemStack stack1 = (ItemStack)iter.next();
                    if (ItemStack.areEqualIgnoreDurability(stack, stack1)) {
                        flag = true;
                        list.remove(stack1);
                        break;
                    }
                }
                if (!flag)
                    return false;
            }
        }
        return list.isEmpty();
    }

    @Override
    public ItemStack craft(Inventory inv) {

        int folder = -1;
        int tape = -1;

        for (int i = 0; i < inv.getInvSize(); i++) {
            ItemStack stack = inv.getInvStack(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() == RFCItems.FOLDER)
                    folder = i;
                if (stack.getItem() == RFCItems.TAPE)
                    tape = i;
            }
        }
        if (folder >= 0 && tape >= 0) {
            ItemStack stack1 = inv.getInvStack(folder);
            if (FolderItem.getFileSize(stack1) <= 0)
                return new ItemStack(RFCItems.EMPTYFOLDER);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {

        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {

        return RFCRecipes.FOLDER_TAPE_RECIPE;
    }

    @Override
    public DefaultedList<ItemStack> getRemainingStacks(Inventory inv) {

        DefaultedList<ItemStack> items = DefaultedList.create(inv.getInvSize(), ItemStack.EMPTY);
        for (int i = 0; i < items.size(); ++i) {
            ItemStack stack = inv.getInvStack(i);
            if (!stack.isEmpty() && stack.getItem() == RFCItems.TAPE) {
                int damage = stack.getDamage();
                stack.setDamage(damage + 1);
                items.set(i, stack.copy());
            }
        }
        return items;
    }
}
