package me.naptie.bukkit.player.utils;

import me.naptie.bukkit.player.Main;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ConfigManager {

    public static File playerDataFolder;
    public static Map<UUID, YamlConfiguration> playerDataMap = new HashMap<>();
    private static ConfigurationSection defaultData;
    private static Map<UUID, File> playerFileMap = new HashMap<>();

    public ConfigManager(File file) {
        playerDataFolder = file;
        defaultData = Main.getInstance().getConfig().getConfigurationSection("default");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void updateDataFile(Player player, boolean existenceCheck) {
        File playerData = new File(playerDataFolder.getAbsolutePath(), player.getUniqueId() + ".yml");
        if (!existenceCheck) {
            try {
                playerData.createNewFile();
                YamlConfiguration data = YamlConfiguration.loadConfiguration(playerData);
                for (String dataItem : defaultData.getKeys(true)) {
                    if (dataItem.equals("name")) {
                        data.set("name", player.getName());
                    } else {
                        data.set(dataItem, defaultData.get(dataItem));
                    }
                }
                data.save(playerData);
                playerDataMap.put(player.getUniqueId(), data);
                playerFileMap.put(player.getUniqueId(), playerData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (playerData.exists()) {
            setupData(player);
            YamlConfiguration data = playerDataMap.containsKey(player.getUniqueId())
                    ? playerDataMap.get(player.getUniqueId())
                    : YamlConfiguration.loadConfiguration(playerData);
            data.set("name", player.getName());
            if (!playerDataMap.containsKey(player.getUniqueId()))
                playerDataMap.put(player.getUniqueId(), data);
            if (!playerFileMap.containsKey(player.getUniqueId()))
                playerFileMap.put(player.getUniqueId(), playerData);
            try {
                data.save(playerData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                playerData.createNewFile();
                YamlConfiguration data = YamlConfiguration.loadConfiguration(playerData);
                for (String dataItem : defaultData.getKeys(true)) {
                    if (dataItem.equals("name")) {
                        data.set("name", player.getName());
                    } else {
                        data.set(dataItem, defaultData.get(dataItem));
                    }
                }
                data.save(playerData);
                playerDataMap.put(player.getUniqueId(), data);
                playerFileMap.put(player.getUniqueId(), playerData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void setupData(Player player) {
        File playerDataFile = new File(getDataFolder().getAbsolutePath(), player.getUniqueId() + ".yml");
        YamlConfiguration data = playerDataMap.containsKey(player.getUniqueId())
                ? playerDataMap.get(player.getUniqueId())
                : YamlConfiguration.loadConfiguration(playerDataFile);
        for (String dataItem : defaultData.getKeys(true)) {
            if (dataItem.equals("name") && data.get("name") == null) {
                data.set("name", player.getName());
            } else {
                if (data.get(dataItem) == null) {
                    data.set(dataItem, defaultData.get(dataItem));
                }
            }
        }
        if (!playerDataMap.containsKey(player.getUniqueId()))
            playerDataMap.put(player.getUniqueId(), data);
        if (!playerFileMap.containsKey(player.getUniqueId()))
            playerFileMap.put(player.getUniqueId(), playerDataFile);

        try {
            data.save(playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static YamlConfiguration getData(OfflinePlayer player) {
        if (!playerDataMap.containsKey(player.getUniqueId())) {
            YamlConfiguration data = YamlConfiguration
                    .loadConfiguration(new File(getDataFolder().getAbsolutePath(), player.getUniqueId() + ".yml"));
            playerDataMap.put(player.getUniqueId(), data);
        }
        return playerDataMap.get(player.getUniqueId());
    }

    public static YamlConfiguration getData(UUID uuid) {
        if (!playerDataMap.containsKey(uuid)) {
            YamlConfiguration data = YamlConfiguration
                    .loadConfiguration(new File(getDataFolder().getAbsolutePath(), uuid + ".yml"));
            playerDataMap.put(uuid, data);
        }
        return playerDataMap.get(uuid);
    }

    /**
     * 根据所给积分点数，返回一个表示等级的整数。
     *
     * @param point 所给的积分点数。
     * @return 一个表示等级的整数。
     */
    public static int getLevel(int point) {
        if (point < 0)
            return 0;
        if (point == 0)
            return 0;
        if (point < 20)
            return 1;
        if (point < 50)
            return 2;
        if (point < 100)
            return 3;
        if (point < 200)
            return 4;
        if (point < 300)
            return 5;
        if (point < 500)
            return 6;
        if (point < 750)
            return 7;
        if (point < 1000)
            return 8;
        if (point < 1300)
            return 9;
        if (point < 1650)
            return 10;
        else
            return point / 150;
    }

    /**
     * 根据所给积分点数，返回一个带有颜色的表示等级的字符串。
     *
     * @param point 所给的积分点数。
     * @return 一个带有颜色的表示等级的字符串。
     */
    public static String getLevelString(int point) {
        if (point < 0)
            return ChatColor.BLACK + "ERROR_NEGATIVE_VALUE_" + point;
        if (point == 0)
            return ChatColor.DARK_GRAY + "" + 0;
        if (point < 20)
            return ChatColor.GRAY + "" + 1;
        if (point < 50)
            return ChatColor.DARK_PURPLE + "" + 2;
        if (point < 100)
            return ChatColor.LIGHT_PURPLE + "" + 3;
        if (point < 200)
            return ChatColor.BLUE + "" + 4;
        if (point < 300)
            return ChatColor.AQUA + "" + 5;
        if (point < 500)
            return ChatColor.DARK_AQUA + "" + 6;
        if (point < 750)
            return ChatColor.GREEN + "" + 7;
        if (point < 1000)
            return ChatColor.DARK_GREEN + "" + 8;
        if (point < 1300)
            return ChatColor.YELLOW + "" + 9;
        if (point < 1650)
            return ChatColor.GOLD + "" + 10;
        else
            return ChatColor.RED + "" + point / 150;
    }

    /**
     * 根据所给玩家，返回一个带有颜色的表示等级的字符串。
     * 玩家的积分点数将从其数据配置中获取。
     *
     * @param player 被查询的玩家。
     * @return 一个带有颜色的表示等级的字符串。
     */
    public static String getLevel(OfflinePlayer player) {
        int point = getData(player).getInt("point");
        if (point == 0)
            return ChatColor.DARK_GRAY + "" + 0;
        if (point < 20)
            return ChatColor.GRAY + "" + 1;
        if (point < 50)
            return ChatColor.DARK_PURPLE + "" + 2;
        if (point < 100)
            return ChatColor.LIGHT_PURPLE + "" + 3;
        if (point < 200)
            return ChatColor.BLUE + "" + 4;
        if (point < 300)
            return ChatColor.AQUA + "" + 5;
        if (point < 500)
            return ChatColor.DARK_AQUA + "" + 6;
        if (point < 750)
            return ChatColor.GREEN + "" + 7;
        if (point < 1000)
            return ChatColor.DARK_GREEN + "" + 8;
        if (point < 1300)
            return ChatColor.YELLOW + "" + 9;
        if (point < 1650)
            return ChatColor.GOLD + "" + 10;
        else
            return ChatColor.RED + "" + point / 150;
    }

    /**
     * 根据所给玩家，返回一个表示等级的整数。
     *
     * @param player 被查询的玩家。
     * @return 一个表示等级的整数。
     */
    public static int getLevelInt(OfflinePlayer player) {
        int point = getData(player).getInt("point");
        if (point == 0)
            return 0;
        if (point < 20)
            return 1;
        if (point < 50)
            return 2;
        if (point < 100)
            return 3;
        if (point < 200)
            return 4;
        if (point < 300)
            return 5;
        if (point < 500)
            return 6;
        if (point < 750)
            return 7;
        if (point < 1000)
            return 8;
        if (point < 1300)
            return 9;
        if (point < 1650)
            return 10;
        else
            return point / 150;
    }

    /**
     * 根据所给玩家，返回一个表示该玩家是否验证了邮箱的布朗值。
     *
     * @param player 被查询的玩家。
     * @return 一个表示该玩家是否验证了邮箱的布朗值。
     */
    public static boolean isEmailVerified(OfflinePlayer player) {
        return getData(player).getBoolean("email-verified") && !getData(player).getString("email").equalsIgnoreCase("example@example.net");
    }

    public static File getDataFile(OfflinePlayer player) {
        if (!playerFileMap.containsKey(player.getUniqueId())) {
            File playerDataFile = new File(getDataFolder().getAbsolutePath(), player.getUniqueId() + ".yml");
            playerFileMap.put(player.getUniqueId(), playerDataFile);
        }
        return playerFileMap.get(player.getUniqueId());
    }

    public static File getDataFile(UUID uuid) {
        if (!playerFileMap.containsKey(uuid)) {
            File playerDataFile = new File(getDataFolder().getAbsolutePath(), uuid + ".yml");
            playerFileMap.put(uuid, playerDataFile);
        }
        return playerFileMap.get(uuid);
    }

    public static int getAge(OfflinePlayer player) {
        ProfileManager.checkAge(player);
        return ConfigManager.getData(player).getString("birth-year").equalsIgnoreCase("UNSET") ? -1 : Integer.parseInt(Main.getInstance().getCurrentFormattedDate("yyyy")) - ConfigManager.getData(player).getInt("birth-year");
    }

    public static File getDataFolder() {
        return playerDataFolder;
    }

    public static String getLanguageName(OfflinePlayer player) {
        if (Objects.nonNull(getData(player).getString("language"))) {
            return getData(player).getString("language");
        } else {
            return "en-US";
        }
    }

}
