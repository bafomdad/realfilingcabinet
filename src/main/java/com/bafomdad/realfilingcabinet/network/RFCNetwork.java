package com.bafomdad.realfilingcabinet.network;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.entity.FilingCabinetEntity;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.networking.CustomPayloadPacketRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

/**
 * Created by bafomdad on 12/12/2018.
 */
public class RFCNetwork {

    public static final Identifier SYNC_CABINET = new Identifier(RealFilingCabinet.MODID, "sync_cabinet");
    public static final Identifier REQUEST_CABINET_SYNC = new Identifier(RealFilingCabinet.MODID, "request_cabinet_sync");

    public static void init() {

        CustomPayloadPacketRegistry.CLIENT.register(SYNC_CABINET, (packetContext, packetByteBuf) -> {
            BlockPos pos = packetByteBuf.readBlockPos();
            boolean isOpen = packetByteBuf.readBoolean();
            if (packetContext.getPlayer() != null && packetContext.getPlayer().getEntityWorld() != null) {
                BlockEntity be = packetContext.getPlayer().getEntityWorld().getBlockEntity(pos);
                if (be instanceof FilingCabinetEntity) {
                    ((FilingCabinetEntity)be).isOpen = isOpen;
                }
            }
        });
        CustomPayloadPacketRegistry.SERVER.register(REQUEST_CABINET_SYNC, (packetContext, packetByteBuf) -> {
            BlockPos pos = packetByteBuf.readBlockPos();
            BlockEntity be = packetContext.getPlayer().getEntityWorld().getBlockEntity(pos);
            if (be instanceof FilingCabinetEntity)
                syncCabinet((FilingCabinetEntity)be, (ServerPlayerEntity)packetContext.getPlayer());
        });
    }

    @Environment(EnvType.SERVER)
    public static void syncCabinet(FilingCabinetEntity cabinet, ServerPlayerEntity player) {

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(cabinet.getPos());
        buf.writeBoolean(cabinet.isOpen);
        player.networkHandler.sendPacket(new CustomPayloadClientPacket(SYNC_CABINET, buf));
    }

    @Environment(EnvType.CLIENT)
    public static void requestCabinetSync(FilingCabinetEntity cabinet) {

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(cabinet.getPos());
        MinecraftClient.getInstance().getNetworkHandler().getClientConnection().sendPacket(new CustomPayloadClientPacket(REQUEST_CABINET_SYNC, buf));
    }
}
