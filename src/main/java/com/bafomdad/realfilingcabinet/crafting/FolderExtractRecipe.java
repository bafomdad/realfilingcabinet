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
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bafomdad on 12/14/2018.
 */
public class FolderExtractRecipe extends AbstractRecipe {

    private ItemStack input;

    public FolderExtractRecipe(String name, ItemStack input) {

        super(new Identifier(RealFilingCabinet.MODID, name));
        this.input = input;
    }

    @Override
    public boolean matches(Inventory inv, World world) {

        List<ItemStack> list = new ArrayList();
        list.add(input);
        for (int i = 0; i < inv.getInvSize(); ++i) {
            ItemStack stack = inv.getInvStack(i);
            if (!stack.isEmpty()) {
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

        int folder = -1;
        for (int i = 0; i < inv.getInvSize(); i++) {
            ItemStack stack = inv.getInvStack(i);
            if (!stack.isEmpty())
                folder = i;
        }
        if (folder >= 0) {
            ItemStack stack = inv.getInvStack(folder);
            ItemStack heldItem = FolderItem.getItem(stack);
            if (stack.getItem() == RFCItems.FOLDER && !heldItem.isEmpty()) {
                long count = FolderItem.getFileSize(stack);
                if (count > 0) {
                    long extract = Math.min(heldItem.getMaxAmount(), count);
                    return new ItemStack(heldItem.getItem(), (int)extract);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {

        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {

        return RFCRecipes.FOLDER_EXTRACT_RECIPE;
    }
}
