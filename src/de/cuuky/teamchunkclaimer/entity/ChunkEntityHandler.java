package de.cuuky.teamchunkclaimer.entity;

import de.cuuky.cfw.serialize.CFWSerializeManager;
import de.cuuky.cfw.serialize.CFWSerializeManager.SaveVisit;
import de.cuuky.cfw.version.VersionUtils;
import de.cuuky.teamchunkclaimer.ChunkClaimer;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.ChunkTeam;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ClaimChunk;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChunkEntityHandler {

	private final String BASE_DIR = "plugins/TeamChunkClaimer/stats/";

	private final ChunkClaimer claimer;
	private final CFWSerializeManager serializeManager;

	private final List<ChunkPlayer> players;
	private final List<ChunkTeam> teams;

	public ChunkEntityHandler(ChunkClaimer claimer) {
		this.claimer = claimer;
		this.serializeManager = claimer.getCuukyFrameWork().getSerializeManager();

		this.players = this.serializeManager.loadSerializeables(ChunkPlayer.class, new File(BASE_DIR + "players.yml"), this);
		this.teams = this.serializeManager.loadSerializeables(ChunkTeam.class, new File(BASE_DIR + "teams.yml"), this);

		for (Player player : claimer.getPlugin().getServer().getOnlinePlayers()) {
			ChunkPlayer tcp = getPlayer(player.getUniqueId().toString());
			if (tcp == null)
				tcp = registerPlayer(player.getUniqueId());

			tcp.setPlayer(player);
		}

		startChunkThread();
	}

	private void startChunkThread() {
		new BukkitRunnable() {

			final Map<ChunkPlayer, ClaimChunk> lastChunks = new HashMap<>();

			@Override
			public void run() {
				for (ChunkPlayer player : players) {
					if (!player.isOnline())
						continue;

					ClaimChunk newChunk = claimer.getEntityHandler().getChunk(player.getPlayer().getLocation().getChunk());
					ClaimChunk oldChunk = lastChunks.get(player);

					if (newChunk != null) {
						if (oldChunk != null && oldChunk.getTeam().equals(newChunk.getTeam()))
							continue;

                        VersionUtils.getVersionAdapter().sendTablist(player.getPlayer(),
                            newChunk.getTeam().getDisplayname(), newChunk.getTeam().getTitle() != null ? newChunk.getTeam().getTitle() : "");
					} else if (oldChunk != null)
                        VersionUtils.getVersionAdapter().sendTablist(player.getPlayer(),
                            "§aWildnis", "§7Hier kannst du Chunks claimen.");

					lastChunks.put(player, newChunk);
				}
			}
		}.runTaskTimerAsynchronously(claimer.getPlugin(), 20, 5);
	}

	public void saveEntities() {
		this.serializeManager.saveFiles(ChunkPlayer.class, this.players, new File(BASE_DIR + "players.yml"), new SaveVisit<ChunkPlayer>() {

			@Override
			public String onKeySave(ChunkPlayer object) {
				return object.getUuid();
			}
		});

		this.serializeManager.saveFiles(ChunkTeam.class, this.teams, new File(BASE_DIR + "teams.yml"), new SaveVisit<ChunkTeam>() {

			@Override
			public String onKeySave(ChunkTeam object) {
				return String.valueOf(object.getTeamId());
			}
		});
	}

	public ClaimChunk getChunk(Chunk chunk) {
		for (ChunkTeam team : claimer.getEntityHandler().getTeams())
			for (ClaimChunk tChunk : team.getClaimedChunks())
				if (tChunk.getChunk().equals(chunk))
					return tChunk;

		return null;
	}

	public ChunkPlayer getPlayer(UUID uuid) {
		return getPlayer(uuid.toString());
	}

	public ChunkPlayer getPlayer(String nameOrUuid) {
		for (ChunkPlayer player : this.players) {
			if ((player.getName() != null && !player.getName().equals(nameOrUuid)) && !player.getUuid().equals(nameOrUuid))
				continue;

			return player;
		}

		return null;
	}

	public ChunkPlayer registerPlayer(UUID uuid) {
		ChunkPlayer player = new ChunkPlayer(this, uuid.toString());
		this.players.add(player);
		return player;
	}

    public ChunkTeam getTeam(long id) {
		for (ChunkTeam team : this.teams)
			if (team.getTeamId() == id)
				return team;

		return null;
	}

	public ChunkTeam getTeam(String name) {
		for (ChunkTeam team : this.teams)
			if (team.getName().equals(name))
				return team;

		return null;
	}

	public ChunkTeam registerTeam(String name) {
		ChunkTeam team = new ChunkTeam(this, name);
		this.teams.add(team);
		return team;
	}

	public void removeTeam(ChunkTeam team) {
		this.teams.remove(team);
	}

	public List<ChunkTeam> getTeams() {
		return teams;
	}

	public ChunkClaimer getClaimer() {
		return claimer;
	}
}