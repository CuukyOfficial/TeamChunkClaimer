package de.cuuky.teamchunkclaimer.menu;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.cuuky.cfw.hooking.hooks.chat.ChatHook;
import de.cuuky.cfw.hooking.hooks.chat.ChatHookHandler;
import de.cuuky.cfw.item.ItemBuilder;
import de.cuuky.cfw.menu.SuperInventory;
import de.cuuky.cfw.menu.utils.ItemClickHandler;
import de.cuuky.cfw.menu.utils.PageAction;
import de.cuuky.cfw.utils.DirectionFace;
import de.cuuky.cfw.version.types.Materials;
import de.cuuky.teamchunkclaimer.ChunkClaimer;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ClaimChunk;

public class ChunkMapMenu extends SuperInventory {

	private ChunkClaimer claimer;
	private ChunkPlayer player;
	private DirectionFace face;

	int chunkJumpHorizontal, chunkJumpVertical;

	private Chunk center;

	public ChunkMapMenu(ChunkClaimer claimer, Player opener) {
		super("§aChunks §8(" + DirectionFace.getFace(opener.getLocation().getYaw()).getIdentifier() + "§8)", opener, 54, false);
		this.claimer = claimer;
		this.face = DirectionFace.getFace(opener.getLocation().getYaw());
		this.setModifier = true;
		this.fillInventory = false;

		this.chunkJumpHorizontal = 1;
		this.chunkJumpVertical = 1;

		this.center = this.opener.getLocation().getChunk();
		this.player = claimer.getEntityHandler().getPlayer(opener.getName());
		this.claimer.getCuukyFrameWork().getInventoryManager().registerInventory(this);

		open();
	}

	private int[] getDirectionChange(boolean horizontal, boolean left) {
		int xOffset = 0, zOffset = 0;
		switch (face) {
		case NORTH:
			xOffset = left ? 1 : -1;
			break;
		case EAST:
			zOffset = left ? 1 : -1;
			break;
		case SOUTH:
			xOffset = left ? -1 : 1;
			break;
		case WEST:
			zOffset = left ? -1 : 1;
			break;
		}

		return new int[] { horizontal ? xOffset : zOffset, horizontal ? zOffset : xOffset };
	}

	@Override
	public boolean onOpen() {
		double height = (45 / 9) / 2, width = 4;

		for (double y = height * -1; y <= height; y++) {
			for (double x = width * -1; x <= width; x++) {
				double[] coords = face.modifyValues(x, y);
				int chunkX = (int) (center.getX() + coords[0]), locationX = chunkX * 16;
				int chunkZ = (int) (center.getZ() + coords[1]), locationZ = chunkZ * 16;

				Chunk worldChunk = center.getWorld().getChunkAt(chunkX, chunkZ);
				ClaimChunk chunk = this.claimer.getEntityHandler().getChunk(worldChunk);
				ItemBuilder builder = new ItemBuilder().itemstack(Materials.WHITE_STAINED_GLASS_PANE.parseItem()).displayname("§fUnclaimed").lore("§7Location:", "§7X§8: §5" + (locationX + 8), "§7Z§8: §5" + (locationZ + 8), "§7Linksklick = claim/Chunk info", "§7Rechtsklick = unclaim/Team info", (worldChunk.equals(this.opener.getLocation().getChunk()) ? "§aDa bist du!" : ""));
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

						if (event.isRightClick())
							return;

						claimer.getPlugin().getServer().dispatchCommand(opener, "chunk claim " + locationX + " " + locationZ);
					}
				});

				linkItemTo(this.size - 2, new ItemBuilder().displayname("§aLinks").itemstack(Materials.ARROW.parseItem()).build(), () -> {
					int[] xz = getDirectionChange(true, false);
					center = center.getWorld().getChunkAt(center.getX() + (xz[0] * chunkJumpHorizontal), center.getZ() + (xz[1] * chunkJumpHorizontal));
					updateInventory();
				});

				linkItemTo(this.size - 1, new ItemBuilder().displayname("§cRechts").itemstack(Materials.ARROW.parseItem()).build(), () -> {
					int[] xz = getDirectionChange(true, true);
					center = center.getWorld().getChunkAt(center.getX() + (xz[0] * chunkJumpHorizontal), center.getZ() + (xz[1] * chunkJumpHorizontal));
					updateInventory();
				});

				linkItemTo(this.size - 9, new ItemBuilder().displayname("§aOben").itemstack(Materials.ARROW.parseItem()).build(), () -> {
					int[] xz = getDirectionChange(false, true);
					center = center.getWorld().getChunkAt(center.getX() + (xz[0] * chunkJumpVertical), center.getZ() + (xz[1] * chunkJumpVertical));
					updateInventory();
				});

				linkItemTo(this.size - 8, new ItemBuilder().displayname("§cUnten").itemstack(Materials.ARROW.parseItem()).build(), () -> {
					int[] xz = getDirectionChange(false, false);
					center = center.getWorld().getChunkAt(center.getX() + (xz[0] * chunkJumpVertical), center.getZ() + (xz[1] * chunkJumpVertical));
					updateInventory();
				});

				linkItemTo(this.size - 4, new ItemBuilder().displayname("§5Zentrieren").itemstack(Materials.DIAMOND.parseItem()).build(), new ItemClickHandler() {

					@Override
					public void onItemClick(InventoryClickEvent event) {
						center = opener.getLocation().getChunk();
						updateInventory();
					}
				});

				linkItemTo(this.size - 7, new ItemBuilder().displayname("§7Vertikale Chunks pro §aKlick§7: §a" + this.chunkJumpVertical).lore("§aLinks §7= hoch", "§cRechts §7= runter").itemstack(Materials.COAL.parseItem()).build(), new ItemClickHandler() {

					@Override
					public void onItemClick(InventoryClickEvent event) {
						chunkJumpVertical += event.isLeftClick() ? 1 : -1;
						updateInventory();
					}
				});
				
				linkItemTo(this.size - 3, new ItemBuilder().displayname("§7Horizontale Chunks pro §aKlick§7: §a" + this.chunkJumpHorizontal).lore("§aLinks §7= hoch", "§cRechts §7= runter").itemstack(Materials.COAL.parseItem()).build(), new ItemClickHandler() {

					@Override
					public void onItemClick(InventoryClickEvent event) {
						chunkJumpHorizontal += event.isLeftClick() ? 1 : -1;
						updateInventory();
					}
				});

				linkItemTo(this.size - 6, new ItemBuilder().displayname("§2Springe zu...").itemstack(Materials.MAP.parseItem()).build(), new ItemClickHandler() {

					@Override
					public void onItemClick(InventoryClickEvent event) {
						close(false);
						claimer.getCuukyFrameWork().getHookManager().registerHook(new ChatHook(player.getPlayer(), claimer.getPrefix() + "Koordinaten eingeben: §8(§7z.B.: §8'§f100 200§8')", new ChatHookHandler() {
							@Override
							public boolean onChat(AsyncPlayerChatEvent event) {
								try {
									String[] args = event.getMessage().split(" ");
									int x = Integer.valueOf(args[0]), z = Integer.valueOf(args[1]);
									center = center.getWorld().getChunkAt(x / 16, z / 16);
								} catch (Exception e) {
									player.getPlayer().sendMessage(claimer.getPrefix() + "X und Z sind keine Zahlen!");
									return false;
								}

								reopenSoon();
								return true;
							}
						}));
					}
				});
			}
		}

		return true;
	}

	@Override
	public boolean onBackClick() {
		if (player.getTeam() != null)
			new TeamMainMenu(player);
		return true;
	}

	@Override
	public void onClick(InventoryClickEvent event) {}

	@Override
	public void onClose(InventoryCloseEvent event) {}

	@Override
	public void onInventoryAction(PageAction action) {}

}