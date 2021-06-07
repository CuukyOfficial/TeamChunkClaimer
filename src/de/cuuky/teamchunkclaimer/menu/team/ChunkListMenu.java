package de.cuuky.teamchunkclaimer.menu.team;

import de.cuuky.cfw.inventory.ItemClick;
import de.cuuky.cfw.inventory.list.AdvancedListInventory;
import de.cuuky.cfw.item.ItemBuilder;
import de.cuuky.cfw.version.types.Materials;
import de.cuuky.teamchunkclaimer.ChunkClaimer;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ClaimChunk;
import de.cuuky.teamchunkclaimer.menu.ChunkMapMenu;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChunkListMenu extends AdvancedListInventory<ClaimChunk> {

    private final ChunkPlayer player;
    private final ChunkClaimer claimer;

    public ChunkListMenu(ChunkPlayer player) {
        super(player.getHandler().getClaimer().getCuukyFrameWork().getAdvancedInventoryManager(), player.getPlayer(), 54);

        this.claimer = player.getHandler().getClaimer();
        this.player = player;
    }

    @Override
    protected List<ClaimChunk> getList() {
        List<ClaimChunk> chunks = new ArrayList<>(player.getTeam().getClaimedChunks());
        Collections.reverse(chunks);
        return chunks;
    }

    @Override
    protected String getTitle() {
        return "§7Chunks " + player.getTeam().getDisplayname();
    }

    @Override
    protected ItemStack getFillerStack() {
        return null;
    }

    @Override
    protected boolean copyList() {
        return false;
    }

    @Override
    protected ItemStack getItemStack(ClaimChunk chunk) {
        return new ItemBuilder().displayname("§7X§8: " + claimer.getColorCode() + chunk.getLocationX() + "§8, §7Z§8: " + claimer.getColorCode() + chunk.getLocationZ()).itemstack(new ItemStack(Materials.GRASS_BLOCK.parseItem())).lore("§7Claimer§8: " + claimer.getColorCode() + (chunk.getClaimedByPlayer() == null ? chunk.getClaimedBy() : chunk.getClaimedByPlayer().getName()), "§7Erstellungsdatum§8: " + claimer.getColorCode() + new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(chunk.getClaimedAt()), "", "§aLinksklick§7, um auf der Karte anzuzeigen", "§cRechtsklick§7, um zu entclaimen").build();
    }

    @Override
    protected ItemClick getClick(ClaimChunk chunk) {
        return (event) -> {
            if (event.isLeftClick())
                this.openNext(new ChunkMapMenu(claimer, player.getPlayer(), chunk.getChunk()));
            else
                player.getTeam().removeChunk(chunk);
        };
    }

//    @Override
//    public boolean onOpen() {
//        List<ClaimChunk> chunks = player.getTeam().getClaimedChunks();
//        Collections.reverse(chunks);
//
//        int start = getSize() * (getPage() - 1);
//        for (int i = 0; i != getSize(); i++) {
//            if (start >= chunks.size())
//                break;
//
//            ClaimChunk chunk = chunks.get(start);
//            linkItemTo(i, new ItemBuilder().displayname("§7X§8: " + claimer.getColorCode() + chunk.getLocationX() + "§8, §7Z§8: " + claimer.getColorCode() + chunk.getLocationZ()).itemstack(new ItemStack(Materials.GRASS_BLOCK.parseItem())).lore("§7Claimer§8: " + claimer.getColorCode() + (chunk.getClaimedByPlayer() == null ? chunk.getClaimedBy() : chunk.getClaimedByPlayer().getName()), "§7Erstellungsdatum§8: " + claimer.getColorCode() + new SimpleDateFormat("HH:mm:ss dd.MM.YYYY").format(chunk.getClaimedAt()), "", "§aLinksklick§7, um auf der Karte anzuzeigen", "§cRechtsklick§7, um zu entclaimen").build(), new ItemClickHandler() {
//
//                @Override
//                public void onItemClick(InventoryClickEvent event) {
//                    if (event.isLeftClick()) {
//                        close(true);
//                        new ChunkMapMenu(claimer, opener, chunk.getChunk());
//                    } else
//                        player.getTeam().removeChunk(chunk);
//                }
//            });
//            start++;
//        }
//
//        return calculatePages(chunks.size(), getSize()) == page;
//    }
}