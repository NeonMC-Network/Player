package me.naptie.bukkit.player.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.naptie.bukkit.player.Main;
import me.naptie.bukkit.player.Messages;

public class ProfileManager implements CommandExecutor {

	private static Main m;

	public ProfileManager() {
		m = Main.getInstance();
	}

	@SuppressWarnings({"deprecation", "NullableProblems"})
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(Messages.NOT_A_PLAYER);
			return true;
		}

		Player player = (Player) sender;

		if (command.getName().equalsIgnoreCase("profile")) {

			if (args.length == 0) {

				player.openInventory(getInventory(player));

			} else if (args.length == 1) {
				OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
				if (target == null) {
					player.sendMessage(Messages.getMessage(player, "PLAYER_NOT_FOUND").replace("%player%", args[0]));
					return true;
				}
				player.openInventory(getInventory(player, target));
			}

		}

		return true;
	}

	public Inventory getInventory(Player player) {
		Inventory i = Bukkit.createInventory(null, 54, Messages.getMessage(player, "MY_PROFILE"));
		setupInventory(player, i, Material.CYAN_STAINED_GLASS_PANE);
		i.setItem(20, getLanguageItem(player));
		i.setItem(21, getStatisticItem(player));
		return i;
	}

	public Inventory getInventory(Player player, OfflinePlayer target) {
		Inventory i = Bukkit.createInventory(null, 54, Messages.getMessage(player, "OTHERS_PROFILE").replace("%player%", target.getName()));
		setupInventory(player, i, Material.ORANGE_STAINED_GLASS_PANE);
		i.setItem(22, SkullManager.getPersonalSkull(target, ConfigManager.getLanguageName(player)));
		i.setItem(30, getStatisticItem(player));
		return i;
	}

	private ItemStack getAgeItem(OfflinePlayer player) {
		checkAge(player);
		ItemStack age = SkullManager.getCustomSkull(
				"http://textures.minecraft.net/texture/8ff88b122ff92513c6a27b7f67cb3fea97439e078821d6861b74332a2396");
		ItemMeta ageMeta = age.getItemMeta();
		ageMeta.setDisplayName(ChatColor.GREEN + Messages.getMessage(player, "AGE"));
		ageMeta.setLore(
				Collections.singletonList(
						ConfigManager.getData(player).getString("birth-year")
								.equalsIgnoreCase("UNSET")
										? Messages.getMessage(player, "AGE_NOT_TOLD")
										: ChatColor.GRAY + Messages.getMessage(player, "AGE_LORE").replace("%age%", "" + (Integer.parseInt(m.getCurrentFormattedDate("yyyy"))
														- ConfigManager.getData(player).getInt("birth-year")))));
		age.setItemMeta(ageMeta);
		return age;
	}

	private ItemStack getGenderItem(OfflinePlayer player) {
		ItemStack gender = SkullManager.getCustomSkull(
				"http://textures.minecraft.net/texture/a1bfee8010c680a961bc83a25a54a5865b244b6d1cb7dc75b6219a93555296");
		ItemMeta genderMeta = gender.getItemMeta();
		genderMeta.setDisplayName(ChatColor.GREEN + Messages.getMessage(player, "GENDER"));
		genderMeta.setLore(Collections.singletonList(ConfigManager.getData(player).getString("gender").equalsIgnoreCase("UNSET")
				? Messages.getMessage(player, "GENDER_NOT_TOLD")
				: (ConfigManager.getData(player).getString("gender").equalsIgnoreCase("male") ? Messages.getMessage(player, "MALE_LORE") : Messages.getMessage(player, "FEMALE_LORE"))));
		gender.setItemMeta(genderMeta);
		return gender;
	}

	private ItemStack getFriendItem(OfflinePlayer player) {
		ItemStack friend = SkullManager.getCustomSkull(
				"http://textures.minecraft.net/texture/19592cad924d70a1f4af2738d4922e1abdc487dff68c0727d37ed9b2ab64071");
		ItemMeta friendMeta = friend.getItemMeta();
		friendMeta.setDisplayName(ChatColor.GREEN + Messages.getMessage(player, "FRIENDS"));
		friendMeta.setLore(Collections.singletonList(Messages.getMessage(player, "FRIENDS_LORE")));
		friend.setItemMeta(friendMeta);
		return friend;
	}

	private ItemStack getPartyItem(OfflinePlayer player) {
		ItemStack party = SkullManager.getCustomSkull(
				"http://textures.minecraft.net/texture/345b2edd9ec69a350a867db0e5b0b87551aff498a88e01e2bd6a036ff4d39");
		ItemMeta partyMeta = party.getItemMeta();
		partyMeta.setDisplayName(ChatColor.GREEN + Messages.getMessage(player, "PARTIES"));
		/* List<String> partyLore = new ArrayList<>(); TODO */
		partyMeta.setLore(Collections.singletonList(ChatColor.GRAY + "This feature is coming soon!"));
		party.setItemMeta(partyMeta);
		return party;
	}

	private ItemStack getLanguageItem(OfflinePlayer player) {
		ItemStack language = SkullManager.getCustomSkull(
				"http://textures.minecraft.net/texture/b1dd4fe4a429abd665dfdb3e21321d6efa6a6b5e7b956db9c5d59c9efab25");
		ItemMeta languageMeta = language.getItemMeta();
		languageMeta.setDisplayName(ChatColor.GREEN + Messages.getMessage(player, "LANGUAGES"));
		languageMeta.setLore(Collections.singletonList(Messages.getMessage(player, "CURRENT_LANGUAGE").replace("%language%", Messages.getMessage(player, "LANGUAGE")).replace("§e", "§7")));
		language.setItemMeta(languageMeta);
		return language;
	}

	private ItemStack getStatisticItem(OfflinePlayer player) {
		ItemStack statistic = new ItemStack(Material.BREWING_STAND, 1);
		ItemMeta statisticMeta = statistic.getItemMeta();
		statisticMeta.setDisplayName(ChatColor.GREEN + Messages.getMessage(player, "STATISTICS"));
		statisticMeta.setLore(Collections.singletonList(Messages.getMessage(player, "STATISTICS_LORE")));
		statistic.setItemMeta(statisticMeta);
		return statistic;
	}

	private void setGlassPanes(Inventory i, Material material) {
		ItemStack glassPane = new ItemStack(material);
		ItemMeta glassPaneMeta = glassPane.getItemMeta();
		glassPaneMeta.setDisplayName(ChatColor.RESET + "");
		glassPane.setItemMeta(glassPaneMeta);
		i.setItem(9, glassPane);
		i.setItem(10, glassPane);
		i.setItem(11, glassPane);
		i.setItem(12, glassPane);
		i.setItem(13, glassPane);
		i.setItem(14, glassPane);
		i.setItem(15, glassPane);
		i.setItem(16, glassPane);
		i.setItem(17, glassPane);
	}

	public void setupInventory(Player p, Inventory i, Material glassPaneMaterial) {
		i.setItem(2, SkullManager.getPersonalSkull(p, ConfigManager.getLanguageName(p)));
		i.setItem(3, getAgeItem(p));
		i.setItem(4, getGenderItem(p));
		i.setItem(5, getFriendItem(p));
		i.setItem(6, getPartyItem(p));
		setGlassPanes(i, glassPaneMaterial);
	}

	public ItemStack getMaleItem(OfflinePlayer player) {
		ItemStack male = SkullManager.getCustomSkull(
				"http://textures.minecraft.net/texture/e7fe6e2316ca384e0c85261b14fc066e231d6127e540dfeddb8b54d911b82831");
		ItemMeta maleMeta = male.getItemMeta();
		maleMeta.setDisplayName(ChatColor.AQUA + Messages.getMessage(player, "MALE"));
		List<String> maleLore = new ArrayList<>();
		maleLore.add(Messages.getMessage(player, "SELECT_IF_MALE"));
		maleLore.add(Messages.getMessage(player, "ONCE_SET"));
		maleMeta.setLore(maleLore);
		male.setItemMeta(maleMeta);
		return male;
	}

	public ItemStack getFemaleItem(OfflinePlayer player) {
		ItemStack female = SkullManager.getCustomSkull(
				"http://textures.minecraft.net/texture/f6aac8c1c290be9d3bd495476aca42c68623b9cbefdd6ddbd6144855b9731c63");
		ItemMeta femaleMeta = female.getItemMeta();
		femaleMeta.setDisplayName(ChatColor.LIGHT_PURPLE + Messages.getMessage(player, "FEMALE"));
		List<String> femaleLore = new ArrayList<>();
		femaleLore.add(Messages.getMessage(player, "SELECT_IF_FEMALE"));
		femaleLore.add(Messages.getMessage(player, "ONCE_SET"));
		femaleMeta.setLore(femaleLore);
		female.setItemMeta(femaleMeta);
		return female;
	}

	public ItemStack getNumericItem(int i) {
		if (i == 0) {
			return get0Item();
		} else if (i == 1) {
			return get1Item();
		} else if (i == 2) {
			return get2Item();
		} else if (i == 3) {
			return get3Item();
		} else if (i == 4) {
			return get4Item();
		} else if (i == 5) {
			return get5Item();
		} else if (i == 6) {
			return get6Item();
		} else if (i == 7) {
			return get7Item();
		} else if (i == 8) {
			return get8Item();
		} else if (i == 9) {
			return get9Item();
		} else {
			return getUnsetItem();
		}
	}

	public ItemStack get0Item() {
		ItemStack item = SkullManager.getCustomSkull(
				"http://textures.minecraft.net/texture/b04fa5ecbcc843807977221a1bb4b523a23cf518090f2a682af52d33e9b064");
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "0");
		item.setItemMeta(itemMeta);
		return item;
	}

	public ItemStack get1Item() {
		ItemStack item = SkullManager.getCustomSkull(
				"http://textures.minecraft.net/texture/d837a6d222013db4f13bd9049b1d6ef1592508dda7057420b954726375ade1");
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "1");
		item.setItemMeta(itemMeta);
		return item;
	}

	public ItemStack get2Item() {
		ItemStack item = SkullManager.getCustomSkull(
				"http://textures.minecraft.net/texture/e37b9d1d275e3e1e6f2adbe5a78389d26efed0bc2fdaebc27538a112a4acc77");
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "2");
		item.setItemMeta(itemMeta);
		return item;
	}

	public ItemStack get3Item() {
		ItemStack item = SkullManager.getCustomSkull(
				"http://textures.minecraft.net/texture/bf7d31383a80838d79a8745a495d1f67d3766a2622cb5f3db4ac3992f7f415");
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "3");
		item.setItemMeta(itemMeta);
		return item;
	}

	public ItemStack get4Item() {
		ItemStack item = SkullManager.getCustomSkull(
				"http://textures.minecraft.net/texture/b9ba8029b28254b6aef5397333138fa5d0abbf4c7dea9e667daed85873f45");
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "4");
		item.setItemMeta(itemMeta);
		return item;
	}

	public ItemStack get5Item() {
		ItemStack item = SkullManager.getCustomSkull(
				"http://textures.minecraft.net/texture/d246ac67620d3b5217c49d9c30173a3793d4d41f4f251c9f6232eec75a74");
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "5");
		item.setItemMeta(itemMeta);
		return item;
	}

	public ItemStack get6Item() {
		ItemStack item = SkullManager.getCustomSkull(
				"http://textures.minecraft.net/texture/16b7eb957bca6678cd0b63d84ea7bf28bf67eb8ab3f8f65beb461e236f429a");
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "6");
		item.setItemMeta(itemMeta);
		return item;
	}

	public ItemStack get7Item() {
		ItemStack item = SkullManager.getCustomSkull(
				"http://textures.minecraft.net/texture/548782887434121a7cffd543d827282f73728851c4e3137dc3637f82b373a");
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "7");
		item.setItemMeta(itemMeta);
		return item;
	}

	public ItemStack get8Item() {
		ItemStack item = SkullManager.getCustomSkull(
				"http://textures.minecraft.net/texture/bbf9a2e71692aa85d064baf06227fc1e5931a6827f3a6f7b7e38c279f5e75084");
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "8");
		item.setItemMeta(itemMeta);
		return item;
	}

	public ItemStack get9Item() {
		ItemStack item = SkullManager.getCustomSkull(
				"http://textures.minecraft.net/texture/36d6161c6f04def6481e03b4c2972798816ddc514aef4b11c2d7a5a8fc9fe");
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "9");
		item.setItemMeta(itemMeta);
		return item;
	}

	public ItemStack getUnsetItem() {
		ItemStack item = SkullManager.getCustomSkull(
				"http://textures.minecraft.net/texture/19ae956fb8a2935275306839539897b55d5e337fdc655da7c40823d859a");
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GRAY + "-");
		item.setItemMeta(itemMeta);
		return item;
	}
	
	public static void setGender(Player player, String gender) {
		ConfigManager.getData(player).set("gender", gender.toUpperCase());
		try {
			ConfigManager.getData(player).save(ConfigManager.getDataFile(player));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void setAge(Player player, int age) {
		int birthYear = Integer.parseInt(Main.getInstance().getCurrentFormattedDate("yyyy")) - age;
		ConfigManager.getData(player).set("birth-year", birthYear);
		try {
			ConfigManager.getData(player).save(ConfigManager.getDataFile(player));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean checkAge(OfflinePlayer player) {
		String age = ConfigManager.getData(player).getString("birth-year")
				.equalsIgnoreCase("UNSET")
				? Messages.getMessage(player, "AGE_NOT_TOLD")
				: String.valueOf(Integer.parseInt(m.getCurrentFormattedDate("yyyy")) - ConfigManager.getData(player).getInt("birth-year"));
		if (age.contains("-1")) {
			ConfigManager.getData(player).set("birth-year", "UNSET");
			try {
				ConfigManager.getData(player).save(ConfigManager.getDataFile(player));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

}
