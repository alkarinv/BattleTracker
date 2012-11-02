package mc.alk.tracker.objects;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mc.alk.tracker.Defaults;
import mc.alk.tracker.controllers.TrackerImpl;
import mc.alk.tracker.events.MaxRatingChangeEvent;
import mc.alk.tracker.objects.VersusRecords.VersusRecord;
import mc.alk.tracker.ranking.EloCalculator;
import mc.alk.tracker.util.Cache.CacheObject;
import mc.alk.tracker.util.Util;

import org.bukkit.entity.Player;

public abstract class Stat extends CacheObject<String,Stat>{
	protected String strid = null;
	protected String name;
	protected float ranking = EloCalculator.DEFAULT_ELO;
	protected float maxRanking = ranking;
	protected int wins = 0, losses= 0, ties = 0;
	protected int streak = 0, maxStreak =0;
	protected int count = 1; /// How many members are in the team
	List<String> members ;

	VersusRecords vRecord = null;
	private TrackerImpl parent;

	@Override
	public String getKey() {
		if (strid.length() > 32 )
			Util.printStackTrace();

		return getStrID();
	}

	public List<String> getMembers() {
		return members;
	}

	public String getStrID(){return strid;}
	public void setName(String name) {
		this.name = name; setDirty();
		if (name != null && name.length() > 32 )
			Util.printStackTrace();

	}

	public String getName(){return name;}

	public void setWins(int wins) {this.wins = wins;setDirty();}
	public int getWins() {return wins;}
	public void setStreak(int i){streak = i;setDirty();}
	public int getStreak() { return streak;}
	public void setLosses(int i){losses = i;setDirty();}
	public int getLosses() {return losses;}
	public void setTies(int i){ties = i;setDirty();}
	public int getTies() {return ties;}
	public int getCount() { return count;}
	public void setCount(int i){count = i;setDirty();}
	public float getKDRatio() { return ((float) wins) / losses;}
	public void incLosses() {
		streak = 0;
		losses++;
		setDirty();
	}
	public void incTies(){
		streak = 0;
		ties++;
		setDirty();
	}
	public void incWins() {
		wins++;
		incStreak();
		setDirty();
	}
	public void incStreak() {
		streak++;
		if (streak > maxStreak)
			maxStreak = streak;
		setDirty();
	}
	public void endStreak() {streak=0;setDirty();}
	public int getMaxStreak() {return maxStreak;}
	public void setMaxStreak(int maxStreak) {this.maxStreak = maxStreak;setDirty();}
	public void setMaxRanking(int maxRanking) {this.maxRanking = maxRanking;setDirty();}

	public int getRanking() {return (int) ranking;}
	public int getMaxRanking() {return (int) maxRanking;}

	public void setRanking(float ranking){
		this.ranking = ranking;
		if (this.ranking > maxRanking){
			int threshold =  ( ((int)maxRanking) /100) *100 + 100;
			double oldRating = maxRanking;
			maxRanking = this.ranking;
			if (maxRanking < threshold && this.ranking >= threshold){
				maxRanking = this.ranking;
				new MaxRatingChangeEvent(this,oldRating).callSyncEvent();
			}
		}
		setDirty();
	}

	@Override
	public boolean equals( Object obj ) {
		if(this == obj) return true;
		if((obj == null) || (obj.getClass() != this.getClass())) return false;
		TeamStat test = (TeamStat)obj;
		return this.compareTo(test) == 0;
	}

	/**
	 * Teams are ordered list of strings
	 */
	public int compareTo(TeamStat o) {
		return this.strid.compareTo(o.strid);
	}

	public VersusRecords getRecord(){
		if (vRecord == null)
			vRecord = new VersusRecords(getKey(),parent.getSQL()) ;
		return vRecord;
	}

	public void win(Stat ts) {
		if (Defaults.DEBUG_ADD_RECORDS) System.out.println("BT Debug: win: tsID="+ts.getStrID() +
				"  parent=" + parent +"  " + (parent !=null? parent.getSQL() : "null") + " key=" + getKey());
		wins++;
		streak++;
		if (streak > maxStreak){
			maxStreak=streak;}
		getRecord().addWin(ts.getStrID());
		setDirty();
	}

	public void loss(Stat ts) {
		losses++;
		streak =0;
		getRecord().addLoss(ts.getStrID());
		setDirty();
	}

	public void tie(Stat ts) {
		ties++;
		getRecord().addTie(ts.getStrID());
		setDirty();
	}

	public static String getKey(Player players){
		return players.getName();
	}
	public static String getKey(String player){
		return player;
	}

	protected static String getKey(List<String> playernames){
		Collections.sort(playernames);
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String s : playernames){
			if (!first) sb.append(",");
			sb.append(s);
			first = false;
		}
		if (sb.length() > 32){
			return sb.toString().hashCode() + "";
		}

		return sb.toString();
	}

	public VersusRecord getRecordVersus(Stat stat) {
		/// We cant get a record if we have no way of loading
		if (vRecord == null){
			vRecord = getRecord();}
		return vRecord.getRecordVersus(stat.getStrID());
	}

	protected void createName(){
		if (name != null && !name.isEmpty()) /// We have a specified name, dont use the naive append all players together
			return;
		//		System.out.println("name="+name);
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String n : members){
			if (!first) sb.append(",");
			sb.append(n);
			first = false;
		}
		name = sb.toString();
		//		System.out.println("afterwards name="+name);
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("[Team=" + getName() + " ["+getRanking()+":"+getKDRatio()+"](" + getWins() + ":" + getLosses() + ":" + getStreak() +") id="+strid +
				",count="+count+",p.size="+ (members==null?"null" : members.size()) );
		if (vRecord != null){
			sb.append("  [Kills]= ");
			HashMap<String,List<WLTRecord>> records = vRecord.getIndividualRecords();
			if (records != null){
				for (String tk : records.keySet()){
					sb.append(tk +":" + vRecord.getIndividualRecords().get(tk) +" ," );}
			}
		}
		sb.append("]");
		return sb.toString();
	}

	public VersusRecords getRecordSet() {
		return vRecord;
	}

	public void setParent(TrackerImpl parent) {
		this.parent = parent;
	}

	public void setSaveIndividual(boolean saveIndividualRecord) {
		if (vRecord != null)
			vRecord.setSaveIndividual(saveIndividualRecord);
	}

}
