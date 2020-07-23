package de.cuuky.teamchunkclaimer.listener.flag;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import de.cuuky.teamchunkclaimer.ChunkClaimer;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ChunkFlag;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ClaimChunk;

public class ChunkBuildListener implements Listener {

	private ChunkClaimer cc;

	public ChunkBuildListener(ChunkClaimer cc) {
		this.cc = cc;
	}

	private boolean canBuild(Location location, Player p) {
		ChunkPlayer player = cc.getEntityHandler().getPlayer(p.getName());
		ClaimChunk chunk = cc.getEntityHandler().getChunk(location.getChunk());
		if (chunk == null) {
			if (cc.getConfiguration().canBuildInUnclaimed())
				return true;

			return false;
		}

		if (chunk.getTeam().getFlag(ChunkFlag.BUILD))
			return true;

		if (player.getTeam() != null && player.getTeam().equals(chunk.getTeam()))
			return true;

		return false;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (canBuild(event.getBlock().getLocation(), event.getPlayer()))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (canBuild(event.getBlock().getLocation(), event.getPlayer()))
			return;

		event.setCancelled(true);
	}
//
//	@EventHandler
//	public void onBlockExplode(BlockExplodeEvent event) {
//		Iterator<Block> blocks = event.blockList().iterator();
//		while (blocks.hasNext()) {
//			Block block = blocks.next();
//
//			if (cc.getEntityHandler().getChunk(block.getLocation().getChunk()) != null)
//				blocks.remove();
//		}
//	}
}