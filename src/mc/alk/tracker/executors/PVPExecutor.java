package mc.alk.tracker.executors;

import mc.alk.tracker.TrackerInterface;
import mc.alk.tracker.objects.Stat;
import mc.alk.tracker.objects.VersusRecords.VersusRecord;
import mc.alk.tracker.objects.WLT;

import org.bukkit.command.CommandSender;

public class PVPExecutor extends TrackerExecutor {
	
	public PVPExecutor(TrackerInterface ti) {
		super(ti);
	}

	@MCCommand(cmds={"addKill"},op=true,min=3,usage="addkill <player1> <player2>: this is a debugging method")
	public boolean addKill(CommandSender sender, String p1, String p2){
		Stat stat = ti.loadPlayerRecord(p1);
		Stat stat2 = ti.loadPlayerRecord(p2);
		if (stat == null || stat2 == null){
			sender.sendMessage("Player not found");
			return true;}
		
		ti.addPlayerRecord(p1, p2, WLT.WIN);
		try {
			VersusRecord or = stat.getRecordVersus(stat2);		
			sendMessage(sender, stat.getName()+ " versus " + stat2.getName()+" (&4"+or.wins +"&e:&8"+or.losses+"&e)");
		} catch(Exception e){
			
		}

		return true;
	}

	
}
