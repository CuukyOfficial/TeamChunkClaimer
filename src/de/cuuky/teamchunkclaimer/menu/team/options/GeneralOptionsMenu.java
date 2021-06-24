package de.cuuky.teamchunkclaimer.menu.team.options;

import de.cuuky.cfw.hooking.HookManager;
import de.cuuky.cfw.hooking.hooks.chat.ChatHook;
import de.cuuky.cfw.hooking.hooks.chat.ChatHookHandler;
import de.cuuky.cfw.inventory.ItemClick;
import de.cuuky.cfw.item.ItemBuilder;
import de.cuuky.cfw.version.types.Materials;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.ChunkTeam;
import de.cuuky.teamchunkclaimer.menu.ChunkClaimerMenu;
import org.bukkit.event.player.PlayerChatEvent;

public class GeneralOptionsMenu extends ChunkClaimerMenu {

    private final ChunkPlayer player;

    public GeneralOptionsMenu(ChunkPlayer player) {
        super(player.getHandler().getClaimer().getCuukyFrameWork().getAdvancedInventoryManager(), player.getPlayer());

        this.player = player;
    }

    private ItemClick getClick(String msg, String command) {
        ChunkTeam team = player.getTeam();
        HookManager hook = team.getHandler().getClaimer().getCuukyFrameWork().getHookManager();

        return (event) -> {
            this.close();
            hook.registerHook(new ChatHook(player.getPlayer(), team.getHandler().getClaimer().getPrefix() + msg,
                    new ChatHookHandler() {

                @Override
                public boolean onChat(PlayerChatEvent event) {
                    if (!team.getHandler().getClaimer().getPlugin().getServer().dispatchCommand(player.getPlayer(),
                            String.format(command, event.getMessage()))) {
                        open();
                        return true;
                    }

                    return false;
                }
            }));
        };
    }

    @Override
    public String getTitle() {
        return "§7Einstellungen";
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public void refreshContent() {
        ItemBuilder builder = new ItemBuilder().material(Materials.BOOK.parseMaterial());
        ChunkTeam team = player.getTeam();
        this.addItem(11, builder.displayname("§2Name").lore("§7Wert§8: §f" + team.getName()).build(),
                this.getClick("§7Neuen Teamnamen eingeben", "team rename %s"));

        this.addItem(12, builder.displayname("§2Tag").lore("§7Wert§8: §f" + (team.getTag() == null ? "-" : team.getTag()))
                        .lore("§7Wert§8: §f" + (team.getTag() == null ? "-" : team.getTag()))
                        .build(),
                this.getClick("§7Neuen Tag eingeben:", "team settag %s"));

        this.addItem(13, builder.displayname("§2Title").lore("§7Wert§8: §f" + (team.getTitle() == null ? "-" : team.getTitle()))
                .lore("§7Wert§8: §f" + (team.getTitle() == null ? "-" : team.getTitle()))
                .build(),
                this.getClick("§7Neuen Title eingeben:", "team settitle %s"));

        this.addItem(14, builder.displayname("§2Color").lore("§7Wert§8: §f" + team.getDisplayname())
                .lore("§7Wert§8: §f" + team.getDisplayname())
                .build(),
                this.getClick("§7Neue Teamfarbe eingeben:", "team setcolor %s"));
    }
}