package com.bafomdad.realfilingcabinet.crafting;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IEmptyFolder;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.init.RFCRecipes;
import com.bafomdad.realfilingcabinet.items.FolderItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by bafomdad on 12/13/2018.
 */
public class FolderStorageRecipe extends AbstractRecipe {

    private ItemStack output, input;

    public FolderStorageRecipe(String name, ItemStack output, ItemStack input) {

        super(new Identifier(RealFilingCabinet.MODID, name));
        this.output = output;
        this.input = input;
    }

    @Override
    public boolean matches(Inventory inv, World world) {

        List<ItemStack> list = new ArrayList();
        list.add(input);

        for (int i = 0; i < inv.getInvSize(); ++i) {
            ItemStack stack = inv.getInvStack(i);
            if (!stack.isEmpty()) {
                if (allowableIngredient(stack))
                    list.add(stack);

                boolean flag = false;
                Iterator iter = list.iterator();

                while (iter.hasNext()) {
                    ItemStack stack1 = (ItemStack)iter.next();

                    if (stack.getItem() == stack1.getItem()) {
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

        int emptyFolder = -1;
        int recipeStack = -1;

        for (int i = 0; i < inv.getInvSize(); i++) {
            ItemStack stack = inv.getInvStack(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof IEmptyFolder)
                    emptyFolder = i;
                else
                    recipeStack = i;
            }
        }
        if (emptyFolder >= 0 && recipeStack >= 0) {
            ItemStack stack1 = inv.getInvStack(recipeStack);
//            ItemStack folder = inv.getInvStack(emptyFolder);

            ItemStack newFolder = output.copy();
            FolderItem.setItem(newFolder, stack1);
            return newFolder.copy();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {

        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {

        return RFCRecipes.FOLDER_STORAGE_RECIPE;
    }

    private boolean allowableIngredient(ItemStack stack) {

        return !(stack.getItem() instanceof IFolder) && !(stack.getItem() instanceof IEmptyFolder);
    }
}
