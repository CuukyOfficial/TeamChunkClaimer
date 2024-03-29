package de.cuuky.teamchunkclaimer.configuration;

import java.util.ArrayList;

import org.bukkit.ChatColor;

import de.cuuky.cfw.configuration.BasicConfigurationHandler;

public class ChunkConfigurationHandler extends BasicConfigurationHandler {

	private String prefix, teamChatFormat, colorCode, header;

	private boolean buildInUnclaimed;
	private int maxChunkGroups;
	private ArrayList<String> blacklistedWorlds;

	public ChunkConfigurationHandler() {
		super("plugins/TeamChunkClaimer/config.yml");

		reload();
	}

	@SuppressWarnings("unchecked")
	public void reload() {
		this.prefix = ChatColor.translateAlternateColorCodes('&', getString("prefix", "&2Teams &8» &7"));
		this.teamChatFormat = ChatColor.translateAlternateColorCodes('&', getString("teamChatFormat", "&8[%team%&8] &a%player% &8» &7%message%"));
		this.colorCode = ChatColor.translateAlternateColorCodes('&', getString("colorCode", "&a"));
		this.header = ChatColor.translateAlternateColorCodes('&', getString("header", "&8&m------------&r &2Teams &8&m------------&r"));

		this.buildInUnclaimed = getBool("chunks.buildInUnclaimed", true);
		this.maxChunkGroups = getInt("chunks.maxChunkGroups", 2);
		this.blacklistedWorlds = (ArrayList<String>) getValue("chunks.blacklistedWorlds", new ArrayList<String>());
	}

	public String getPrefix() {
		return prefix;
	}

	public String getTeamChatFormat() {
		return teamChatFormat;
	}

	public String getColorCode() {
		return colorCode;
	}

	public String getHeader() {
		return header;
	}

	public boolean canBuildInUnclaimed() {
		return buildInUnclaimed;
	}

	public int getMaxChunkGroups() {
		return maxChunkGroups;
	}

	public ArrayList<String> getBlacklistedWorlds() {
		return blacklistedWorlds;
	}
}