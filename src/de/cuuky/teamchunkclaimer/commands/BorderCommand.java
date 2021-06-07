package de.cuuky.teamchunkclaimer.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import de.cuuky.teamchunkclaimer.ChunkClaimer;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ClaimChunk;

public class BorderCommand implements CommandExecutor {

	private static final int WALL_HEIGHT = 18, WALL_WIDTH = 16, MAX_PARTICLES = 4000;

	private ChunkClaimer instance;

	private Map<ChunkPlayer, BukkitTask> borders;
	private Object borderEffect;
	private Method spawnParticleMethod;

	public BorderCommand(ChunkClaimer instance) {
		this.instance = instance;

		this.borders = new HashMap<ChunkPlayer, BukkitTask>();

		try {
			Class<?> particleClass = Class.forName("org.bukkit.Particle");
			this.spawnParticleMethod = Player.class.getMethod("spawnParticle", particleClass, double.class, double.class, double.class, int.class);
			this.borderEffect = particleClass.getDeclaredField("VILLAGER_HAPPY").get(null);
		} catch (Exception e) {
			this.borderEffect = Effect.valueOf("HAPPY_VILLAGER");
		}
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
			sender.sendMessage(instance.getPrefix() + "Hinweis: Es werden nicht alle Chunkgrenzen gleichzeitig angezeigt (immer die, die am nächsten von dir sind)");
			return false;
		}

		sendBorders(player, args[0].equalsIgnoreCase("all") ? 0 : 1);
		sender.sendMessage(instance.getPrefix() + "Dir werden nun die Grenzen angezeigt!");
		return true;
	}

	public void sendBorders(ChunkPlayer player, int mode) {
		this.borders.put(player, new BukkitRunnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if (!player.isOnline() || player.getTeam() == null)
					return;

				Location pLocation = player.getPlayer().getLocation();
				Map<Double, ClaimChunk> chunks = new TreeMap<Double, ClaimChunk>();
				for (ClaimChunk chunk : new ArrayList<>(player.getTeam().getClaimedChunks())) {
					int chunkX = chunk.getLocationX(), chunkZ = chunk.getLocationZ();
					double distance = Math.sqrt(Math.pow(chunkX - pLocation.getX(), 2) + Math.pow(chunkZ - pLocation.getZ(), 2));
					if (distance <= 60)
						chunks.put(distance, chunk);
				}

				World world = player.getPlayer().getWorld();

				int particlesSent = 0;
				for (double distance : chunks.keySet()) {
					ClaimChunk chunk = chunks.get(distance);
					int yStart = player.getPlayer().getLocation().getBlockY() - 2, chunkX = chunk.getLocationX() - 8, chunkZ = chunk.getLocationZ() - 8;

					List<DirectionFace> toRender = chunk.getBorders(mode == 1);
					int possibleParticles = (WALL_HEIGHT * WALL_WIDTH) * toRender.size();
					if (particlesSent + possibleParticles >= MAX_PARTICLES)
						return;
					else
						particlesSent += possibleParticles;

					for (DirectionFace face : toRender) {
						final int add = face == DirectionFace.WEST || face == DirectionFace.SOUTH ? 16 : 0;
						for (int x = 0; x <= WALL_WIDTH; x++) {
							for (int y = yStart; y <= yStart + WALL_HEIGHT; y++) {
								final double[] locs = face.modifyValues(x, 0);
								final int locX = (int) locs[0] + chunkX + add, locY = y, locZ = (int) locs[1] + chunkZ + add;

								if (spawnParticleMethod != null)
									try {
										spawnParticleMethod.invoke(player.getPlayer(), borderEffect, locX, locY, locZ, 1);
									} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
										e.printStackTrace();
									}
								else
									player.getPlayer().playEffect(new Location(world, locX, locY, locZ), (Effect) borderEffect, 1);
							}
						}
					}
				}
			}
		}.runTaskTimerAsynchronously(instance.getPlugin(), 0, 20));
	}
}