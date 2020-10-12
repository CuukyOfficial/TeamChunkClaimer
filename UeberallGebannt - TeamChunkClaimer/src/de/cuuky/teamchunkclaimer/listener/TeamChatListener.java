package de.cuuky.teamchunkclaimer.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.cuuky.teamchunkclaimer.ChunkClaimer;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;

public class TeamChatListener implements Listener {

	private ChunkClaimer cc;

	public TeamChatListener(ChunkClaimer cc) {
		this.cc = cc;
	}

	@EventHandler
	public void onASyncChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled())
			return;

		if (!event.getMessage().startsWith("#"))
			return;

		String format = cc.getConfiguration().getTeamChatFormat();
		if (format.isEmpty())
			return;

		ChunkPlayer player = cc.getEntityHandler().getPlayer(event.getPlayer().getName());
		if (player.getTeam() == null)
			return;

		String message = event.getMessage().replaceFirst("#", "");
		if (message.isEmpty())
			return;

		player.getTeam().sendMessage(format.replace("%team%", player.getTeam().getDisplayname()).replace("%player%", player.getName()).replace("%message%", event.getMessage().replaceFirst("#", "")));
		event.setCancelled(true);
	}
}