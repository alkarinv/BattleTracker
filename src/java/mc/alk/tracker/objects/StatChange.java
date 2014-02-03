package mc.alk.tracker.objects;

public class StatChange {
	final Stat team1;
	final Stat team2;
	final WLT wlt;
	final boolean changeWinLossRecords;

	public StatChange(final Stat team1, final Stat team2,
			final WLT wlt, final boolean changeWinLossRecords){
		this.team1 = team1;
		this.team2 = team2;
		this.wlt = wlt;
		this.changeWinLossRecords = changeWinLossRecords;
	}

	public Stat getTeam1() {
		return team1;
	}
	public Stat getTeam2() {
		return team2;
	}
	public WLT getWlt() {
		return wlt;
	}
	public boolean isChangeWinLossRecords() {
		return changeWinLossRecords;
	}

}
