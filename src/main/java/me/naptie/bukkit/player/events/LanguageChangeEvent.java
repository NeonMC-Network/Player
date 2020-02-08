package me.naptie.bukkit.player.events;

import me.naptie.bukkit.player.listener.ParkourListener;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LanguageChangeEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	private Player player;
	private String previousLanguage;
	private String newLanguage;
	private boolean changeItemName;

	public LanguageChangeEvent(Player player, String previousLanguage, String newLanguage) {
		this.player = player;
		this.previousLanguage = previousLanguage;
		this.newLanguage = newLanguage;
		this.changeItemName = !ParkourListener.playersInGame.contains(player);
	}

	public Player getPlayer() {
		return this.player;
	}

	public String getPreviousLanguage() {
		return this.previousLanguage;
	}

	public String getNewLanguage() {
		return this.newLanguage;
	}

	public boolean isChangingItemName() {
		return this.changeItemName;
	}
}
