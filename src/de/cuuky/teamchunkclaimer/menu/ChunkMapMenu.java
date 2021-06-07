package de.cuuky.teamchunkclaimer.menu;

import de.cuuky.cfw.hooking.hooks.chat.ChatHook;
import de.cuuky.cfw.hooking.hooks.chat.ChatHookHandler;
import de.cuuky.cfw.inventory.ItemInfo;
import de.cuuky.cfw.inventory.ItemInserter;
import de.cuuky.cfw.inventory.inserter.DirectInserter;
import de.cuuky.cfw.item.ItemBuilder;
import de.cuuky.cfw.utils.DirectionFace;
import de.cuuky.cfw.version.types.Materials;
import de.cuuky.teamchunkclaimer.ChunkClaimer;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ClaimChunk;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;

public class ChunkMapMenu extends ChunkClaimerMenu {

    private ChunkClaimer claimer;
    private ChunkPlayer player;
    private DirectionFace face;
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
        this.addItem(this.getSize() - minusIndex, new ItemBuilder().displayname(name)
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
    protected ItemInserter getInserter() {
        return new DirectInserter();
    }

    @Override
    protected ItemStack getFillerStack() {
        return null;
    }

    @Override
    protected String getTitle() {
        return "§aChunks §8(" + this.face.getIdentifier() + "§8)";
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    protected ItemInfo getBackInfo() {
        ItemInfo closeInfo = super.getCloseInfo();
        return super.getPrevious() != null ? super.getBackInfo().setIndex(closeInfo.getIndex()) : null;
    }

    @Override
    protected ItemInfo getCloseInfo() {
        return super.getPrevious() != null ? null : super.getCloseInfo();
    }

    @Override
    protected void refreshContent() {
        double height = (this.getUsableSize() / 9) / 2, width = 4;

        for (double y = height * -1; y <= height; y++) {
            for (double x = width * -1; x <= width; x++) {
                double[] coords = face.modifyValues(x, y);
                int chunkX = (int) (center.getX() + coords[0]), locationX = chunkX * 16;
                int chunkZ = (int) (center.getZ() + coords[1]), locationZ = chunkZ * 16;

                Chunk worldChunk = center.getWorld().getChunkAt(chunkX, chunkZ);
                ClaimChunk chunk = this.claimer.getEntityHandler().getChunk(worldChunk);
                ItemBuilder builder = new ItemBuilder().itemstack(Materials.WHITE_STAINED_GLASS_PANE.parseItem())
                        .displayname("§fUnclaimed")
                        .lore("§7Location:", "§7X§8: §5" + (locationX + 8), "§7Z§8: §5" + (locationZ + 8), "§7Linksklick = claim/Chunk info", "§7Rechtsklick = unclaim/Team info", (worldChunk.equals(getPlayer().getLocation().getChunk()) ? "§aDa bist du!" : ""));

                if (chunk != null) {
                    if (player.getTeam() == null || !player.getTeam().equals(chunk.getTeam()))
                        builder.itemstack(Materials.RED_STAINED_GLASS_PANE.parseItem());
                    else
                        builder.itemstack(Materials.GREEN_STAINED_GLASS_PANE.parseItem());

                    builder.displayname(chunk.getTeam().getDisplayname());
                }

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
                this.addNav(8, "§cUnten", false, false);
                this.addNav(9, "§aOben", false, true);

                addItem(this.getSize() - 4, new ItemBuilder().displayname("§5Zentrieren").
                                itemstack(Materials.DIAMOND.parseItem()).build(),
                        event -> center = getPlayer().getLocation().getChunk());

                addItem(this.getSize() - 7, new ItemBuilder()
                                .displayname("§7Vertikale Chunks pro §aKlick§7: §a" + this.chunkJumpVertical)
                                .lore("§aLinks §7= hoch", "§cRechts §7= runter")
                                .itemstack(Materials.COAL.parseItem()).build(),
                        event -> chunkJumpVertical += event.isLeftClick() ? 1 : -1
                );

                addItem(this.getSize() - 3, new ItemBuilder().displayname("§7Horizontale Chunks pro §aKlick§7: §a" + this.chunkJumpHorizontal).
                                lore("§aLinks §7= hoch", "§cRechts §7= runter").itemstack(Materials.COAL.parseItem()).build(),
                        (event) -> chunkJumpHorizontal += event.isLeftClick() ? 1 : -1);

                addItem(this.getSize() - 6, new ItemBuilder().displayname("§2Springe zu...").itemstack(Materials.MAP.parseItem()).build(), (event) -> {
                    this.close();
                    claimer.getCuukyFrameWork().getHookManager().registerHook(new ChatHook(player.getPlayer(), claimer.getPrefix() + "Koordinaten eingeben: §8(§7z.B.: §8'§f100 200§8')", new ChatHookHandler() {
                        @Override
                        public boolean onChat(PlayerChatEvent event) {
                            try {
                                String[] args = event.getMessage().split(" ");
                                int x = Integer.valueOf(args[0]), z = Integer.valueOf(args[1]);
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