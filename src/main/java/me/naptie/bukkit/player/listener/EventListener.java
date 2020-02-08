package me.naptie.bukkit.player.listener;

import me.naptie.bukkit.player.Main;
import me.naptie.bukkit.player.Messages;
import me.naptie.bukkit.player.commands.Language;
import me.naptie.bukkit.player.events.LanguageChangeEvent;
import me.naptie.bukkit.player.utils.*;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EventListener implements Listener {

    private ProfileManager p;
    private FriendsManager f;
    private Main plugin;
    private Set<Player> justWarnedWithMessage = new HashSet<>();
    private Set<Player> justWarned = new HashSet<>();
    private Map<Player, Location> locations = new HashMap<>();

    public EventListener(Main plugin, ProfileManager p, FriendsManager f) {
        this.plugin = plugin;
        this.p = p;
        this.f = f;
    }

    @SuppressWarnings("ConstantConditions")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        locations.put(player, player.getLocation());
        YamlConfiguration data = YamlConfiguration
                .loadConfiguration(new File(ConfigManager.getDataFolder().getAbsolutePath(), player.getUniqueId() + ".yml"));
        ConfigManager.playerDataMap.put(player.getUniqueId(), data);
        ConfigManager.updateDataFile(player, true);
        if (ConfigManager.getData(player).getString("gender").equalsIgnoreCase("UNSET")) {
            player.sendMessage(Messages.getMessage(player, "GENDER_UNSET"));
        } else if (ConfigManager.getData(player).getString("birth-year").equalsIgnoreCase("UNSET")) {
            player.sendMessage(Messages.getMessage(player, "AGE_UNSET"));
        }
        if (!ConfigManager.isEmailVerified(player)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> player.sendMessage(Messages.getMessage(player, "EMAIL_NOT_VERIFIED")), 20);
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                if (!ConfigManager.isEmailVerified(player))
                    player.kickPlayer(Messages.getMessage(player, "PLEASE_VERIFY_EMAIL"));
            }, 6000);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!ConfigManager.isEmailVerified(player)) {
            if (!justWarned.contains(player)) {
                player.teleport(locations.get(player));
                justWarned.add(player);
                Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> justWarned.remove(player), 1);
            }
        }
        if (!justWarnedWithMessage.contains(player)) {
            if (!ConfigManager.isEmailVerified(player)) {
                player.sendMessage(Messages.getMessage(player, "EMAIL_NOT_VERIFIED"));
                justWarnedWithMessage.add(player);
                Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> justWarnedWithMessage.remove(player), 190);
            } else {
                justWarned.remove(player);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ConfigManager.playerDataMap.remove(player.getUniqueId());
    }

    @SuppressWarnings({"UnusedAssignment", "deprecation", "StatementWithEmptyBody"})
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        Inventory i = event.getClickedInventory();
        InventoryView view = event.getView();
        if (i != null) {
            if (view.getTitle().contains(Messages.getMessage(player, "PROFILE"))) {
                event.setCancelled(true);
                ItemStack item = event.getCurrentItem();
                if (item != null && item.getItemMeta() != null) {

                    if (event.getSlot() == 2) {
                        player.openInventory(p.getInventory(player));
                    }

                    if (event.getSlot() == 3) {
                        Inventory age = Bukkit.createInventory(null, 54, Messages.getMessage(player, "MY_PROFILE") + " - " + Messages.getMessage(player, "AGE"));
                        p.setupInventory(player, age, Material.RED_STAINED_GLASS_PANE);

                        ItemStack tens = p.getUnsetItem();
                        ItemMeta tensMeta = tens.getItemMeta();
                        tensMeta.setDisplayName(Messages.getMessage(player, "TENS"));
                        tensMeta.setLore(Collections.singletonList(Messages.getMessage(player, "PLEASE_ENTER")));
                        tens.setItemMeta(tensMeta);

                        ItemStack ones = p.getUnsetItem();
                        ItemMeta onesMeta = ones.getItemMeta();
                        onesMeta.setDisplayName(Messages.getMessage(player, "ONES"));
                        onesMeta.setLore(Collections.singletonList(Messages.getMessage(player, "PLEASE_ENTER")));
                        ones.setItemMeta(onesMeta);

                        ItemStack backspace = SkullManager.getCustomSkull(
                                "http://textures.minecraft.net/texture/841dd127595a25c2439c5db31ccb4914507ae164921aafec2b979aad1cfe7");
                        ItemMeta backspaceMeta = backspace.getItemMeta();
                        backspaceMeta.setDisplayName(Messages.getMessage(player, "BACKSPACE"));
                        backspace.setItemMeta(backspaceMeta);

                        ItemStack enter = SkullManager.getCustomSkull(
                                "http://textures.minecraft.net/texture/d2d0313b6680141286396e71c361e5962a39baf596d7e54771775d5fa3d");
                        ItemMeta enterMeta = enter.getItemMeta();
                        enterMeta.setDisplayName(Messages.getMessage(player, "ENTER"));
                        enter.setItemMeta(enterMeta);

                        age.setItem(28, tens);
                        age.setItem(29, ones);
                        age.setItem(31, p.get0Item());
                        age.setItem(32, p.get1Item());
                        age.setItem(33, p.get2Item());
                        age.setItem(34, p.get3Item());
                        age.setItem(35, p.get4Item());
                        age.setItem(40, p.get5Item());
                        age.setItem(41, p.get6Item());
                        age.setItem(42, p.get7Item());
                        age.setItem(43, p.get8Item());
                        age.setItem(44, p.get9Item());
                        age.setItem(52, backspace);
                        age.setItem(53, enter);

                        player.openInventory(age);
                    }

                    if (event.getSlot() == 4) {
                        Inventory gender = Bukkit.createInventory(null, 54, Messages.getMessage(player, "MY_PROFILE") + " - " + Messages.getMessage(player, "GENDER"));
                        p.setupInventory(player, gender, Material.ORANGE_STAINED_GLASS_PANE);
                        if (ConfigManager.getData(player).getString("gender").equalsIgnoreCase("unset")) {
                            gender.setItem(29, p.getMaleItem(player));
                            gender.setItem(33, p.getFemaleItem(player));
                        } else if (ConfigManager.getData(player).getString("gender").equalsIgnoreCase("male")) {
                            ItemStack glassPane = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
                            ItemMeta glassPaneMeta = glassPane.getItemMeta();
                            glassPaneMeta.setDisplayName(ChatColor.RESET + "");
                            glassPane.setItemMeta(glassPaneMeta);
                            ItemStack male = p.getMaleItem(player);
                            ItemMeta maleMeta = male.getItemMeta();
                            maleMeta.setLore(Collections.singletonList(Messages.getMessage(player, "ALREADY_CHOSEN_MALE")));
                            male.setItemMeta(maleMeta);
                            gender.setItem(31, male);
                            for (int n = 18; n < gender.getSize(); n++) {
                                if (n != 31) {
                                    gender.setItem(n, glassPane);
                                }
                            }
                        } else if (ConfigManager.getData(player).getString("gender").equalsIgnoreCase("female")) {
                            ItemStack glassPane = new ItemStack(Material.PINK_STAINED_GLASS_PANE);
                            ItemMeta glassPaneMeta = glassPane.getItemMeta();
                            glassPaneMeta.setDisplayName(ChatColor.RESET + "");
                            glassPane.setItemMeta(glassPaneMeta);
                            ItemStack female = p.getFemaleItem(player);
                            ItemMeta femaleMeta = female.getItemMeta();
                            femaleMeta
                                    .setLore(Collections.singletonList(Messages.getMessage(player, "ALREADY_CHOSEN_FEMALE")));
                            female.setItemMeta(femaleMeta);
                            gender.setItem(31, female);
                            for (int n = 18; n < gender.getSize(); n++) {
                                if (n != 31) {
                                    gender.setItem(n, glassPane);
                                }
                            }
                        } else {
                            ItemStack glassPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                            ItemMeta glassPaneMeta = glassPane.getItemMeta();
                            glassPaneMeta.setDisplayName(Messages.getMessage(player, "ERROR"));
                            glassPaneMeta.setLore(Collections.singletonList(Messages.getMessage(player, "GENDER_DATA_FAILURE")));
                            glassPane.setItemMeta(glassPaneMeta);
                            gender.setItem(31, glassPane);
                        }
                        player.openInventory(gender);
                    }

                    if (event.getSlot() == 5 && f != null) {
                        Inventory friendsInv = Bukkit.createInventory(null, 54, Messages.getMessage(player, "MY_PROFILE") + " - " + Messages.getMessage(player, "FRIENDS"));
                        p.setupInventory(player, friendsInv, Material.ORANGE_STAINED_GLASS_PANE);
                        List<String> list = f.getFriends(player.getUniqueId());

                        if (list.size() == 0) {
                            ItemStack glassPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                            ItemMeta glassPaneMeta = glassPane.getItemMeta();
                            glassPaneMeta.setDisplayName(Messages.getMessage(player, "NO_FRIENDS"));
                            glassPaneMeta.setLore(Collections.singletonList(Messages.getMessage(player, "NO_FRIENDS_LORE")));
                            glassPane.setItemMeta(glassPaneMeta);
                            friendsInv.setItem(31, glassPane);
                        } else {

                            List<ItemStack> online = new ArrayList<>();
                            List<ItemStack> offline = new ArrayList<>();

                            for (String uuid : list) {
                                OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(UUID.fromString(uuid));
                                ItemStack targetSkull = SkullManager.getPersonalSkull(target, ConfigManager.getLanguageName(player));
                                //TODO
                                if (target.isOnline()) {
                                    online.add(targetSkull);
                                } else {
                                    offline.add(targetSkull);
                                }
                            }

                            List<ItemStack> all = new ArrayList<>();
                            all.addAll(online);
                            all.addAll(offline);

                            int page = 1;
                            int currentPage = page;

                            int onePage = 8;
                            page = page * onePage;

                            int min = page - (onePage - 1);
                            int max = page;

                            double ceil = (double) all.size() / onePage;
                            int maxPage = (int) Math.ceil(ceil) - 1;

                            if (currentPage > maxPage) {
                                maxPage = currentPage;
                            }
                            ItemStack pageCounter = SkullManager.getCustomSkull("http://textures.minecraft.net/texture/19592cad924d70a1f4af2738d4922e1abdc487dff68c0727d37ed9b2ab64071");
                            ItemMeta counterMeta = pageCounter.getItemMeta();
                            counterMeta.setDisplayName(ChatColor.GREEN + Messages.getMessage(player, "FRIENDS"));
                            counterMeta.setLore(Collections.singletonList(CU.t("&a" + currentPage + " &f/ " + maxPage)));
                            pageCounter.setItemMeta(counterMeta);
                            ItemStack previousPage = SkullManager.getCustomSkull("http://textures.minecraft.net/texture/6ccbf9883dd359fdf2385c90a459d737765382ec4117b04895ac4dc4b60fc");
                            ItemMeta previousMeta = previousPage.getItemMeta();
                            previousMeta.setDisplayName(CU.t(Messages.getMessage(player, "PREVIOUS_PAGE")));
                            previousPage.setItemMeta(previousMeta);
                            ItemStack nextPage = SkullManager.getCustomSkull("http://textures.minecraft.net/texture/72431911f4178b4d2b413aa7f5c78ae4447fe9246943c31df31163c0e043e0d6");
                            ItemMeta nextMeta = nextPage.getItemMeta();
                            nextMeta.setDisplayName(CU.t(Messages.getMessage(player, "NEXT_PAGE")));
                            nextPage.setItemMeta(nextMeta);
                            friendsInv.setItem(22, pageCounter);
                            friendsInv.setItem(34, previousPage);
                            friendsInv.setItem(43, nextPage);

                            while (true) {
                                ItemStack printItem;
                                try {
                                    printItem = all.get(min - 1);
                                } catch (Exception e) {
                                    break;
                                }
                                int slot = 27;
                                if (min <= 4) slot += min;
                                else slot += 9 + (min - 4);
                                friendsInv.setItem(slot, printItem);

                                if (min == max) {
                                    break;
                                }

                                min++;
                            }
                        }

                        player.openInventory(friendsInv);
                    }

                    if (event.getSlot() == 6) {
                        // TODO
                    }

                    if (view.getTitle().equals(Messages.getMessage(player, "MY_PROFILE"))) {

                        if (event.getSlot() == 20 && event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + Messages.getMessage(player, "LANGUAGES"))) {
                            Inventory languageInv = Bukkit.createInventory(null, 54, Messages.getMessage(player, "MY_PROFILE") + " - " + Messages.getMessage(player, "LANGUAGES"));
                            p.setupInventory(player, languageInv, Material.ORANGE_STAINED_GLASS_PANE);
                            List<ItemStack> languages = new ArrayList<>();
                            for (String language : Language.supportedLanguages) {
                                ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
                                if (language.equals("en-GB")) {
                                    stack = SkullManager.getCustomSkull("http://textures.minecraft.net/texture/a9edcdd7b06173d7d221c7274c86cba35730170788bb6a1db09cc6810435b92c");
                                }
                                if (language.equals("en-US")) {
                                    stack = SkullManager.getCustomSkull("http://textures.minecraft.net/texture/4cac9774da1217248532ce147f7831f67a12fdcca1cf0cb4b3848de6bc94b4");
                                }
                                if (language.equals("zh-CN")) {
                                    stack = SkullManager.getCustomSkull("http://textures.minecraft.net/texture/7f9bc035cdc80f1ab5e1198f29f3ad3fdd2b42d9a69aeb64de990681800b98dc");
                                }
                                ItemMeta meta = stack.getItemMeta();
                                meta.setDisplayName(ChatColor.GREEN + Messages.getMessage(language, "LANGUAGE"));
                                stack.setItemMeta(meta);
                                languages.add(stack);
                            }
                            for (int key = 0; key < languages.size(); key++) {
                                languageInv.setItem(18 + key, languages.get(key));
                            }
                            player.openInventory(languageInv);
                        }

                        if (event.getSlot() == 21 && event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + Messages.getMessage(player, "STATISTICS"))) {
                            Inventory statisticInv = Bukkit.createInventory(null, 54, Messages.getMessage(player, "MY_PROFILE") + " - " + Messages.getMessage(player, "STATISTICS"));
                            p.setupInventory(player, statisticInv, Material.ORANGE_STAINED_GLASS_PANE);
                            Set<String> games = new HashSet<>();
                            for (String key : ConfigManager.getData(player).getKeys(true)) {
                                if (key.endsWith(".wins")) {
                                    games.add(key.replace(".wins", ""));
                                }
                            }
                            int slot = 18;
                            for (String game : games) {
                                ItemStack stack;
                                switch (game) {
                                    case "skywars":
                                        stack = new ItemStack(Material.ENDER_EYE, 1);
                                        break;
                                    case "bedwars":
                                        stack = new ItemStack(Material.RED_BED, 1);
                                        break;
                                    case "thebridge":
                                        stack = new ItemStack(Material.RED_TERRACOTTA, 1);
                                        break;
                                    default:
                                        stack = new ItemStack(Material.WHITE_WOOL, 1);
                                        break;
                                }
                                ItemMeta meta = stack.getItemMeta();
                                meta.setDisplayName(ChatColor.GREEN + Messages.getMessage(player, game.toUpperCase()));
                                List<String> lore = new ArrayList<>();
                                int wins = ConfigManager.getData(player).getInt(game + ".wins");
                                Integer kills = null, deaths = null;
                                lore.add(ChatColor.WHITE + Messages.getMessage(player, "WINS") + ": " + ChatColor.GREEN + wins);
                                if (ConfigManager.getData(player).contains(game + ".kills")) {
                                    kills = ConfigManager.getData(player).getInt(game + ".kills");
                                    lore.add(ChatColor.WHITE + Messages.getMessage(player, "KILLS") + ": " + ChatColor.GREEN + kills);
                                }
                                if (ConfigManager.getData(player).contains(game + ".deaths")) {
                                    deaths = ConfigManager.getData(player).getInt(game + ".deaths");
                                    lore.add(ChatColor.WHITE + Messages.getMessage(player, "DEATHS") + ": " + ChatColor.GREEN + deaths);
                                }
                                if (kills != null && deaths != null) {
                                    if (kills == 0 || deaths == 0) {
                                        lore.add(ChatColor.WHITE + Messages.getMessage(player, "KDRATIO") + ": " + ChatColor.GREEN + "N/A");
                                    } else {
                                        float kdRatio = (float) kills / deaths;
                                        lore.add(ChatColor.WHITE + Messages.getMessage(player, "KDRATIO") + ": " + ChatColor.GREEN + (("" + kdRatio).length() > 4 ? ("" + kdRatio).substring(0, 4) : (("" + kdRatio).length() < 4 ? kdRatio + "0" : kdRatio)));
                                    }
                                }
                                meta.setLore(lore);
                                stack.setItemMeta(meta);
                                statisticInv.setItem(slot, stack);
                                slot++;
                            }
                            player.openInventory(statisticInv);
                        }
                    } else {
                        OfflinePlayer target = Bukkit.getOfflinePlayer(view.getTitle().replace(Messages.getMessage(player, "OTHERS_PROFILE").replace("%player%", ""), ""));
                        if (event.getSlot() == 22) {
                            System.out.println("Slot 22 was clicked. Current target is " + target.getName());
                            player.openInventory(p.getInventory(player, target));
                        }
                        if (event.getSlot() == 30 && event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + Messages.getMessage(player, "STATISTICS"))) {
                            Inventory statisticInv = Bukkit.createInventory(null, 54, Messages.getMessage(player, "OTHERS_PROFILE").replace("%player%", target.getName()) + " - " + Messages.getMessage(player, "STATISTICS"));
                            p.setupInventory(player, statisticInv, Material.ORANGE_STAINED_GLASS_PANE);
                            statisticInv.setItem(22, SkullManager.getPersonalSkull(target, ConfigManager.getLanguageName(player)));
                            Set<String> games = new HashSet<>();
                            for (String key : ConfigManager.getData(target).getKeys(true)) {
                                if (key.endsWith(".wins")) {
                                    games.add(key.replace(".wins", ""));
                                }
                            }
                            int slot = 27;
                            for (String game : games) {
                                ItemStack stack;
                                switch (game) {
                                    case "skywars":
                                        stack = new ItemStack(Material.ENDER_EYE, 1);
                                        break;
                                    case "bedwars":
                                        stack = new ItemStack(Material.RED_BED, 1);
                                        break;
                                    case "thebridge":
                                        stack = new ItemStack(Material.RED_TERRACOTTA, 1);
                                        break;
                                    default:
                                        stack = new ItemStack(Material.WHITE_WOOL, 1);
                                        break;
                                }
                                ItemMeta meta = stack.getItemMeta();
                                meta.setDisplayName(ChatColor.GREEN + Messages.getMessage(player, game.toUpperCase()));
                                List<String> lore = new ArrayList<>();
                                int wins = ConfigManager.getData(target).getInt(game + ".wins");
                                Integer kills = null, deaths = null;
                                lore.add(ChatColor.WHITE + Messages.getMessage(player, "WINS") + ": " + ChatColor.GREEN + wins);
                                if (ConfigManager.getData(target).contains(game + ".kills")) {
                                    kills = ConfigManager.getData(target).getInt(game + ".kills");
                                    lore.add(ChatColor.WHITE + Messages.getMessage(player, "KILLS") + ": " + ChatColor.GREEN + kills);
                                }
                                if (ConfigManager.getData(target).contains(game + ".deaths")) {
                                    deaths = ConfigManager.getData(target).getInt(game + ".deaths");
                                    lore.add(ChatColor.WHITE + Messages.getMessage(player, "DEATHS") + ": " + ChatColor.GREEN + deaths);
                                }
                                if (kills != null && deaths != null) {
                                    if (kills == 0 || deaths == 0) {
                                        lore.add(ChatColor.WHITE + Messages.getMessage(player, "KDRATIO") + ": " + ChatColor.GREEN + "N/A");
                                    } else {
                                        float kdRatio = (float) kills / deaths;
                                        lore.add(ChatColor.WHITE + Messages.getMessage(player, "KDRATIO") + ": " + ChatColor.GREEN + (("" + kdRatio).length() > 4 ? ("" + kdRatio).substring(0, 4) : (("" + kdRatio).length() < 4 ? kdRatio + "0" : kdRatio)));
                                    }
                                }
                                meta.setLore(lore);
                                stack.setItemMeta(meta);
                                statisticInv.setItem(slot, stack);
                                slot++;
                            }
                            player.openInventory(statisticInv);
                        }
                    }
                }
                if (view.getTitle().contains(Messages.getMessage(player, "AGE"))) {
                    if ((event.getSlot() >= 31 && event.getSlot() <= 35)
                            || (event.getSlot() >= 40 && event.getSlot() <= 44)) {
                        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
                        int current = Character.getNumericValue(itemName.charAt(itemName.length() - 1));
                        if (i.getItem(29).getItemMeta().getDisplayName().contains(Messages.getMessage(player, "ONES"))) {
                            i.setItem(29, p.getNumericItem(current));
                        } else if (i.getItem(28).getItemMeta().getDisplayName().contains(Messages.getMessage(player, "TENS"))) {
                            i.setItem(28, i.getItem(29));
                            i.setItem(29, p.getNumericItem(current));
                        }
                    } else if (event.getSlot() == 52) {
                        ItemStack tens = p.getUnsetItem();
                        ItemMeta tensMeta = tens.getItemMeta();
                        tensMeta.setDisplayName(Messages.getMessage(player, "TENS"));
                        tensMeta.setLore(Collections.singletonList(Messages.getMessage(player, "PLEASE_ENTER")));
                        tens.setItemMeta(tensMeta);
                        ItemStack ones = p.getUnsetItem();
                        ItemMeta onesMeta = ones.getItemMeta();
                        onesMeta.setDisplayName(Messages.getMessage(player, "ONES"));
                        onesMeta.setLore(Collections.singletonList(Messages.getMessage(player, "PLEASE_ENTER")));
                        ones.setItemMeta(onesMeta);
                        if (!i.getItem(28).getItemMeta().getDisplayName().contains(Messages.getMessage(player, "TENS"))) {
                            i.setItem(29, i.getItem(28));
                            i.setItem(28, tens);
                        } else {
                            i.setItem(29, ones);
                            i.setItem(28, tens);
                        }
                    } else if (event.getSlot() == 53) {
                        int age;
                        if (i.getItem(28).getItemMeta().getDisplayName().contains(Messages.getMessage(player, "TENS"))) {
                            if (i.getItem(29).getItemMeta().getDisplayName().contains(Messages.getMessage(player, "ONES"))) {
                                player.sendMessage(Messages.getMessage(player, "VALUE_NOT_GIVEN"));
                                player.closeInventory();
                                return;
                            }
                            String itemName = i.getItem(29).getItemMeta().getDisplayName();
                            age = Character.getNumericValue(itemName.charAt(itemName.length() - 1));
                        } else {
                            String tensItemName = i.getItem(28).getItemMeta().getDisplayName();
                            int tens = Character.getNumericValue(tensItemName.charAt(tensItemName.length() - 1));
                            String onesItemName = i.getItem(29).getItemMeta().getDisplayName();
                            int ones = Character.getNumericValue(onesItemName.charAt(onesItemName.length() - 1));
                            age = (tens * 10) + ones;
                        }
                        ProfileManager.setAge(player, age);
                        player.closeInventory();
                        player.sendMessage(Messages.getMessage(player, "AGE_SET").replace("%age%", age + ""));
                    }
                }

                if (view.getTitle().contains(Messages.getMessage(player, "GENDER"))) {
                    if (ConfigManager.getData(player).getString("gender").equalsIgnoreCase("unset")) {
                        if (event.getSlot() == 29) {
                            if (event.getCurrentItem().getItemMeta().getDisplayName().contains(Messages.getMessage(player, "BE_SURE_TO_CHOOSE"))) {
                                ProfileManager.setGender(player, "MALE");
                                player.closeInventory();
                                player.sendMessage(Messages.getMessage(player, "SUCCESSFULLY_CHOSEN_MALE"));
                                return;
                            }
                            i.setItem(22, null);
                            i.setItem(23, null);
                            i.setItem(24, null);
                            i.setItem(25, null);
                            i.setItem(26, null);
                            i.setItem(31, null);
                            i.setItem(32, null);
                            i.setItem(33, p.getFemaleItem(player));
                            i.setItem(34, null);
                            i.setItem(35, null);
                            i.setItem(40, null);
                            i.setItem(41, null);
                            i.setItem(42, null);
                            i.setItem(43, null);
                            i.setItem(44, null);
                            i.setItem(49, null);
                            i.setItem(50, null);
                            i.setItem(51, null);
                            i.setItem(52, null);
                            i.setItem(53, null);
                            ItemStack glassPane = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
                            ItemMeta glassPaneMeta = glassPane.getItemMeta();
                            glassPaneMeta.setDisplayName(ChatColor.RESET + "");
                            glassPane.setItemMeta(glassPaneMeta);
                            i.setItem(18, glassPane);
                            i.setItem(19, glassPane);
                            i.setItem(20, glassPane);
                            i.setItem(21, glassPane);
                            i.setItem(22, glassPane);
                            i.setItem(27, glassPane);
                            i.setItem(28, glassPane);
                            i.setItem(30, glassPane);
                            i.setItem(31, glassPane);
                            i.setItem(36, glassPane);
                            i.setItem(37, glassPane);
                            i.setItem(38, glassPane);
                            i.setItem(39, glassPane);
                            i.setItem(40, glassPane);
                            i.setItem(45, glassPane);
                            i.setItem(46, glassPane);
                            i.setItem(47, glassPane);
                            i.setItem(48, glassPane);
                            i.setItem(49, glassPane);

                            ItemStack male = i.getItem(29);
                            ItemMeta maleMeta = male.getItemMeta();
                            maleMeta.setDisplayName(Messages.getMessage(player, "BE_SURE_TO_CHOOSE_MALE"));
                            maleMeta.setLore(Arrays.asList(Messages.getMessage(player, "CONFIRM"), Messages.getMessage(player, "ONCE_SET")));
                            male.setItemMeta(maleMeta);
                            i.setItem(29, male);
                        }

                        if (event.getSlot() == 33) {
                            if (event.getCurrentItem().getItemMeta().getDisplayName().contains(Messages.getMessage(player, "BE_SURE_TO_CHOOSE"))) {
                                ProfileManager.setGender(player, "FEMALE");
                                player.closeInventory();
                                player.sendMessage(Messages.getMessage(player, "SUCCESSFULLY_CHOSEN_FEMALE"));
                                return;
                            }
                            i.setItem(18, null);
                            i.setItem(19, null);
                            i.setItem(20, null);
                            i.setItem(21, null);
                            i.setItem(22, null);
                            i.setItem(27, null);
                            i.setItem(28, null);
                            i.setItem(29, p.getMaleItem(player));
                            i.setItem(30, null);
                            i.setItem(31, null);
                            i.setItem(36, null);
                            i.setItem(37, null);
                            i.setItem(38, null);
                            i.setItem(39, null);
                            i.setItem(40, null);
                            i.setItem(45, null);
                            i.setItem(46, null);
                            i.setItem(47, null);
                            i.setItem(48, null);
                            i.setItem(49, null);
                            ItemStack glassPane = new ItemStack(Material.PINK_STAINED_GLASS_PANE);
                            ItemMeta glassPaneMeta = glassPane.getItemMeta();
                            glassPaneMeta.setDisplayName(ChatColor.RESET + "");
                            glassPane.setItemMeta(glassPaneMeta);
                            i.setItem(22, glassPane);
                            i.setItem(23, glassPane);
                            i.setItem(24, glassPane);
                            i.setItem(25, glassPane);
                            i.setItem(26, glassPane);
                            i.setItem(31, glassPane);
                            i.setItem(32, glassPane);
                            i.setItem(34, glassPane);
                            i.setItem(35, glassPane);
                            i.setItem(40, glassPane);
                            i.setItem(41, glassPane);
                            i.setItem(42, glassPane);
                            i.setItem(43, glassPane);
                            i.setItem(44, glassPane);
                            i.setItem(49, glassPane);
                            i.setItem(50, glassPane);
                            i.setItem(51, glassPane);
                            i.setItem(52, glassPane);
                            i.setItem(53, glassPane);

                            ItemStack female = i.getItem(33);
                            ItemMeta femaleMeta = female.getItemMeta();
                            femaleMeta.setDisplayName(CU.t(Messages.getMessage(player, "BE_SURE_TO_CHOOSE_FEMALE")));
                            femaleMeta.setLore(Arrays.asList(Messages.getMessage(player, "CONFIRM"), Messages.getMessage(player, "ONCE_SET")));
                            female.setItemMeta(femaleMeta);
                            i.setItem(33, female);
                        }
                    }
                }

                if (view.getTitle().contains(Messages.getMessage(player, "LANGUAGES"))) {
                    for (String language : Language.supportedLanguages)
                        if (event.getCurrentItem().getItemMeta().getDisplayName().contains(Messages.getMessage(language, "LANGUAGE"))) {
                            String previousLanguage = ConfigManager.getLanguageName(player);
                            ConfigManager.getData(player).set("language", language);
                            try {
                                ConfigManager.getData(player).save(ConfigManager.getDataFile(player));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Bukkit.getServer().getPluginManager().callEvent(new LanguageChangeEvent(player, previousLanguage, language));
                            player.closeInventory();
                            player.sendMessage(Messages.getMessage(player, "NEW_LANGUAGE").replace("%language%", Messages.getLanguage(player).getString("LANGUAGE")));
                        }
                }
            }
        }
    }

}
