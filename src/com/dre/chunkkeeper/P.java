package com.dre.chunkkeeper;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.File;
import org.apache.commons.lang.math.NumberUtils;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.Chunk;
import org.bukkit.event.HandlerList;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;

public class P extends JavaPlugin {
	public static P p;
	public CopyOnWriteArrayList<int[]> persistentChunks = new CopyOnWriteArrayList<int[]>();
	public CopyOnWriteArrayList<String> worldIds = new CopyOnWriteArrayList<String>();
	public boolean doAverage;
	public boolean forever;
	public long totalTime = 0;
	public long amount = 0;
	public int overAverage = 0;
	public float peakTime = 0;
	public int wrongChunkLoads = 0;
	public int cancelFails = 0;
	public List<String> excludedWorlds;

	// Listeners
	public WorldListener worldListener;

	@Override
	public void onEnable() {
		p = this;

		// Read Config
		File file = new File(p.getDataFolder(), "config.yml");
		if (!file.exists()) {
			saveDefaultConfig();
		}	 
		doAverage = p.getConfig().getBoolean("testTime", true);
		forever = p.getConfig().getBoolean("checkOnce", false);
		if (p.getConfig().contains("excludedWorlds")) {
			excludedWorlds = p.getConfig().getStringList("excludedWorlds");
		}

		// Listeners
		worldListener = new WorldListener();
		getCommand("ChunkKeeper").setExecutor(new CommandListener());
		CommonPlugin.getInstance().addTimingsListener(ChunkTimingsListener.INSTANCE);

		p.getServer().getPluginManager().registerEvents(worldListener, p);

		// Autosave
		int autosaveInterval = p.getConfig().getInt("autosave", 10);
		if (autosaveInterval > 0) {
			Long autosaveTicks = autosaveInterval * 20L * 60L;
			p.getServer().getScheduler().scheduleSyncRepeatingTask(p, new Runnable() {
				public void run() {
					save();
				}
			}, autosaveTicks, autosaveTicks);
		}

		// Preload Chunks
		if (getConfig().getBoolean("Preload", false)) {
			preload();
		} else {
			this.log(this.getDescription().getName() + " enabled!");
		}
	}

	@Override
	public void onDisable() {
		// save Data
		save();

		// Disable listeners
		HandlerList.unregisterAll(p);
		this.removeTimingsListener();
	}

	public void save() {
		File datafile = new File(p.getDataFolder(), "data.yml");

		FileConfiguration data = new YamlConfiguration();

		if (doAverage) {

			data.set("Average Loading Time of a Chunk in miliseconds", (float) (getAverage() / 1000000.0));
			data.set("Amount of Chunks that needed more than 3 times longer", overAverage);
			data.set("Which are Percent of all Chunks", getPercent());
			data.set("Peak loading Time of any Chunk in miliseconds", peakTime);

		}

		if (!persistentChunks.isEmpty()) {
			Map<String, ArrayList<String>> worldMap = new HashMap<String, ArrayList<String>>();
			int count = 0;
			for (int[] chunkData : persistentChunks) {

				String worldName = worldIds.get(chunkData[0]);
				int x = chunkData[1];
				int z = chunkData[2];

				if (!worldMap.containsKey(worldName)) {
					worldMap.put(worldName, new ArrayList<String>());
				}

				worldMap.get(worldName).add(x + "/" + z);
				count++;

			}
			if (p.getConfig().getInt("maxChunks", Integer.MAX_VALUE) == count) {
				data.set("Maximum amount of persistent Chunks reached", count);
			} else {
				data.set("Amount of persistent Chunks", count);
			}
			data.set("Chunks", worldMap);
		}

		try {
			data.save(datafile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void preload() {
		File file = new File(p.getDataFolder(), "data.yml");
		if (file.exists()) {

			FileConfiguration data = YamlConfiguration.loadConfiguration(file);

			Map<String, Object> worldMap = null;
			ConfigurationSection chunkSection = data.getConfigurationSection("Chunks");
			if (chunkSection != null) {
				worldMap = chunkSection.getValues(true);
			}

			if (worldMap != null && !worldMap.isEmpty()) {

				this.log("Preloading persistent Chunks...");

				long time = System.nanoTime();
				int count = 0;

				for (String worldName : worldMap.keySet()) {
					World world = getServer().getWorld(worldName);
					if (world != null) {
						ArrayList<String> serializedChunks = (ArrayList<String>) worldMap.get(worldName);
						count += serializedChunks.size();

						for (String serializedChunk : serializedChunks) {
							String[] split = serializedChunk.split("/");
							int x = parseInt(split[0]);
							int z = parseInt(split[1]);

							world.loadChunk(x, z, false);
							
							if (forever) {
								Chunk chunk = world.getChunkAt(x, z);
								addPersistingChunk(chunk);
							}
						}
					}
				}

			/*	this.log("loading some chunks");
				for (int x = -100; x < 100; x++) {
					for (int z = -100; z < 100; z++) {
						getServer().getWorld("world").loadChunk(x, z, false);
					}
				}
			*/
				time = System.nanoTime() - time;
				float ftime = (float) (time / 1000000.0);
				this.log("Preloading Done, loaded " + count + " Chunks! (" + ftime + "ms)");

			} else {
				this.log("No Chunk needed Preloading!");
			}

		} else {
			this.log(this.getDescription().getName() + " enabled!");
		}
	}

	public void removeTimingsListener() {
		CommonPlugin.getInstance().removeTimingsListener(ChunkTimingsListener.INSTANCE);
	}

	public int getWorldId(World world) {
		String name = world.getName();
		worldIds.addIfAbsent(name);

		return worldIds.indexOf(name);
	}

	public boolean isPersistingChunk(Chunk chunk) {
		int worldId = getWorldId(chunk.getWorld());
		int[] chunkData = { worldId, chunk.getX(), chunk.getZ() };

		return hasChunkData(chunkData);
	}

	// returns false if Chunk is already Persistent
	public boolean addPersistingChunk(Chunk chunk) {
		int worldId = getWorldId(chunk.getWorld());
		int[] chunkData = { worldId, chunk.getX(), chunk.getZ() };

		if (!hasChunkData(chunkData)) {
			persistentChunks.add(chunkData);
			return true;
		}
		return false;
	}

	public boolean hasChunkData(int[] chunkData) {
		for (int[] pData : persistentChunks) {
			if (Arrays.equals(pData, chunkData)) {
				return true;
			}
		}
		return false;
	}

	public long getAverage() {
		if (amount != 0) {
			return totalTime / amount;
		}
		return 0;
	}

	public float getPercent() {
		return (float) (overAverage * 100.0) / amount;
	}

	public void msg(CommandSender sender, String msg) {
		sender.sendMessage(ChatColor.DARK_GREEN + "[ChunkKeeper] " + ChatColor.WHITE + msg);
	}

	public void log(String msg) {
		this.msg(Bukkit.getConsoleSender(), msg);
	}

	public int parseInt(String string) {
		return NumberUtils.toInt(string, 0);
	}

}