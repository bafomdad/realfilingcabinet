package com.bafomdad.realfilingcabinet.items;

import com.bafomdad.realfilingcabinet.api.IEmptyFolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

/**
 * Created by bafomdad on 12/13/2018.
 */
public class EmptyFolderItem extends Item implements IEmptyFolder {

    public EmptyFolderItem() {

        super(new Item.Settings().stackSize(16).itemGroup(ItemGroup.MISC));
    }
}
