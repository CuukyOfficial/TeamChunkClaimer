package de.cuuky.teamchunkclaimer.menu.team;

import de.cuuky.cfw.inventory.ItemClick;
import de.cuuky.cfw.inventory.list.AdvancedListInventory;
import de.cuuky.cfw.utils.item.BuildItem;
import de.cuuky.cfw.version.types.Materials;
import de.cuuky.teamchunkclaimer.ChunkClaimer;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ClaimChunk;
import de.cuuky.teamchunkclaimer.menu.ChunkMapMenu;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;

public class ChunkListMenu extends AdvancedListInventory<ClaimChunk> {

    private final ChunkPlayer player;
    private final ChunkClaimer claimer;

    public ChunkListMenu(ChunkPlayer player) {
        super(player.getHandler().getClaimer().getCuukyFrameWork().getAdvancedInventoryManager(),
            player.getPlayer(), player.getTeam().getClaimedChunksReverse());

        this.claimer = player.getHandler().getClaimer();
        this.player = player;
    }

    @Override
    public ItemStack getFillerStack() {
        return null;
    }

    @Override
    protected boolean copyList() {
        return false;
    }

    @Override
    protected ItemStack getItemStack(ClaimChunk chunk) {
        return new BuildItem().displayName("§7X§8: " + claimer.getColorCode() + chunk.getLocationX() + "§8, §7Z§8: " + claimer.getColorCode() + chunk.getLocationZ()).itemstack(new ItemStack(Materials.GRASS_BLOCK.parseItem())).lore("§7Claimer§8: " + claimer.getColorCode() + (chunk.getClaimedByPlayer() == null ? chunk.getClaimedBy() : chunk.getClaimedByPlayer().getName()), "§7Erstellungsdatum§8: " + claimer.getColorCode() + new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(chunk.getClaimedAt()), "", "§aLinksklick§7, um auf der Karte anzuzeigen", "§cRechtsklick§7, um zu entclaimen").build();
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

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    public String getTitle() {
        return "§7Chunks " + player.getTeam().getDisplayname();
    }
}