package com.bafomdad.realfilingcabinet.init;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.FilingCabinetBlock;
import net.fabricmc.fabric.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.block.BlockItem;
import net.minecraft.util.registry.Registry;

/**
 * Created by bafomdad on 12/11/2018.
 */
public class RFCBlocks {

    public static Block FILINGCABINET;

    public static void init() {

        FILINGCABINET = registerBlock(new FilingCabinetBlock(FabricBlockSettings.create(Material.METAL).setHardness(5.0F).setResistance(1000.0F).build()), "filingcabinet");
    }

    private static Block registerBlock(Block block, String name) {

        return registerBlock(block, name, true);
    }

    private static Block registerBlock(Block block, String name, boolean itemBlock) {

        Registry.register(Registry.BLOCK, RealFilingCabinet.MODID + ":" + name, block);
        if (itemBlock) {
            BlockItem item = new BlockItem(block, new Item.Settings().itemGroup(ItemGroup.DECORATIONS));
            item.registerBlockItemMap(Item.BLOCK_ITEM_MAP, item);
            RFCItems.registerItem(item, "item" + name);
        }
        return block;
    }
}
