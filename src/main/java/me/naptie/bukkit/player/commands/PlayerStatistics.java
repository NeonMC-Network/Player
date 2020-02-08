package me.naptie.bukkit.player.commands;

import me.naptie.bukkit.player.Messages;
import me.naptie.bukkit.player.utils.CU;
import me.naptie.bukkit.player.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PlayerStatistics implements CommandExecutor {

	@SuppressWarnings("NullableProblems")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		int total = 0, genderUnset = 0, male = 0, female = 0, ageUnset = 0, age_0_10 = 0, age_11_15 = 0, age_16_20 = 0, age_21_30 = 0, age_31_40 = 0, age_41_60 = 0, age_61_80 = 0, age_81 = 0, owner = 0, admin = 0, mod = 0, helper = 0, up = 0, mvpPP = 0, mvpP = 0, mvp = 0, vipP = 0, vip = 0, member = 0;
		Map<String, Integer> languageUsage = new HashMap<>();
		for (File file : Objects.requireNonNull(ConfigManager.getDataFolder().listFiles())) {
			if (file.getName().endsWith(".yml")) {
				YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
				if (data.contains("gender")) {
					total++;
					if (data.getString("gender").equalsIgnoreCase("UNSET")) {
						genderUnset++;
					} else if (data.getString("gender").equalsIgnoreCase("MALE")) {
						male++;
					} else if (data.getString("gender").equalsIgnoreCase("FEMALE")) {
						female++;
					}
				}
				if (data.contains("birth-year")) {
					if (data.getString("birth-year").equalsIgnoreCase("UNSET")) {
						ageUnset++;
					} else {
						int age = ConfigManager.getAge(Bukkit.getOfflinePlayer(UUID.fromString(file.getName().replace(".yml", ""))));
						if (age >= 0 && age <= 10)
							age_0_10++;
						else if (age <= 15)
							age_11_15++;
						else if (age <= 20)
							age_16_20++;
						else if (age <= 30)
							age_21_30++;
						else if (age <= 40)
							age_31_40++;
						else if (age <= 60)
							age_41_60++;
						else if (age <= 80)
							age_61_80++;
						else age_81++;
					}
				}
				if (data.contains("rank")) {
					String rank = ConfigManager.getData(Bukkit.getOfflinePlayer(UUID.fromString(file.getName().replace(".yml", "")))).getString("rank");
					assert rank != null;
					if (rank.equalsIgnoreCase("OWNER"))
						owner++;
					if (rank.equalsIgnoreCase("ADMIN"))
						admin++;
					if (rank.equalsIgnoreCase("MOD"))
						mod++;
					if (rank.equalsIgnoreCase("HELPER"))
						helper++;
					if (rank.equalsIgnoreCase("UP"))
						up++;
					if (rank.equalsIgnoreCase("MVP++") || rank.equalsIgnoreCase("MVPPLUSPLUS"))
						mvpPP++;
					if (rank.equalsIgnoreCase("MVP+") || rank.equalsIgnoreCase("MVPPLUS"))
						mvpP++;
					if (rank.equalsIgnoreCase("MVP"))
						mvp++;
					if (rank.equalsIgnoreCase("VIP+") || rank.equalsIgnoreCase("VIPPLUS"))
						vipP++;
					if (rank.equalsIgnoreCase("VIP"))
						vip++;
					if (rank.equalsIgnoreCase("MEMBER") || rank.equalsIgnoreCase("HACKER"))
						member++;
				}
				if (data.contains("language")) {
					String language = data.getString("language");
					languageUsage.put(language, languageUsage.getOrDefault(language, 0) + 1);
				} else {
					languageUsage.put("en-US", languageUsage.getOrDefault("en-US", 0) + 1);
				}
			}
		}
		String maleP;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) male / (double) total) * 100);
			maleP = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String femaleP;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) female / (double) total) * 100);
			femaleP = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String genderUnsetP;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) genderUnset / (double) total) * 100);
			genderUnsetP = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String age_0_10P;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) age_0_10 / (double) total) * 100);
			age_0_10P = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String age_11_15P;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) age_11_15 / (double) total) * 100);
			age_11_15P = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String age_16_20P;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) age_16_20 / (double) total) * 100);
			age_16_20P = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String age_21_30P;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) age_21_30 / (double) total) * 100);
			age_21_30P = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String age_31_40P;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) age_31_40 / (double) total) * 100);
			age_31_40P = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String age_41_60P;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) age_41_60 / (double) total) * 100);
			age_41_60P = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String age_61_80P;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) age_61_80 / (double) total) * 100);
			age_61_80P = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String age_81P;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) age_81 / (double) total) * 100);
			age_81P = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String ageUnsetP;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) ageUnset / (double) total) * 100);
			ageUnsetP = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String ownerP;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) owner / (double) total) * 100);
			ownerP = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String adminP;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) admin / (double) total) * 100);
			adminP = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String modP;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) mod / (double) total) * 100);
			modP = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String helperP;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) helper / (double) total) * 100);
			helperP = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String upP;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) up / (double) total) * 100);
			upP = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String mvpPP_P;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) mvpPP / (double) total) * 100);
			mvpPP_P = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String mvpP_P;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) mvpP / (double) total) * 100);
			mvpP_P = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String mvp_P;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) mvp / (double) total) * 100);
			mvp_P = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String vipP_P;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) vipP / (double) total) * 100);
			vipP_P = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String vip_P;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) vip / (double) total) * 100);
			vip_P = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		String memberP;
		{
			BigDecimal bigDecimal = new BigDecimal(((double) member / (double) total) * 100);
			memberP = bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
		}
		Map<String, String> languageUsageP = new HashMap<>();
		for (String language : languageUsage.keySet()) {
			BigDecimal bigDecimal = new BigDecimal(((double) languageUsage.get(language) / (double) total) * 100);
			languageUsageP.put(language, bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%");
		}
		sender.sendMessage(Messages.HYPHEN);
		sender.sendMessage(Messages.getMessageOnCommand(sender, "PLAYER_STATISTICS"));
		if (args.length == 0) {
			sender.sendMessage("");
			sender.sendMessage(Messages.getMessageOnCommand(sender, "TOTAL_PLAYER_COUNT").replace("%count%", total + ""));
			sender.sendMessage("");
			for (String string : Messages.getMessageOnCommand(sender, "GENDER_COUNT").split("%n%")) {
				if (string.contains("%malec%"))
					string = string.replace("%malec%", male + "");
				if (string.contains("%malep%"))
					string = string.replace("%malep%", maleP);
				if (string.contains("%femalec%"))
					string = string.replace("%femalec%", female + "");
				if (string.contains("%femalep%"))
					string = string.replace("%femalep%", femaleP);
				if (string.contains("%unsetc%"))
					string = string.replace("%unsetc%", genderUnset + "");
				if (string.contains("%unsetp%"))
					string = string.replace("%unsetp%", genderUnsetP);
				sender.sendMessage(string);
			}
			sender.sendMessage("");
			sender.sendMessage(Messages.getMessageOnCommand(sender, "AGE_COUNT"));
			sender.sendMessage(CU.t("  &e0-10: &a" + age_0_10 + " &9(" + age_0_10P + ")"));
			sender.sendMessage(CU.t("  &e11-15: &a" + age_11_15 + " &9(" + age_11_15P + ")"));
			sender.sendMessage(CU.t("  &e16-20: &a" + age_16_20 + " &9(" + age_16_20P + ")"));
			sender.sendMessage(CU.t("  &e21-30: &a" + age_21_30 + " &9(" + age_21_30P + ")"));
			sender.sendMessage(CU.t("  &e31-40: &a" + age_31_40 + " &9(" + age_31_40P + ")"));
			sender.sendMessage(CU.t("  &e41-60: &a" + age_41_60 + " &9(" + age_41_60P + ")"));
			sender.sendMessage(CU.t("  &e61-80: &a" + age_61_80 + " &9(" + age_61_80P + ")"));
			sender.sendMessage(CU.t("  &e\u226581: &a" + age_81 + " &9(" + age_81P + ")"));
			sender.sendMessage(CU.t("  " + CU.t(Messages.getMessageOnCommand(sender, "COUNT_UNSET") + ": &a" + ageUnset + " &9(" + ageUnsetP + ")")));
			sender.sendMessage("");
			sender.sendMessage(CU.t(Messages.getMessageOnCommand(sender, "RANK_COUNT")));
			sender.sendMessage(CU.t("  &cOWNER&e: &a" + owner + " &9(" + ownerP + ")"));
			sender.sendMessage(CU.t("  &cADMIN&e: &a" + admin + " &9(" + adminP + ")"));
			sender.sendMessage(CU.t("  &2MOD&e: &a" + mod + " &9(" + modP + ")"));
			sender.sendMessage(CU.t("  &9HELPER&e: &a" + helper + " &9(" + helperP + ")"));
			sender.sendMessage(CU.t("  &fUP&e: &a" + up + " &9(" + upP + ")"));
			sender.sendMessage(CU.t("  &6MVP&c++&e: &a" + mvpPP + " &9(" + mvpPP_P + ")"));
			sender.sendMessage(CU.t("  &bMVP&c+&e: &a" + mvpP + " &9(" + mvpP_P + ")"));
			sender.sendMessage(CU.t("  &bMVP&e: &a" + mvp + " &9(" + mvp_P + ")"));
			sender.sendMessage(CU.t("  &aVIP&6+&e: &a" + vipP + " &9(" + vipP_P + ")"));
			sender.sendMessage(CU.t("  &aVIP&e: &a" + vip + " &9(" + vip_P + ")"));
			sender.sendMessage(CU.t("  &7MEMBER&e: &a" + member + " &9(" + memberP + ")"));
			sender.sendMessage("");
			sender.sendMessage(Messages.getMessageOnCommand(sender, "LANGUAGE_USAGE"));
			for (String language : languageUsage.keySet()) {
				sender.sendMessage(CU.t("  &e" + Messages.getMessageOnCommand(sender, language) + ": &a" + languageUsage.get(language) + " &9(" + languageUsageP.get(language) + ")"));
			}
		} else {
			if (args[0].equalsIgnoreCase("total")) {
				sender.sendMessage(Messages.getMessageOnCommand(sender, "TOTAL_PLAYER_COUNT").replace("%count%", total + ""));
			} else if (args[0].equalsIgnoreCase("gender")) {
				for (String string : Messages.getMessageOnCommand(sender, "GENDER_COUNT").split("%n%")) {
					if (string.contains("%malec%"))
						string = string.replace("%malec%", male + "");
					if (string.contains("%malep%"))
						string = string.replace("%malep%", maleP);
					if (string.contains("%femalec%"))
						string = string.replace("%femalec%", female + "");
					if (string.contains("%femalep%"))
						string = string.replace("%femalep%", femaleP);
					if (string.contains("%unsetc%"))
						string = string.replace("%unsetc%", genderUnset + "");
					if (string.contains("%unsetp%"))
						string = string.replace("%unsetp%", genderUnsetP);
					sender.sendMessage(string);
				}
			} else if (args[0].equalsIgnoreCase("age")) {
				sender.sendMessage(CU.t(Messages.getMessageOnCommand(sender, "AGE_COUNT")));
				sender.sendMessage(CU.t("  &e0-10: &a" + age_0_10 + " &9(" + age_0_10P + ")"));
				sender.sendMessage(CU.t("  &e11-15: &a" + age_11_15 + " &9(" + age_11_15P + ")"));
				sender.sendMessage(CU.t("  &e16-20: &a" + age_16_20 + " &9(" + age_16_20P + ")"));
				sender.sendMessage(CU.t("  &e21-30: &a" + age_21_30 + " &9(" + age_21_30P + ")"));
				sender.sendMessage(CU.t("  &e31-40: &a" + age_31_40 + " &9(" + age_31_40P + ")"));
				sender.sendMessage(CU.t("  &e41-60: &a" + age_41_60 + " &9(" + age_41_60P + ")"));
				sender.sendMessage(CU.t("  &e61-80: &a" + age_61_80 + " &9(" + age_61_80P + ")"));
				sender.sendMessage(CU.t("  &e\u226581: &a" + age_81 + " &9(" + age_81P + ")"));
				sender.sendMessage(CU.t("  " + CU.t(Messages.getMessageOnCommand(sender, "COUNT_UNSET") + ": &a" + ageUnset + " &9(" + ageUnsetP + ")")));
			} else if (args[0].equalsIgnoreCase("rank")) {
				sender.sendMessage(CU.t(Messages.getMessageOnCommand(sender, "RANK_COUNT")));
				sender.sendMessage(CU.t("  &cOWNER&e: &a" + owner + " &9(" + ownerP + ")"));
				sender.sendMessage(CU.t("  &cADMIN&e: &a" + admin + " &9(" + adminP + ")"));
				sender.sendMessage(CU.t("  &2MOD&e: &a" + mod + " &9(" + modP + ")"));
				sender.sendMessage(CU.t("  &9HELPER&e: &a" + helper + " &9(" + helperP + ")"));
				sender.sendMessage(CU.t("  &fUP&e: &a" + up + " &9(" + upP + ")"));
				sender.sendMessage(CU.t("  &6MVP&c++&e: &a" + mvpPP + " &9(" + mvpPP_P + ")"));
				sender.sendMessage(CU.t("  &bMVP+&e: &a" + mvpP + " &9(" + mvpP_P + ")"));
				sender.sendMessage(CU.t("  &bMVP&e: &a" + mvp + " &9(" + mvp_P + ")"));
				sender.sendMessage(CU.t("  &aVIP&6+&e: &a" + vipP + " &9(" + vipP_P + ")"));
				sender.sendMessage(CU.t("  &aVIP&e: &a" + vip + " &9(" + vip_P + ")"));
				sender.sendMessage(CU.t("  &7MEMBER&e: &a" + member + " &9(" + memberP + ")"));
			} else if (args[0].equalsIgnoreCase("language")) {
				sender.sendMessage(Messages.getMessageOnCommand(sender, "LANGUAGE_USAGE"));
				for (String language : languageUsage.keySet()) {
					sender.sendMessage(CU.t("  &e" + Messages.getMessageOnCommand(sender, language) + ": &a" + languageUsage.get(language) + " &9(" + languageUsageP.get(language) + ")"));
				}
			} else {
				sender.sendMessage(Messages.getMessageOnCommand(sender, "USAGE").replace("%usage%", sender instanceof Player ? "/playerstatistics [total|gender|age|rank|language]" : "playerstatistics [total|gender|age|rank|language]"));
			}
		}
		sender.sendMessage(Messages.HYPHEN);
		return true;
	}

}
