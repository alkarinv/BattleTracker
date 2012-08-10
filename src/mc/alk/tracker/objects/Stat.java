package mc.alk.tracker.objects;

import java.util.Collections;
import java.util.List;

import mc.alk.tracker.TrackerInterface;
import mc.alk.tracker.controllers.EloCalculator;
import mc.alk.tracker.objects.VersusRecords.VersusRecord;
import mc.alk.tracker.test.Cache.CacheObject;

import org.bukkit.entity.Player;

public abstract class Stat extends CacheObject<String,Stat>{
	protected String strid = null;
	protected String name;
	protected float elo = EloCalculator.DEFAULT_ELO;
	protected int wins = 0, losses= 0, ties = 0;
	protected int streak = 0, maxStreak =0;
	protected int count = 1; /// How many members are in the team
	List<String> p ;
	
	VersusRecords vRecord = null;
	private TrackerInterface parent;

	@Override
	public String getKey() {
		return getStrID();
	}

	public List<String> getMembers() {
		return p;
	}

	public String getStrID(){return strid;}
	public void setName(String name) {this.name = name; setDirty();}

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

	public int getElo() {return (int) elo;}

	public void setElo(float elo){this.elo = elo;setDirty();}
	
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

	protected VersusRecords getRecord(){
		if (vRecord == null)
			vRecord = new VersusRecords(getKey(),parent.getSQL()) ;
		return vRecord;
	}

	public void win(Stat ts) {
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

	public void setID(String id) {
		this.strid= id;
	}

	public VersusRecord getRecordVersus(String id) {
		/// We cant get a record if we have no way of loading
		if (vRecord == null){
			vRecord = getRecord();}
		return vRecord.getRecordVersus(id);
	}

	protected void createName(){
		if (name != null && !name.isEmpty()) /// We have a specified name, dont use the naive append all players together
			return;
		//		System.out.println("name="+name);
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String n : p){
			if (!first) sb.append(",");
			sb.append(n);
			first = false;
		}
		name = sb.toString();
		//		System.out.println("afterwards name="+name);
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("[Team=" + getName() + " ["+getElo()+":"+getKDRatio()+"](" + getWins() + ":" + getLosses() + ":" + getStreak() +") id="+strid +
				",count="+count+",p.size="+ (p==null?"null" : p.size()) );
		if (vRecord != null){
			sb.append("  [Kills]= ");
			for (String tk : vRecord.getIndividualRecords().keySet()){
				sb.append(tk +":" + vRecord.getIndividualRecords().get(tk) +" ," );
			}
		}
		sb.append("]");
		return sb.toString();
	}

	public VersusRecords getRecordSet() {
		return vRecord;
	}

	public void setParent(TrackerInterface parent) {
		this.parent = parent;
	}

	public void setSaveIndividual(boolean saveIndividualRecord) {
		if (vRecord != null)
			vRecord.setSaveIndividual(saveIndividualRecord);
	}

}
