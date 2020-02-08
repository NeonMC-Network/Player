package me.naptie.bukkit.player;

import me.naptie.bukkit.player.utils.CU;
import me.naptie.bukkit.player.utils.ConfigManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class Messages {

	public static final String NOT_A_PLAYER = getMessage("zh-CN", "NOT_A_PLAYER");
	public static final String MYSQL_CONNECTED = CU.t("[" + Main.getInstance().getDescription().getName() + "] " + getMessage("zh-CN", "MYSQL_CONNECTED"));
	public static final String HYPHEN = CU.t("&a-----------------------------");

	public static String getMessage(YamlConfiguration language, String message) {
		return CU.t(language.getString(message));
	}

	public static String getMessage(String language, String message) {
		return CU.t(YamlConfiguration.loadConfiguration(new File(Main.getInstance().getDataFolder(), language + ".yml")).getString(message));
	}

	public static String getMessage(OfflinePlayer player, String message) {
		return CU.t(getLanguage(player).getString(message));
	}

	public static String getMessageOnCommand(CommandSender sender, String message) {
		if (sender instanceof Player)
			return CU.t(getLanguage((Player) sender).getString(message));
		else
			return getMessage("zh-CN", message);
	}

	public static YamlConfiguration getLanguage(OfflinePlayer player) {
		File locale = new File(Main.getInstance().getDataFolder(), ConfigManager.getLanguageName(player) + ".yml");
		return YamlConfiguration.loadConfiguration(locale);
	}

	public static String translate(String text, String fromLanguage, String toLanguage) {
		String result = "";
		YamlConfiguration sourceYaml = YamlConfiguration.loadConfiguration(new File(Main.getInstance().getDataFolder(), fromLanguage + ".yml"));
		YamlConfiguration targetYaml = YamlConfiguration.loadConfiguration(new File(Main.getInstance().getDataFolder(), toLanguage + ".yml"));
		for (String key : sourceYaml.getKeys(false)) {
			if (text.equalsIgnoreCase(sourceYaml.getString(key))) {
				result = targetYaml.getString(key);
			}
		}
		return result;
	}

}
