package mc.alk.tracker.executors;

import java.util.List;

import mc.alk.tracker.TrackerInterface;
import mc.alk.tracker.objects.Stat;
import mc.alk.tracker.objects.StatType;
import mc.alk.tracker.objects.VersusRecords;
import mc.alk.tracker.objects.VersusRecords.VersusRecord;
import mc.alk.tracker.objects.WLT;
import mc.alk.tracker.objects.WLTRecord;
import mc.alk.tracker.util.TimeUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alk.executors.CustomCommandExecutor;
import com.alk.util.Log;

public class TrackerExecutor extends CustomCommandExecutor {
	TrackerInterface ti;

	public TrackerExecutor(TrackerInterface ti) {
		super();
		this.ti =ti;
	}

	@MCCommand(cmds={"top"}, min=1,usage="top [x] [team size]")
	public boolean showTopXOther(CommandSender sender, Command cmd, String[] args){
		int x = 5;
		StatType st = null;
		if (args.length > 1){
			int xIndex = 1;
			st = StatType.fromName((String)args[1]);
			if (st != null){
				xIndex = 2;
			}
			if (args.length > xIndex)
				try {x = Integer.valueOf((String) args[xIndex]);}catch (Exception e){}
		}

		if (x<=0 || x > 100){
			return sendMessage(sender,"x must be between 1 and 100");}
		List<Stat> stats = st == null ? ti.getTopXRanking(x) : ti.getTopX(st, x);
		String stname = st == null ? "Ranking" : st.getName();
		int min = (int) Math.min(x, stats.size());
		if (min==0){
			return sendMessage(sender,ChatColor.RED+"there are no records in the &6" + ti.getInterfaceName() +"&c table");}
		sendMessage(sender,"&4=============== &6"+ti.getInterfaceName()+" "+stname+"&4===============");

		Stat stat;
		for (int i=0;i<min;i++){
			stat = stats.get(i);
			sendMessage(sender,"&6"+i+"&e: &c" + stat.getName()+"&6["+stat.getRanking()+"] &eWins(&6"+stat.getWins()+
					"&e),Losses(&8"+stat.getLosses()+"&e),Streak(&b"+stat.getStreak()+"&e) W/L(&c"+stat.getKDRatio()+"&e)");
		}

		return true;
	}

	@MCCommand(cmds={"versus","vs"}, inGame=true, min=2,usage="vs <player> [# records]")
	public boolean versus(CommandSender sender, String[] args){
		int x = 5;
		if (args.length > 1){
			try {x = Integer.valueOf(args[2]);}catch (Exception e){}
		}
		if (x<=0 || x > 100){
			return sendMessage(sender,"x must be between 1 and 100");}

		Player p = (Player) sender;
		Stat stat1 = findStat(p.getName());
		if (stat1 == null){
			return sendMessage(sender, "&4A record for player &6"+args[1] +"&4 couldn't be found");}
		Stat stat2 = findStat((String)args[1]);
		if (stat2 == null){
			return sendMessage(sender, "&4A record for player &6"+args[2] +"&4 couldn't be found");}

		ti.save(stat1,stat2);

		VersusRecord or = stat1.getRecordVersus(stat2);
		sendMessage(sender,"&4======================== &6"+ti.getInterfaceName()+" &4========================");
		sendMessage(sender,"&4================ &6"+stat1.getName()+" ("+stat1.getRanking()+")&e vs &6" +
				stat2.getName()+"("+stat2.getRanking()+") &4================");
		sendMessage(sender,"&eOverall Record (&2"+or.wins +" &e:&8 "+or.losses+" &e)");
		List<WLTRecord> records = ti.getVersusRecords(stat1.getName(), stat2.getName(),x);
		int min = Math.min(x, records.size());
		for (int i=0;i< min;i++){
			WLTRecord wlt = records.get(i);
			final String color = wlt.wlt == WLT.WIN ? "&2" : "&8";
			sendMessage(sender,color+wlt.wlt +"&e : &6" + TimeUtil.convertLongToDate(wlt.date));			
		}

		return true;
	}

	@MCCommand(cmds={"versus","vs"}, min=3,usage="vs <player> <player2> [# records] [time]")
	public boolean versusOther(CommandSender sender, String[] args){
		int x = 5;
		if (args.length > 1){
			try {x = Integer.valueOf(args[1]);}catch (Exception e){}}
		Long time = System.currentTimeMillis() - 1000*60*60;
		if (args.length > 2){
			try {time = Long.valueOf(args[2]);}catch (Exception e){}}
		if (x<=0 || x > 100){
			return sendMessage(sender,"x must be between 1 and 100");}

		Stat stat1 = findStat((String)args[1]);
		if (stat1 == null){
			return sendMessage(sender, "&4A record for player &6"+args[1] +"&4 couldn't be found");}
		Stat stat2 = findStat((String)args[2]);
		if (stat2 == null){
			return sendMessage(sender, "&4A record for player &6"+args[2] +"&4 couldn't be found");}

		ti.save(stat1,stat2);

		VersusRecord or = stat1.getRecordVersus(stat2);
		sendMessage(sender,"&4======================== &6"+ti.getInterfaceName()+" &4========================");
		sendMessage(sender,"&4================ &6"+stat1.getName()+" ("+stat1.getRanking()+")&e vs &6" +
				stat2.getName()+"("+stat2.getRanking()+") &4================");
		sendMessage(sender,"&eOverall Record (&2"+or.wins +" &e:&8 "+or.losses+" &e)");
		List<WLTRecord> records = ti.getVersusRecords(stat1.getName(), stat2.getName(),x);
		int min = Math.min(x, records.size());
		for (int i=0;i< min;i++){
			WLTRecord wlt = records.get(i);
			final String color = wlt.wlt == WLT.WIN ? "&2" : "&8";
			sendMessage(sender,color+wlt.wlt +"&e : &6" + TimeUtil.convertLongToDate(wlt.date));			
		}
		VersusRecords vr = stat1.getRecord();
		Log.debug(vr.getIndividualRecords()+"");
		
		records = ti.getWinsSince(stat1,time);
		for (WLTRecord win: records){
			sendMessage(sender, "&2" + win.getDate());
		}
		return true;
	}


	@MCCommand(cmds={"addKill"},op=true,min=3,usage="addkill <player1> <player2>: this is a debugging method")
	public boolean addKill(CommandSender sender, String p1, String p2){
		Stat stat = ti.loadPlayerRecord(p1);
		Stat stat2 = ti.loadPlayerRecord(p2);
		if (stat == null || stat2 == null){
			sender.sendMessage("Player not found");
			return true;}
		
		ti.addStatRecord(stat, stat2, WLT.WIN,true);
		try {
			VersusRecord or = stat.getRecordVersus(stat2);		
			sendMessage(sender, stat.getName()+ " versus " + stat2.getName()+" (&4"+or.wins +"&e:&8"+or.losses+"&e)");
		} catch(Exception e){
			
		}

		return true;
	}
	
	protected boolean addKill(CommandSender sender, Player p1, Player p2) {
		ti.addPlayerRecord(p1.getName(), p2.getName(), WLT.WIN);
		return sendMessage(sender, "Added kill " + p1.getDisplayName() + " wins over " + p2.getDisplayName());
	}

	protected String getFullStatMsg(Stat stat) {
		StringBuilder sb = new StringBuilder();
		sb.append("&5"+stat.getName() +"&6["+stat.getRanking()+"] &eWins(&6"+stat.getWins()+"&e),Losses(&8"+stat.getLosses()+
				"&e),Streak(&b"+stat.getStreak()+"&e),MaxStreak(&7"+stat.getMaxStreak()+"&e) W/L(&c"+stat.getKDRatio()+"&e)");
		return sb.toString();
	}

//	private String getStatMsg(Stat stat) {
//		StringBuilder sb = new StringBuilder();
//		sb.append("&e"+stat.getName() +"&6["+stat.getRanking()+"] &e(&4"+stat.getWins()+"&e:&8"+stat.getLosses()+"&e)");
//		return sb.toString();
//	}

	protected String getStatMsg(Stat stat1, Stat stat2) {
		StringBuilder sb = new StringBuilder();
		sb.append(getFullStatMsg(stat2));
		VersusRecord or = stat1.getRecordVersus(stat2);
		sb.append("record versus (&4"+or.wins +"&e:&8"+or.losses+"&e)");
		return sb.toString();
	}

	@MCCommand(inGame=true)
	public boolean showStatsSelf(Player p) {
		Stat stat = ti.loadRecord(p);
		String msg = getFullStatMsg(stat);
		return sendMessage(p, msg);
	}

	@MCCommand
	public boolean showStatsOther(CommandSender sender, OfflinePlayer p) {
		/// Try to find the stat from what they typed
		Stat stat = findStat(p.getName());
		if (stat == null){ /// Find a player matching that name, hopefully
			return sendMessage(sender, "&4A record for player &6"+p.getName() +"&4 couldn't be found");}
		String msg=null;
		Stat selfStat = ti.loadRecord(p);
		msg = getStatMsg(selfStat,stat);

		return sendMessage(sender, msg);
	}

	public Stat findStat(String name){
		Stat stat = ti.getRecord(name);
		if (stat == null){ /// Find a player matching that name, hopefully
			Player op = findOnlinePlayer(name);
			if (op == null){
				return null;}
			stat = ti.loadRecord(op);
		}
		return stat;
	}

	protected Player findOnlinePlayer(String name) {
		if (name == null)
			return null;
		Server server =Bukkit.getServer();
		Player lastPlayer = server.getPlayer(name);
		if (lastPlayer != null) 
			return lastPlayer;

		Player[] online = server.getOnlinePlayers();

		for (Player player : online) {
			String playerName = player.getName();
			if (playerName.equalsIgnoreCase(name)) {
				return player;
			} else if (playerName.toLowerCase().indexOf(name.toLowerCase()) != -1) {
				if (lastPlayer != null) {
					return null;}
				lastPlayer = player;
			}
		}

		return lastPlayer;
	}
}
