package de.cuuky.teamchunkclaimer.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import de.cuuky.cfw.utils.DirectionFace;
import de.cuuky.cfw.version.BukkitVersion;
import de.cuuky.cfw.version.VersionUtils;
import de.cuuky.teamchunkclaimer.ChunkClaimer;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ClaimChunk;

public class BorderCommand implements CommandExecutor {

	private ChunkClaimer instance;

	private Map<ChunkPlayer, BukkitTask> borders;

	public BorderCommand(ChunkClaimer instance) {
		this.instance = instance;

		this.borders = new HashMap<ChunkPlayer, BukkitTask>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(instance.getPrefix() + "Not for console!");
			return false;
		}

		ChunkPlayer player = this.instance.getEntityHandler().getPlayer(((Player) sender).getUniqueId().toString());
		if (borders.containsKey(player)) {
			borders.get(player).cancel();
			borders.remove(player);
			sender.sendMessage(instance.getPrefix() + "Du siehst nun keine Grenzen mehr!");
			return true;
		}

		if (player.getTeam() == null) {
			sender.sendMessage(instance.getPrefix() + "Du bist in keinem Team, weshalb du dir nicht die Grenzen deines Grundstücks anzeigen lassen kannst!");
			return false;
		}

		if (args.length == 0) {
			sender.sendMessage(instance.getPrefix() + "/border outline - Zeigt alle Grenzen nach außen an");
			sender.sendMessage(instance.getPrefix() + "/border all - Zeigt alle Grenzen der Chunks an");
			return false;
		}

		sendBorders(player, args[0].equalsIgnoreCase("all") ? 0 : 1);
		sender.sendMessage(instance.getPrefix() + "Dir werden nun die Grenzen angezeigt!");
		return true;
	}

	public void sendBorders(ChunkPlayer player, int mode) {
		this.borders.put(player, new BukkitRunnable() {

			World world = player.getPlayer().getWorld();

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				for (ClaimChunk chunk : player.getTeam().getClaimedChunks()) {
					int yStart = player.getPlayer().getLocation().getBlockY() - 2, chunkX = chunk.getLocationX() - 8, chunkZ = chunk.getLocationZ() - 8;
					faceLoop: for (DirectionFace face : DirectionFace.values()) {
						if (mode == 1) {
							Chunk neighbour = null;
							switch (face) {
							case NORTH:
								neighbour = world.getChunkAt(chunk.getChunkX(), chunk.getChunkZ() - 1);
								break;
							case WEST:
								neighbour = world.getChunkAt(chunk.getChunkX() + 1, chunk.getChunkZ());
								break;
							case SOUTH:
								neighbour = world.getChunkAt(chunk.getChunkX(), chunk.getChunkZ() + 1);
								break;
							case EAST:
								neighbour = world.getChunkAt(chunk.getChunkX() - 1, chunk.getChunkZ());
								break;
							default:
								break;
							}

							ClaimChunk claimed = instance.getEntityHandler().getChunk(neighbour);
							if (claimed != null && claimed.getTeam().equals(player.getTeam()))
								continue faceLoop;
						}

						for (int x = 0; x <= 16; x++) {
							for (int y = yStart; y <= yStart + 18; y++) {
								final double[] locs = face.modifyValues(x, 0);

								final int add = face == DirectionFace.WEST || face == DirectionFace.SOUTH ? 16 : 0;
								final int locX = (int) locs[0] + chunkX + add, locY = y, locZ = (int) locs[1] + chunkZ + add;
								Effect effect = null;
								if (VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_12))
									// 1.13+
									effect = Effect.valueOf("VILLAGER_PLANT_GROW");
								else
									// < 1.12
									effect = Effect.valueOf("VILLAGER_HAPPY");
								player.getPlayer().playEffect(new Location(world, locX, locY, locZ), effect, 1);
							}
						}
					}
				}
			}
		}.runTaskTimerAsynchronously(instance.getPlugin(), 0, 20));
	}
}