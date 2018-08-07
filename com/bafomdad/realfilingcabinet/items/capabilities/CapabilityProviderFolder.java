package com.bafomdad.realfilingcabinet.items.capabilities;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// Funwayguy: This is just in charge of the capability definitions and passing along the save/load methods.
public class CapabilityProviderFolder implements ICapabilitySerializable<NBTTagCompound>
{
    @CapabilityInject(CapabilityFolder.class)
    public static Capability<CapabilityFolder> FOLDER_CAP = null;
    public static final ResourceLocation FOLDER_ID = new ResourceLocation(RealFilingCabinet.MOD_ID, "folder");
    
    private final CapabilityFolder folder;
    
    public CapabilityProviderFolder(ItemStack stack)
    {
        folder = new CapabilityFolder(stack);
    }
    
    public static void register()
    {
        CapabilityManager.INSTANCE.register(CapabilityFolder.class, new Capability.IStorage<CapabilityFolder>()
        {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<CapabilityFolder> capability, CapabilityFolder instance, EnumFacing side)
            {
                return instance.serializeNBT();
            }
    
            @Override
            public void readNBT(Capability<CapabilityFolder> capability, CapabilityFolder instance, EnumFacing side, NBTBase nbt)
            {
                if(nbt instanceof NBTTagCompound)
                {
                    instance.deserializeNBT((NBTTagCompound)nbt);
                }
            }
        }, () -> new CapabilityFolder(ItemStack.EMPTY));
    }
    
    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == FOLDER_CAP;
    }
    
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return FOLDER_CAP.cast(folder);
    }
    
    @Override
    public NBTTagCompound serializeNBT()
    {
        return folder.serializeNBT();
    }
    
    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        folder.deserializeNBT(nbt);
    }
}
