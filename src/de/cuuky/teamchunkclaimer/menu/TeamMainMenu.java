package de.cuuky.teamchunkclaimer.menu;

import de.cuuky.cfw.utils.item.BuildItem;
import de.cuuky.cfw.utils.item.BuildSkull;
import de.cuuky.cfw.version.types.Materials;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.TeamMemberType;
import de.cuuky.teamchunkclaimer.menu.team.ChunkListMenu;
import de.cuuky.teamchunkclaimer.menu.team.TeamMemberMenu;
import de.cuuky.teamchunkclaimer.menu.team.TeamOptionsMenu;

public class TeamMainMenu extends ChunkClaimerMenu {

    private final ChunkPlayer player;

    public TeamMainMenu(ChunkPlayer player) {
        super(player.getHandler().getClaimer().getCuukyFrameWork().getAdvancedInventoryManager(), player.getPlayer());

        this.player = player;
    }

    @Override
    public String getTitle() {
        return "§7Team " + player.getTeam().getDisplayname();
    }

    @Override
    public void refreshContent() {
        this.addItem(10, new BuildSkull().player(player.getTeam().getOwner().getName()).displayName("§5Mitglieder").build(),
                e -> this.openNext(new TeamMemberMenu(player)));

        if (player.getTeam().getMemberType(player) != TeamMemberType.MEMBER)
            this.addItem(12, new BuildItem().itemstack(Materials.COMMAND_BLOCK.parseItem()).displayName("§cEinstellungen").build(),
                    e -> this.openNext(new TeamOptionsMenu(player)));

        this.addItem(14, new BuildItem().itemstack(Materials.GRASS_BLOCK.parseItem()).displayName("§a" + player.getTeam().getClaimedChunks().size() + "§7/" + (player.getTeam().hasMaximumChunksReached() ? "§c" : "§a") + player.getTeam().getAllowedChunkAmount() + " §aChunks §7geclaimt").build(),
                e -> this.openNext(new ChunkListMenu(player)));

        this.addItem(16, new BuildItem().itemstack(Materials.MAP.parseItem()).displayName("§2Karte öffnen").build(),
                e -> this.openNext(new ChunkMapMenu(player.getHandler().getClaimer(), player.getPlayer())));
    }

    @Override
    public int getSize() {
        return 27;
    }
}
