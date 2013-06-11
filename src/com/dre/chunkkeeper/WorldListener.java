package com.dre.chunkkeeper;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.ChunkUnloadEvent;

public class WorldListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChunkUnload(ChunkUnloadEvent event) {
		Chunk chunk = event.getChunk();
		if (chunk != null) {
			if (P.p.persistingChunks.contains(chunk)) {
				event.setCancelled(true);
			}
		}
	}

}