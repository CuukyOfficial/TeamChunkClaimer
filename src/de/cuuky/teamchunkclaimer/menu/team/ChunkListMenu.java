package de.cuuky.teamchunkclaimer.menu.team;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import de.cuuky.cfw.item.ItemBuilder;
import de.cuuky.cfw.menu.SuperInventory;
import de.cuuky.cfw.menu.utils.ItemClickHandler;
import de.cuuky.cfw.menu.utils.PageAction;
import de.cuuky.cfw.version.types.Materials;
import de.cuuky.teamchunkclaimer.ChunkClaimer;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ClaimChunk;
import de.cuuky.teamchunkclaimer.menu.ChunkMapMenu;
import de.cuuky.teamchunkclaimer.menu.TeamMainMenu;

public class ChunkListMenu extends SuperInventory {

	private ChunkPlayer player;
	private ChunkClaimer claimer;

	public ChunkListMenu(ChunkPlayer player) {
		super("§7Chunks " + player.getTeam().getDisplayname(), player.getPlayer(), 54, false);

		this.setModifier = true;
		this.fillInventory = false;
		this.claimer = player.getHandler().getClaimer();
		this.player = player;

		player.getHandler().getClaimer().getCuukyFrameWork().getInventoryManager().registerInventory(this);
		open();
	}

	@Override
	public boolean onBackClick() {
		new TeamMainMenu(player);
		return true;
	}

	@Override
	public void onClick(InventoryClickEvent event) {}

	@Override
	public void onClose(InventoryCloseEvent event) {}

	@Override
	public void onInventoryAction(PageAction action) {}

	@Override
	public boolean onOpen() {
		List<ClaimChunk> chunks = player.getTeam().getClaimedChunks();
		Collections.reverse(chunks);

		int start = getSize() * (getPage() - 1);
		for (int i = 0; i != getSize(); i++) {
			if (start >= chunks.size())
				break;

			ClaimChunk chunk = chunks.get(start);
			linkItemTo(i, new ItemBuilder().displayname("§7X§8: " + claimer.getColorCode() + chunk.getLocationX() + "§8, §7Z§8: " + claimer.getColorCode() + chunk.getLocationZ()).itemstack(new ItemStack(Materials.GRASS_BLOCK.parseItem())).lore("§7Claimer§8: " + claimer.getColorCode() + (chunk.getClaimedByPlayer() == null ? chunk.getClaimedBy() : chunk.getClaimedByPlayer().getName()), "§7Erstellungsdatum§8: " + claimer.getColorCode() + new SimpleDateFormat("HH:mm:ss dd.MM.YYYY").format(chunk.getClaimedAt()), "", "§aLinksklick§7, um auf der Karte anzuzeigen", "§cRechtsklick§7, um zu entclaimen").build(), new ItemClickHandler() {

				@Override
				public void onItemClick(InventoryClickEvent event) {
					if (event.isLeftClick()) {
						close(true);
						new ChunkMapMenu(claimer, opener, chunk.getChunk());
					} else
						player.getTeam().removeChunk(chunk);
				}
			});
			start++;
		}

		return calculatePages(chunks.size(), getSize()) == page;
	}
}