package de.cuuky.teamchunkclaimer.menu.team;

import de.cuuky.cfw.inventory.ItemClick;
import de.cuuky.cfw.inventory.list.AdvancedListInventory;
import de.cuuky.cfw.utils.item.BuildSkull;
import de.cuuky.teamchunkclaimer.ChunkClaimer;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class TeamMemberMenu extends AdvancedListInventory<ChunkPlayer> {

	private final ChunkClaimer claimer;
	private final ChunkPlayer player;

	public TeamMemberMenu(ChunkPlayer player) {
		super(player.getHandler().getClaimer().getCuukyFrameWork().getAdvancedInventoryManager(), player.getPlayer(),
            new ArrayList<>(player.getTeam().getMembers().keySet()));

		this.claimer = player.getHandler().getClaimer();
		this.player = player;
	}

	@Override
	public int getSize() {
		return 54;
	}

	@Override
    public ItemStack getFillerStack() {
		return null;
	}

	@Override
	protected ItemStack getItemStack(ChunkPlayer member) {
		return new BuildSkull().player(member.getName()).displayName("§7" + member.getName())
				.lore("§7Rank§8: " + claimer.getColorCode() + member.getTeam().getMemberType(member).toString())
				.build();
	}

	@Override
	protected ItemClick getClick(ChunkPlayer chunkPlayer) {
		return null;
	}

	@Override
	public String getTitle() {
		return "§7Member " + player.getTeam().getDisplayname();
	}
}