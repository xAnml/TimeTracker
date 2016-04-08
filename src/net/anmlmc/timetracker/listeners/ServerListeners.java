package net.anmlmc.timetracker.listeners;

import net.anmlmc.timetracker.TimeTracker;
import net.anmlmc.timetracker.player.NemePlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/*******************
 * Created by Anml *
 *******************/
public class ServerListeners implements Listener {

    private TimeTracker instance;

    public ServerListeners(TimeTracker instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerLeave(PlayerDisconnectEvent e) {
        NemePlayer nemePlayer = instance.getNemePlayer(e.getPlayer().getUniqueId());

        if (nemePlayer.getTask() != null)
            nemePlayer.getTask().cancel();

        nemePlayer.save();
        instance.getPlayers().remove(e.getPlayer().getUUID());
    }

}
