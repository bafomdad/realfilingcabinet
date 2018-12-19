package com.bafomdad.realfilingcabinet.init;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.container.FabricContainerProvider;
import com.bafomdad.realfilingcabinet.gui.GuiCabinet;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.networking.CustomPayloadPacketRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bafomdad on 12/19/2018.
 */
public class RFCContainer {

    private static final Identifier OPEN_CONTAINER = new Identifier(RealFilingCabinet.MODID, "open_container");
    private static final Map<Identifier, Pair<ContainerSupplier<Container>, ContainerSupplier<ContainerGui>>> containerMap = new HashMap();

    public static final ContainerSupplier<Container> DEFAULT_CONTAINER_SUPPLIER = (player, pos) -> {
        BlockEntity be = player.world.getBlockEntity(pos);
        if (be instanceof FabricContainerProvider) {
            return ((FabricContainerProvider)be).createContainer(player.inventory, player);
        }
        return null;
    };

    public static void init() {

        CustomPayloadPacketRegistry.CLIENT.register(OPEN_CONTAINER, (ctx, buf) -> {
            Identifier identifier = new Identifier(buf.readString(64));
            BlockPos pos = buf.readBlockPos();
            openGui(identifier, (ClientPlayerEntity)ctx.getPlayer(), pos);
        });
        addContainerMapping(new Identifier(RealFilingCabinet.MODID, "cabinet"), DEFAULT_CONTAINER_SUPPLIER, (player, pos) -> new GuiCabinet((FabricContainerProvider)player.world.getBlockEntity(pos), player));
    }

    public static void openGui(FabricContainerProvider provider, BlockPos pos, ServerPlayerEntity player) {

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        Identifier identifier = provider.getContainerIdentifier();
        if (!containerMap.containsKey(identifier)) {
            throw new RuntimeException("No gui found for " + identifier);
        }
        buf.writeString(identifier.toString());
        buf.writeBlockPos(pos);
        player.networkHandler.sendPacket(new CustomPayloadClientPacket(OPEN_CONTAINER, buf));

        player.container = containerMap.get(identifier).getKey().get(player, pos);
        player.container.addListener(player);
    }

    private static void openGui(Identifier container, ClientPlayerEntity player, BlockPos pos) {

        Pair<ContainerSupplier<Container>, ContainerSupplier<ContainerGui>> pair = containerMap.get(container);
        if (pair == null)
            throw new RuntimeException("No container found for " + container);
        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().openGui(pair.getRight().get(player, pos)));
    }

    public static void addContainerMapping(Identifier identifier, ContainerSupplier<Container> containerSupplier, ContainerSupplier<ContainerGui> guiSupplier) {

        Validate.isTrue(!containerMap.containsKey(identifier));
        containerMap.put(identifier, Pair.of(containerSupplier, guiSupplier));
    }

    public interface ContainerSupplier<T> {

        T get(PlayerEntity player, BlockPos pos);
    }
}
