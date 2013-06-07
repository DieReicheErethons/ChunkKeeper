package com.dre.chunkkeeper;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.Chunk;
import org.bukkit.World;

public class CommandListener implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		P p = P.p;
		String cmd = "loaded";
		if (args.length > 0) {
			cmd = args[0];
		}

		if (cmd.equalsIgnoreCase("loaded")) {

			int loadedChunks = 0;
			int usedChunks = 0;

			for (World world : p.getServer().getWorlds()) {

				Chunk[] chunks = world.getLoadedChunks();
				loadedChunks += chunks.length;

				for (Chunk chunk : chunks) {
					if (world.isChunkInUse(chunk.getX(), chunk.getZ())) {
						usedChunks++;
					}
				}
			}

			p.msg(sender, loadedChunks + " Chunks are currently loaded");
			p.msg(sender, loadedChunks - usedChunks + " of them are not used by any Player");
			p.msg(sender, p.persistingChunks.size() + " Chunks are made persistent by ChunkKeeper");

		} else if (cmd.equalsIgnoreCase("stats") || cmd.equalsIgnoreCase("statistics")) {

			if (p.doAverage) {
				p.msg(sender, "Average Loading Time of a Chunk in miliseconds: " + (float) (p.getAverage() / 1000000.0));
				p.msg(sender, "Amount of Chunks that needed more than 3 times longer: " + p.overAverage);
				p.msg(sender, "Which are Percent of all Chunks: " + p.getPercent());
				p.msg(sender, "Peak loading Time of any Chunk in miliseconds: " + p.peakTime);
			} else {
				p.msg(sender, "Statistics are not enabled!");
			}

		} else {
			return false;
		}
		return true;

	}

}