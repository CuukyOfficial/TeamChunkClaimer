package de.cuuky.teamchunkclaimer.menu;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import de.cuuky.cfw.item.ItemBuilder;
import de.cuuky.cfw.menu.SuperInventory;
import de.cuuky.cfw.menu.utils.ItemClickHandler;
import de.cuuky.cfw.menu.utils.PageAction;
import de.cuuky.cfw.version.types.Materials;
import de.cuuky.teamchunkclaimer.ChunkClaimer;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ClaimChunk;

public class ChunkMapMenu extends SuperInventory {

	private static enum Face {

		NORTH("NORTH", 135f, -135f),
		EAST("EAST", -135f, -45f),
		SOUTH("SOUTH", -45f, 45f),
		WEST("WEST", 45f, 135f);

		private String identifier;
		private float start, end;

		private Face(String identifier, float start, float end) {
			this.identifier = identifier;
			this.start = start;
			this.end = end;
		}

		public String getIdentifier() {
			return identifier;
		}

		public boolean isIn(float yaw) {
			return start <= yaw && end > yaw;
		}

		public double[] modifyValues(double x, double z) {
			switch (this) {
			case EAST:
				return new double[] { -z, x };
			case WEST:
				return new double[] { z, -x };
			case SOUTH:
				return new double[] { -x, -z };
			default:
				break;
			}

			return new double[] { x, z };
		}

		public static Face getFace(float yaw) {
			yaw = yaw >= 180 ? -180 + (yaw - 180) : yaw;
			for (Face face : values())
				if (face.isIn(yaw))
					return face;

			return NORTH;
		}
	}

	private ChunkClaimer claimer;
	private ChunkPlayer player;
	private Face face;

	public ChunkMapMenu(ChunkClaimer claimer, Player opener) {
		super("§aChunkMap §8" + Face.getFace(opener.getLocation().getYaw()).getIdentifier(), opener, 45, false);
		this.claimer = claimer;
		this.face = Face.getFace(opener.getLocation().getYaw());
		this.setModifier = false;
		this.fillInventory = false;

		this.player = claimer.getEntityHandler().getPlayer(opener.getName());
		this.claimer.getCfw().getInventoryManager().registerInventory(this);
		open();
	}

	@Override
	public boolean onOpen() {
		double height = (45 / 9) / 2, width = 4;

		Chunk from = this.opener.getLocation().getChunk();

		for (double y = height * -1; y <= height; y++) {
			for (double x = width * -1; x <= width; x++) {
				double[] coords = face.modifyValues(x, y);
				int chunkX = (int) (from.getX() + coords[0]), locationX = chunkX * 16;
				int chunkZ = (int) (from.getZ() + coords[1]), locationZ = chunkZ * 16;

				ItemBuilder builder = new ItemBuilder().itemstack(Materials.WHITE_STAINED_GLASS_PANE.parseItem()).displayname("§fUnclaimed").lore("§7Location:", "§7X§8: §5" + (locationX + 8), "§7Z§8: §5" + (locationZ + 8), "§7Linksklick = claim/Chunk info", "§7Rechtsklick = unclaim/Team info", (x == 0 && y == 0 ? "§aDa bist du!" : ""));
				Chunk worldChunk = from.getWorld().getChunkAt(chunkX, chunkZ);
				ClaimChunk chunk = this.claimer.getEntityHandler().getChunk(worldChunk);
				if (chunk != null) {
					if (player.getTeam() == null || !player.getTeam().equals(chunk.getTeam()))
						builder.itemstack(Materials.RED_STAINED_GLASS_PANE.parseItem());
					else
						builder.itemstack(Materials.GREEN_STAINED_GLASS_PANE.parseItem());

					builder.displayname(chunk.getTeam().getDisplayname());
				}

				double invX = x + width;
				double invY = y + height;
				linkItemTo((int) ((invY * 9) + invX), builder.build(), new ItemClickHandler() {

					@Override
					public void onItemClick(InventoryClickEvent event) {
						ClaimChunk chunk = claimer.getEntityHandler().getChunk(worldChunk);
						if (chunk != null) {
							if (player.getTeam() != null && player.getTeam().equals(chunk.getTeam()) && event.isRightClick()) {
								claimer.getPlugin().getServer().dispatchCommand(opener, "chunk unclaim " + locationX + " " + locationZ);
								return;
							}

							if (event.isLeftClick())
								claimer.getPlugin().getServer().dispatchCommand(opener, "chunk info " + locationX + " " + locationZ);
							else
								claimer.getPlugin().getServer().dispatchCommand(opener, "team info " + chunk.getTeam().getName());
							return;
						}

						if (player.getTeam() == null || event.isRightClick())
							return;

						claimer.getPlugin().getServer().dispatchCommand(opener, "chunk claim " + locationX + " " + locationZ);
					}
				});
			}
		}
		return true;
	}

	@Override
	public boolean onBackClick() {
		return false;
	}

	@Override
	public void onClick(InventoryClickEvent event) {}

	@Override
	public void onClose(InventoryCloseEvent event) {}

	@Override
	public void onInventoryAction(PageAction action) {}

}