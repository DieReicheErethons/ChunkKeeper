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
		float timeMs = (float) (executionTime / 1000000.0);

		if (showLoadTime) {
			p.log("chunkload took " + timeMs + "ms");
		}

		if (chunk != null) {

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
					if (maxChunks == 0 || p.persistingChunks.size() < maxChunks) {
						p.persistingChunks.add(chunk);
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