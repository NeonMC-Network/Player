package me.naptie.bukkit.player.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ExperienceUpdateEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private OfflinePlayer player;
	private int previousPoint;
	private int currentPoint;
	public ExperienceUpdateEvent(OfflinePlayer player, int previousPoint, int currentPoint) {
		this.player = player;
		this.previousPoint = previousPoint;
		this.currentPoint = currentPoint;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public OfflinePlayer getPlayer() {
		return this.player;
	}

	public int getPreviousPoint() {
		return previousPoint;
	}

	public int getCurrentPoint() {
		return currentPoint;
	}
}
