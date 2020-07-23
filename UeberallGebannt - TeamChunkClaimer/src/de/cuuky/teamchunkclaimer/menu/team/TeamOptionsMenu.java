package de.cuuky.teamchunkclaimer.menu.team;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import de.cuuky.cfw.item.ItemBuilder;
import de.cuuky.cfw.menu.SuperInventory;
import de.cuuky.cfw.menu.utils.PageAction;
import de.cuuky.cfw.version.types.Materials;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.menu.TeamMainMenu;
import de.cuuky.teamchunkclaimer.menu.team.options.FlagOptionsMenu;
import de.cuuky.teamchunkclaimer.menu.team.options.GeneralOptionsMenu;

public class TeamOptionsMenu extends SuperInventory {

	private ChunkPlayer player;

	public TeamOptionsMenu(ChunkPlayer player) {
		super("§7Einstellungsmenü", player.getPlayer(), 27, false);

		this.setModifier = true;
		this.fillInventory = true;

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
		linkItemTo(12, new ItemBuilder().displayname("§aGenerelle Einstellungen").itemstack(Materials.SIGN.parseItem()).build(), new Runnable() {

			@Override
			public void run() {
				new GeneralOptionsMenu(player);
			}
		});

		linkItemTo(14, new ItemBuilder().displayname("§5Flags").itemstack(Materials.NAME_TAG.parseItem()).build(), new Runnable() {

			@Override
			public void run() {
				new FlagOptionsMenu(player);
			}
		});
		return true;
	}
}