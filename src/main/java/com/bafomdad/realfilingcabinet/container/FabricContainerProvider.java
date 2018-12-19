package com.bafomdad.realfilingcabinet.container;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;

/**
 * Created by bafomdad on 12/18/2018.
 */
public interface FabricContainerProvider extends ContainerProvider {

    @Override
    default String getContainerId() {

        return getContainerIdentifier().toString();
    }

    @Override
    default boolean hasCustomName() {

        return false;
    }

    @Override
    default TextComponent getName() {

        return new TranslatableTextComponent(getContainerIdentifier() + ".name");
    }

    @Override
    default Container createContainer(PlayerInventory playerInv, PlayerEntity player) {

        return createContainer(player);
    }

    Container createContainer(PlayerEntity player);

    Identifier getContainerIdentifier();
}
