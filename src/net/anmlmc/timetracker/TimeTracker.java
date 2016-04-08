package net.anmlmc.timetracker;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import net.anmlmc.timetracker.commands.GPlayTimeCommand;
import net.anmlmc.timetracker.commands.PlayTimeCommand;
import net.anmlmc.timetracker.database.DatabaseManager;
import net.anmlmc.timetracker.listeners.ServerListeners;
import net.anmlmc.timetracker.player.NemePlayer;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/*******************
 * Created by Anml *
 *******************/
public class TimeTracker extends Plugin {

    private static TimeTracker instance;
    private DatabaseManager databaseManager;
    private Configuration configuration = null;
    private List<String> lobbies = Lists.newArrayList();
    private Map<UUID, NemePlayer> players = Maps.newConcurrentMap();
    private Map<String, List<String>> serverLeaderboards = Maps.newConcurrentMap();
    private List<String> globalLeaderboard = Lists.newArrayList();

    public static TimeTracker getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        loadConfig();
        databaseManager = new DatabaseManager("nyc.ggserv.co", "3306", "135201", "135201", "wxRDSLMvU4Z9GtLz");


        BungeeCord.getInstance().getScheduler().schedule(instance, new Runnable() {
            @Override
            public void run() {
                for (String server : BungeeCord.getInstance().getServers().keySet()) {
                    try {
                        if (serverLeaderboards.containsKey(server)) {
                            serverLeaderboards.replace(server, getLeaderboards(server));
                        } else {
                            serverLeaderboards.put(server, getLeaderboards(server));
                        }
                    } catch (Exception e) {
                        BungeeCord.getInstance().getLogger().log(Level.WARNING, server + " leaderboard were unable to be loaded!");
                    }
                }

                try {
                    globalLeaderboard = getLeaderboards("GLOBAL");
                } catch (Exception e) {
                    BungeeCord.getInstance().getLogger().log(Level.WARNING, "Global leaderboard were unable to be loaded!");
                }
            }
        }, 1, 5, TimeUnit.MINUTES);

        getProxy().getPluginManager().registerListener(this, new ServerListeners(this));
        getProxy().getPluginManager().registerCommand(this, new PlayTimeCommand(this));
        getProxy().getPluginManager().registerCommand(this, new GPlayTimeCommand(this));
    }

    @Override
    public void onDisable() {

        for (NemePlayer nemePlayer : players.values())
            nemePlayer.save();

        saveConfig();
        instance = null;
    }

    public NemePlayer getNemePlayer(UUID uuid) {
        if (players.containsKey(uuid))
            return players.get(uuid);

        if (BungeeCord.getInstance().getPlayer(uuid) != null) {
            NemePlayer nemePlayer = new NemePlayer(uuid);
            players.put(uuid, nemePlayer);
            return nemePlayer;
        } else {
            return new NemePlayer(uuid);
        }
    }

    public Map<UUID, NemePlayer> getPlayers() {
        return players;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    private void loadConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                try (InputStream is = getResourceAsStream("config.yml");
                     OutputStream os = new FileOutputStream(configFile)) {
                    ByteStreams.copy(is, os);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error! Was unable to create a configuration file.", e);
            }
        }
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> lobbies = configuration.getStringList("Lobbies");
        if (lobbies == null)
            lobbies = Lists.newArrayList();
        for (String lobby : lobbies) {
            if (BungeeCord.getInstance().getServerInfo(lobby) != null)
                this.lobbies.add(lobby);
        }
    }

    private void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getLobbies() {
        return lobbies;
    }

    private List<String> getLeaderboards(String server) throws SQLException {

        List<String> top = Lists.newArrayList();

        if(!server.equalsIgnoreCase("GLOBAL")) {
            ResultSet set = databaseManager.getSet("SELECT * FROM (SELECT * FROM TimePlayed_" + server + " ORDER BY timeplayed) WHERE ROWNUM <= 5");
            while (set.next()) {
                top.add(set.getString("playeruuid") + ";" + set.getString("timeplayed"));
            }
        } else {
            return Arrays.asList("");
        }

        return top;
    }

}
