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
			sender.sendMessage(claimer.getPrefix() + "/chunk map - Öffnet ein maximal großes GUI in dem man eine 'Karte' der Umgebung");
			sender.sendMessage(claimer.getPrefix() + "/chunk info [x] [z] - Zeigt Infos zu dem Chunk auf dem du stehst");
			sender.sendMessage(claimer.getPrefix() + "/chunk claim [x] [z]- Claimt den Chunk für das Team");
			sender.sendMessage(claimer.getPrefix() + "/chunk unclaim [x] [z] - Unclaimt den Chunk vom Team");
			sender.sendMessage(claimer.getPrefix() + "/chunk list - Öffnet ein GUI mit allen Chunks deines Teams");
			sender.sendMessage(claimer.getPrefix() + "/chunk flag <pvp/use/build> <true/false> - Stellt die Flags für alle Chunks des Teams ein, wenn nur /chunk flag dann GUI");
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
		if (claimer.getConfiguration().getBlacklistetWorlds().contains(player.getPlayer().getWorld().getName())) {
			sender.sendMessage(claimer.getPrefix() + "In dieser Welt kannst du das Chunksystem nicht nutzen!");
			return false;
		}

		if (args[0].equalsIgnoreCase("map")) {
			new ChunkMapMenu(claimer, player.getPlayer());
			return true;
		} else if (args[0].equalsIgnoreCase("info")) {
			if (chunk == null) {
				sender.sendMessage(claimer.getPrefix() + "Chunk ungeclaimt!");
				return false;
			}

			sender.sendMessage(claimer.getPrefix() + "§5Chunk §7" + chunk.getLocationX() + "§8:§7" + chunk.getLocationZ() + " §7in " + chunk.getWorld() + "§7:");
			sender.sendMessage(claimer.getPrefix() + "§7Claimed at: " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(chunk.getClaimedAt()));
			sender.sendMessage(claimer.getPrefix() + "§7Chunk-Team: " + chunk.getTeam().getDisplayname());
			sender.sendMessage(claimer.getPrefix() + "§7Claimed by: " + chunk.getClaimedBy());
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
				sender.sendMessage(claimer.getPrefix() + "Dieser Chunk ist bereits geclaimt (/chunk info)");
				return false;
			}

			if (player.getTeam().hasMaximumChunksReached()) {
				sender.sendMessage(claimer.getPrefix() + "Dein Team hat bereits die volle Anzahl an Chunks erreicht!");
				return false;
			}

			if (!player.getTeam().canAddChunk(worldChunk)) {
				sender.sendMessage(claimer.getPrefix() + "Dein Team hat bereits die volle Anzahl an Chunkregionen erreicht!");
				return false;
			}

			try {
				if (WorldGuardChecker.isInWorldGuardRegion(new Location(worldChunk.getWorld(), worldChunk.getX() * 16, player.getPlayer().getLocation().getY(), worldChunk.getZ() * 16))) {
					sender.sendMessage(claimer.getPrefix() + "Dieser Bereich ist von WorldGuard gesichert!");
					return false;
				}
			} catch (Exception | Error e) {}

			player.getTeam().addChunk(worldChunk, player);
			sender.sendMessage(claimer.getPrefix() + "Chunk erfolgreich geclaimt!");
			return true;
		} else if (args[0].equalsIgnoreCase("unclaim")) {
			if (player.getTeam() == null) {
				sender.sendMessage(claimer.getPrefix() + "Du bist in keinem Team!");
				return false;
			}

			if (chunk == null) {
				sender.sendMessage(claimer.getPrefix() + "Dieser Chunk ist noch nicht geclaimt!");
				return false;
			}

			if (!player.getTeam().equals(chunk.getTeam()) && !player.getPlayer().hasPermission("chunk.admin")) {
				sender.sendMessage(claimer.getPrefix() + "Dieser Chunk gehört nicht deinem Team!");
				return false;
			}

			player.getTeam().removeChunk(chunk);
			sender.sendMessage(claimer.getPrefix() + "Chunk erfolgreich unclaimed!");
			return true;
		} else if (args[0].equalsIgnoreCase("list")) {
			new ChunkListMenu(player);
			return true;
		}

		// MOD COMMANDS

		// if (!player.getTeam().equals(chunk.getTeam())) {
		// sender.sendMessage(tcc.getPrefix() + "Dieser Chunk gehört nicht deinem Team!");
		// return false;
		// }

		if (args[0].equalsIgnoreCase("flag")) {
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
				sender.sendMessage(claimer.getPrefix() + "Flag nicht gefunden! /team flag");
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
			sender.sendMessage(claimer.getPrefix() + "Flag erfolgreich gesetzt!");
			return true;
		}

		sender.sendMessage(claimer.getPrefix() + "Command nicht gefunden! /chunk");
		return false;
	}
}