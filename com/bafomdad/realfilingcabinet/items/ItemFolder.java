package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.TileEntityRFC;
import com.bafomdad.realfilingcabinet.core.StorageUtils;
import com.bafomdad.realfilingcabinet.core.Utils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

public class ItemFolder extends Item {
	
	private static final String TAG_FILE_NAME = "fileName";
	private static final String TAG_FILE_META = "fileMeta";
	private static final String TAG_FILE_SIZE = "fileSize";
	
	public String[] folderTypes = new String[] { "folder", "enderfolder" };
	public IIcon[] iconArray;
	
	public ItemFolder() {
		
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(1);
		FMLCommonHandler.instance().bus().register(this);
	}
	
	@SubscribeEvent
	public void onMergeFolders(PlayerEvent.ItemCraftedEvent event) {
		
		if (event.crafting.getItem() == this)
		{
			for (int slot = 0; slot < event.craftMatrix.getSizeInventory(); slot++)
			{
				ItemStack stack = event.craftMatrix.getStackInSlot(slot);
				if (stack == null)
					continue;
				if (stack.getItem() == this)
				{
					if (stack.getItemDamage() == 0)
						event.craftMatrix.setInventorySlotContents(slot, null);
				}
			}
			return;
		}
	}
	
	@SubscribeEvent
	public void updateFolderInCraftingWindow(TickEvent.PlayerTickEvent event) {
		
		if (event.player.openContainer != null) {
			Container cont = event.player.openContainer;
			if (cont == event.player.inventoryContainer || cont instanceof ContainerWorkbench)
			{
				List listy = cont.getInventory();
				for (int i = 0 ; i < listy.size(); i++) {
					ItemStack folder = (ItemStack)listy.get(i);
					if (folder != null && folder.getItem() == RealFilingCabinet.itemFolder && folder.getItemDamage() == 1)
					{
						TileEntityRFC tile = getTileLoc(folder);
						if (tile != null) {
							StorageUtils.instance().syncToFolder(tile, folder, Utils.getInt(folder, "RFC_slotindex", 0));
							break;
						}
					}
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		
		iconArray = new IIcon[folderTypes.length];
		for (int i = 0; i < iconArray.length; i++) {
			iconArray[i] = register.registerIcon(RealFilingCabinet.MOD_ID + ":" + folderTypes[i]);
		}
	}
	
	public IIcon getIconFromDamage(int meta) {
		
		return this.iconArray[meta];
	}
	
	public String getUnlocalizedName(ItemStack stack) {
		
		return "item." + RealFilingCabinet.MOD_ID + "." + folderTypes[stack.getItemDamage()];
	}

    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean whatisthis) {
    	
    	ItemStack stacky = getStack(stack);
    	
    	if (stacky != null)
    	{
    		int count = getFileSize(stack);
    		list.add(count + " " + StatCollector.translateToLocal(stacky.getUnlocalizedName() + ".name"));
    	}
    	
    	if (stack.getItemDamage() == 1 && stack.stackTagCompound.hasKey("RFC_slotindex"))
    	{
        	TileEntityRFC tile = getTileLoc(stack);
        	if (tile == null || !tile.isEnder)
        		list.add("Bound tile is null");
        	else if (tile != null && list.size() > 2)
        		list.remove(2);
    	}
    }
    
    public ItemStack getContainerItem(ItemStack stack) {
    	
    	int count = getFileSize(stack);
    	
    	if (stack.getItemDamage() == 1 && getTileLoc(stack) != null)
    	{
    		StorageUtils.instance().syncBackToTile(getTileLoc(stack), stack, Utils.getInt(stack, "RFC_slotindex", 0));
    	}
    	if (count == 0)
    		return null;

    	int extract = Math.min(64, count);
    	ItemStack copy = stack.copy();
    	remove(copy, extract);

    	return copy;
    }
    
    public boolean hasContainerItem(ItemStack stack) {
    	
    	return getContainerItem(stack) != null;
    }
    
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {
    	
    	return false;
    }
    
    public static String getFileName(ItemStack stack) {
    	
    	return Utils.getString(stack, TAG_FILE_NAME, "");
    }
    
    public static int getFileMeta(ItemStack stack) {
    	
    	return Utils.getInt(stack, TAG_FILE_META, 0);
    }
    
    public static void setFileSize(ItemStack stack, int count) {
    	
    	Utils.setInt(stack, TAG_FILE_SIZE, count);
    }
    
    public static int getFileSize(ItemStack stack) {
    	
    	return Utils.getInt(stack, TAG_FILE_SIZE, 0);
    }
    
    public static int remove(ItemStack stack, int count) {
    	
    	int current = getFileSize(stack);
    	setFileSize(stack, Math.max(current - count, 0));
    	
    	return Math.min(current, count);
    }
    
    public static void add(ItemStack stack, int count) {
    	
    	int current = getFileSize(stack);
    	setFileSize(stack, current + count);
    }
    
    public static ItemStack getStack(ItemStack stack) {
    	
    	String str = getFileName(stack);
    	
    	if (Item.itemRegistry.getObject(str) != null) {
    		Item item = (Item)Item.itemRegistry.getObject(str);
    		int meta = getFileMeta(stack);
    		return new ItemStack(item, 1, meta);
    	}
    	if (Block.getBlockFromName(str) != null) {
    		Block block = Block.getBlockFromName(str);
    		int meta = getFileMeta(stack);
    		return new ItemStack(block, 1, meta);
    	}
    	return null;
    }
    
    public static boolean setStack(ItemStack folder, ItemStack stack, int damage) {
    	
    	if (getStack(folder) == null || getFileSize(folder) == 0)
    	{
    		if (stack.getItem() instanceof Item && !Item.itemRegistry.getNameForObject(stack.getItem()).isEmpty())
    			Utils.setString(folder, TAG_FILE_NAME, Item.itemRegistry.getNameForObject(stack.getItem()));
    		else if (Block.getBlockFromItem(stack.getItem()) instanceof Block && !Block.blockRegistry.getNameForObject(Block.getBlockFromItem(stack.getItem())).isEmpty())
    			Utils.setString(folder, TAG_FILE_NAME, Block.blockRegistry.getNameForObject(Block.getBlockFromItem(stack.getItem())));
    		
    		Utils.setInt(folder, TAG_FILE_META, damage);
    		ItemFolder.add(folder, 1);
    		return true;
    	}
    	return false;
    }
    
    public static ItemStack createEnderFolder(TileEntityRFC tile, EntityPlayer player, ItemStack stack) {
    	
    	NBTTagCompound playertag = player.getEntityData();
    	if (!playertag.hasKey("RFC_slotindex"))
    	{  		
    		playertag.setInteger("RFC_slotindex", 0);
    	}
    	ItemStack enderFolder = stack.copy();
    	enderFolder.setItemDamage(1);
    	Utils.setInt(enderFolder, "RFC_slotindex", playertag.getInteger("RFC_slotindex"));
    	setTileLoc(tile, enderFolder);
    	return enderFolder;
    }
    
    public static void setTileLoc(TileEntityRFC tile, ItemStack stack) {
    	
    	int x = tile.xCoord;
    	int y = tile.yCoord;
    	int z = tile.zCoord;
    	int dim = tile.getWorldObj().provider.dimensionId;
    	
    	Utils.setInt(stack, "RFC_xLoc", x);
    	Utils.setInt(stack, "RFC_yLoc", y);
    	Utils.setInt(stack, "RFC_zLoc", z);
    	Utils.setInt(stack, "RFC_dim", dim);
    }
    
    public static TileEntityRFC getTileLoc(ItemStack stack) {
    	
    	int x = Utils.getInt(stack, "RFC_xLoc", -1);
    	int y = Utils.getInt(stack, "RFC_yLoc", -1);
    	int z = Utils.getInt(stack, "RFC_zLoc", -1);
    	int dim = Utils.getInt(stack, "RFC_dim", 0);
    	
    	return findLoadedTileEntityInWorld(x, y, z, dim);
    }
    
    private static TileEntityRFC findLoadedTileEntityInWorld(int x, int y, int z, int dim) {
    	
    	MinecraftServer server = MinecraftServer.getServer();
    	for (WorldServer world : server.worldServers) {
    		for (Object obj : world.loadedTileEntityList) {
    			if (obj instanceof TileEntityRFC)
    			{
    				if (world.provider.dimensionId == dim)
    					return (TileEntityRFC)world.getTileEntity(x, y, z);
    			}
    		}
    	}
    	return null;
    }
    
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean watdis) {
    	
    	if (stack.getItemDamage() != 1 || !stack.getTagCompound().hasKey("RFC_slotindex"))
    		return;
    	
    	TileEntityRFC tile = getTileLoc(stack);
    	if (tile != null && tile.isEnder)
    	{
    		StorageUtils.instance().syncToFolder(tile, stack, Utils.getInt(stack, "RFC_slotindex", 0));
    	}
    	else
    	{
    		ItemFolder.setFileSize(stack, 0);
    	}
    }
}
