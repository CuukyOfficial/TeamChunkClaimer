package de.cuuky.teamchunkclaimer.commands;

import de.cuuky.cfw.utils.JavaUtils;
import de.cuuky.cfw.version.VersionUtils;
import de.cuuky.teamchunkclaimer.ChunkClaimer;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.player.invites.TeamInvite;
import de.cuuky.teamchunkclaimer.entity.team.ChunkTeam;
import de.cuuky.teamchunkclaimer.entity.team.TeamMemberType;
import de.cuuky.teamchunkclaimer.menu.TeamMainMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommand implements CommandExecutor {

    private final ChunkClaimer claimer;

    public TeamCommand(ChunkClaimer claimer) {
        this.claimer = claimer;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(claimer.getPrefix() + "Not for console!");
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(claimer.getConfiguration().getHeader());
            sender.sendMessage(
                claimer.getPrefix() + claimer.getColorCode() + "/team info <Name> §8- §7Zeig infos zu einem Team");
            sender.sendMessage(
                claimer.getPrefix() + claimer.getColorCode() + "/team create <Name> §8- §7Erstellt ein Team");
            sender.sendMessage(claimer.getPrefix() + claimer.getColorCode() +
                "/team accept/deny <Team> §8- §7Nimmt eine Teameinladung an oder lehnt sie ab");
            sender.sendMessage(
                claimer.getPrefix() + claimer.getColorCode() + "/team leave §8- §7Lässt dich das Team verlassen");
            sender.sendMessage(claimer.getPrefix() + claimer.getColorCode() +
                "/team setcolor <Farbe> §8- §7Setzt die Farbe deines Teams");
            sender.sendMessage(claimer.getPrefix() + claimer.getColorCode() +
                "/team invite <Spieler> §8- §7Lädt einen Spieler in dein Team ein");
            sender.sendMessage(claimer.getPrefix() + claimer.getColorCode() +
                "/team kick <Spieler> §8- §7Kickt einen Spieler aus deinem Team");
            sender.sendMessage(claimer.getPrefix() + claimer.getColorCode() +
                "/team promote <Spieler> §8- §7Macht einen Spieler zum Moderator deines Teams");
            sender.sendMessage(claimer.getPrefix() + claimer.getColorCode() + "/team delete §8- §7Löscht dein Team");
            sender.sendMessage(claimer.getConfiguration().getHeader());
            return true;
        }

        ChunkPlayer player = claimer.getEntityHandler().getPlayer(((Player) sender).getUniqueId());

        // USER COMMANDS

        if (args[0].equalsIgnoreCase("info")) {
            if (args.length < 2) {
                sender.sendMessage(claimer.getPrefix() + "/team info <Name>");
                return false;
            }

            ChunkTeam team = claimer.getEntityHandler().getTeam(args[1]);
            if (team == null) {
                sender.sendMessage(claimer.getPrefix() + "Dieses Team wurde nicht gefunden!");
                return false;
            }

            sender.sendMessage(claimer.getPrefix() + "Team-Name: " + claimer.getColorCode() + team.getDisplayname());
            sender.sendMessage(
                claimer.getPrefix() + "Team-Tag: " + claimer.getColorCode() + team.getTag() == null ? "-" :
                    team.getTag());
            sender.sendMessage(
                claimer.getPrefix() + "Mitglieder: " + claimer.getColorCode() + team.getMembers().size());
            sender.sendMessage(
                claimer.getPrefix() + "Chunks: " + claimer.getColorCode() + team.getClaimedChunks().size() + "§8/" +
                    claimer.getColorCode() + team.getAllowedChunkAmount());
            return true;
        } else if (args[0].equalsIgnoreCase("create")) {
            if (player.getTeam() != null) {
                sender.sendMessage(claimer.getPrefix() + "Du bist bereits in einem Team!");
                return false;
            }

            if (args.length < 2) {
                sender.sendMessage(claimer.getPrefix() + "/team create <Name> [Farbe]");
                return false;
            }

            if (args[1].length() > 8) {
                sender.sendMessage(claimer.getPrefix() + "Teamlänge darf 8 nicht überschreiten!");
                return false;
            }

            if (claimer.getEntityHandler().getTeam(args[1]) != null) {
                sender.sendMessage(claimer.getPrefix() + "Team existiert bereits!");
                return false;
            }

            ChunkTeam team = claimer.getEntityHandler().registerTeam(args[1]);
            team.addMember(player, TeamMemberType.OWNER);
            sender.sendMessage(claimer.getPrefix() + "Dein Team wurde erfolgreich erstellt!");
            return true;
        } else if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("deny")) {
            if (args.length < 2) {
                sender.sendMessage(claimer.getPrefix() + "/team accept/deny <Name>");
                return false;
            }

            if (player.getTeam() != null) {
                sender.sendMessage(claimer.getPrefix() + "Du bist bereits in einem Team!");
                return false;
            }

            ChunkTeam team = claimer.getEntityHandler().getTeam(args[1]);
            if (team == null) {
                sender.sendMessage(claimer.getPrefix() + "Dieses Team wurde nicht gefunden!");
                return false;
            }

            TeamInvite invite = player.getInvite(team);
            if (invite == null) {
                sender.sendMessage(claimer.getPrefix() + "Du hast von diesem Team keine Einladung erhalten!");
                return false;
            }

            if (args[0].equalsIgnoreCase("accept"))
                team.addMember(player, TeamMemberType.MEMBER);

            player.removeInvite(invite);
            sender.sendMessage(claimer.getPrefix() + "Du hast die Einladung erfolgreich " +
                (args[0].equalsIgnoreCase("accept") ? "angenommen" : "abgelehnt") + "!");
            return true;
        }

        if (player.getTeam() == null) {
            sender.sendMessage(claimer.getPrefix() + "Du bist in keinem Team!");
            return false;
        }

        if (args[0].equalsIgnoreCase("gui")) {
            new TeamMainMenu(player);
            return true;
        } else if (args[0].equalsIgnoreCase("leave")) {
            if (player.getTeam().getMemberType(player) == TeamMemberType.OWNER) {
                sender.sendMessage(claimer.getPrefix() + "Lösche das Team erst, bevor du es als Besitzer verlässt!");
                return false;
            }

            ChunkTeam team = player.getTeam();
            team.removeMember(player);
            sender.sendMessage(
                claimer.getPrefix() + "Du hast das Team " + team.getDisplayname() + " §7erfolgreich verlassen");
            return true;
        }

        // MOD COMMANDS
        if (player.getTeam().getMemberType(player) == TeamMemberType.MEMBER) {
            sender.sendMessage(
                claimer.getPrefix() + "Du musst Team-Moderator sein, um diese Einstellung vornehmen zu können!");
            return false;
        }

        if (args[0].equalsIgnoreCase("setcolor")) {
            if (args.length < 2) {
                sender.sendMessage(claimer.getPrefix() + "/team setcolor <Farbe>");
                return false;
            }

            if (getColorFromInput(args[1]) == null) {
                sender.sendMessage(claimer.getPrefix() + "Bitte gib eine gültige Farbe ein (zB 'RED').");
                return false;
            }

            player.getTeam().setColor(getColorFromInput(args[1]));
            sender.sendMessage(claimer.getPrefix() + "Farbcode erfolgreich auf " + player.getTeam().getColor() +
                ChatColor.getByChar(player.getTeam().getColor().split("(?!^)")[1]).name() + " §7gesetzt!");
            return true;
        } else if (args[0].equalsIgnoreCase("rename")) {
            if (args.length < 2) {
                sender.sendMessage(claimer.getPrefix() + "/team rename <Name>");
                return false;
            }

            if (args[1].length() > 8) {
                sender.sendMessage(claimer.getPrefix() + "Teamlänge darf 8 nicht überschreiten!");
                return false;
            }

            player.getTeam().setName(args[1]);
            sender.sendMessage(claimer.getPrefix() + "Das Team wurde erfolgreich umbenannt!");
            return true;
        } else if (args[0].equalsIgnoreCase("settag")) {
            if (args.length < 2) {
                sender.sendMessage(claimer.getPrefix() + "/team settag <Tag>");
                return false;
            }

            if (args[1].length() > 5) {
                sender.sendMessage(claimer.getPrefix() + "Die Taglänge darf 5 nicht überschreiten!");
                return false;
            }

            player.getTeam().setTag(args[1]);
            sender.sendMessage(claimer.getPrefix() + "Tag erfolgreich geändert!");
            return true;
        } else if (args[0].equalsIgnoreCase("settitle")) {
            if (args.length < 2) {
                sender.sendMessage(claimer.getPrefix() + "/team setitle <Title>");
                return false;
            }

            String title = JavaUtils.getArgsToString(JavaUtils.removeString(args, 0), " ");
            if (title.length() > 20) {
                sender.sendMessage(claimer.getPrefix() + "Die Titlelänge darf 20 nicht überschreiten!");
                return false;
            }

            player.getTeam().setTitle(title);
            sender.sendMessage(claimer.getPrefix() + "Title erfolgreich geändert!");
            return true;
        } else if (args[0].equalsIgnoreCase("invite")) {
            if (args.length < 2) {
                sender.sendMessage(claimer.getPrefix() + "/team invite <Spieler>");
                return false;
            }

            ChunkPlayer toInvite = this.claimer.getEntityHandler().getPlayer(args[1]);
            if (toInvite == null) {
                sender.sendMessage(claimer.getPrefix() + "Dieser Spieler wurde nicht gefunden!");
                return false;
            }

            if (toInvite.getTeam() != null && toInvite.getTeam().equals(player.getTeam())) {
                sender.sendMessage(claimer.getPrefix() + "Dieser Spieler ist bereits in deinem Team!");
                return false;
            }

            if (toInvite.getInvite(player.getTeam()) != null) {
                sender.sendMessage(
                    claimer.getPrefix() + "Dieser Spieler hat bereits eine Einladung von deinem Team erhalten!");
                return false;
            }

            toInvite.inviteTo(player.getTeam(), player.getName());
            sender.sendMessage(claimer.getPrefix() + "Der Spieler wurde erfolgreich in dein Team eingeladen!");
            return true;
        } else if (args[0].equalsIgnoreCase("kick")) {
            if (args.length < 2) {
                sender.sendMessage(claimer.getPrefix() + "/team kick <Spieler>");
                return false;
            }

            ChunkPlayer toKick = this.claimer.getEntityHandler().getPlayer(args[1]);
            if (toKick == null) {
                sender.sendMessage(claimer.getPrefix() + "Dieser Spieler wurde nicht gefunden!");
                return false;
            }

            if (toKick.getTeam() == null || !toKick.getTeam().equals(player.getTeam())) {
                sender.sendMessage(claimer.getPrefix() + "Dieser Spieler ist nicht in deinem Team!");
                return false;
            }

            if (player.getTeam().getMemberType(toKick) != TeamMemberType.MEMBER) {
                sender.sendMessage(claimer.getPrefix() + "Du kannst keine Moderatoren/Admins kicken.");
                return false;
            }

            player.getTeam().removeMember(toKick);
            sender.sendMessage(claimer.getPrefix() + toKick.getName() + " wurde erfolgreich aus deinem Team geworfen!");
            return true;
        }

        // OWNER COMMANDS
        if (player.getTeam().getMemberType(player) != TeamMemberType.OWNER) {
            sender.sendMessage(
                claimer.getPrefix() + "Du musst Team-Owner sein, um diese Einstellung vornehmen zu können!");
            return false;
        }

        if (args[0].equalsIgnoreCase("promote")) {
            if (args.length < 2) {
                sender.sendMessage(claimer.getPrefix() + "/team promote <Spieler>");
                return false;
            }

            ChunkPlayer toPromote = this.claimer.getEntityHandler().getPlayer(args[1]);
            if (toPromote == null) {
                sender.sendMessage(claimer.getPrefix() + "Spieler nicht gefunden!");
                return false;
            }

            if (toPromote.getTeam() == null || !toPromote.getTeam().equals(player.getTeam())) {
                sender.sendMessage(claimer.getPrefix() + "Dieser Spieler ist nicht in deinem Team!");
                return false;
            }

            if (player.getTeam().getMemberType(toPromote) != TeamMemberType.MEMBER) {
                sender.sendMessage(claimer.getPrefix() + "Dieser Spieler ist bereits Moderator.");
                return false;
            }

            player.getTeam().setMemberType(toPromote, TeamMemberType.MODERATOR);
            sender.sendMessage(
                claimer.getPrefix() + toPromote.getName() + " wurde erfolgreich als Moderator registriert!");
            return true;
        } else if (args[0].equalsIgnoreCase("delete")) {
            this.claimer.getEntityHandler().removeTeam(player.getTeam());
            player.getTeam().remove();

            sender.sendMessage(claimer.getPrefix() + "Das Team wurde erfolgreich aufgelöst!");
            return true;
        }

        sender.sendMessage(claimer.getPrefix() + "Dieser Befehl existiert nicht (/team).");
        return false;
    }

    // Returns the color string by chatcolor name or null if invalid
    public String getColorFromInput(String input) {
        input = input.toUpperCase();
        for(ChatColor color : ChatColor.values()) {
            if (color.name().equals(input))
                return "&" + ChatColor.valueOf(input).getChar();
        }
        return null;
    }
}