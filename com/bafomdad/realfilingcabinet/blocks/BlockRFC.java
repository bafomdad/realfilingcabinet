package com.bafomdad.realfilingcabinet.blocks;

import java.util.ArrayList;

import com.bafomdad.realfilingcabinet.core.UpgradeHandler;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRFC extends BlockContainer {

	public BlockRFC(Material material) {
		
		super(material);
		setHardness(1.5F);
		setResistance(20.0F);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void rightClickBlock(PlayerInteractEvent event) {
		
		if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z) == this)
		{
			ItemStack stack = event.entityPlayer.getCurrentEquippedItem();
			TileEntity tile = event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z);
			if (tile != null && tile instanceof TileEntityRFC)
			{
				if (!((TileEntityRFC)tile).isOpen)
					return;
				
				if (stack == null && event.entityPlayer.isSneaking())
				{
					if (!((TileEntityRFC)tile).isEnder)
					{
						for (int i = ((TileEntityRFC)tile).getSizeInventory() - 2; i >= 0; i--)
						{
							ItemStack folder = ((TileEntityRFC)tile).getStackInSlot(i);
							
							if (folder != null)
							{
								((TileEntityRFC)tile).setInventorySlotContents(i, null);
								event.entityPlayer.setCurrentItemOrArmor(0, folder);
								break;
							}
						}
					}
					else
					{
						NBTTagCompound playertag = event.entityPlayer.getEntityData();
						if (playertag.hasKey(ItemFolder.TAG_SLOTINDEX))
						{
							int index = playertag.getInteger(ItemFolder.TAG_SLOTINDEX);
							ItemStack folder = ((TileEntityRFC)tile).getStackInSlot(index);
							
							if (folder == null) {
								if (findNextFolder((TileEntityRFC)tile, playertag.getInteger(ItemFolder.TAG_SLOTINDEX)) == -1)
								{
									folder = ((TileEntityRFC)tile).getStackInSlot(0);
									playertag.setInteger(ItemFolder.TAG_SLOTINDEX, 0);
									index = 0;
								}
								index = findNextFolder((TileEntityRFC)tile, playertag.getInteger(ItemFolder.TAG_SLOTINDEX));
								folder = ((TileEntityRFC)tile).getStackInSlot(index);
								playertag.setInteger(ItemFolder.TAG_SLOTINDEX, index);
							}
							ItemStack newFolder = ItemFolder.createEnderFolder((TileEntityRFC)tile, event.entityPlayer, folder);
							event.entityPlayer.setCurrentItemOrArmor(0, newFolder);
							playertag.setInteger(ItemFolder.TAG_SLOTINDEX, index += 1);
						}
						else
						{
							for (int i = 0; i < ((TileEntityRFC)tile).getSizeInventory() - 2; i++)
							{
								ItemStack folder = ((TileEntityRFC)tile).getStackInSlot(i);
								if (folder != null)
								{
									ItemStack newFolder = ItemFolder.createEnderFolder((TileEntityRFC)tile, event.entityPlayer, folder);
									event.entityPlayer.setCurrentItemOrArmor(i, newFolder);
									break;
								}
							}
						}
					}
				}
			}
		}
	}
	
	private int findNextFolder(TileEntityRFC tile, int slot) {
		
		ItemStack folder = null;
		
		for (int i = slot; i < tile.getSizeInventory() - 2; i++) {
			ItemStack stack = tile.getStackInSlot(i);
			if (stack != null)
			{
				return i;
			}
		}
		return -1;
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess block, int x, int y, int z, int side) {
	
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		
		return false;
	}
	
	@Override
	public int getRenderType() {
		
		return -1;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		
		return false;
	}
	
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
	
		if (!world.isRemote && !player.capabilities.isCreativeMode)
		{
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			if (tileEntity != null && tileEntity instanceof TileEntityRFC)
				((TileEntityRFC)tileEntity).leftClick(player);
		}
	}
	
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {		
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity != null && tileEntity instanceof TileEntityRFC)
			((TileEntityRFC)tileEntity).rightClick(player);
    		
        return true;
    }
	
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		
		int l = MathHelper.floor_double(entity.rotationYaw * 4.0f / 360.0f + 0.5d) & 0x3;
		
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof TileEntityRFC)
		{
			if (l == 0)
				// SOUTH
				((TileEntityRFC)tile).facing = 2;
			if (l == 1)
				// WEST
				((TileEntityRFC)tile).facing = 3;
			if (l == 2)
				// NORTH
				((TileEntityRFC)tile).facing = 0;
			if (l == 3)
				// EAST
				((TileEntityRFC)tile).facing = 1;
			
			if (stack.hasTagCompound())
				((TileEntityRFC)tile).readInv(stack.getTagCompound());
		}
	}
	
	public void onBlockHarvested(World world, int x, int y, int z, int metadata, EntityPlayer player) {
		
		TileEntity te = world.getTileEntity(x, y, z);
		
		if (!world.isRemote)
		{
			if (te instanceof TileEntityRFC)
			{
				ItemStack stack = new ItemStack(this);
				NBTTagCompound tag = new NBTTagCompound();
				((TileEntityRFC)te).writeInv(tag, true);
				if (!tag.hasNoTags())
				{
					stack.setTagCompound(tag);
					world.spawnEntityInWorld(new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, stack));
				}
				if (((TileEntityRFC)te).isAutoCraft || ((TileEntityRFC)te).isEnder) {
					UpgradeHandler.dropUpgrade((TileEntityRFC)te);
				}
			}
		}
	}
	
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		
		return ret;
	}
	
	public TileEntity createNewTileEntity(World world, int meta) {

		return new TileEntityRFC();
	}
}
