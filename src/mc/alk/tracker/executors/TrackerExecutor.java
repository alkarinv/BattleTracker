package mc.alk.tracker.executors;

import java.util.List;

import mc.alk.tracker.TrackerInterface;
import mc.alk.tracker.controllers.MessageController;
import mc.alk.tracker.objects.Stat;
import mc.alk.tracker.objects.VersusRecords.VersusRecord;
import mc.alk.tracker.objects.WLT;
import mc.alk.tracker.objects.WLTRecord;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alk.controllers.MC;
import com.alk.executors.CustomCommandExecutor;
import com.alk.executors.MCCommand;
import com.alk.util.TimeUtil;

public class TrackerExecutor extends CustomCommandExecutor {
	TrackerInterface ti;

	public TrackerExecutor(TrackerInterface ti) {
		super();
		this.ti =ti;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (args.length==0 || (args.length != 0 && !hasMethod(args[0].toLowerCase()))){
			/// Handle them typing a command like /pvp, or /pve.  show their own stats
			if (args.length==0){
				if (!(sender instanceof Player))
					return super.onCommand(sender, cmd, commandLabel, args);
				else 
					return showStatsSelf(sender,(sender instanceof Player) ? (Player) sender: null,args);
			} 
			else if (args[0].equalsIgnoreCase("stats")){

			}
			/// They are querying a player
			return showStats(sender,(sender instanceof Player) ? (Player) sender: null,args);
		}
		return super.onCommand(sender, cmd, commandLabel, args);
	}

	@MCCommand(cmds={"top"}, min=1,usage="top [x] [team size]")
	public boolean showTopXOther(CommandSender sender, Player p, Object[] args){
		int x = 5;
		if (args.length > 1){
			try {x = Integer.valueOf((String) args[1]);}catch (Exception e){}
		}
		if (x<=0 || x > 100){
			return sendMessage(sender,"x must be between 1 and 100");}
		List<Stat> stats = ti.getTopXElo(x);
		int min = (int) Math.min(x, stats.size());
		if (min==0){
			return sendMessage(sender,ChatColor.RED+"there are no records in the &6" + ti.getName() +"&c table");}
		sendMessage(sender,"&4======================== &6"+ti.getName()+" &4========================");
		Stat stat;
		for (int i=0;i<min;i++){
			stat = stats.get(i);
			sendMessage(sender,"&6"+i+"&e: &c" + stat.getName()+"&6["+stat.getElo()+"] &eWins(&6"+stat.getWins()+
					"&e),Losses(&8"+stat.getLosses()+"&e),Streak(&b"+stat.getStreak()+"&e) W/L(&c"+stat.getKDRatio()+"&e)");
		}
		return true;
	}

	@MCCommand(cmds={"versus","vs"}, min=3,usage="vs <player> <player2> [# records]")
	public boolean versusOther(CommandSender sender, Player p, Object[] args){
		int x = 5;
		if (args.length > 1){
			try {x = Integer.valueOf((String) args[1]);}catch (Exception e){}
		}
		if (x<=0 || x > 100){
			return sendMessage(sender,"x must be between 1 and 100");}

		Stat stat1 = findStat((String)args[1]);
		if (stat1 == null){
			return sendMessage(sender, "&4A record for player &6"+args[1] +"&4 couldn't be found");}
		Stat stat2 = findStat((String)args[2]);
		if (stat2 == null){
			return sendMessage(sender, "&4A record for player &6"+args[2] +"&4 couldn't be found");}

		ti.save(stat1,stat2);

		VersusRecord or = stat1.getRecordVersus(stat2.getKey());
		sendMessage(sender,"&4======================== &6"+ti.getName()+" &4========================");
		sendMessage(sender,"&4================ &6"+stat1.getName()+" ("+stat1.getElo()+")&e vs &6" +
				stat2.getName()+"("+stat2.getElo()+") &4================");
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

	@Override
	public void showHelp(CommandSender sender, Command command, String label) {
		MC.sendMessage(sender, "&2/pvp");
		MC.sendMessage(sender, "&2/pvp");
	}

	protected boolean addKill(CommandSender sender, Player p, Object[] args) {
		ti.addPlayerRecord((String) args[1], (String) args[2], WLT.WIN);
		return sendMessage(sender, "Added kill " + args[1] + " wins over " + args[2]);
	}

	protected String getFullStatMsg(Stat stat) {
		StringBuilder sb = new StringBuilder();
		sb.append("&5"+stat.getName() +"&6["+stat.getElo()+"] &eWins(&6"+stat.getWins()+"&e),Losses(&8"+stat.getLosses()+
				"&e),Streak(&b"+stat.getStreak()+"&e),MaxStreak(&7"+stat.getMaxStreak()+"&e) W/L(&c"+stat.getKDRatio()+"&e)");
		return sb.toString();
	}

	private String getStatMsg(Stat stat) {
		StringBuilder sb = new StringBuilder();
		sb.append("&e"+stat.getName() +"&6["+stat.getElo()+"] &e(&4"+stat.getWins()+"&e:&8"+stat.getLosses()+"&e)");
		return sb.toString();
	}

	private String getStatMsg(Stat stat1, Stat stat2) {
		StringBuilder sb = new StringBuilder();
		sb.append("&5"+stat2.getName() +"&6["+stat2.getElo()+"] &e(&4"+stat2.getWins()+"&e:&8"+stat2.getLosses()+"&e), ");
		VersusRecord or = stat1.getRecordVersus(stat2.getKey());
		sb.append("record against (&4"+or.wins +"&e:&8"+or.losses+"&e)");
		return sb.toString();
	}

	protected boolean showStatsSelf(CommandSender sender, Player p, String[] args) {
		Stat stat = ti.loadRecord(p);
		String msg = getFullStatMsg(stat);
		return sendMessage(sender, msg);
	}

	protected boolean showStats(CommandSender sender, Player p, String[] args) {
		/// Try to find the stat from what they typed
		Stat stat = findStat(args[0]);
		if (stat == null){ /// Find a player matching that name, hopefully
			return sendMessage(sender, "&4A record for player &6"+args[0] +"&4 couldn't be found");}
		String msg=null;
		if (p != null){
			Stat selfStat = ti.loadRecord(p);
			msg = getStatMsg(selfStat,stat);
		} else {
			msg = getStatMsg(stat);	
		}

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

	protected boolean sendMessage(CommandSender sender, String string) {
		sender.sendMessage(MessageController.colorChat(string));
		return true;
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
