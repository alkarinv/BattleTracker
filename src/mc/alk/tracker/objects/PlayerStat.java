package mc.alk.tracker.objects;

import java.util.ArrayList;

import org.bukkit.OfflinePlayer;

public class PlayerStat extends TeamStat{

	public PlayerStat(OfflinePlayer player) {
		this(player.getName());
	}

	public PlayerStat(String player) {
		this.name = this.strid = player;
		members = new ArrayList<String>();
		members.add(player);
		count = 1;
	}
	
}
