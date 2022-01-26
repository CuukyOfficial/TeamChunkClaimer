package de.cuuky.teamchunkclaimer.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.cuuky.cfw.version.types.Materials;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Biome;

public class ChunkUtils {

	public static List<Chunk> getChunksAround(Chunk chunk) {
		List<Chunk> chunks = new ArrayList<Chunk>();
		int chunkX = chunk.getX(), chunkZ = chunk.getZ();
		chunks.add(chunk.getWorld().getChunkAt(chunkX - 1, chunkZ));
		chunks.add(chunk.getWorld().getChunkAt(chunkX + 1, chunkZ));
		chunks.add(chunk.getWorld().getChunkAt(chunkX, chunkZ + 1));
		chunks.add(chunk.getWorld().getChunkAt(chunkX, chunkZ - 1));
		return chunks;
	}

}