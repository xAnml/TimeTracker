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
public class GPlayTimeCommand extends Command {

    private TimeTracker instance;

    public GPlayTimeCommand(TimeTracker instance) {
        super("gplaytime");
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("This command can only be executed by a player.").color(ChatColor.RED).create());
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        NemePlayer nemePlayer = instance.getNemePlayer(player.getUniqueId());
        String time = TimeUtils.simplyTime(nemePlayer.getTotalTimePlayed());

        player.sendMessage(new ComponentBuilder("Global Playtime: ").color(ChatColor.GREEN)
                .append(time).color(ChatColor.GOLD).create());
    }
}
