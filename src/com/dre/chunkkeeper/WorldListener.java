package com.dre.chunkkeeper;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.ChunkUnloadEvent;

public class WorldListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onChunkUnload(ChunkUnloadEvent event) {
		Chunk chunk = event.getChunk();
		if (chunk != null) {
			if (P.p.isPersistingChunk(chunk)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void chunkUnloadMonitor(ChunkUnloadEvent event) {
		Chunk chunk = event.getChunk();
		if (chunk != null) {
			if (!event.isCancelled()) {
				if (P.p.isPersistingChunk(chunk)) {
					P.p.log("Failed to cancel Chunk Unload!");
					P.p.cancelFails++;
				}
			}
		}
	}

}