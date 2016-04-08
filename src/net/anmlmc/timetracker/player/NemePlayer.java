package net.anmlmc.timetracker.player;

import com.google.common.collect.Maps;
import net.anmlmc.timetracker.TimeTracker;
import net.anmlmc.timetracker.database.DatabaseManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/*******************
 * Created by Anml *
 *******************/
public class NemePlayer {

    private TimeTracker instance = TimeTracker.getInstance();
    private DatabaseManager databaseManager = instance.getDatabaseManager();
    private UUID uuid;
    private Map<String, Integer> timePlayed = Maps.newConcurrentMap();
    private ScheduledTask task;

    public NemePlayer(UUID uuid) {
        this.uuid = uuid;
        try {
            load();
        } catch (Exception e) {
        }

        if (BungeeCord.getInstance().getPlayer(uuid) != null) {
            task = BungeeCord.getInstance().getScheduler().schedule(instance, new Runnable() {
                @Override
                public void run() {
                    increment(BungeeCord.getInstance().getPlayer(uuid).getServer().getInfo().getName());
                }
            }, 1, 1, TimeUnit.MINUTES);
        }
    }

    public UUID getUUID() {
        return uuid;
    }

    public ScheduledTask getTask() {
        return task;
    }

    private void load() throws Exception {
        for (ServerInfo serverInfo : BungeeCord.getInstance().getServers().values()) {
            String tableName = "TimePlayed_" + serverInfo.getName();
            databaseManager.createTable(tableName, Arrays.asList("playeruuid;VARCHAR(60)", "playtime;INTEGER(500)"));
            if (databaseManager.exists(tableName, "playeruuid", uuid.toString())) {
                ResultSet set = databaseManager.getResultSetByUUID(tableName, uuid.toString());
                timePlayed.put(serverInfo.getName(), set.getInt("playtime"));
            } else {
                timePlayed.put(serverInfo.getName(), 0);
            }
        }
    }

    public void save() {
        for (ServerInfo serverInfo : BungeeCord.getInstance().getServers().values()) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("playtime", getTimePlayed(serverInfo.getName()));

            String tableName = "TimePlayed_" + serverInfo.getName();
            databaseManager.createTable(tableName, Arrays.asList("playeruuid;VARCHAR(60)", "playtime;INTEGER(500)"));
            databaseManager.set(tableName, data, "playeruuid", uuid.toString());
        }
    }

    public int getTimePlayed(String server) {
        if (timePlayed.containsKey(server))
            return timePlayed.get(server);

        if (BungeeCord.getInstance().getServerInfo(server) == null)
            return -1;

        timePlayed.put(server, 0);
        return 0;
    }

    public int getTotalTimePlayed() {
        int sum = 0;

        for (int value : timePlayed.values())
            sum += value;

        return sum;

    }

    private void increment(String server) {
        if (timePlayed.containsKey(server)) {
            timePlayed.replace(server, timePlayed.get(server) + 1);
            return;
        }

        if (BungeeCord.getInstance().getServerInfo(server) == null)
            return;

            timePlayed.put(server, 1);
    }


}
