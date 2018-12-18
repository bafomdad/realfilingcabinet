package com.bafomdad.realfilingcabinet.init;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.items.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

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
        FILTER = registerItem(new Item(new Item.Settings().stackSize(16).itemGroup(ItemGroup.MISC)), "filter");
        MAGNIFYINGGLASS = registerItem(new Item(new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC)), "magnifyingglass");
        TAPE = registerItem(new Item(new Item.Settings().stackSize(1).durability(25).recipeRemainder(TAPE).itemGroup(ItemGroup.MISC)), "whiteouttape");
//        SUITCASE = registerItem(new Item(new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC)), "suitcase");
    }

    public static Item registerItem(Item item, String name) {

        Registry.register(Registry.ITEM, RealFilingCabinet.MODID + ":" + name, item);
        return item;
    }
}
