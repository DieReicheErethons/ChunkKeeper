package com.dre.chunkkeeper.listeners;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import com.bergerkiller.bukkit.common.internal.TimingsListener;
import com.dre.chunkkeeper.P;

public class NLETimingsListener implements TimingsListener {
	public static final NLETimingsListener INSTANCE = new NLETimingsListener();
	


	/* Not used methods */
	@Override
	public void onChunkGenerate(Chunk arg0, long arg1) {
	}

	@Override
	public void onChunkPopulate(Chunk arg0, BlockPopulator arg1, long arg2) {
	}

	@Override
	public void onChunkUnloading(World arg0, long arg1) {
		P.p.log("Wuaka: " + arg1);
	}

	@Override
	public void onNextTicked(Runnable arg0, long arg1) {
	}

	@Override
	public void onChunkLoad(Chunk arg0, long arg1) {
		P.p.log("Wuaka: ");
		/*if (time > P.p.maxTime) {
			P.p.addChunk(chunk.getX(), chunk.getZ());
			P.p.log("Chunk took to long, added to the list. Chunk: " + chunk.getX() + "," + chunk.getZ());
		} else {
			P.p.removeChunk(chunk.getX(), chunk.getZ());
		}*/
	}
}
