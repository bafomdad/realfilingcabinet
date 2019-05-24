package com.bafomdad.realfilingcabinet.items.capabilities;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.helpers.enums.FolderType;

public class CapabilityFolder implements INBTSerializable<NBTTagCompound> {
	
	// The ItemStack instance you're working within (REFERENCE PURPOSES ONLY!)
	private final ItemStack rootStack;
	
	private String displayName = "";
	private Object contents;
	private long count = 0;
	private int remSize = 0;
	private int extractSize = 0;
	
	public CapabilityFolder(ItemStack rootStack) {
		
		this.rootStack = rootStack;
	}
	
	public void addTooltips(List<String> list) {
		
		if (isItemStack()) {
			ItemStack item = getItemStack();
			if (remSize != 0)
				list.add(TextHelper.format(count) + " " + item.getDisplayName() + " [" + remSize + " / " + item.getMaxDamage() + "]");
			else
				list.add(TextHelper.format(count) + " " + item.getDisplayName());
		}
		if (isFluidStack()) {
			FluidStack fluid = getFluidStack();
			list.add(count + "mb " + fluid.getLocalizedName());
		}
		if (isEntity()) {
			list.add(TextHelper.format(count) + " " + displayName);
		}
	}
	
	public Object extract(long amount, boolean sim) {
		
		Object obj = null;
		if (count <= 0) return obj;
		
		obj = ((IFolder)rootStack.getItem()).extractFromFolder(rootStack, amount, sim);
		return obj;
	}
	
	public Object insert(Object objects, boolean sim) {

		Object obj = ((IFolder)rootStack.getItem()).insertIntoFolder(rootStack, objects, sim);
		if (obj instanceof ItemStack)
			return (ItemStack)obj;

		return obj;
	}
	
	public boolean setContents(Object obj) {
		
		if (obj instanceof ItemStack) {
			ItemStack stack = ((ItemStack)obj);
			if (stack.isEmpty()) return false;
			
			this.displayName = stack.getDisplayName();
			this.contents = new ItemStack(stack.getItem(), 1, stack.getItemDamage());
			this.count = stack.getCount();
			((IFolder)rootStack.getItem()).setAdditionalData(rootStack, stack);
			
			return true;
		} else if (obj instanceof FluidStack) {
			FluidStack fluid = (FluidStack)obj;
			this.displayName = fluid.getLocalizedName();
			this.contents = fluid.copy();
			this.count = fluid.amount;
			
			return true;
		} else if (obj instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase)obj;
			this.displayName = EntityRegistry.getEntry(entity.getClass()).getName();
			this.contents = entity.getClass();
			this.count = 1;
			
			return true;
		} else if (obj == null) {
			return false;
		}
		return false;
	}
	
	public Object getContents() {
		
		if (rootStack.hasTagCompound() && rootStack.getTagCompound().hasKey("fileName"))
			deserializeNBT(new NBTTagCompound());
		
		else if (isFluidStack()) {
			return getFluidStack().copy();
		}
		return contents;
	}
	
	public void setTagCompound(NBTTagCompound tag) {
		
		((ItemStack)this.contents).setTagCompound(tag);
	}
	
	public long getCount() {
		
		return this.count;
	}
	
	public void setCount(long value) {
		
		this.count = value;
	}
	
	public int getExtractSize() {
		
		return this.extractSize;
	}
	
	public void setExtractSize(int size) {
		
		this.extractSize = size;
	}
	
	public int getRemainingDurability() {
		
		return this.remSize;
	}
	
	public int setRemainingDurability(int value) {
		
		this.remSize = value;
		return remSize;
	}
	
	public String getDisplayName() {
		
		return this.displayName;
	}
	
	public boolean hasItemTag() {
		
		return getItemStack().hasTagCompound();
	}
	
	public boolean isItemStack() {
		
		return this.contents instanceof ItemStack;
	}
	
	public boolean isFluidStack() {
		
		return this.contents instanceof FluidStack;
	}
	
	public boolean isBlock() {
		
		return this.contents instanceof IBlockState;
	}
	
	public boolean isEntity() {
		
		return this.contents instanceof Class && EntityLivingBase.class.isAssignableFrom((Class)this.contents);
	}
	
	public Class<EntityLivingBase> getEntityClass() {
		
		return isEntity() ? (Class<EntityLivingBase>)this.contents : null;
	}
	
	public EntityLivingBase getEntity(World world) {
		
		return isEntity() ? (EntityLivingBase)EntityList.newEntity((Class<EntityLivingBase>)this.contents, world) : null;
	}
	
	public ItemStack getItemStack() {
		
		return isItemStack() ? (ItemStack)this.contents : ItemStack.EMPTY;
	}
	
	public FluidStack getFluidStack() {
		
		return isFluidStack() ? (FluidStack)this.contents : null;
	}
	
	public IBlockState getBlock() {
		
		return isBlock() ? (IBlockState)this.contents : Blocks.AIR.getDefaultState();
	}

	@Override
	public NBTTagCompound serializeNBT() {

		NBTTagCompound nbt = new NBTTagCompound();
		if (getContents() == null) {
			nbt.setByte("folderType", (byte)0);
		} else if (isItemStack()) {
			nbt.setByte("folderType", (byte)1);
			nbt.setTag("objectData", getItemStack().writeToNBT(new NBTTagCompound()));
		} else if (isFluidStack()) {
			nbt.setByte("folderType", (byte)3);
			nbt.setTag("objectData", getFluidStack().writeToNBT(new NBTTagCompound()));
		} else if (isEntity()) {
			nbt.setByte("folderType", (byte)4);
			nbt.setString("objectData", EntityList.getKey((Class<EntityLivingBase>)this.contents).toString());
		}
		nbt.setLong("folderSize", this.count);
		nbt.setInteger("folderRem", this.remSize);
		nbt.setInteger("folderExtractSize", this.extractSize);
		
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {

		if (rootStack.hasTagCompound() && rootStack.getTagCompound().hasKey("fileName")) {
            // Legacy conversion. Ignores NBT parameter and reads directly off the stack
            // Notes:
            // - Removes old tags so this doesn't run again and overwrite the new values.
            // - Not using NBTUtils for this because it'd be unnecessary at this point
            NBTTagCompound rootTag = rootStack.getTagCompound();
            String fName = rootTag.getString("fileName");
            int fMeta = rootTag.getInteger("fileMeta");
            this.count = rootTag.getLong("fileSize");
            this.remSize = rootTag.getInteger("leftoverSize");
            NBTTagCompound tags = rootTag.getCompoundTag("itemTagCompound");
            
            rootTag.removeTag("fileName");
            rootTag.removeTag("fileMeta");
            rootTag.removeTag("fileSize");
            rootTag.removeTag("leftoverSize");
            rootTag.removeTag("itemTagCompound");
            
            this.displayName = fName; // Temporary cause I'm lazy and rather the NBT read/write update it
            
            // Now to figure out what it was without breaking it
            if(rootStack.getItemDamage() == FolderType.FLUID.ordinal()) {
                // Fluid blocks aren't really a thing players should normally get so I'm going to ignore it
                Fluid fluid = FluidRegistry.getFluid(fName);  
                if(fluid != null) {
                    this.contents = new FluidStack(fluid, 1);
                } else { // This is probably a vanilla fluid stored strangely
                    Block block = Block.REGISTRY.getObject(new ResourceLocation(fName));
                    if(block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                        this.contents = new FluidStack(FluidRegistry.WATER, 1);
                    } else if(block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
                        this.contents = new FluidStack(FluidRegistry.LAVA, 1);
                    }
                }
                return;
            }
            Item item = Item.getByNameOrId(fName);
            if (item != null && item != Items.AIR) {
                this.contents = new ItemStack(item, 1, fMeta);
                if(!tags.isEmpty())
                    ((ItemStack)contents).setTagCompound(tags);
                
                return;
            }
            Block block = Block.getBlockFromName(fName);
            if (block != null && block != Blocks.AIR)
                contents = block.getStateFromMeta(fMeta);
            
            Class<?> entity = EntityList.getClass(new ResourceLocation(fName));
            if (entity != null || EntityLivingBase.class.isAssignableFrom(entity))
                this.contents = entity;
            
            return;
		}
		else if (rootStack.hasTagCompound() && rootStack.getTagCompound().hasKey("folderCap")) {
			nbt = rootStack.getTagCompound().getCompoundTag("folderCap");
		}
		byte type = nbt.getByte("folderType");
		switch (type) {
			case 0: { // Empty. Reset everything and ignore reading the rest
				this.displayName = "";
				this.contents = null;
				this.count = 0;
				this.remSize = 0;
				break;
			}
			case 1: { // ItemStack
				this.contents = new ItemStack(nbt.getCompoundTag("objectData"));
				this.displayName = ((ItemStack)this.contents).getDisplayName();
				break;
			}
			case 3: { // FluidStack
				this.contents = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("objectData"));
				if (this.contents != null)
					this.displayName = ((FluidStack)this.contents).getLocalizedName();
				break;
			}
			case 4: { // Entity
				this.contents = EntityList.getClass(new ResourceLocation(nbt.getString("objectData")));
				if (this.contents != null)
					this.displayName = EntityRegistry.getEntry(getEntityClass()).getName();
			}
		}
		this.count = nbt.getLong("folderSize");
		this.remSize = nbt.getInteger("folderRem");
		this.extractSize = (nbt.hasKey("folderExtractSize")) ? nbt.getInteger("folderExtractSize") : 0;
	}
}
