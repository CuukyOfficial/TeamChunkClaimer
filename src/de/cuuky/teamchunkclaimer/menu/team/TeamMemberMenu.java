package de.cuuky.teamchunkclaimer.menu.team;

import de.cuuky.cfw.inventory.ItemClick;
import de.cuuky.cfw.inventory.list.AdvancedListInventory;
import de.cuuky.cfw.item.ItemBuilder;
import de.cuuky.teamchunkclaimer.ChunkClaimer;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TeamMemberMenu extends AdvancedListInventory<ChunkPlayer> {

	private final ChunkClaimer claimer;
	private final ChunkPlayer player;

	public TeamMemberMenu(ChunkPlayer player) {
		super(player.getHandler().getClaimer().getCuukyFrameWork().getAdvancedInventoryManager(), player.getPlayer(), 54);

		this.claimer = player.getHandler().getClaimer();
		this.player = player;
	}

	@Override
	protected ItemStack getFillerStack() {
		return null;
	}

	@Override
	public String getTitle() {
		return "§7Member " + player.getTeam().getDisplayname();
	}

	@Override
	protected List<ChunkPlayer> getList() {
		return new ArrayList<>(player.getTeam().getMembers().keySet());
	}

	@Override
	protected ItemStack getItemStack(ChunkPlayer member) {
		return new ItemBuilder().playername(member.getName()).displayname("§7" + member.getName())
				.lore("§7Rank§8: " + claimer.getColorCode() + member.getTeam().getMemberType(member).toString())
				.buildSkull();
	}

	@Override
	protected ItemClick getClick(ChunkPlayer chunkPlayer) {
		return null;
	}

//	@Override
//	public boolean onOpen() {
//		List<ChunkPlayer> members = player.getTeam().getMembers().keySet().stream().collect(Collectors.toList());
//
//		int start = getSize() * (getPage() - 1);
//		for (int i = 0; i != getSize(); i++) {
//			if (start >= members.size())
//				break;
//
//			ChunkPlayer member = members.get(start);
//			linkItemTo(i, new ItemBuilder().playername(member.getName()).displayname("§7" + member.getName()).lore("§7Rank§8: " + claimer.getColorCode() + member.getTeam().getMemberType(member).toString()).buildSkull());
//			start++;
//		}
//
//		return calculatePages(members.size(), getSize()) == page;
//	}
}