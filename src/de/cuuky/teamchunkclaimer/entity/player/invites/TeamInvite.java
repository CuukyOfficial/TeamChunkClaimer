package de.cuuky.teamchunkclaimer.entity.player.invites;

import de.cuuky.cfw.serialize.identifiers.CFWSerializeField;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.ChunkTeam;

public class TeamInvite implements CFWSerializeable {

	@CFWSerializeField(path = "invitor")
	private String invitor;

	@CFWSerializeField(path = "team")
	private long teamId;

	private ChunkPlayer player;

	public TeamInvite(ChunkPlayer player) {
		this.player = player;
	}

	public TeamInvite(ChunkPlayer player, String invitor, ChunkTeam team) {
		this.player = player;

		this.invitor = invitor;
		this.teamId = team.getTeamId();
	}

	public String getInvitor() {
		return invitor;
	}

	public ChunkTeam getTeam() {
		return player.getHandler().getTeam(this.teamId);
	}
}