package mc.alk.tracker.events;

import mc.alk.tracker.Tracker;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TrackerEvent extends Event{
	private static final HandlerList handlers = new HandlerList();

	public void callEvent(){
		Bukkit.getServer().getPluginManager().callEvent(this);
	}

	public void callSyncEvent(){
		final Event event = this;
		Bukkit.getScheduler().scheduleSyncDelayedTask(Tracker.getSelf(), new Runnable(){
			@Override
			public void run() {
				Bukkit.getServer().getPluginManager().callEvent(event);
			}
		});
	}

	@Override
	public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
