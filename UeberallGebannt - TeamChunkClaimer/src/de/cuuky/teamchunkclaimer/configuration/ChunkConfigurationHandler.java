package de.cuuky.teamchunkclaimer.configuration;

import java.util.ArrayList;

import org.bukkit.ChatColor;

import de.cuuky.cfw.configuration.BasicConfigurationHandler;

public class ChunkConfigurationHandler extends BasicConfigurationHandler {

	private String prefix, teamChatFormat;

	private boolean buildInUnclaimed;
	private int maxChunkGroups;
	private ArrayList<String> blacklistetWorlds;

	public ChunkConfigurationHandler() {
		super("plugins/TeamChunkClaimer/config.yml");

		reload();
	}

	@SuppressWarnings("unchecked")
	public void reload() {
		this.prefix = ChatColor.translateAlternateColorCodes('&', getString("prefix", "&8[&5Chunk&8] &7"));
		this.teamChatFormat = ChatColor.translateAlternateColorCodes('&', getString("teamChatFormat", "&8[%team%&8] &5%player%&8: &f%message%"));

		this.buildInUnclaimed = getBool("chunks.buildInUnclaimed", true);
		this.maxChunkGroups = getInt("chunks.maxChunkGroups", 2);
		this.blacklistetWorlds = (ArrayList<String>) getValue("chunks.blacklistetWorlds", new ArrayList<String>());
	}

	public String getPrefix() {
		return prefix;
	}

	public String getTeamChatFormat() {
		return teamChatFormat;
	}

	public boolean canBuildInUnclaimed() {
		return buildInUnclaimed;
	}

	public int getMaxChunkGroups() {
		return maxChunkGroups;
	}

	public ArrayList<String> getBlacklistetWorlds() {
		return blacklistetWorlds;
	}
}