package com.dre.chunkkeeper;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.dre.chunkkeeper.listeners.NLETimingsListener;
import com.dre.chunkkeeper.listeners.WorldListener;

public class P extends PluginBase {
	public static P p;
	
	public int maxTime = 100;
	private Set<int[]> chunks;
	
	
	@Override
	public void enable(){
		p = this;
		
		//Register listeners
		Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
		CommonPlugin.getInstance().addTimingsListener(NLETimingsListener.INSTANCE);
		
		// Load data
		this.load();
	}
	
	@Override
	public void disable(){
		this.save();
	}	
	
	@Override
	public boolean command(CommandSender arg0, String arg1, String[] arg2) {
		return false;
	}

	@Override
	public int getMinimumLibVersion() {
		return 0;
	}
	
	/* Chunk */
	public void addChunk(int x, int z){
		int[] array = {x, z};
		
		chunks.add(array);
	}
	
	public void removeChunk(int x, int z){
		int[] array = {x, z};
		
		chunks.remove(array);
	}
	
	public boolean containsChunk(int x, int z){
		int[] array = {x, z};
		
		/*if(chunks.contains(array)){
			return true;
		}*/
		
		return false;
	}
	
	/* Save & Load */
	public void save(){
		File file = new File(this.getDataFolder(), "data.yml");
		FileConfiguration configFile = new YamlConfiguration();
		
		configFile.set("chunks", this.chunks);
		
		try {
			configFile.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void load(){
		File file = new File(this.getDataFolder(), "data.yml");
		FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);

		this.chunks = (Set<int[]>) configFile.getList("chunks");
		
		if(this.chunks == null){
			this.chunks = new HashSet<int[]>();
		}
	}
	
	/* Logger */
	public void log(String msg) {
		log(Level.INFO, msg);
	}

	public void log(Level level, Object msg) {
		Logger.getLogger("Minecraft").log(level, "[" + this.getDescription().getFullName() + "] " + msg);
	}
}
