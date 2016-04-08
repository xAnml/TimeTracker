package net.anmlmc.timetracker.database;

import com.google.common.collect.Maps;
import net.md_5.bungee.BungeeCord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/*******************
 * Created by Anml *
 *******************/

public class DatabaseManager {

    private MySQL database;

    public DatabaseManager(String ip, String port, String dbName, String pass, String user) {
        database = new MySQL(ip, port, dbName, pass, user);
        try {
            database.openConnection();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            BungeeCord.getInstance().stop("Database Connection Error!");
        }
    }

    public MySQL getMySQL() {
        return database;
    }

    public void closeConnection() {
        if (database.connection == null)
            return;
        try {
            database.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getIntByUUID(String table, String columnName, String uuid) {
        try {
            ResultSet set = database.query("SELECT * FROM " + table + " WHERE playeruuid = '" + uuid + "';");
            return set.getInt(columnName);
        } catch (Exception e) {
            return 0;
        }
    }

    public String getStringByUUID(String table, String columnName, UUID uuid) {
        try {
            ResultSet set = database.query("SELECT " + columnName + " FROM " + table + " WHERE playeruuid = '" + uuid + "';");
            set.next();
            return set.getString(columnName);
        } catch (Exception e) {
            return null;
        }
    }

    public Object getObjectByUUID(String table, String columnName, String uuid) {
        try {
            ResultSet set = database.query("SELECT * FROM " + table + " WHERE playeruuid = '" + uuid + "';");
            return set.getObject(columnName);
        } catch (Exception e) {
            return null;
        }
    }

    public ResultSet getResultSetByUUID(String table, String uuid) {
        try {
            ResultSet set = database.query("SELECT * FROM " + table + " WHERE playeruuid = '" + uuid + "';");
            set.next();
            return set;
        } catch (Exception e) {
            return null;
        }
    }

    public ResultSet getSet(String query) {
        try {
            System.out.println(query);
            return database.query(query);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean exists(String table, String column, String value) {
        try {
            return database.query("SELECT * FROM " + table + " WHERE " + column + " = '" + value + "';").next();
        } catch (Exception e) {
            return false;
        }
    }

    public void createTable(String table, List<String> keys) {
        String query = "";
        try {

            query = "CREATE TABLE IF NOT EXISTS " + table + "(`" + keys.get(0).split(Pattern.quote(";"))[0] + "` "
                    + keys.get(0).split(Pattern.quote(";"))[1];

            for (int i = 1; i < keys.size(); i++) {
                String[] s = keys.get(i).split(Pattern.quote(";"));
                query += ", `" + s[0] + "` " + s[1];
            }
            query += ", PRIMARY KEY(`" + keys.get(0).split(Pattern.quote(";"))[0] + "`));";
            database.update(query);
        } catch (Exception e) {
        }
    }

    public void set(String table, HashMap<String, Object> map, String whereClause, String whereValue) {
        try {
            String[] keySet = map.keySet().toArray(new String[map.keySet().size()]);
            String query = "";
            if (exists(table, whereClause, whereValue)) {
                query = "UPDATE " + table + " SET " + keySet[0] + " = '" + map.get(keySet[0]) + "'";
                for (int i = 1; i < map.keySet().size(); i++) {
                    String s = keySet[i];
                    query += ", " + s + " = '" + map.get(s) + "'";
                }

                query += " WHERE " + whereClause + " = '" + whereValue + "';";
            } else {
                query = "INSERT INTO " + table + " (`" + whereClause + "`, `" + keySet[0] + "`";
                for (int i = 1; i < map.keySet().size(); i++) {
                    String s = keySet[i];
                    query += ", `" + s + "`";
                }
                query += ") VALUES ('" + whereValue + "', '" + map.get(keySet[0]) + "'";
                for (int i = 1; i < map.keySet().size(); i++) {
                    String s = keySet[i];
                    query += ", '" + map.get(s) + "'";
                }
                query += ");";
            }

            database.update(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPlus(String table, String col, Integer val, String whereClause, String whereValue) {
        try {

            int current = val;
            ResultSet set = getSet("SELECT * FROM " + table + " WHERE " + whereClause + " = '" + whereValue + "';");
            if (set.next()) {
                current += set.getInt(col);
            }
            HashMap<String, Object> map = Maps.newHashMap();
            map.put(col, current);

            set(table, map, whereClause, whereValue);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(String table, HashMap<String, String> map) {
        String query;
        String[] keySet = map.keySet().toArray(new String[map.keySet().size()]);

        query = "INSERT INTO " + table + " (`" + keySet[0] + "`";
        for (int i = 1; i < map.keySet().size(); i++) {
            String s = keySet[i];
            query += ", `" + s + "`";
        }
        query += ") VALUES ('" + map.get(keySet[0]) + "'";
        for (int i = 1; i < map.keySet().size(); i++) {
            String s = keySet[i];
            query += ", '" + map.get(s) + "'";
        }
        query += ");";

        try {
            database.update(query);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    public void update(String string) {
        try {
            database.update(string);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

}
