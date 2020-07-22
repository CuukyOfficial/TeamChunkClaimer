package de.cuuky.teamchunkclaimer.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.cuuky.teamchunkclaimer.ChunkClaimer;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.player.invites.TeamInvite;
import de.cuuky.teamchunkclaimer.entity.team.ChunkTeam;
import de.cuuky.teamchunkclaimer.entity.team.TeamMemberType;

public class TeamCommand implements CommandExecutor {

	private ChunkClaimer tcc;

	public TeamCommand(ChunkClaimer tcc) {
		this.tcc = tcc;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(tcc.getPrefix() + "Not for console!");
			return false;
		}

		if (args.length == 0) {
			sender.sendMessage(tcc.getPrefix() + "/team info <Name>");
			sender.sendMessage(tcc.getPrefix() + "/team create <Name>");
			sender.sendMessage(tcc.getPrefix() + "/team accept/deny <Team>");
			sender.sendMessage(tcc.getPrefix() + "------");
			sender.sendMessage(tcc.getPrefix() + "/team leave");
			sender.sendMessage(tcc.getPrefix() + "------");
			sender.sendMessage(tcc.getPrefix() + "/team setcolor <Farbe>");
			sender.sendMessage(tcc.getPrefix() + "/team invite <Spieler>");
			sender.sendMessage(tcc.getPrefix() + "/team kick <Spieler>");
			sender.sendMessage(tcc.getPrefix() + "/team promote <Spieler>");
			sender.sendMessage(tcc.getPrefix() + "/team delete");
			return true;
		}

		ChunkPlayer player = tcc.getEntityHandler().getPlayer(((Player) sender).getUniqueId());

		// USER COMMANDS

		if (args[0].equalsIgnoreCase("info")) {
			if (args.length < 2) {
				sender.sendMessage(tcc.getPrefix() + "/team info <Name>");
				return false;
			}

			ChunkTeam team = tcc.getEntityHandler().getTeam(args[1]);
			if (team == null) {
				sender.sendMessage(tcc.getPrefix() + "Team wurde nicht gefunden!");
				return false;
			}

			sender.sendMessage(tcc.getPrefix() + "§7-- " + team.getDisplayname() + " §7--");
			sender.sendMessage(tcc.getPrefix() + "Team-Tag: " + team.getTag() == null ? "-" : team.getTag());
			sender.sendMessage(tcc.getPrefix() + "Mitglieder: " + team.getMembers().size());
			sender.sendMessage(tcc.getPrefix() + "Chunks: " + team.getClaimedChunks().size());
			return true;
		} else if (args[0].equalsIgnoreCase("create")) {
			if (player.getTeam() != null) {
				sender.sendMessage(tcc.getPrefix() + "Du bist bereits in einem Team!");
				return false;
			}

			if (args.length < 2) {
				sender.sendMessage(tcc.getPrefix() + "/team create <Name> [Farbe]");
				return false;
			}

			ChunkTeam team = tcc.getEntityHandler().registerTeam(args[1]);
			team.addMember(player, TeamMemberType.OWNER);
			sender.sendMessage(tcc.getPrefix() + "Team erfolgreich erstellt!");
			return true;
		} else if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("deny")) {
			if (args.length < 2) {
				sender.sendMessage(tcc.getPrefix() + "/team accept/deny <Name>");
				return false;
			}

			if (player.getTeam() != null) {
				sender.sendMessage(tcc.getPrefix() + "Du bist bereits in einem Team!");
				return false;
			}

			ChunkTeam team = tcc.getEntityHandler().getTeam(args[1]);
			if (team == null) {
				sender.sendMessage(tcc.getPrefix() + "Team wurde nicht gefunden!");
				return false;
			}

			TeamInvite invite = player.getInvite(team);
			if (invite == null) {
				sender.sendMessage(tcc.getPrefix() + "Du hast von diesem Team keine Einladung erhalten!");
				return false;
			}

			if (args[0].equalsIgnoreCase("accept"))
				team.addMember(player, TeamMemberType.MEMBER);

			player.removeInvite(invite);
			sender.sendMessage(tcc.getPrefix() + "Einladung erfolgreich " + (args[0].equalsIgnoreCase("accept") ? "angenommen" : "abgelehnt") + "!");
			return true;
		}

		if (player.getTeam() == null) {
			sender.sendMessage(tcc.getPrefix() + "Du bist in keinem Team!");
			return false;
		}

		if (args[0].equalsIgnoreCase("leave")) {
			player.getTeam().removeMember(player);
			sender.sendMessage(tcc.getPrefix() + "Du hast das Team " + player.getTeam().getDisplayname() + " §7erfolgreich verlassen");
			return true;
		}

		if (player.getTeam().getMemberType(player) == TeamMemberType.MEMBER) {
			sender.sendMessage(tcc.getPrefix() + "Du musst Team-Moderator sein, um diese Einstellung vornehmen zu können!");
			return false;
		}

		// MOD COMMANDS

		if (args[0].equalsIgnoreCase("setcolor")) {
			if (args.length < 2) {
				sender.sendMessage(tcc.getPrefix() + "/team setcolor <Farbe>");
				return false;
			}

			player.getTeam().setColor(args[1]);
			sender.sendMessage(tcc.getPrefix() + "Farbcode erfolgreich auf " + player.getTeam().getDisplayname() + " §7gesetzt!");
			return true;
		} else if (args[0].equalsIgnoreCase("invite")) {
			if (args.length < 2) {
				sender.sendMessage(tcc.getPrefix() + "/team invite <Spieler>");
				return false;
			}

			ChunkPlayer toInvite = this.tcc.getEntityHandler().getPlayer(args[1]);
			if (toInvite == null) {
				sender.sendMessage(tcc.getPrefix() + "Spieler nicht gefunden!");
				return false;
			}

			if (toInvite.getTeam() != null && toInvite.getTeam().equals(player.getTeam())) {
				sender.sendMessage(tcc.getPrefix() + "Dieser Spieler ist bereits in deinem Team!");
				return false;
			}

			if (toInvite.getInvite(player.getTeam()) != null) {
				sender.sendMessage(tcc.getPrefix() + "Dieser Spieler hat bereits eine Einladung von deinem Team erhalten!");
				return false;
			}

			toInvite.inviteTo(player.getTeam(), player.getName());
			sender.sendMessage(tcc.getPrefix() + "Spieler erfolgreich in dein Team eingeladen!");
			return true;
		} else if (args[0].equalsIgnoreCase("kick")) {
			if (args.length < 2) {
				sender.sendMessage(tcc.getPrefix() + "/team kick <Spieler>");
				return false;
			}

			ChunkPlayer toKick = this.tcc.getEntityHandler().getPlayer(args[1]);
			if (toKick == null) {
				sender.sendMessage(tcc.getPrefix() + "Spieler nicht gefunden!");
				return false;
			}

			if (toKick.getTeam() == null || !toKick.getTeam().equals(player.getTeam())) {
				sender.sendMessage(tcc.getPrefix() + "Dieser Spieler ist nicht in deinem Team!");
				return false;
			}

			if (player.getTeam().getMemberType(toKick) != TeamMemberType.MEMBER) {
				sender.sendMessage(tcc.getPrefix() + "Paul hat mir zwar nicht gesagt, dass ich das einbauen soll, aber dass ein Mod einen anderen kickt oder sogar den Owner, finde ich schon ziemlich doof");
				return false;
			}

			player.getTeam().removeMember(toKick);
			sender.sendMessage(tcc.getPrefix() + toKick.getName() + " wurde erfolgreich aus deinem Team geworfen!");
			return true;
		} else if (args[0].equalsIgnoreCase("promote")) {
			if (args.length < 2) {
				sender.sendMessage(tcc.getPrefix() + "/team promote <Spieler>");
				return false;
			}

			ChunkPlayer toPromote = this.tcc.getEntityHandler().getPlayer(args[1]);
			if (toPromote == null) {
				sender.sendMessage(tcc.getPrefix() + "Spieler nicht gefunden!");
				return false;
			}

			if (toPromote.getTeam() == null || !toPromote.getTeam().equals(player.getTeam())) {
				sender.sendMessage(tcc.getPrefix() + "Dieser Spieler ist nicht in deinem Team!");
				return false;
			}

			if (player.getTeam().getMemberType(toPromote) != TeamMemberType.MEMBER) {
				sender.sendMessage(tcc.getPrefix() + "Paul hat mir zwar nicht gesagt, dass ich das einbauen soll, aber dass ein Mod einen Owner promoten kann, finde ich schon ziemlich doof");
				return false;
			}

			player.getTeam().setMemberType(toPromote, TeamMemberType.MODERATOR);
			sender.sendMessage(tcc.getPrefix() + toPromote.getName() + " wurde erfolgreich als Mod registriert!");
			return true;
		}

		if (player.getTeam().getMemberType(player) == TeamMemberType.OWNER) {
			sender.sendMessage(tcc.getPrefix() + "Du musst Team-Owner sein, um diese Einstellung vornehmen zu können!");
			return false;
		}

		// OWNER COMMANDS

		if (args[0].equalsIgnoreCase("delete")) {
			this.tcc.getEntityHandler().removeTeam(player.getTeam());
			player.getTeam().remove();

			sender.sendMessage(tcc.getPrefix() + "Team erfolgreich aufgelöst!");
			return true;
		}
		return false;
	}
}