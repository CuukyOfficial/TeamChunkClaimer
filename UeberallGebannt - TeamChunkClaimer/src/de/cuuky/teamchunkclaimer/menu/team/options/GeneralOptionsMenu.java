package de.cuuky.teamchunkclaimer.menu.team.options;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChatEvent;

import de.cuuky.cfw.hooking.HookManager;
import de.cuuky.cfw.hooking.hooks.chat.ChatHook;
import de.cuuky.cfw.hooking.hooks.chat.ChatHookHandler;
import de.cuuky.cfw.item.ItemBuilder;
import de.cuuky.cfw.menu.SuperInventory;
import de.cuuky.cfw.menu.utils.PageAction;
import de.cuuky.cfw.version.types.Materials;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.ChunkTeam;
import de.cuuky.teamchunkclaimer.menu.team.TeamOptionsMenu;

@SuppressWarnings("deprecation")
public class GeneralOptionsMenu extends SuperInventory {

	private ChunkPlayer player;

	public GeneralOptionsMenu(ChunkPlayer player) {
		super("§7Einstellungen", player.getPlayer(), 27, false);

		this.fillInventory = true;
		this.setModifier = true;

		this.player = player;

		player.getHandler().getTcc().getCfw().getInventoryManager().registerInventory(this);
		open();
	}

	@Override
	public boolean onBackClick() {
		new TeamOptionsMenu(player);
		return true;
	}

	@Override
	public void onClick(InventoryClickEvent event) {}

	@Override
	public void onClose(InventoryCloseEvent event) {}

	@Override
	public void onInventoryAction(PageAction action) {}

	@Override
	public boolean onOpen() {
		ItemBuilder builder = new ItemBuilder().itemstack(Materials.BOOK.parseItem());
		ChunkTeam team = player.getTeam();
		HookManager hook = team.getHandler().getTcc().getCfw().getHookManager();

		linkItemTo(11, builder.displayname("§2Name").lore("§7Wert§8: §f" + team.getName()).build(), new Runnable() {

			@Override
			public void run() {
				close(false);
				hook.registerHook(new ChatHook(player.getPlayer(), team.getHandler().getTcc().getPrefix() + "§7Neuen Teamnamen eingeben:", new ChatHookHandler() {

					@Override
					public boolean onChat(PlayerChatEvent event) {
						if (!team.getHandler().getTcc().getPlugin().getServer().dispatchCommand(player.getPlayer(), "team rename " + event.getMessage())) {
							reopenSoon();
							return true;
						}
						
						return false;
					}
				}));
			}
		});

		linkItemTo(12, builder.displayname("§2Tag").lore("§7Wert§8: §f" + (team.getTag() == null ? "-" : team.getTag())).build(), new Runnable() {

			@Override
			public void run() {
				close(false);
				hook.registerHook(new ChatHook(player.getPlayer(), team.getHandler().getTcc().getPrefix() + "§7Neuen Tag eingeben:", new ChatHookHandler() {

					@Override
					public boolean onChat(PlayerChatEvent event) {
						if (!team.getHandler().getTcc().getPlugin().getServer().dispatchCommand(player.getPlayer(), "team settag " + event.getMessage())) {
							reopenSoon();
							return true;
						}
						
						return false;
					}
				}));
			}
		});

		linkItemTo(13, builder.displayname("§2Title").lore("§7Wert§8: §f" + (team.getTitle() == null ? "-" : team.getTitle())).build(), new Runnable() {

			@Override
			public void run() {
				close(false);
				hook.registerHook(new ChatHook(player.getPlayer(), team.getHandler().getTcc().getPrefix() + "§7Neuen Title eingeben:", new ChatHookHandler() {

					@Override
					public boolean onChat(PlayerChatEvent event) {
						if (!team.getHandler().getTcc().getPlugin().getServer().dispatchCommand(player.getPlayer(), "team settitle " + event.getMessage())) {
							reopenSoon();
							return true;
						}
						
						return false;
					}
				}));
			}
		});

		linkItemTo(14, builder.displayname("§2Color").lore("§7Wert§8: §f" + team.getDisplayname()).build(), new Runnable() {

			@Override
			public void run() {
				close(false);
				hook.registerHook(new ChatHook(player.getPlayer(), team.getHandler().getTcc().getPrefix() + "§7Neue Teamfarbe eingeben:", new ChatHookHandler() {

					@Override
					public boolean onChat(PlayerChatEvent event) {
						if (!team.getHandler().getTcc().getPlugin().getServer().dispatchCommand(player.getPlayer(), "team setcolor " + event.getMessage())) {
							reopenSoon();
							return true;
						}
						
						return false;
					}
				}));
			}
		});

		return true;
	}
}