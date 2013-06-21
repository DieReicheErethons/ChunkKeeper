package com.dre.chunkkeeper;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
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

		if (cmd.equalsIgnoreCase("loaded") || cmd.equalsIgnoreCase("status")) {

			int loadedChunks = 0;
			int notUsedChunks = 0;
			int notLoadedChunks = 0;

			for (World world : p.getServer().getWorlds()) {

				Chunk[] chunks = world.getLoadedChunks();
				loadedChunks += chunks.length;

				for (Chunk chunk : chunks) {
					if (!world.isChunkInUse(chunk.getX(), chunk.getZ())) {
						notUsedChunks++;
					}
				}

			}

			for (Chunk pChunk : p.persistingChunks) {
				if (!pChunk.isLoaded()) {
					notLoadedChunks++;
				}
			}

			p.msg(sender, ChatColor.GOLD + "" + loadedChunks + ChatColor.WHITE + " Chunks are currently loaded");
			p.msg(sender, ChatColor.GOLD + "" + notUsedChunks + ChatColor.WHITE + " of all loaded Chunks are not used and would unload (Bukkit seems to keep 256 Chunks per world though)");
			sender.sendMessage(" ");
			p.msg(sender,  ChatColor.GOLD + "" + p.persistingChunks.size() + ChatColor.WHITE + " Chunks are made persistent by ChunkKeeper, keeping them from unloading");

			if (notLoadedChunks == 0) {
				p.msg(sender, ChatColor.GREEN + "All persistent Chunks are loaded");
			} else {
				p.msg(sender, ChatColor.DARK_RED + "" + notLoadedChunks + ChatColor.RED + " Persistent Chunks are not loaded! They should be!");
			}

			if (p.wrongChunkLoads != 0) {
				p.msg(sender, ChatColor.DARK_RED + "" + p.wrongChunkLoads + ChatColor.RED + " Chunks were loaded from Disk again, after being made persistent! They shouldnt do that!");
			} else {
				p.msg(sender, ChatColor.GREEN + "All persistent Chunks never loaded from Disk again");
			}

			if (p.cancelFails != 0) {
				p.msg(sender, ChatColor.DARK_RED + "" + p.cancelFails + ChatColor.RED + " ChunkUnloads failed to cancel!");
			} else {
				p.msg(sender, ChatColor.GREEN + "All ChunkUnloads for persistent Chunks were properly cancelled");
			}

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