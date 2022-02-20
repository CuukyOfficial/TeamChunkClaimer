package de.cuuky.teamchunkclaimer.menu.team;

import de.cuuky.cfw.inventory.ItemClick;
import de.cuuky.cfw.inventory.list.AdvancedListInventory;
import de.cuuky.cfw.utils.item.BuildSkull;
import de.cuuky.teamchunkclaimer.ChunkClaimer;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.TeamMemberType;
import org.bukkit.event.inventory.InventoryClickEvent;
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
	protected ItemStack getItemStack(ChunkPlayer member) {
		return new BuildSkull().player(member.getName()).displayName(member.getTeam().getMemberType(member) == TeamMemberType.MEMBER ? "§7" : "§c" + member.getName())
				.lore("§7Rank§8: " + claimer.getColorCode() + member.getTeam().getMemberType(member).toString(),
                    ((member.getTeam().getMemberType(member) == TeamMemberType.MEMBER && player.getTeam().getMemberType(player) == TeamMemberType.OWNER)
                        ? "§7Klicke zum Befördern." : ""))
				.build();
	}

	@Override
	protected ItemClick getClick(ChunkPlayer chunkPlayer) {
		return inventoryClickEvent -> {
            String displayName = inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName();
            if (player.getTeam().getMemberType(player) == TeamMemberType.OWNER) {
                claimer.getPlugin().getServer().dispatchCommand(player.getPlayer(), "team promote " + displayName.substring(2));
            }
        };
	}

	@Override
	public String getTitle() {
		return "§7Member " + player.getTeam().getDisplayname();
	}
}