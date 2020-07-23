package de.cuuky.teamchunkclaimer.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import de.cuuky.cfw.item.ItemBuilder;
import de.cuuky.cfw.menu.SuperInventory;
import de.cuuky.cfw.menu.utils.PageAction;
import de.cuuky.cfw.version.types.Materials;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.TeamMemberType;
import de.cuuky.teamchunkclaimer.menu.team.ChunkListMenu;
import de.cuuky.teamchunkclaimer.menu.team.TeamMemberMenu;
import de.cuuky.teamchunkclaimer.menu.team.TeamOptionsMenu;

public class TeamMainMenu extends SuperInventory {

	private ChunkPlayer player;

	public TeamMainMenu(ChunkPlayer player) {
		super("§7Team " + player.getTeam().getDisplayname(), player.getPlayer(), 27, true);

		this.setModifier = false;

		this.player = player;

		player.getHandler().getClaimer().getCuukyFrameWork().getInventoryManager().registerInventory(this);
		open();
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

	@Override
	public boolean onOpen() {
		linkItemTo(10, new ItemBuilder().playername(player.getTeam().getOwner().getName()).displayname("§5Mitgleider").buildSkull(), new Runnable() {

			@Override
			public void run() {
				new TeamMemberMenu(player);
			}
		});

		if (player.getTeam().getMemberType(player) != TeamMemberType.MEMBER)
			linkItemTo(12, new ItemBuilder().itemstack(Materials.COMMAND_BLOCK.parseItem()).displayname("§cEinstellungen").build(), new Runnable() {

				@Override
				public void run() {
					new TeamOptionsMenu(player);
				}
			});

		linkItemTo(14, new ItemBuilder().itemstack(Materials.GRASS_BLOCK.parseItem()).displayname("§a" + player.getTeam().getClaimedChunks().size() + "§7/" + (player.getTeam().hasMaximumChunksReached() ? "§c" : "§a") + player.getTeam().getAllowedChunkAmount() + " §aChunks §7geclaimt").build(), new Runnable() {

			@Override
			public void run() {
				new ChunkListMenu(player);
			}
		});

		linkItemTo(16, new ItemBuilder().itemstack(Materials.MAP.parseItem()).displayname("§2Karte öffnen").build(), new Runnable() {

			@Override
			public void run() {
				new ChunkMapMenu(player.getHandler().getClaimer(), player.getPlayer());
			}
		});
		return true;
	}
}
