package net.anmlmc.timetracker.commands;

import net.anmlmc.timetracker.TimeTracker;
import net.anmlmc.timetracker.Utils.TimeUtils;
import net.anmlmc.timetracker.player.NemePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/*******************
 * Created by Anml *
 *******************/

public class PlayTimeCommand extends Command {

    private TimeTracker instance;

    public PlayTimeCommand(TimeTracker instance) {
        super("playtime");
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("This command can only be executed by a player.").color(ChatColor.RED).create());
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        String server = player.getServer().getInfo().getName();
        NemePlayer nemePlayer = instance.getNemePlayer(player.getUniqueId());
        String time = "";
        if (instance.getLobbies().contains(server)) {
            int lobbyTime = 0;
            for (String lobby : instance.getLobbies()) {
                lobbyTime += nemePlayer.getTimePlayed(lobby);
            }

            server = "Lobby";
            time = TimeUtils.simplyTime(lobbyTime);
        } else {
            time = TimeUtils.simplyTime(nemePlayer.getTimePlayed(server));
        }

        player.sendMessage(new ComponentBuilder("Server Playtime").color(ChatColor.GREEN)
                .append(" (" + server + ")").color(ChatColor.YELLOW)
                .append(": ").color(ChatColor.GREEN)
                .append(time).color(ChatColor.GOLD).create());
    }
}
