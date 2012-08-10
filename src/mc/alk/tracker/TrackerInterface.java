package mc.alk.tracker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mc.alk.tracker.controllers.ConfigController;
import mc.alk.tracker.controllers.EloCalculator;
import mc.alk.tracker.controllers.TrackerController;
import mc.alk.tracker.objects.PlayerStat;
import mc.alk.tracker.objects.Stat;
import mc.alk.tracker.objects.TeamStat;
import mc.alk.tracker.objects.WLT;
import mc.alk.tracker.objects.WLTRecord;
import mc.alk.tracker.serializers.SQLInstance;
import mc.alk.tracker.test.Cache;
import mc.alk.tracker.test.Cache.CacheSerializer;

import org.apache.commons.lang.mutable.MutableBoolean;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;


public class TrackerInterface implements CacheSerializer<String,Stat>{
	Cache<String, Stat> cache = new Cache<String, Stat>(this);
	boolean trackIndividual = false;
	EloCalculator ec = new EloCalculator();
	SQLInstance sql = null;
	final String tableName;

	public static class DBConnectionException extends Exception{
		private static final long serialVersionUID = 1L;
		public DBConnectionException(String e){super(e);}
	}
	public String toString(){
		StringBuilder sb = new StringBuilder("[TI=");
		sb.append( sql != null ? sql.getTable(): "null" );
		sb.append("]");
		return sb.toString();
	}
	public TrackerInterface(String tableName) throws DBConnectionException{
		sql = new SQLInstance();
		sql.setTable(tableName);
		this.tableName = tableName;
		sql.setDB(ConfigController.getString("SQLOptions.db"));
		sql.setUsername(ConfigController.getString("SQLOptions.username"));
		sql.setPassword(ConfigController.getString("SQLOptions.password"));
		ec.setEloDefault((float) ConfigController.getDouble("elo.default",1250));
		ec.setEloSpread((float) ConfigController.getDouble("elo.spread",400));
		cache.setSaveEvery(Defaults.SAVE_EVERY_X_SECONDS *1000);
		try{
			sql.init();
		} catch (Exception e){
			throw new DBConnectionException("Couldnt start the sql connection");
		}
	}

	public TrackerInterface(String db, String table, String user, String password) {
		sql = new SQLInstance();
		this.tableName = table;
		sql.setDB(db);
		sql.setTable(table);
		sql.setUsername(user);
		sql.setPassword(password);
		sql.init();
		ec.setEloDefault((float) 1250);
		ec.setEloSpread((float) 400);
		cache.setSaveEvery(Defaults.SAVE_EVERY_X_SECONDS *1000);
	}

	public Stat load(String id, MutableBoolean dirty, Object... varArgs) {
		Stat stat = sql.getRecord(id);
		if (Cache.DEBUG) System.out.println(" sql returning " + stat);
		if (stat != null){
			stat.setCache(cache);
			dirty.setValue(false);
			stat.setParent(this);
		} else if (varArgs.length != 0){
			dirty.setValue(true);
			stat = (Stat) varArgs[0];
			if (Cache.DEBUG) System.out.println(" returning premade " + stat);
			stat.setCache(cache);
			stat.setParent(this);
			stat.setElo(ec.getEloDefault());
		}
		return stat;
	}

	public void save(List<Stat> stats) {
		sql.saveAll((Stat[]) stats.toArray(new Stat[stats.size()]));
	}
	public void save(Stat... stats) {
		sql.saveAll(stats);
	}

	private void addStatRecord(Stat team1, Stat team2, WLT wlt,boolean saveIndividualRecord){
		Stat ts1 = cache.get(team1.getStrID(), team1);
		Stat ts2 = cache.get(team2.getStrID(), team2);
		if (ts1 == null){
			ts1 = team1;
			ts1.setCache(cache);
			ts1.setParent(this);
			ts1.setElo(ec.getEloDefault());
			cache.put(team1);
		}
		if (ts2 == null){
			ts2 = team2;
			ts2.setCache(cache);
			ts2.setParent(this);
			ts2.setElo(ec.getEloDefault());
			cache.put(team2);
		}
//		System.out.println(" ts1 = " + ts1 +"    " + ts2);

		ts1.setSaveIndividual(saveIndividualRecord);
		ts2.setSaveIndividual(saveIndividualRecord);
		switch(wlt){
		case WIN: 
			ts1.win(ts2); ts2.loss(ts1);
			ec.changeElo(ts1,ts2,1.0f);
			break;
		case LOSS:
			ts1.loss(ts2); ts2.win(ts1);
			ec.changeElo(ts1,ts2,0.0f);
			break;
		case TIE: 
			ts1.tie(ts2); ts2.tie(ts1);
			ec.changeElo(ts1,ts2,0.5f);
			break;
		}
	}

	public void addPlayerRecord(String p1, String p2, WLT wlt) {
		Stat ts1 = new PlayerStat(p1);
		Stat ts2 = new PlayerStat(p2);
		addStatRecord(ts1, ts2, wlt, true);
	}

	public void addPlayerRecord(String p1, String p2, WLT wlt, boolean saveIndividualRecord) {
		Stat ts1 = new PlayerStat(p1);
		Stat ts2 = new PlayerStat(p2);
		addStatRecord(ts1, ts2,wlt,saveIndividualRecord);
	}

	public void addPlayerRecord(OfflinePlayer p1, OfflinePlayer p2, WLT wlt) {
		addPlayerRecord(p1.getName(), p2.getName(),wlt);
	}

	public void addTeamRecord(String t1, String t2, WLT wlt) {
		TeamStat ts1 = new TeamStat(t1,false);
		TeamStat ts2 = new TeamStat(t2,false);
		addStatRecord(ts1, ts2,wlt,true);
	}

	public void addTeamRecord(Set<String> team1, Set<String> team2, WLT wlt) {
		TeamStat ts1 = new TeamStat(team1);
		TeamStat ts2 = new TeamStat(team2);
		addStatRecord(ts1, ts2,wlt,true);
	}

	public void addTeamRecord(Collection<Player> team1, Collection<Player> team2, WLT wlt) {
		HashSet<String> names = new HashSet<String>();
		for (OfflinePlayer p : team1){
			names.add(p.getName());}
		TeamStat ts1 = new TeamStat(names);
		names = new HashSet<String>();
		for (OfflinePlayer p : team2){
			names.add(p.getName());}
		TeamStat ts2 = new TeamStat(names);
		addStatRecord(ts1, ts2,wlt,true);
	}

	public TeamStat getTeamRecord(String teamName) {
		TeamStat ts = new TeamStat(teamName, false);
		return (TeamStat) cache.get(ts.getKey());
	}

	public TeamStat getTeamRecord(Set<String> players) {
		TeamStat ts = new TeamStat(players);
		return (TeamStat) cache.get(ts.getKey());
	}

	public PlayerStat getPlayerRecord(String player) {
		return (PlayerStat) cache.get(player);
	}
	public PlayerStat getPlayerRecord(Player player) {
		return (PlayerStat) cache.get(player.getName());
	}

	public void stopTracking(String player) {TrackerController.stopTracking(player);}
	public void resumeTracking(String player) {TrackerController.resumeTracking(player);}
	public void stopMessages(String player) {TrackerController.stopAnnouncing(player);}
	public void resumeMessages(String player) {TrackerController.resumeAnnouncing(player);}

	public void stopTracking(OfflinePlayer player) {TrackerController.stopTracking(player.getName());}
	public void resumeTracking(OfflinePlayer player) {TrackerController.resumeTracking(player.getName());}
	public void stopMessages(OfflinePlayer player) {TrackerController.stopAnnouncing(player.getName());}
	public void resumeMessages(OfflinePlayer player) {TrackerController.resumeAnnouncing(player.getName());}

	public void resumeMessages(Collection<Player> players) {TrackerController.resumeAnnouncing(players);}
	public void resumeTracking(Collection<Player> players) {TrackerController.resumeTracking(players);}
	public void stopMessages(Collection<Player> players) {TrackerController.stopAnnouncing(players);}
	public void stopTracking(Collection<Player> players) {TrackerController.stopTracking(players);}

	public void addRecordGroup(Collection<Player> team1, Collection<Collection<Player>> teams, WLT wlt) {
		TeamStat ts = new TeamStat(toStringCollection(team1));
		Stat ts1 = cache.get(ts,ts);
		ts1.incWins();
		Collection<Stat> lstats = new ArrayList<Stat>();
		//		Set<Player> players = new HashSet<Player>();
		for (Collection<Player> t : teams){
			TeamStat loser = new TeamStat(toStringCollection(t));
			Stat lstat = cache.get(loser,loser);
			lstat.incLosses();
			lstats.add(lstat);
		}
		//		TeamStat ts3 = stats.loadTeam(players);
		//		win(wlt, ts1, teamstats,ts3);
		ec.changeElo(ts1, lstats, 1.0f);
	}

	private Set<String> toStringCollection(Collection<Player> players) {
		Set<String> col = new HashSet<String>();
		for (Player p: players){
			col.add(p.getName());
		}
		return col;
	}
	public Stat getRecord(String player) {
		return cache.get(player);
	}

	public Stat getRecord(OfflinePlayer player) {
		return cache.get(player.getName());
	}
	public Stat loadRecord(OfflinePlayer op) {
		PlayerStat stat = new PlayerStat(op);
		return loadStat(stat);
	}
	public Stat loadPlayerRecord(String name) {
		PlayerStat stat = new PlayerStat(name);
		return loadStat(stat);
	}
	public Stat loadRecord(Set<Player> players) {
		HashSet<String> names = new HashSet<String>();
		for (OfflinePlayer p : players){
			names.add(p.getName());}
		Stat stat = new TeamStat(names);
		return loadStat(stat);
	}
	private Stat loadStat(Stat stat){
		Stat s = cache.get(stat, stat);
		if (s==null){
			cache.put(stat);
			return stat;
		}
		return s;
	}

	public Stat getRecord(Collection<Player> players) {
		HashSet<String> names = new HashSet<String>();
		for (OfflinePlayer p : players){
			names.add(p.getName());}

		return cache.get(new TeamStat(names));
	}

	public void saveAll() {cache.save();}

	public List<Stat> getTopXElo(int x) {
		return getTopXElo(x,1);
	}
	public List<Stat> getTopXLosses(int x) {
		return getTopXLosses(x,1);
	}
	public List<Stat> getTopXWins(int x) {
		return getTopXWins(x,1);
	}
	public List<Stat> getTopXKDRatio(int x) {
		return getTopXKDRatio(x,1);
	}

	public List<Stat> getTopXElo(int x, int teamcount) {
		cache.save();
		return sql.getTopXElo(x,teamcount);
	}
	public List<Stat> getTopXLosses(int x, int teamcount) {
		cache.save();
		return sql.getTopXLosses(x,teamcount);
	}
	public List<Stat> getTopXWins(int x, int teamcount) {
		cache.save();
		return sql.getTopXWins(x,teamcount);
	}
	public List<Stat> getTopXKDRatio(int x, int teamcount) {
		cache.save();
		return sql.getTopXRatio(x,teamcount);
	}

	public void resetStats() {
		//		if (dbname == null || dbname.isEmpty())
		//			return;
		//		sql.reset();
		//		startConnection(dbname);
	}

	public void flush() {
		cache.flush();
	}

	public void onlyTrackOverallStats(boolean b) {
		trackIndividual = !b;
	}

	public EloCalculator getEC() {
		return ec;
	}

	public SQLInstance getSQL() {
		return sql;
	}
	public boolean setElo(Player player, int elo) {
		Stat stat = cache.get(new PlayerStat(player));
		if (stat == null)
			return false;
		stat.setElo(elo);
		return true;
	}
	
	public String getName() {
		return tableName;
	}
	
	public List<WLTRecord> getVersusRecords(String name, String name2) {
		return getVersusRecords(name,name2,10);
	}

	public List<WLTRecord> getVersusRecords(String name, String name2, int x) {
		cache.save();
		return sql.getVersusRecords(name,name2,x);
	}

}
