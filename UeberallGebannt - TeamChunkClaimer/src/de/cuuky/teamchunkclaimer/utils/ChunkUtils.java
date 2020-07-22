package de.cuuky.teamchunkclaimer.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;

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
