package com.bafomdad.realfilingcabinet.proxies;

import thaumcraft.api.aspects.Aspect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.bafomdad.realfilingcabinet.NewConfigRFC.ConfigRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.events.EventHandlerClient;
import com.bafomdad.realfilingcabinet.gui.GuiFileList;
import com.bafomdad.realfilingcabinet.helpers.ResourceUpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCBlocks;
import com.bafomdad.realfilingcabinet.init.RFCEntities;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.integration.BotaniaRFC;
import com.bafomdad.realfilingcabinet.items.ItemAspectFolder;
import com.bafomdad.realfilingcabinet.network.RFCPacketHandler;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		
		super.preInit(event);
		MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
	}

	@Override
	public void initAllModels() {
		
		ResourceUpgradeHelper.registerUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/endercabinet.png"), StringLibs.TAG_ENDER);
		ResourceUpgradeHelper.registerUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/craftingcabinet.png"), StringLibs.TAG_CRAFT);
		ResourceUpgradeHelper.registerUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/oredictcabinet.png"), StringLibs.TAG_OREDICT);
		ResourceUpgradeHelper.registerUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/mobcabinet.png"), StringLibs.TAG_MOB);
		ResourceUpgradeHelper.registerUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/fluidcabinet.png"), StringLibs.TAG_FLUID);
		ResourceUpgradeHelper.registerUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/smeltingcabinet.png"), StringLibs.TAG_SMELT);
		
		RFCBlocks.initModels();
		RFCEntities.initModels();
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
		MinecraftForge.EVENT_BUS.register(new GuiFileList(Minecraft.getMinecraft()));
	}
	
	@Override
	public void registerColors() {
		
		if (RealFilingCabinet.tcLoaded && ConfigRFC.tcIntegration) {
			ItemColors ic = Minecraft.getMinecraft().getItemColors();
			ic.registerItemColorHandler(new IItemColor() {
				
				@Override
				public int colorMultiplier(ItemStack stack, int tintIndex) {
					
					if (!stack.isEmpty() && stack.getItem() == RFCItems.aspectFolder) {
						if (tintIndex == 1) {
							Aspect asp = ItemAspectFolder.getAspectFromFolder(stack);
							if (asp != null)
								return asp.getColor();
						}
					}
					return 0xffffff;
				}
			}, RFCItems.aspectFolder);
		}
	}
}
