package mc.alk.tracker.controllers;

import java.util.Collection;

import mc.alk.tracker.objects.Stat;

public class EloCalculator {
	public static final float MIN_ELO = 100;
	public static final float DEFAULT_ELO = 1250;

	float defaultElo, spread;
	private float eloChange(Stat p1, Stat p2, float result){
		final float k = getK(p1.getElo());
		final float Di = p2.getElo() - p1.getElo();

		final float expected = (float) ( 1.0f / (1 + Math.pow(10, Di/spread)));

		float eloChange = k * (result-expected);
		return eloChange;
	}

	public void changeElo(Stat p1, Stat p2, float result){
		final float eloChange = eloChange(p1,p2,result);
		final float p1elo = p1.getElo()+eloChange;
		final float p2elo = p2.getElo()-eloChange;
		p1.setElo(p1elo > MIN_ELO? p1elo : MIN_ELO);
		p2.setElo(p2elo > MIN_ELO? p2elo : MIN_ELO);
		//				System.out.println(p1.getElo() + " : " + p2.getElo());
	}

	public void changeElo(Stat ts1, Collection<Stat> teamstats, float result) {
		for (Stat ts : teamstats){
			final double eloChange = eloChange(ts1,ts,result) / teamstats.size();
			ts1.setElo((int) (ts1.getElo() + eloChange));
			ts.setElo((int) (ts.getElo() - eloChange));	
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

	public void setEloDefault(float elo) {
		defaultElo = elo;
	}

	public void setEloSpread(float spread) {
		this.spread = spread;
	}

	public float getEloDefault() {
		return defaultElo;
	}
}
