package de.cuuky.teamchunkclaimer;

import de.cuuky.cfw.CuukyFrameWork;
import de.cuuky.teamchunkclaimer.commands.ChunkCommand;
import de.cuuky.teamchunkclaimer.commands.TeamCommand;
import de.cuuky.teamchunkclaimer.configuration.ChunkConfigurationHandler;
import de.cuuky.teamchunkclaimer.entity.ChunkEntityHandler;
import de.cuuky.teamchunkclaimer.listener.PlayerListener;
import de.cuuky.teamchunkclaimer.listener.TeamChatListener;
import de.cuuky.teamchunkclaimer.listener.flag.ChunkBuildListener;
import de.cuuky.teamchunkclaimer.listener.flag.ChunkPvPListener;
import de.cuuky.teamchunkclaimer.listener.flag.ChunkUseListener;

public class ChunkClaimer {

	private Main plugin;

	private CuukyFrameWork cfw;
	private ChunkEntityHandler entityHandler;
	private ChunkConfigurationHandler configuration;

	public ChunkClaimer(Main main) {
		this.plugin = main;

		System.out.println(getConsolePrefix() + "Initialising framework...");
		this.cfw = new CuukyFrameWork(plugin);

		loadFiles();
		startSaveSchedule();

		registerListener();
		registerCommands();
	}

	private void loadFiles() {
		System.out.println(getConsolePrefix() + "Loading configuration...");
		this.configuration = new ChunkConfigurationHandler();

		System.out.println(getConsolePrefix() + "Loading entities...");
		this.entityHandler = new ChunkEntityHandler(this);
	}

	private void registerListener() {
		plugin.getServer().getPluginManager().registerEvents(new PlayerListener(this), this.plugin);
		plugin.getServer().getPluginManager().registerEvents(new TeamChatListener(this), this.plugin);

		plugin.getServer().getPluginManager().registerEvents(new ChunkBuildListener(this), this.plugin);
		plugin.getServer().getPluginManager().registerEvents(new ChunkPvPListener(this), this.plugin);
		plugin.getServer().getPluginManager().registerEvents(new ChunkUseListener(this), this.plugin);
	}

	private void registerCommands() {
		plugin.getCommand("chunk").setExecutor(new ChunkCommand(this));
		plugin.getCommand("team").setExecutor(new TeamCommand(this));
	}

	private void startSaveSchedule() {
		this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {

			@Override
			public void run() {
				saveAll();
			}
		}, 6000, 6000);
	}

	public void saveAll() {
		this.entityHandler.saveEntities();
	}

	public void disable() {
		System.out.println(getConsolePrefix() + "Saving entities...");
		saveAll();
	}

	public Main getPlugin() {
		return plugin;
	}

	public CuukyFrameWork getCuukyFrameWork() {
		return cfw;
	}

	public ChunkEntityHandler getEntityHandler() {
		return entityHandler;
	}

	public ChunkConfigurationHandler getConfiguration() {
		return configuration;
	}

	public String getPrefix() {
		return this.configuration.getPrefix();
	}

	public String getConsolePrefix() {
		return this.plugin.getConsolePrefix();
	}
}