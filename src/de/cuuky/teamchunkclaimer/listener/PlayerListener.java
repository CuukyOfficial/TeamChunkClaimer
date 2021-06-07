package de.cuuky.teamchunkclaimer.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.cuuky.teamchunkclaimer.ChunkClaimer;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;

public class PlayerListener implements Listener {

	private ChunkClaimer tcc;

	public PlayerListener(ChunkClaimer tcc) {
		this.tcc = tcc;
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		ChunkPlayer player = tcc.getEntityHandler().getPlayer(event.getPlayer().getUniqueId());
		if (player == null)
			player = tcc.getEntityHandler().registerPlayer(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		ChunkPlayer player = tcc.getEntityHandler().getPlayer(event.getPlayer().getUniqueId());

		player.setPlayer(event.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		ChunkPlayer player = tcc.getEntityHandler().getPlayer(event.getPlayer().getUniqueId());

		player.refreshAllowedChunks();
		player.setPlayer(null);
	}
}