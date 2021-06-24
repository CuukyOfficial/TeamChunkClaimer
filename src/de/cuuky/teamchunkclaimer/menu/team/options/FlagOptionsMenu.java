package de.cuuky.teamchunkclaimer.menu.team.options;

import de.cuuky.cfw.inventory.ItemInserter;
import de.cuuky.cfw.inventory.inserter.DirectInserter;
import de.cuuky.cfw.item.ItemBuilder;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ChunkFlag;
import de.cuuky.teamchunkclaimer.menu.ChunkClaimerMenu;

public class FlagOptionsMenu extends ChunkClaimerMenu {

    private final ChunkPlayer player;

    public FlagOptionsMenu(ChunkPlayer player) {
        super(player.getHandler().getClaimer().getCuukyFrameWork().getAdvancedInventoryManager(), player.getPlayer());

        this.player = player;
    }

    @Override
    public String getTitle() {
        return "§7Flags";
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public void refreshContent() {
        int start = 11;
        for (ChunkFlag flag : ChunkFlag.values()) {
            addItem(start, new ItemBuilder().displayname((player.getTeam().getFlag(flag) ? "§2" : "§c") + flag.getName())
                            .lore("§aBeschreibung§8: §7" + flag.getDescription(), "§7Aktiviert§8: " + (player.getTeam().getFlag(flag) ? "§2Ja" : "§cNein"))
                            .itemstack(flag.getMaterial().parseItem()).build(),
                    e -> {
                        player.getTeam().setFlag(flag, !player.getTeam().getFlag(flag));
                        this.updateOthers(inv ->
                                inv instanceof FlagOptionsMenu && ((FlagOptionsMenu) inv).player.equals(player));
                    });

            start += 2;
        }
    }
}