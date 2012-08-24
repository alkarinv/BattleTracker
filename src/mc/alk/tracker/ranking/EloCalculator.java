package mc.alk.tracker.ranking;

import java.util.Collection;

import mc.alk.tracker.objects.Stat;

public class EloCalculator implements RankingCalculator {
	public static final float MIN_ELO = 100;
	public static final float DEFAULT_ELO = 1250;

	float defaultElo, spread;
	
	public String getName(){
		return "Elo";
	}
	private float eloChange(Stat p1, Stat p2, float result){
		final float k = getK(p1.getRanking());
		final float Di = p2.getRanking() - p1.getRanking();

		final float expected = (float) ( 1.0f / (1 + Math.pow(10, Di/spread)));

		float eloChange = k * (result-expected);
		return eloChange;
	}

	public void changeRankings(Stat p1, Stat p2, boolean tie){
		float result = tie ? 0.5f : 1.0f;
		final float eloChange = eloChange(p1,p2,result);
		final float p1elo = p1.getRanking()+eloChange;
		final float p2elo = p2.getRanking()-eloChange;
		p1.setRanking(p1elo > MIN_ELO? p1elo : MIN_ELO);
		p2.setRanking(p2elo > MIN_ELO? p2elo : MIN_ELO);
		//				System.out.println(p1.getElo() + " : " + p2.getElo());
	}

	public void changeRankings(Stat ts1, Collection<Stat> teamstats, boolean tie) {
		float result = tie ? 0.5f : 1.0f;
		for (Stat ts : teamstats){
			final double eloChange = eloChange(ts1,ts,result) / teamstats.size();
			ts1.setRanking((int) (ts1.getRanking() + eloChange));
			ts.setRanking((int) (ts.getRanking() - eloChange));	
		}
	}


	protected int getK(float elo) {
		if (elo < 1600){
			return 50;
		} else if (elo < 1800){
			return 35;
		} else if (elo < 2000){
			return 20;
		} else if (elo < 2500){
			return 10;
		}
		return 6;
	}

	public void setEloSpread(float spread) {
		this.spread = spread;
	}

	public void setDefaultRanking(float elo) {
		defaultElo = elo;
	}

	public float getDefaultRanking() {
		return defaultElo;
	}
}
