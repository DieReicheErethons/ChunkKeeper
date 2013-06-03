package com.dre.chunkkeeper.listeners;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.dre.chunkkeeper.P;

public class WorldListener implements Listener{
	@EventHandler()
	public void onChunkUnload(ChunkUnloadEvent event){
		Chunk chunk = event.getChunk();
		
		if(P.p.containsChunk(chunk.getX(), chunk.getZ())){
			event.setCancelled(true);
			P.p.log("Chunk keeped in memory. Chunk: " + chunk.getX() + "," + chunk.getZ());
		}
	}
}
