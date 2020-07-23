package de.cuuky.teamchunkclaimer.commands;

import java.text.SimpleDateFormat;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.cuuky.teamchunkclaimer.ChunkClaimer;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.TeamMemberType;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ChunkFlag;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ClaimChunk;
import de.cuuky.teamchunkclaimer.menu.ChunkMapMenu;
import de.cuuky.teamchunkclaimer.menu.TeamMainMenu;
import de.cuuky.teamchunkclaimer.menu.team.ChunkListMenu;
import de.cuuky.teamchunkclaimer.utils.WorldGuardChecker;

public class ChunkCommand implements CommandExecutor {

	private ChunkClaimer claimer;

	public ChunkCommand(ChunkClaimer claimer) {
		this.claimer = claimer;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(claimer.getPrefix() + "Not for console!");
			return false;
		}

		if (args.length == 0) {
			sender.sendMessage(claimer.getConfiguration().getHeader());
			sender.sendMessage(claimer.getPrefix() + claimer.getColorCode() + "/chunk map §8- §7Öffnet eine Karte der Umgebung mit allen geclaimten Chunks");
			sender.sendMessage(claimer.getPrefix() + claimer.getColorCode() + "/chunk info [x] [z] §8- §7Zeigt Infos zu dem Chunk auf dem du stehst");
			sender.sendMessage(claimer.getPrefix() + claimer.getColorCode() + "/chunk claim [x] [z] §8- §7Claimt den Chunk für das Team");
			sender.sendMessage(claimer.getPrefix() + claimer.getColorCode() + "/chunk unclaim [x] [z] §8- §7Entclaimt den Chunk vom Team");
			sender.sendMessage(claimer.getPrefix() + claimer.getColorCode() + "/chunk list §8- §7Öffnet ein GUI mit allen Chunks deines Teams");
			sender.sendMessage(claimer.getPrefix() + claimer.getColorCode() + "/chunk flag <pvp/use/build> <true/false> §8- §7Öffnet ein GUI zum Einstellen der Flags");
			sender.sendMessage(claimer.getConfiguration().getHeader());
			return true;
		}

		ChunkPlayer player = claimer.getEntityHandler().getPlayer(((Player) sender).getUniqueId());
		Chunk worldChunk = player.getPlayer().getLocation().getChunk();
		if (args.length > 1) {
			try {
				int x = Integer.valueOf(args[1]), z = Integer.valueOf(args[2]);
				worldChunk = player.getPlayer().getWorld().getChunkAt(x / 16, z / 16);
			} catch (Exception e) {
				sender.sendMessage(claimer.getPrefix() + "X und Z sind keine Zahlen! /chunk");
				return false;
			}
		}

		ClaimChunk chunk = claimer.getEntityHandler().getChunk(worldChunk);
		if (claimer.getConfiguration().getBlacklistedWorlds().contains(player.getPlayer().getWorld().getName())) {
			sender.sendMessage(claimer.getPrefix() + "In dieser Welt kannst du keine Chunks claimen.");
			return false;
		}

		if (args[0].equalsIgnoreCase("map")) {
			new ChunkMapMenu(claimer, player.getPlayer());
			return true;
		} else if (args[0].equalsIgnoreCase("info")) {
			if (chunk == null) {
				sender.sendMessage(claimer.getPrefix() + "Dieser Chunk ist nicht geclaimt.");
				return false;
			}

			sender.sendMessage(claimer.getPrefix() + claimer.getColorCode() + "Chunk §7" + chunk.getLocationX() + "§8:§7" + chunk.getLocationZ() + " §7in " + chunk.getWorld() + "§7:");
			sender.sendMessage(claimer.getPrefix() + "§7Erstellungsdatum§8: " + claimer.getColorCode() + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(chunk.getClaimedAt()));
			sender.sendMessage(claimer.getPrefix() + "§7Besitzer (Team)§8: " + claimer.getColorCode() + chunk.getTeam().getDisplayname());
			sender.sendMessage(claimer.getPrefix() + "§7Besitzer (Spieler)§8: " + claimer.getColorCode() + chunk.getClaimedBy());
			return true;
		}

		// MEMBER COMMANDS

		if (args[0].equalsIgnoreCase("gui")) {
			if (player.getTeam() == null) {
				sender.sendMessage(claimer.getPrefix() + "Du bist in keinem Team!");
				return false;
			}

			new TeamMainMenu(player);
			return true;
		} else if (args[0].equalsIgnoreCase("claim")) {
			if (player.getTeam() == null) {
				sender.sendMessage(claimer.getPrefix() + "Du bist in keinem Team!");
				return false;
			}

			if (chunk != null) {
				sender.sendMessage(claimer.getPrefix() + "Dieser Chunk ist bereits geclaimt (/chunk info).");
				return false;
			}

			if (player.getTeam().hasMaximumChunksReached()) {
				sender.sendMessage(claimer.getPrefix() + "Die maximale Anzahl von Chunks wurde erreicht.");
				return false;
			}

			if (!player.getTeam().canAddChunk(worldChunk)) {
				sender.sendMessage(claimer.getPrefix() + "Du kannst nur an " + claimer.getColorCode() + claimer.getConfiguration().getMaxChunkGroups() + " §7verschiedenen Stellen Chunks claimen.");
				return false;
			}

			try {
				if (WorldGuardChecker.isInWorldGuardRegion(new Location(worldChunk.getWorld(), worldChunk.getX() * 16, player.getPlayer().getLocation().getY(), worldChunk.getZ() * 16))) {
					sender.sendMessage(claimer.getPrefix() + "Dieser Bereich ist von WorldGuard gesichert!");
					return false;
				}
			} catch (Exception | Error e) {}

			player.getTeam().addChunk(worldChunk, player);
			sender.sendMessage(claimer.getPrefix() + "Du hast den Chunk erfolgreich geclaimt!");
			return true;
		} else if (args[0].equalsIgnoreCase("unclaim")) {
			if (player.getTeam() == null) {
				sender.sendMessage(claimer.getPrefix() + "Du bist in keinem Team!");
				return false;
			}

			if (chunk == null) {
				sender.sendMessage(claimer.getPrefix() + "Dieser Chunk ist noch nicht geclaimt.");
				return false;
			}

			if (!player.getTeam().equals(chunk.getTeam()) && !player.getPlayer().hasPermission("chunk.admin")) {
				sender.sendMessage(claimer.getPrefix() + "Dieser Chunk gehört nicht deinem Team!");
				return false;
			}

			player.getTeam().removeChunk(chunk);
			sender.sendMessage(claimer.getPrefix() + "Du hast den Chunk erfolgreich entclaimt!");
			return true;
		} else if (args[0].equalsIgnoreCase("list")) {
			new ChunkListMenu(player);
			return true;
		}

		// MOD COMMANDS

		if (args[0].equalsIgnoreCase("flag")) {
			if (player.getTeam() == null) {
				sender.sendMessage(claimer.getPrefix() + "Du bist in keinem Team!");
				return false;
			}

			if (player.getTeam().getMemberType(player) == TeamMemberType.MEMBER) {
				sender.sendMessage(claimer.getPrefix() + "Du musst Team-Moderator sein, um diese Einstellung vornehmen zu können!");
				return false;
			}

			if (args.length < 3) {
				sender.sendMessage(claimer.getPrefix() + "/team flag <pvp/use/build> <true/false>");
				return false;
			}

			ChunkFlag flag = null;
			try {
				flag = ChunkFlag.valueOf(args[1].toUpperCase());
			} catch (IllegalArgumentException e) {
				sender.sendMessage(claimer.getPrefix() + "Dieser Flag wurde nicht gefunden (/team flag).");
				return false;
			}

			boolean enabled = false;
			try {
				enabled = Boolean.valueOf(args[2]);
			} catch (Exception e) {
				sender.sendMessage(claimer.getPrefix() + "/team flag <pvp/use/build> <true/false>");
				return false;
			}

			if (player.getTeam().getFlag(flag) == enabled) {
				sender.sendMessage(claimer.getPrefix() + flag.toString() + " ist bereits auf " + enabled + "!");
				return false;
			}

			player.getTeam().setFlag(flag, enabled);
			sender.sendMessage(claimer.getPrefix() + "Der Flag wurde erfolgreich gesetzt!");
			return true;
		}

		sender.sendMessage(claimer.getPrefix() + "Dieser Befehl existiert nicht (/chunk).");
		return false;
	}
}