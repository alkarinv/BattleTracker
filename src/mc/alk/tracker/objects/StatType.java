package mc.alk.tracker.objects;

import org.bukkit.ChatColor;


public enum StatType {
	WINS("Wins"),KILLS("Kills"), LOSSES("Losses"),DEATHS("Deaths"),TIES("Ties"),
	STREAK("Streak"),MAXSTREAK("MaxStreak"), RANKING("Ranking"),MAXRANKING("MaxRanking"),
	RATING("Rating"),MAXRATING("MaxRating"),
	KDRATIO("K/D Ratio"),WLRATIO("W/L Ratio");

	final String name;
	StatType(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
	@Override
	public String toString(){
		return name;
	}

	public static StatType fromName(String name) {
		if (name ==null)
			return null;
		name = name.toUpperCase();
		StatType gt = null;
		try{
			gt = StatType.valueOf(name);
		} catch (Exception e){}
		if (name.equals("KDR")){
			return KDRATIO;
		} else if (name.equals("WLR")){
			return WLRATIO;
		}
		return gt;
	}
	public ChatColor color(){
		switch(this){
		case WLRATIO:
		case KDRATIO:
			return ChatColor.DARK_GREEN;
		case WINS:
		case KILLS:
			return ChatColor.GREEN;
		case DEATHS:
		case LOSSES:
			return ChatColor.DARK_RED;
		case MAXRANKING:
			return ChatColor.GOLD;
		case MAXRATING:
			return ChatColor.GOLD;
		case RANKING:
		case RATING:
			return ChatColor.GOLD;
		case STREAK:
		case MAXSTREAK:
			return ChatColor.DARK_PURPLE;
		case TIES:
			return ChatColor.YELLOW;
		default:
			return ChatColor.YELLOW;
		}
	}
}
