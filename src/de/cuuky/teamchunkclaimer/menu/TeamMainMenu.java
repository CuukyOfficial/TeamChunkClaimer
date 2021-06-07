package de.cuuky.teamchunkclaimer.menu;

import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.item.ItemBuilder;
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
    protected String getTitle() {
        return "§7Team " + player.getTeam().getDisplayname();
    }

    @Override
    protected void refreshContent() {
        this.addItem(10, new ItemBuilder().playername(player.getTeam().getOwner().getName()).displayname("§5Mitglieder").buildSkull(),
                e -> this.openNext(new TeamMemberMenu(player)));

        if (player.getTeam().getMemberType(player) != TeamMemberType.MEMBER)
            this.addItem(12, new ItemBuilder().itemstack(Materials.COMMAND_BLOCK.parseItem()).displayname("§cEinstellungen").build(),
                    e -> this.openNext(new TeamOptionsMenu(player)));

        this.addItem(14, new ItemBuilder().itemstack(Materials.GRASS_BLOCK.parseItem()).displayname("§a" + player.getTeam().getClaimedChunks().size() + "§7/" + (player.getTeam().hasMaximumChunksReached() ? "§c" : "§a") + player.getTeam().getAllowedChunkAmount() + " §aChunks §7geclaimt").build(),
                e -> this.openNext(new ChunkListMenu(player)));

        this.addItem(16, new ItemBuilder().itemstack(Materials.MAP.parseItem()).displayname("§2Karte öffnen").build(),
                e -> this.openNext(new ChunkMapMenu(player.getHandler().getClaimer(), player.getPlayer())));
    }

    @Override
    public int getSize() {
        return 27;
    }

//    @Override
//    public boolean onOpen() {
//        linkItemTo(10, new ItemBuilder().playername(player.getTeam().getOwner().getName()).displayname("§5Mitglieder").buildSkull(), new Runnable() {
//
//            @Override
//            public void run() {
//                new TeamMemberMenu(player);
//            }
//        });
//
//        if (player.getTeam().getMemberType(player) != TeamMemberType.MEMBER)
//            linkItemTo(12, new ItemBuilder().itemstack(Materials.COMMAND_BLOCK.parseItem()).displayname("§cEinstellungen").build(), new Runnable() {
//
//                @Override
//                public void run() {
//                    new TeamOptionsMenu(player);
//                }
//            });
//
//        linkItemTo(14, new ItemBuilder().itemstack(Materials.GRASS_BLOCK.parseItem()).displayname("§a" + player.getTeam().getClaimedChunks().size() + "§7/" + (player.getTeam().hasMaximumChunksReached() ? "§c" : "§a") + player.getTeam().getAllowedChunkAmount() + " §aChunks §7geclaimt").build(), new Runnable() {
//
//            @Override
//            public void run() {
//                new ChunkListMenu(player);
//            }
//        });
//
//        linkItemTo(16, new ItemBuilder().itemstack(Materials.MAP.parseItem()).displayname("§2Karte öffnen").build(), new Runnable() {
//
//            @Override
//            public void run() {
//                new ChunkMapMenu(player.getHandler().getClaimer(), player.getPlayer());
//            }
//        });
//        return true;
//    }
}
