package me.naptie.bukkit.player.commands;


import me.naptie.bukkit.player.commands.utils.AbstractCommand;
import me.naptie.bukkit.player.commands.utils.CheckSenderCommand;
import me.naptie.bukkit.player.commands.utils.RootCommand;
import me.naptie.bukkit.player.commands.utils.SwitchCommand;
import me.naptie.bukkit.player.Main;
import me.naptie.bukkit.player.Messages;
import me.naptie.bukkit.player.utils.ConfigManager;
import me.naptie.bukkit.player.utils.EmailChecker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * 验证邮箱
 *
 * @author yuanlu
 */
public final class Verify {

	/**
	 * 禁止实例化
	 */
	private Verify() {
	}

	/**
	 * 初始化命令
	 */
	public static void init() {
		final String name = "verify";
		final String description = "To verify a player's email";
		final String usageMessage = "/verify help";
		final List<String> aliases = Arrays.asList("email", "verifyaccount", "verifyemail", "verifymail",
				" emailverification", " mailverification");
		HashMap<String, AbstractCommand> m = new HashMap<>();

		m.put("check", new CheckSenderCommand(new AbstractCommand() {

			@Override
			public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, int argsIndex) {
				Player p = (Player) sender;
				if (ConfigManager.isEmailVerified(p)) {
					p.sendMessage(Messages.getMessage(p, "EMAIL_ALREADY_VERIFIED"));
					return true;
				}
				if (args.length > argsIndex) {
					String vc = args[argsIndex];
					boolean back = EmailChecker.check(p, vc);
					if (back) {
						ConfigManager.getData(p).set("email-verified", true);
						ConfigManager.getData(p).set("email", EmailChecker.emailAddresses.get(p));
						try {
							ConfigManager.getData(p).save(ConfigManager.getDataFile(p));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					p.sendMessage(Messages.getMessage(p, back ? "EMAIL_CHECK_SUCCESS" : "EMAIL_CHECK_FAILURE"));// 验证成功/失败
				}
				return true;
			}

			@Override
			public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args,
			                                  int argsIndex) {
				return null;
			}

		}, Messages.NOT_A_PLAYER));
		m.put("send", new CheckSenderCommand(new AbstractCommand() {

			@Override
			public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, int argsIndex) {

				Player p = (Player) sender;
				if (ConfigManager.isEmailVerified(p)) {
					p.sendMessage(Messages.getMessage(p, "EMAIL_ALREADY_VERIFIED"));
					return true;
				}
				if (args.length > argsIndex) {
					String email = args[argsIndex];
					for (File file : Objects.requireNonNull(ConfigManager.getDataFolder().listFiles())) {
						if (file.getName().endsWith(".yml")) {
							YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
							if (data.getString("email") != null)
								if (data.getString("email").equalsIgnoreCase(email)) {
									p.sendMessage(Messages.getMessage(p, "EMAIL_ALREADY_VERIFIED_BY_OTHERS"));
									return true;
								}
						}
					}
					new BukkitRunnable() {

						@Override
						public void run() {
							p.sendMessage(Messages.getMessage(p, "EMAIL_SENDING"));
							try {
								byte r = EmailChecker.email(p, email);
								if (r == 0)
									p.sendMessage(Messages.getMessage(p, "EMAIL_SENT").replace("%address%", email));// 成功发送
								else if (r == -1)
									p.sendMessage(Messages.getMessage(p, "EMAIL_SENDING_ERROR").replace("%address%", email));// 邮箱格式错误
								else if (r == -2)
									p.sendMessage(Messages.getMessage(p, "EMAIL_SENDING_COOLDOWN"));// 冷却中
							} catch (MessagingException e) {
								sender.sendMessage(e.toString());
							}
						}
					}.runTaskAsynchronously(Main.getInstance());
				} else {
					p.sendMessage(Messages.getMessage(p, "EMAIL_NOT_GIVEN"));// 用户未输入邮箱
				}
				return true;
			}

			@Override
			public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args,
			                                  int argsIndex) {
				return EMPTY_TAB_LIST;
			}
		}, Messages.NOT_A_PLAYER));

		AbstractCommand COMMAND = new SwitchCommand("", null, true, m) {

			@Override
			protected boolean nextNullCase(CommandSender sender, Command cmd, String label, String[] args, int argsIndex) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					p.sendMessage(Messages.getMessage(p, "VERIFY_USAGE"));
				} else {
					sender.sendMessage(Messages.NOT_A_PLAYER);
				}
				return true;
			}

			@Override
			protected boolean noArgsCase(CommandSender sender, Command cmd, String label, String[] args,
			                             int argsIndex) {
				return nextNullCase(sender, cmd, label, args, argsIndex);
			}
		};

		RootCommand root = new RootCommand(name, description, usageMessage, aliases, COMMAND);
		root.register();
	}

}
