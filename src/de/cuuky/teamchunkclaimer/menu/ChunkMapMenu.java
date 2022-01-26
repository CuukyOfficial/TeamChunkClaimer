package de.cuuky.teamchunkclaimer.menu;

import de.cuuky.cfw.hooking.hooks.chat.ChatHook;
import de.cuuky.cfw.hooking.hooks.chat.ChatHookHandler;
import de.cuuky.cfw.inventory.ItemInfo;
import de.cuuky.cfw.inventory.ItemInserter;
import de.cuuky.cfw.inventory.inserter.DirectInserter;
import de.cuuky.cfw.utils.DirectionFace;
import de.cuuky.cfw.utils.item.BuildItem;
import de.cuuky.cfw.utils.item.BuildSkull;
import de.cuuky.cfw.version.types.Materials;
import de.cuuky.teamchunkclaimer.ChunkClaimer;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ClaimChunk;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;

public class ChunkMapMenu extends ChunkClaimerMenu {

    private final ChunkClaimer claimer;
    private final ChunkPlayer player;
    private final DirectionFace face;
    private Chunk center;

    private int chunkJumpHorizontal = 1, chunkJumpVertical = 1;

    public ChunkMapMenu(ChunkClaimer claimer, Player opener, Chunk center) {
        super(claimer.getCuukyFrameWork().getAdvancedInventoryManager(), opener);
        this.claimer = claimer;
        this.face = DirectionFace.getFace(opener.getLocation().getYaw());
        this.center = center;
        this.player = claimer.getEntityHandler().getPlayer(opener.getName());
    }

    public ChunkMapMenu(ChunkClaimer claimer, Player opener) {
        this(claimer, opener, opener.getLocation().getChunk());
    }

    private void addNav(int minusIndex, String name, boolean hor, boolean left) {
        this.addItem(this.getSize() - minusIndex, new BuildItem().displayName(name)
                .itemstack(Materials.ARROW.parseItem()).build(), e -> {
            int[] xz = getDirectionChange(hor, left);
            center = center.getWorld().getChunkAt(center.getX() + (xz[0] * chunkJumpVertical),
                    center.getZ() + (xz[1] * chunkJumpVertical));
        });
    }

    private int[] getDirectionChange(boolean horizontal, boolean left) {
        int xOffset = 0, zOffset = 0;
        switch (face) {
            case NORTH:
                xOffset = left ? 1 : -1;
                break;
            case EAST:
                zOffset = left ? 1 : -1;
                break;
            case SOUTH:
                xOffset = left ? -1 : 1;
                break;
            case WEST:
                zOffset = left ? -1 : 1;
                break;
        }

        return new int[]{horizontal ? xOffset : zOffset, horizontal ? zOffset : xOffset};
    }

    @Override
    public ItemInserter getInserter() {
        return new DirectInserter();
    }

    @Override
    public ItemStack getFillerStack() {
        return null;
    }

    @Override
    public ItemInfo getBackInfo() {
        ItemInfo closeInfo = super.getCloseInfo();
        return super.getPrevious() != null ? super.getBackInfo().setIndex(closeInfo.getIndex()) : null;
    }

    @Override
    public ItemInfo getCloseInfo() {
        return super.getPrevious() != null ? null : super.getCloseInfo();
    }

    @Override
    public String getTitle() {
        return "§aChunks §8(" + this.face.getIdentifier() + "§8)";
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    public void refreshContent() {
        double height = (this.getUsableSize() / 9f) / 2, width = 4;

        for (double y = height * -1; y <= height - 1; y++) {
            for (double x = width * -1; x <= width; x++) {
                double[] coords = face.modifyValues(x, y);
                int chunkX = (int) (center.getX() + coords[0]), locationX = chunkX * 16;
                int chunkZ = (int) (center.getZ() + coords[1]), locationZ = chunkZ * 16;

                Chunk worldChunk = center.getWorld().getChunkAt(chunkX, chunkZ);
                ClaimChunk chunk = this.claimer.getEntityHandler().getChunk(worldChunk);
                ItemStack item = Materials.GREEN_STAINED_GLASS_PANE.parseItem();
                BuildItem builder = new BuildItem().itemstack(item)
                        .displayName("§fUnclaimed")
                        .lore("§7Location:", "§7X§8: §2" + (locationX + 8), "§7Z§8: §2" + (locationZ + 8), "§7Linksklick: §aClaim", "§7Shift-Linksklick: §aInfo", "§7Rechtsklick: §aUnclaim", "§7Shift-Rechtsklick: §aTeam Info", (worldChunk.equals(getPlayer().getLocation().getChunk()) ? "§cDa bist du!" : ""));

                if (chunk != null) {
                    if (player.getTeam() == null || !player.getTeam().equals(chunk.getTeam()))
                        builder.itemstack(Materials.RED_STAINED_GLASS_PANE.parseItem());
                    else
                        builder.itemstack(Materials.CYAN_STAINED_GLASS_PANE.parseItem());

                    builder.displayName(chunk.getTeam().getDisplayname());
                }

                if (worldChunk.equals(getPlayer().getLocation().getChunk()))
                    builder.itemstack(new BuildSkull().player(getPlayer().getName()).build());

                double invX = x + width;
                double invY = y + height;
                this.addItem((int) ((invY * 9) + invX), builder.build(), (event) -> {
                    if (chunk != null) {
                        if (player.getTeam() != null && player.getTeam().equals(chunk.getTeam()) && event.isRightClick()) {
                            claimer.getPlugin().getServer().dispatchCommand(getPlayer(), "chunk unclaim " + locationX + " " + locationZ);
                            return;
                        }

                        if (event.isLeftClick())
                            claimer.getPlugin().getServer().dispatchCommand(getPlayer(), "chunk info " + locationX + " " + locationZ);
                        else
                            claimer.getPlugin().getServer().dispatchCommand(getPlayer(), "team info " + chunk.getTeam().getName());
                        return;
                    }

                    if (event.isRightClick())
                        return;

                    claimer.getPlugin().getServer().dispatchCommand(getPlayer(), "chunk claim " + locationX + " " + locationZ);
                });

                this.addNav(1, "§cRechts", true, true);
                this.addNav(2, "§aLinks", true, false);
                this.addNav(8, "§cRunter", false, true);
                this.addNav(9, "§aHoch", false, false);

                addItem(this.getSize() - 4, new BuildItem().displayName("§2Zentrieren").
                                itemstack(Materials.COMPASS.parseItem()).build(),
                        event -> center = getPlayer().getLocation().getChunk());
/*
                addItem(this.getSize() - 7, new BuildItem()
                                .displayName("§7Vertikale Chunks pro §aKlick§7: §a" + this.chunkJumpVertical)
                                .lore("§aLinks §7= hoch", "§cRechts §7= runter")
                                .itemstack(Materials.COAL.parseItem()).build(),
                        event -> chunkJumpVertical += event.isLeftClick() ? 1 : -1
                );

                addItem(this.getSize() - 3, new BuildItem().displayName("§7Horizontale Chunks pro §aKlick§7: §a" + this.chunkJumpHorizontal).
                                lore("§aLinks §7= hoch", "§cRechts §7= runter").itemstack(Materials.COAL.parseItem()).build(),
                        (event) -> chunkJumpHorizontal += event.isLeftClick() ? 1 : -1);
*/
                addItem(this.getSize() - 6, new BuildItem().displayName("§2Springe zu...").itemstack(Materials.CLOCK.parseItem()).build(), (event) -> {
                    this.close();
                    claimer.getCuukyFrameWork().getHookManager().registerHook(new ChatHook(player.getPlayer(), claimer.getPrefix() + "Koordinaten eingeben: §8(§7z.B.: §8'§f100 200§8')", new ChatHookHandler() {
                        @Override
                        public boolean onChat(PlayerChatEvent event) {
                            try {
                                String[] args = event.getMessage().split(" ");
                                int x = Integer.parseInt(args[0]), z = Integer.parseInt(args[1]);
                                center = center.getWorld().getChunkAt(x / 16, z / 16);
                            } catch (Exception e) {
                                player.getPlayer().sendMessage(claimer.getPrefix() + "X und Z sind keine Zahlen!");
                                return false;
                            }

                            open();
                            return true;
                        }
                    }));
                });
            }
        }
    }
}