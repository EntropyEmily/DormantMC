package com.dormant.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

public class Database {
    protected final JavaPlugin plugin;
    private String dbname;

    public Database(JavaPlugin main, String dbname, String... tables) {
        plugin = main;
        this.dbname = dbname;
        createTables(tables);
    }

    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname + ".db");
        if (!dataFolder.exists()) {
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: " + dbname + ".db", e);
            }
        }
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize.", e);
        } catch (ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "SQLite JDBC Library not found.");
        }
        return connection;
    }

    private void createTables(String... tables) {
        Connection con = getSQLConnection();
        try {
            Statement s = con.createStatement();
            for (String table : tables) {
                s.executeUpdate(table);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    protected void executeUpdate(String... statements) {
        Connection con = null;
        try {
            con = getSQLConnection();
            Statement s = con.createStatement();
            for (String statement : statements) {
                s.executeUpdate(statement);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
        } finally {
            close(con, null, null);
        }
    }
    
    public void close(Connection con, PreparedStatement ps, ResultSet res) {
        try {
            if (con != null) {
                con.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (res != null) {
                res.close();
            }
        } catch (SQLException e) {
            Error.close(plugin, e);
        }
    }

}
