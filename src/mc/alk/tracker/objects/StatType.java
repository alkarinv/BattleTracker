package mc.alk.tracker.objects;


public enum StatType {
	WINS("Wins"),KILLS("Kills"), LOSSES("Losses"),DEATHS("Deaths"),TIES("Ties"),
	STREAK("Streak"),MAXSTREAK("MaxStreak"), RANKING("Ranking"),MAXRANKING("MaxRanking"),
	KDRATIO("K/D Ratio"),WLRATIO("W/L Ratio");

	final String name; 
	StatType(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
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

		return gt;
	}
}
