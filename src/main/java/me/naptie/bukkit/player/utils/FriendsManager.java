package me.naptie.bukkit.player.utils;

import me.naptie.bukkit.player.Main;
import me.naptie.bukkit.player.Messages;
import me.naptie.bukkit.player.tools.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FriendsManager {

	private FileConfiguration config;

	private MySQL.Editor editor;

	private boolean debug;

	public FriendsManager(Main plugin) {
		this.config = plugin.getConfig();
		this.debug = config.getBoolean("friends.debug");
		this.loginToMySQL();
	}

	private void loginToMySQL() {
		MySQL mySQL = new MySQL(this.config.getString("friends.username"), this.config.getString("friends.password"));
		mySQL.setAddress(this.config.getString("friends.address"));
		mySQL.setDatabase(this.config.getString("friends.database"));
		mySQL.setTable("bungeefriends");
		mySQL.setTimezone(this.config.getString("friends.timezone"));
		mySQL.setUseSSL(this.config.getBoolean("friends.useSSL"));

		try {
			this.editor = mySQL.connect();
			Bukkit.getConsoleSender().sendMessage(Messages.MYSQL_CONNECTED);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Executors.newSingleThreadExecutor().execute(this::autoReconnect);
	}

	@SuppressWarnings("InfiniteRecursion")
	private void autoReconnect() {
		if (this.debug) {
			Main.logger.info("Reconnecting in 30 minutes...");
		}

		try {
			TimeUnit.MINUTES.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (this.debug) {
			Main.logger.info("Reconnecting...");
		}
		try {
			this.editor = this.editor.getMySQL().reconnect();
		} catch (SQLException e) {
			if (this.debug) {
				Main.logger.info("Reconnection failed!");
			}
			this.autoReconnect();
			return;
		}
		if (this.debug) {
			Main.logger.info("Reconnection succeeded!");
		}
		this.autoReconnect();
	}

	public List<String> getFriends(UUID uuid) {
		return this.editor.getList("Player." + uuid + ".Friends");
	}

	public MySQL.Editor getEditor() {
		return editor;
	}
}
