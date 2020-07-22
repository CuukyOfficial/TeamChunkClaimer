package de.cuuky.teamchunkclaimer.listener.flag;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.cuuky.teamchunkclaimer.ChunkClaimer;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ChunkFlag;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ClaimChunk;

public class ChunkUseListener implements Listener {

	private ChunkClaimer cc;

	public ChunkUseListener(ChunkClaimer claimer) {
		this.cc = claimer;
	}

	private boolean canUse(Location location, Player p) {
		ChunkPlayer player = cc.getEntityHandler().getPlayer(p.getName());
		ClaimChunk chunk = cc.getEntityHandler().getChunk(location.getChunk());
		if (chunk == null) {
			if (cc.getConfiguration().canBuildInUnclaimed())
				return true;

			return false;
		}

		if (chunk.getTeam().getFlag(ChunkFlag.USE))
			return true;

		if (player.getTeam() != null && player.getTeam().equals(chunk.getTeam()))
			return true;

		return false;
	}

	@EventHandler
	public void onChunkUse(PlayerInteractEvent event) {
		if ((event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) || canUse(event.getPlayer().getLocation(), event.getPlayer()))
			return;

		event.setCancelled(true);
	}
}