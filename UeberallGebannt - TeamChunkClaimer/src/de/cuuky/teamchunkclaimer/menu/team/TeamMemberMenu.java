package de.cuuky.teamchunkclaimer.menu.team;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import de.cuuky.cfw.item.ItemBuilder;
import de.cuuky.cfw.menu.SuperInventory;
import de.cuuky.cfw.menu.utils.PageAction;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.menu.TeamMainMenu;

public class TeamMemberMenu extends SuperInventory {

	private ChunkPlayer player;

	public TeamMemberMenu(ChunkPlayer player) {
		super("§7Member " + player.getTeam().getDisplayname(), player.getPlayer(), 54, false);

		this.fillInventory = false;
		this.setModifier = true;

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
		List<ChunkPlayer> members = player.getTeam().getMembers().keySet().stream().collect(Collectors.toList());

		int start = getSize() * (getPage() - 1);
		for (int i = 0; i != getSize(); i++) {
			if (start >= members.size())
				break;

			ChunkPlayer member = members.get(start);
			linkItemTo(i, new ItemBuilder().playername(member.getName()).displayname("§7" + member.getName()).lore("§7Rank§8: §5" + member.getTeam().getMemberType(member).toString()).buildSkull());
			start++;
		}

		return calculatePages(members.size(), getSize()) == page;
	}
}