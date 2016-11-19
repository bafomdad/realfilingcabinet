package com.bafomdad.realfilingcabinet.blocks;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import com.bafomdad.realfilingcabinet.LogRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockGarbageBin extends Block {
	
	protected static final AxisAlignedBB BIN_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.7D, 0.75D);
	private long cost = 0;

	public BlockGarbageBin() {
		
		super(Material.ROCK);
		setRegistryName("gbin");
		setUnlocalizedName(RealFilingCabinet.MOD_ID + ".garbagebin");
		setHardness(3.5F);
		setResistance(20.0F);
		setCreativeTab(TabRFC.instance);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this), getRegistryName());
	}
	
	@Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        
		return BIN_AABB;
    }
	
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
    	
		if (hand == EnumHand.MAIN_HAND)
		{
			if (!world.isRemote)
			{
				ItemStack folder = player.getHeldItem(EnumHand.MAIN_HAND);
				if (folder != null && folder.getItem() instanceof IFolder && folder.getItemDamage() < 3)
				{
					String parseconfig = RealFilingCabinet.config.binRecipe;
					if (parseconfig == null || parseconfig.isEmpty()) {
						LogRFC.error("binRecipe config is empty or null.");
						return false;
					}
					List<String> csv = Arrays.asList(parseconfig.split(","));
					ItemStack output = null;
					
					for (int i = 0; i < csv.size(); i++) {
						String loopstr = csv.get(i);
						List<String> csv2 = Arrays.asList(loopstr.split("/"));
						if (csv2.size() > 3) {
							LogRFC.error("cannot have a size greater than 3.");
							break;
						}
						output = processFolder(folder, csv2);
						if (output != null)
							break;
					}
					if (output != null)
					{
						EntityItem ei = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, output);
						world.spawnEntityInWorld(ei);
						eatFolderWithCost(folder);
					}
				}
			}
			return true;
		}
		return false;
    }
	
	private ItemStack processFolder(ItemStack folder, List<String> str) {
		
		if (ItemFolder.getObject(folder) != null) {
			ItemStack folderstack = (ItemStack)ItemFolder.getObject(folder);
			if (folderstack != null)
			{
				long size = ItemFolder.getFileSize(folder);
				String input = "";
				String quantity = "";
				String output = "";
				ItemStack stack = null;
				
				for (int i = 0; i < str.size(); i++) {
					String loopstr = str.get(i);
					
					if (i % 3 == 0 && folderstack.getUnlocalizedName().equals(loopstr)) {
						input = loopstr;
						continue;
					}
					if (i % 3 == 1 && size >= Long.parseLong(loopstr)) {
						quantity = loopstr;
						continue;
					}
					if (i % 3 == 2 && !input.isEmpty() && !quantity.isEmpty()) {
						stack = matchRecipe(loopstr);
						if (stack != null)
						{
							cost = Long.parseLong(quantity);
							return stack;
						}
					}
				}
			}
		}
		return null;
	}
	
	private ItemStack matchRecipe(String str) {
		
		Block block = Block.getBlockFromName(str.substring(5));
		Item item = Item.getByNameOrId(str.substring(5));
		
		if (block != null)
			return new ItemStack(block, 1, 0);
		
		if (item != null)
			return new ItemStack(item, 1, 0);
		else
			System.out.println("These objects are null");
		
		return null;
	}
	
	private void eatFolderWithCost(ItemStack folder) {
		
		if (this.cost > 0)
		{
			ItemFolder.remove(folder, cost);
			this.cost = 0;
		}
	}
	
	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		
		return false;
	}
	
	@Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }
}
