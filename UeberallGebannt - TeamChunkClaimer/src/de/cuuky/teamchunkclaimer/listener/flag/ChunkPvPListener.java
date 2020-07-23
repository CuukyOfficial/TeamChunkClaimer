package de.cuuky.teamchunkclaimer.listener.flag;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.cuuky.cfw.utils.listener.EntityDamageByEntityUtil;
import de.cuuky.teamchunkclaimer.ChunkClaimer;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ChunkFlag;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ClaimChunk;

public class ChunkPvPListener implements Listener {

	private ChunkClaimer cc;

	public ChunkPvPListener(ChunkClaimer cc) {
		this.cc = cc;
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Player pDamager = new EntityDamageByEntityUtil(event).getDamager();
		if (pDamager == null)
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();

		ClaimChunk damagerChunk = cc.getEntityHandler().getChunk(pDamager.getLocation().getChunk());
		ClaimChunk playerChunk = cc.getEntityHandler().getChunk(player.getLocation().getChunk());
		if (damagerChunk != null && !damagerChunk.getTeam().getFlag(ChunkFlag.PVP) || playerChunk != null && !playerChunk.getTeam().getFlag(ChunkFlag.PVP))
			return;
		pDamager.sendMessage(cc.getPrefix() + "Du kannst diesen Spieler hier nicht angreifen.");
		event.setCancelled(true);
	}
}