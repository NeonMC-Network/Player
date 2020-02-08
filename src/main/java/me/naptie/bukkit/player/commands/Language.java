package me.naptie.bukkit.player.commands;

import me.naptie.bukkit.player.Messages;
import me.naptie.bukkit.player.events.LanguageChangeEvent;
import me.naptie.bukkit.player.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class Language implements CommandExecutor {

	public static String[] supportedLanguages = { "zh-CN", "en-GB", "en-US" };

	@SuppressWarnings({"NullableProblems", "ConstantConditions"})
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			String previousLanguage = ConfigManager.getData(player).getString("language");
			if (args.length > 0) {
				for (String supportedLanguage : supportedLanguages) {
					if (args[0].toLowerCase().equals(supportedLanguage.toLowerCase())) {
						if (supportedLanguage.equals(previousLanguage)) {
							player.sendMessage(Messages.getMessage(player, "CURRENT_LANGUAGE").replace("%language%", Messages.getLanguage(player).getString("LANGUAGE")));
							return true;
						}
						ConfigManager.getData(player).set("language", supportedLanguage);
						try {
							ConfigManager.getData(player).save(ConfigManager.getDataFile(player));
						} catch (IOException e) {
							e.printStackTrace();
						}
						Bukkit.getServer().getPluginManager().callEvent(new LanguageChangeEvent(player, previousLanguage, supportedLanguage));
						player.sendMessage(Messages.getMessage(player, "NEW_LANGUAGE").replace("%language%", Messages.getLanguage(player).getString("LANGUAGE")));
						return true;
					}
				}
				StringBuilder languages = new StringBuilder();
				for (String availableLanguage : supportedLanguages) {
					if (languages.toString().equals("")) {
						languages = new StringBuilder(availableLanguage);
					} else {
						languages.append(", ").append(availableLanguage);
					}
				}
				player.sendMessage(Messages.getMessage(player, "AVAILABLE_LANGUAGES") + languages);
				return true;
			} else {
				player.sendMessage(Messages.getMessage(player, "CURRENT_LANGUAGE").replace("%language%", Messages.getLanguage(player).getString("LANGUAGE")));
				return true;
			}
		} else {
			sender.sendMessage(Messages.NOT_A_PLAYER);
			return true;
		}
	}
}
