package com.dre.chunkkeeper;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import com.bergerkiller.bukkit.common.internal.TimingsListener;


public class ChunkTimingsListener implements TimingsListener {
	public static final ChunkTimingsListener INSTANCE = new ChunkTimingsListener();
	public static P p = P.p;
	public static float maxTime = (float) p.getConfig().getDouble("maxLoadTime", 0);
	public static int maxChunks = p.getConfig().getInt("maxChunks", 0);
	public static boolean showLoadTime = p.getConfig().getBoolean("showAllTimes", false);

	@Override
	public void onChunkLoad(Chunk chunk, long executionTime) {
		if (chunk != null) {
			String worldName = chunk.getWorld().getName();
			if (worldName.startsWith("DXL_")) {
				return;
			}
			if (p.excludedWorlds != null) {
				if (!p.excludedWorlds.isEmpty()) {
					for (String excludedWorld : p.excludedWorlds) {
						if (worldName.equalsIgnoreCase(excludedWorld)) {
							return;
						}
					}
				}
			}

		float timeMs = (float) (executionTime / 1000000.0);

		if (showLoadTime) {
			p.log("chunkload took " + timeMs + "ms");
		}

			if (p.doAverage) {
				p.amount++;
				p.totalTime += executionTime;

				if (executionTime > p.getAverage() * 2) {
					p.overAverage += 1;
				}

				if (p.peakTime < timeMs) {
					p.peakTime = timeMs;
				}

			}

			if (maxTime != 0) {
				if (executionTime > maxTime * 1000000) {
					if (maxChunks == 0 || p.persistentChunks.size() < maxChunks) {

						if (!p.addPersistingChunk(chunk)) {
							p.wrongChunkLoads++;
							p.log("Loaded a Chunk that should already be loaded!");
						}
						
					} else {
						p.log("Maximum amount (" + maxChunks + ") of persisting Chunks reached!");
						p.removeTimingsListener();
					}
				}
			}

		}
	}

	public void onNextTicked(Runnable runnable, long executionTime) {}
	public void onChunkGenerate(Chunk chunk, long executionTime) {}
	public void onChunkUnloading(World world, long executionTime) {}
	public void onChunkPopulate(Chunk chunk, BlockPopulator populator, long executionTime) {}

}