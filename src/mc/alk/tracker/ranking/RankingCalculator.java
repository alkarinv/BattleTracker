package mc.alk.tracker.ranking;

import java.util.Collection;

import mc.alk.tracker.objects.Stat;

public interface RankingCalculator {
	
	/**
	 * What is this ranking called
	 * @return name of ranking (like elo)
	 */
	public String getName();
	/**
	 * Set the default ranking
	 * @param initialRanking
	 */
	public void setDefaultRanking(float initialRanking);
	/**
	 * Get the default ranking
	 * @return
	 */
	public float getDefaultRanking();
	
	/**
	 * Change the rankings of stat1 and stat2
	 * @param stat1
	 * @param stat2
	 * @param tie
	 */
	public void changeRankings(Stat stat1, Stat stat2, boolean tie);
	
	/**
	 * Change the rankings of a group of stats
	 * @param stat1
	 * @param stats
	 * @param tie
	 */
	public void changeRankings(Stat stat1, Collection<Stat> stats, boolean tie);
}
