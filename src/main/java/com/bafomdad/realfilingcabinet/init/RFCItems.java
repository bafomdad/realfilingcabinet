package com.bafomdad.realfilingcabinet.init;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.items.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by bafomdad on 12/11/2018.
 */
public class RFCItems {

    public static Item EMPTYFOLDER;
    public static Item FOLDER;
    public static Item FILTER;
    public static Item MAGNIFYINGGLASS;
    public static Item TAPE;
    public static Item SUITCASE;

    public static void init() {

        EMPTYFOLDER = registerItem(new EmptyFolderItem(), "emptyfolder_normal");
        FOLDER = registerItem(new FolderItem(), "folder_normal");
        setContainerItemAgain(FOLDER, "recipeRemainder", "containerItem", "awj");
        FILTER = registerItem(new Item(new Item.Settings().stackSize(16).itemGroup(ItemGroup.MISC)), "filter");
        MAGNIFYINGGLASS = registerItem(new Item(new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC)), "magnifyingglass");
        TAPE = registerItem(new Item(new Item.Settings().stackSize(1).durability(25).recipeRemainder(TAPE).itemGroup(ItemGroup.MISC)), "whiteouttape");
        setContainerItemAgain(TAPE, "recipeRemainder", "containerItem", "awj");
 //       SUITCASE = registerItem(new Item(new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC)), "suitcase");
    }

    public static Item registerItem(Item item, String name) {

        Registry.register(Registry.ITEM, RealFilingCabinet.MODID + ":" + name, item);
        return item;
    }

    private static void setContainerItemAgain(Item item, String... fieldNames) {

        for (String fieldName : fieldNames) {
            try {
                Field f = Item.class.getDeclaredField(fieldName);
                f.setAccessible(true);

                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                f.set(item, item);
                break;

            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
