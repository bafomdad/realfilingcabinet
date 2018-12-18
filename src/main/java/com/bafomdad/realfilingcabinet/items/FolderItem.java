package com.bafomdad.realfilingcabinet.items;

import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.utils.CompoundUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.block.BlockItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by bafomdad on 12/12/2018.
 */
public class FolderItem extends Item implements IFolder {

    private static final String FILE_NAME = "fileName";
    private static final String FILE_SIZE = "fileSize";

    public FolderItem() {

        super(new Item.Settings().stackSize(1).recipeRemainder(RFCItems.FOLDER).rarity(Rarity.UNCOMMON));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void buildTooltip(ItemStack stack, World world, List<TextComponent> text, TooltipOptions tooltip) {

        text.add(new TranslatableTextComponent(getItem(stack).getTranslationKey()));
        text.add(new StringTextComponent("Count: " + getFileSize(stack)));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {

        ItemStack stack = ctx.getItemStack();
        ItemStack stackToPlace = getItem(stack);
        if (!stackToPlace.isEmpty() && stackToPlace.getItem() instanceof BlockItem && getFileSize(stack) > 0) {
            ActionResult ar = stackToPlace.useOnBlock(new ItemUsageContext(ctx.getPlayer(), stackToPlace.copy(), ctx.getPos(), ctx.getFacing(), ctx.getHitX(), ctx.getHitY(), ctx.getHitZ()));
            if (ar == ActionResult.SUCCESS) {
                if (!ctx.getPlayer().isCreative()) {
                    remove(stack, 1);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    public static void setFileSize(ItemStack stack, long count) {

        CompoundUtils.setLong(stack, FILE_SIZE, count);
    }

    public static long getFileSize(ItemStack stack) {

        return CompoundUtils.getLong(stack, FILE_SIZE, 0);
    }

    public static void remove(ItemStack stack, long count) {

        long current = getFileSize(stack);
        setFileSize(stack, Math.max(current - count, 0));
    }

    public static void add(ItemStack stack, long count) {

        long current = getFileSize(stack);
        setFileSize(stack, current + count);
    }

    public static ItemStack getItem(ItemStack folder) {

        return ItemStack.fromTag(CompoundUtils.getCompound(folder, FILE_NAME, false));
    }

    public static void setItem(ItemStack folder, ItemStack toSet) {

        CompoundTag tag = toSet.toTag(new CompoundTag());
        CompoundUtils.setCompound(folder, FILE_NAME, tag);
        add(folder, 1);
    }
}
