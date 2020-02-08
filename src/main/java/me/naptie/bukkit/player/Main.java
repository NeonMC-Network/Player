package me.naptie.bukkit.player;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import me.naptie.bukkit.player.commands.Experience;
import me.naptie.bukkit.player.commands.Language;
import me.naptie.bukkit.player.commands.PlayerStatistics;
import me.naptie.bukkit.player.commands.Verify;
import me.naptie.bukkit.player.listener.EventListener;
import me.naptie.bukkit.player.listener.ParkourListener;
import org.apache.commons.lang.SystemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.naptie.bukkit.player.utils.ConfigManager;
import me.naptie.bukkit.player.utils.EmailChecker;
import me.naptie.bukkit.player.utils.FriendsManager;
import me.naptie.bukkit.player.utils.MySQLManager;
import me.naptie.bukkit.player.utils.ProfileManager;
import me.naptie.bukkit.player.utils.RankManager;
import me.naptie.bukkit.player.utils.SkullManager;


public class Main extends JavaPlugin {
    
    public static Logger logger;
    
    private static Main instance;
    
    private ProfileManager p;
    
    public static Main getInstance() {
        return instance;
    }
    
    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onEnable() {
        instance = this;
        logger = getLogger();
        p = new ProfileManager();
        getConfig().options().copyDefaults(true);
        getConfig().options().copyHeader(true);
        saveDefaultConfig();
        for (String language : getConfig().getStringList("languages")) {
            File localeFile = new File(getDataFolder(), language + ".yml");
            if (localeFile.exists()) {
                if (getConfig().getBoolean("update-language-files")) {
                    saveResource(language + ".yml", true);
                }
            } else {
                saveResource(language + ".yml", false);
            }
        }
        File playerDataFolder;
        if (getConfig().getBoolean("auto-detect")) {
            playerDataFolder = new File(getDataFolder().getAbsolutePath()
                    .split("NeonMC" + (SystemUtils.IS_OS_WINDOWS ? "\\\\" : File.separator))[0] + "NeonMC"
                    + File.separator + "PlayerData" + File.separator);
            if (!playerDataFolder.exists()) {
                playerDataFolder.mkdir();
            }
        } else {
            playerDataFolder = new File(getConfig().getString("data-path"));
        }
        FriendsManager f = null;
        if (getConfig().getBoolean("mysql.enable")) {
            new MySQLManager(this);
        }
        if (getConfig().getBoolean("friends.enable")) {
            f = new FriendsManager(this);
        }
        getServer().getPluginManager().registerEvents(new EventListener(this, p, f), this);
        if (Bukkit.getPluginManager().isPluginEnabled("NeonMCParkour"))
            getServer().getPluginManager().registerEvents(new ParkourListener(), this);
        new ConfigManager(playerDataFolder);
        new SkullManager();
        new RankManager();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            ConfigManager.updateDataFile(player, true);
        }
        try {
            EmailChecker.init(getConfig().getConfigurationSection("email"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        getCommand("profile").setExecutor(p);
        getCommand("language").setExecutor(new Language());
        getCommand("experience").setExecutor(new Experience());
        getCommand("playerstatistics").setExecutor(new PlayerStatistics());
        Verify.init();
        
        logger.info("Enabled " + getDescription().getName() + " v" + getDescription().getVersion());
    }
    
    @Override
    public void onDisable() {
        instance = null;
        logger.info("Disabled " + getDescription().getName() + " v" + getDescription().getVersion());
    }
    
    public String getCurrentFormattedDate(String format) {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat(format);
        return ft.format(date);
    }
    
}
