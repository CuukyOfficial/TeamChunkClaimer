package de.cuuky.teamchunkclaimer.entity.player;

import de.cuuky.cfw.serialize.identifiers.CFWSerializeField;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.teamchunkclaimer.entity.ChunkEntityHandler;
import de.cuuky.teamchunkclaimer.entity.player.invites.TeamInvite;
import de.cuuky.teamchunkclaimer.entity.team.ChunkTeam;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.List;

public class ChunkPlayer implements CFWSerializeable {

	private final ChunkEntityHandler handler;

	@CFWSerializeField(path = "uuid")
	private String uuid;

	@CFWSerializeField(path = "name")
	private String name;

	@CFWSerializeField(path = "allowedChunks")
	private int allowedChunks;

	@CFWSerializeField(path = "invites", keyClass = TeamInvite.class)
	private List<TeamInvite> invites;

	private ChunkTeam team;
	private Player player;

	public ChunkPlayer(ChunkEntityHandler handler) {
		this.handler = handler;

		this.invites = new ArrayList<>();
	}

	public ChunkPlayer(ChunkEntityHandler handler, String uuid) {
		this(handler);

		this.uuid = uuid;
	}

	public void unregister() {
		if (this.team != null)
			this.team.removeMember(this);
	}

	public int refreshAllowedChunks() {
		int allowed = this.allowedChunks;
		for (PermissionAttachmentInfo info : this.player.getEffectivePermissions()) {
			if (!info.getValue() || !info.getPermission().startsWith("chunk.limit."))
				continue;

			int tempAllowed;
			try {
				tempAllowed = Integer.parseInt(info.getPermission().replace("chunk.limit.", ""));
			} catch (NumberFormatException e) {
				continue;
			}

			if (tempAllowed > allowed)
				allowed = tempAllowed;
		}

		return allowed;
	}

	public int getAllowedChunks() {
		return isOnline() ? this.allowedChunks = refreshAllowedChunks() : this.allowedChunks;
	}

	public boolean isOnline() {
		return this.player != null;
	}

	public TeamInvite getInvite(ChunkTeam team) {
		if (this.invites.isEmpty())
			return null;

		for (TeamInvite invite : new ArrayList<>(this.invites)) {
			if (invite.getTeam() == null) {
				this.invites.remove(invite);
				continue;
			}

			if (invite.getTeam().equals(team))
				return invite;
		}

		return null;
	}

	public void inviteTo(ChunkTeam team, String by) {
		this.invites.add(new TeamInvite(this, by, team));

//			this.player.sendMessage(this.handler.getClaimer().getPrefix() + "Du wurdest in das Team " + team.getDisplayname() + " §7eingeladen! §8(§7/team accept/deny <Team>§8)");
//			TextComponent accept = new TextComponent(this.handler.getClaimer().getPrefix() + "§aAnnehmen");
//			accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team accept " + team.getName()));
//			TextComponent deny = new TextComponent(this.handler.getClaimer().getPrefix() + "§cAblehnen");
//			deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team deny " + team.getName()));
//			this.player.sendMessage(accept + "\n" + deny);
    }

	public void removeInvite(TeamInvite invite) {
		this.invites.remove(invite);
	}

	public String getUuid() {
		return uuid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setPlayer(Player player) {
		this.player = player;

		if (player != null) {
            this.name = this.player.getName();
		}
	}

	public ChunkTeam getTeam() {
		return team;
	}

	/*
	 * Do not use this method
	 */
	public void setTeam(ChunkTeam team) {
		this.team = team;
	}

	public Player getPlayer() {
		return player;
	}

	public ChunkEntityHandler getHandler() {
		return handler;
	}
}