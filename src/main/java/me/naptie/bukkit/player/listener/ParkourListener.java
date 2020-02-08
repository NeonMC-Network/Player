package me.naptie.bukkit.player.listener;

import me.naptie.bukkit.parkour.events.ParkourFinishEvent;
import me.naptie.bukkit.player.utils.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParkourListener implements Listener {

	public static List<Player> playersInGame = new ArrayList<>();
	
	@EventHandler
	public void onParkourFinish(ParkourFinishEvent event) {
		Player player = event.getPlayer();
		ConfigManager.getData(player).set("parkour-records." + event.getArena().getName(), event.getRecord());
		try {
			ConfigManager.getData(player).save(ConfigManager.getDataFile(player));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
