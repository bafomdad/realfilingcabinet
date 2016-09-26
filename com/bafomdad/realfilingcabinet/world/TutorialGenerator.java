package com.bafomdad.realfilingcabinet.world;

import java.util.Random;

import com.bafomdad.realfilingcabinet.LogRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.common.IWorldGenerator;

public class TutorialGenerator implements IWorldGenerator {

	public static final ResourceLocation TUTORIAL_STRUCTURE = new ResourceLocation(RealFilingCabinet.MOD_ID, "TutorialWorld");
	static final String worldName = RealFilingCabinet.MOD_NAME;

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		
		if (world.getSaveHandler().getWorldDirectory().getName().equals(worldName) && world.getWorldType() == WorldType.FLAT)
		{
			if (!(world instanceof WorldServer))
				return;
			
			if (RFCWorldInfo.getInstance().hasGenerated())
				return;
			
			WorldServer serverworld = (WorldServer)world;
			BlockPos playerspawn = serverworld.provider.getSpawnPoint();
			if (!playerspawn.equals(new BlockPos(0, 0, 0)))
				this.generateTutorial(serverworld, playerspawn);
		}
	}
	
	public void generateTutorial(WorldServer world, BlockPos pos) {
		
		MinecraftServer server = world.getMinecraftServer();
		Template template = world.getStructureTemplateManager().func_189942_b(server, TUTORIAL_STRUCTURE);
		PlacementSettings settings = new PlacementSettings();
		settings.setRotation(Rotation.NONE);
		
		BlockPos newPos = pos.add(-19, -1, -5);
		template.addBlocksToWorld(world, newPos, settings);
		RFCWorldInfo.getInstance().setStructureGenerated(true);
		
		LogRFC.debug("Structure generated at: " + pos.toString() + " in world: " + world.getSaveHandler().getWorldDirectory().getName());
	}
}
