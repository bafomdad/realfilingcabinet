package com.bafomdad.realfilingcabinet.items.capabilities;

import com.bafomdad.realfilingcabinet.NewConfigRFC.ConfigRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.lwjgl.input.Keyboard;

import java.util.List;

// Funwayguy: Your new capability based class for dealing with folder stuff. Unique to each item rootStack so feel free to add/remove stuf.
public class CapabilityFolder implements INBTSerializable<NBTTagCompound>
{
    // The ItemStack instance you're working within (REFERENCE PURPOSES ONLY!)
    private final ItemStack rootStack;
    
    private String displayName = "";
    private Object contents;
    private long count = 0;
    private int remSize = 0;
    
    public CapabilityFolder(ItemStack rootStack)
    {
        this.rootStack = rootStack;
    }
    
    public void addTooltips(World world, List<String> list, ITooltipFlag tooltipFlag)
    {
        if(rootStack.getItemDamage() == ItemFolder.FolderType.FLUID.ordinal() && isFluidStack())
        {
            FluidStack fluid = getFluidStack();
            list.add(count + "mb " + fluid.getLocalizedName());
            list.add(NBTUtils.getBoolean(rootStack, StringLibs.RFC_PLACEMODE, false) ? TextFormatting.GREEN + TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".placemode.on") : TextFormatting.RED + TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".placemode.off"));
        } else if(rootStack.getItemDamage() == ItemFolder.FolderType.MOB.ordinal() && isEntity())
        {
            list.add(count + " " + displayName);
            if(!ConfigRFC.mobUpgrade)
            {
                list.add(TextHelper.localize("message." + RealFilingCabinet.MOD_ID + ".disabled"));
            }
        } else if(isItemStack())
        {
            ItemStack item = getItemStack();
    
            list.add((Keyboard.isKeyDown(42)) || (Keyboard.isKeyDown(54)) ? count + " " + item.getDisplayName() : TextHelper.format(count) + " " + item.getDisplayName());
    
            if(rootStack.getItemDamage() == ItemFolder.FolderType.DURA.ordinal())
            {
                list.add("Durability: " + remSize + " / " + item.getItemDamage());
                list.add(NBTUtils.getBoolean(rootStack, StringLibs.RFC_IGNORENBT, false) ? TextFormatting.GREEN + TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".ignorenbt.true") : TextFormatting.RED + TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".ignorenbt.false"));
            }
        }
    }
    
    public ItemStack extractItems(long amount, boolean sim)
    {
        if(!isItemStack() || count <= 0)
        {
            return ItemStack.EMPTY;
        }
        
        ItemStack items = getItemStack().copy();
        items.setCount((int)Math.min(count, items.getMaxStackSize()));
        
        if(!sim)
        {
            count -= items.getCount();
        }
        
        return items;
    }
    
    public ItemStack insertItems(ItemStack items, boolean sim)
    {
        if(!isItemStack() || count <= 0)
        {
            return items;
        }
        
        ItemStack stack = getItemStack();
        
        if(!ItemStack.areItemsEqual(stack, items))
        {
            return items;
        }
        
        items.copy();
        items.setCount(0); // TODO: Add capcity/transfer limits here
        
        if(!sim)
        {
            count += items.getCount();
        }
        
        return items; // Return left overs that didn't fit
    }
    
    public boolean setContents(Object obj)
    {
        if(obj instanceof EntityLivingBase)
        {
            EntityLivingBase entity = (EntityLivingBase)obj;
            
            if(entity instanceof EntityPlayer || (entity instanceof IEntityOwnable && ((IEntityOwnable)entity).getOwnerId() != null) || !entity.isNonBoss() || !(entity instanceof EntityZombie || !entity.isChild()))
            {
                return false;
            }
            
            this.displayName = EntityRegistry.getEntry(entity.getClass()).getName();
            this.contents = entity.getClass();
            this.count = 1;
            return true;
        } else if(obj instanceof ItemStack)
        {
            ItemStack stack = ((ItemStack)obj).copy();
            this.displayName = stack.getDisplayName();
            this.contents = stack.copy();
            this.count = stack.getCount();
            stack.setCount(1);
            
            if(rootStack.getItemDamage() == ItemFolder.FolderType.DURA.ordinal())
            {
                this.remSize = stack.getItemDamage();
            }
            
            if(rootStack.getItemDamage() != ItemFolder.FolderType.NBT.ordinal())
            {
                // TODO: Test NBT folder
                stack.setTagCompound(null); // Delete the tags if this folder doesn't support it
            }
            
            return true;
        } else if(obj instanceof FluidStack)
        {
            this.displayName = ((FluidStack)obj).getLocalizedName();
            this.contents = ((FluidStack)obj).copy();
            this.count = ((FluidStack)obj).amount;
            return true;
        } else if(obj instanceof IBlockState)
        {
            this.displayName = ((IBlockState)obj).getBlock().getLocalizedName();
            this.contents = obj;
            this.count = 1;
            return true;
        } else if(obj == null)
        {
            this.displayName = "";
            this.contents = null;
            this.count = 0;
            this.remSize = 0;
            return true;
        }
        
        return false;
    }
    
    public String getDisplayName()
    {
        return this.displayName;
    }
    
    @Deprecated // Really shouldn't be using this anymore
    public String getContentID()
    {
        if(isItemStack())
        {
            return getItemStack().getItem().getRegistryName().toString();
        } else if(isBlock())
        {
            return getBlock().getBlock().getRegistryName().toString();
        } else if(isFluidStack())
        {
            return FluidRegistry.getFluidName(getFluidStack());
        } else if(isEntity())
        {
            return EntityRegistry.getEntry((Class<EntityLivingBase>)this.contents).getRegistryName().toString();
        }
        
        return "";
    }
    
    public Object getContents()
    {
        if(rootStack.hasTagCompound() && rootStack.getTagCompound().hasKey("fileName"))
        {
            deserializeNBT(new NBTTagCompound());
        }
        
        if(isItemStack())
        {
            ItemStack item = getItemStack().copy();
            item.setCount(1);
        } else if(isFluidStack())
        {
            return getFluidStack().copy();
        }
        
        return contents;
    }
    
    public boolean isItemStack()
    {
        return this.contents instanceof ItemStack;
    }
    
    public long getCount()
    {
        return this.count;
    }
    
    public void setCount(long value)
    {
        this.count = value;
    }
    
    public int getRemaining()
    {
        return this.remSize;
    }
    
    public void setRemaining(int value)
    {
        this.remSize = value;
    }
    
    public ItemStack getItemStack()
    {
        return isItemStack() ? (ItemStack)this.contents : ItemStack.EMPTY;
    }
    
    public boolean isFluidStack()
    {
        return this.contents instanceof FluidStack;
    }
    
    public FluidStack getFluidStack()
    {
        return isFluidStack() ? (FluidStack)this.contents : null;
    }
    
    public boolean isBlock()
    {
        return this.contents instanceof IBlockState;
    }
    
    public IBlockState getBlock()
    {
        return isBlock() ? (IBlockState)this.contents : Blocks.AIR.getDefaultState();
    }
    
    public boolean isEntity()
    {
        return this.contents instanceof Class && EntityLivingBase.class.isAssignableFrom((Class)this.contents);
    }
    
    public Class<EntityLivingBase> getEntityClass()
    {
        return isEntity() ? (Class<EntityLivingBase>)this.contents : null;
    }
    
    public EntityLivingBase getEntity(World world)
    {
        return isEntity() ? (EntityLivingBase)EntityList.newEntity((Class<EntityLivingBase>)this.contents, world) : null;
    }
    
    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        
        if(getContents() == null) // Fail fast if there's nothing in here
        {
            nbt.setByte("folderType", (byte)0);
        } else if(isItemStack())
        {
            ItemStack item = getItemStack();
            
            nbt.setByte("folderType", (byte)1);
            nbt.setTag("objectData", item.writeToNBT(new NBTTagCompound()));
        } else if(isBlock())
        {
            IBlockState blockState = getBlock();
            
            nbt.setByte("folderType", (byte)2);
            NBTTagCompound dataTag = new NBTTagCompound();
            dataTag.setString("blockId", blockState.getBlock().getRegistryName().toString());
            dataTag.setInteger("blockMeta", blockState.getBlock().getMetaFromState(blockState));
            nbt.setTag("objectData", dataTag);
        } else if(isFluidStack())
        {
            FluidStack fluid = getFluidStack();
            
            nbt.setByte("folderType", (byte)3);
            nbt.setTag("objectData", fluid.writeToNBT(new NBTTagCompound()));
        } else if(isEntity())
        {
            nbt.setByte("folderType", (byte)4);
            nbt.setString("objectData", EntityList.getKey((Class<EntityLivingBase>)this.contents).toString());
        }
        
        nbt.setLong("folderSize", this.count);
        nbt.setInteger("folderRem", this.remSize);
        
        return nbt;
    }
    
    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        if(rootStack.hasTagCompound() && rootStack.getTagCompound().hasKey("fileName"))
        {
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
            if(rootStack.getItemDamage() == ItemFolder.FolderType.FLUID.ordinal())
            {
                // Fluid blocks aren't really a thing players should normally get so I'm going to going to ignore it
                Fluid fluid = FluidRegistry.getFluid(fName);
                
                if(fluid != null)
                {
                    this.contents = new FluidStack(fluid, 1);
                } else // This is probably a vanilla fluid stored strangely
                {
                    Block block = Block.REGISTRY.getObject(new ResourceLocation(fName));
                    if(block == Blocks.WATER || block == Blocks.FLOWING_WATER)
                    {
                        this.contents = new FluidStack(FluidRegistry.WATER, 1);
                    } else if(block == Blocks.LAVA || block == Blocks.FLOWING_LAVA)
                    {
                        this.contents = new FluidStack(FluidRegistry.LAVA, 1);
                    }
                }
                
                return;
            }
            
            Item item = Item.getByNameOrId(fName);
            
            if(item != null && item != Items.AIR)
            {
                this.contents = new ItemStack(item, 1, fMeta);
                
                if(!tags.isEmpty())
                {
                    ((ItemStack)contents).setTagCompound(tags);
                }
                
                return;
            }
            
            Block block = Block.getBlockFromName(fName);
            
            if(block != null && block != Blocks.AIR)
            {
                contents = block.getStateFromMeta(fMeta);
            }
            
            Class<?> entity = EntityList.getClass(new ResourceLocation(fName));
            
            if(entity != null || EntityLivingBase.class.isAssignableFrom(entity))
            {
                this.contents = entity;
            }
            
            return;
        } else if(rootStack.hasTagCompound() && rootStack.getTagCompound().hasKey("folderCap"))
        {
            nbt = rootStack.getTagCompound().getCompoundTag("folderCap");
            rootStack.getTagCompound().removeTag("folderCap"); // This might be a bad idea but it works for now
        }
        
        // Back to normal read/write
        
        byte type = nbt.getByte("folderType");
        
        switch(type)
        {
            case 0: // Empty. Reset everything ignore reading the rest
            {
                this.displayName = "";
                this.contents = null;
                this.count = 0;
                this.remSize = 0;
                break;
            }
            case 1: // ItemStack
            {
                this.contents = new ItemStack(nbt.getCompoundTag("objectData"));
                ((ItemStack)this.contents).setCount(1);
                this.displayName = ((ItemStack)this.contents).getDisplayName();
                break;
            }
            case 2: // BlockState
            {
                NBTTagCompound tags = nbt.getCompoundTag("objectData");
                Block block = Block.REGISTRY.getObject(new ResourceLocation(tags.getString("blockId")));
                if(block != Blocks.AIR)
                {
                    this.contents = block.getStateFromMeta(tags.getInteger("blockMeta"));
                    this.displayName = block.getLocalizedName();
                } else
                {
                    this.contents = null;
                    this.displayName = "";
                }
                break;
            }
            case 3: // FluidStac
            {
                this.contents = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("objectData"));
                
                if(this.contents != null)
                {
                    this.displayName = ((FluidStack)this.contents).getLocalizedName();
                }
                break;
            }
            case 4: // Entity
            {
                this.contents = EntityList.getClass(new ResourceLocation(nbt.getString("objectData")));
                if(this.contents != null)
                {
                    this.displayName = EntityRegistry.getEntry(getEntityClass()).getName();
                }
            }
        }
        
        this.count = nbt.getLong("folderSize");
        this.remSize = nbt.getInteger("folderRem");
    }
}
