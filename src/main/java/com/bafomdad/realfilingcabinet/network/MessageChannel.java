package com.bafomdad.realfilingcabinet.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.networking.CustomPayloadPacketRegistry;
import net.fabricmc.fabric.networking.PacketContext;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.server.network.packet.CustomPayloadServerPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Created by bafomdad on 12/15/2018.
 */
public final class MessageChannel<T> {

    private final Identifier id;
    private final BiConsumer<T, PacketByteBuf> serializer;
    private final Function<PacketByteBuf, T> deserializer;

    public MessageChannel(Identifier id, BiConsumer<T, PacketByteBuf> serializer, Function<PacketByteBuf, T> deserializer) {

        this.id = id;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    public void registerHandler(CustomPayloadPacketRegistry registry, BiConsumer<PacketContext, T> handler) {

        registry.register(id, (ctx, buf) -> {
            T msg = deserializer.apply(buf);
            ctx.getTaskQueue().execute(() -> handler.accept(ctx, msg));
        });
    }

    public CustomPayloadClientPacket createClientboundPacket(T msg) {

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        serializer.accept(msg, buf);
        return new CustomPayloadClientPacket(id, buf);
    }

    public CustomPayloadServerPacket createServerboundPacket(T msg) {

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        serializer.accept(msg, buf);
        return new CustomPayloadServerPacket(id, buf);
    }
}
