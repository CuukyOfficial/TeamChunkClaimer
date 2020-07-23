package de.cuuky.teamchunkclaimer.menu.team.options;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import de.cuuky.cfw.item.ItemBuilder;
import de.cuuky.cfw.menu.SuperInventory;
import de.cuuky.cfw.menu.utils.PageAction;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ChunkFlag;
import de.cuuky.teamchunkclaimer.menu.team.TeamOptionsMenu;

public class FlagOptionsMenu extends SuperInventory {

	private ChunkPlayer player;

	public FlagOptionsMenu(ChunkPlayer player) {
		super("§7Flags", player.getPlayer(), 27, false);

		this.setModifier = true;

		this.player = player;

		player.getHandler().getTcc().getCfw().getInventoryManager().registerInventory(this);
		open();
	}

	@Override
	public boolean onBackClick() {
		new TeamOptionsMenu(player);
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
		int start = 11;
		for (ChunkFlag flag : ChunkFlag.values()) {
			linkItemTo(start, new ItemBuilder().displayname((player.getTeam().getFlag(flag) ? "§2" : "§c") + flag.getName()).lore("§aBeschreibung§8: §7" + flag.getDescription(), "§7Aktiviert§8: " + (player.getTeam().getFlag(flag) ? "§2Ja" : "§cNein")).itemstack(flag.getMaterial().parseItem()).build(), new Runnable() {

				@Override
				public void run() {
					player.getTeam().setFlag(flag, !player.getTeam().getFlag(flag));
				}
			});

			start += 2;
		}
		return true;
	}
}