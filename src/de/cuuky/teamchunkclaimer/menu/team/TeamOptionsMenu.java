package de.cuuky.teamchunkclaimer.menu.team;

import de.cuuky.cfw.utils.item.BuildItem;
import de.cuuky.cfw.version.types.Materials;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.menu.ChunkClaimerMenu;
import de.cuuky.teamchunkclaimer.menu.team.options.FlagOptionsMenu;
import de.cuuky.teamchunkclaimer.menu.team.options.GeneralOptionsMenu;

public class TeamOptionsMenu extends ChunkClaimerMenu {

	private final ChunkPlayer player;

	public TeamOptionsMenu(ChunkPlayer player) {
		super(player.getHandler().getClaimer().getCuukyFrameWork().getAdvancedInventoryManager(), player.getPlayer());

		this.player = player;
	}

	@Override
	public String getTitle() {
		return "§7Einstellungsmenü";
	}

	@Override
	public int getSize() {
		return 27;
	}

	@Override
	public void refreshContent() {
		this.addItem(11, new BuildItem().displayName("§aGenerelle Einstellungen")
				.itemstack(Materials.SIGN.parseItem())
				.build(), (e) -> this.openNext(new GeneralOptionsMenu(player)));

		this.addItem(15,new BuildItem().displayName("§5Flags")
				.itemstack(Materials.NAME_TAG.parseItem())
				.build(), (e) -> this.openNext(new FlagOptionsMenu(player)));
	}
}