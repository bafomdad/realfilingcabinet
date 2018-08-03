package com.bafomdad.realfilingcabinet.network;

import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemSuitcase;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketMouse implements IMessage {
	
	private boolean next;
	
	public PacketMouse() {}
	
	public PacketMouse(boolean next) {
		
		this.next = next;
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		next = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {

		buf.writeBoolean(next);
	}
	
	public static class Handler implements IMessageHandler<PacketMouse, IMessage> {

		@Override
		public IMessage onMessage(PacketMouse message, MessageContext ctx) {

			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				
				@Override
				public void run() {
					
					handle(message, ctx);
				}
			});
			return null;
		}
		
		private void handle(PacketMouse message, MessageContext ctx) {
			
			EntityPlayerMP player = ctx.getServerHandler().player;
			ItemStack stack = player.getHeldItemMainhand();
			if (!stack.isEmpty() && stack.getItem() == RFCItems.suitcase) {
				((ItemSuitcase)stack.getItem()).cycleIndex(stack, message.next);
			}
		}
	}
}
