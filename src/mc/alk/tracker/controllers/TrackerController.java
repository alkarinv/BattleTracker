package mc.alk.tracker.controllers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;


public class TrackerController {
	static Set<String> dontTrack = new HashSet<String>();
	static Set<String> dontAnnounce = new HashSet<String>();


	public static boolean dontTrack(Player target) {
		return dontTrack.contains(target.getName())	;
	}

	public static boolean dontTrack(String target) {
		return dontTrack.contains(target);
	}

	public static void stopTracking(String playername){dontTrack.add(playername);}
	public static void resumeTracking(String playername){dontTrack.remove(playername);}
	public static void stopAnnouncing(String playername){dontAnnounce.add(playername);}
	public static void resumeAnnouncing(String playername){dontAnnounce.remove(playername);}


	public static void stopAnnouncing(Collection<Player> players) {for (Player p:players)dontAnnounce.add(p.getName());}
	public static void resumeAnnouncing(Collection<Player> players) {for (Player p:players)dontAnnounce.remove(p.getName());}
	public static void stopTracking(Collection<Player> players) {for (Player p:players){dontTrack.add(p.getName());}}
	public static void resumeTracking(Collection<Player> players) {for (Player p:players)dontTrack.remove(p.getName());}
}
