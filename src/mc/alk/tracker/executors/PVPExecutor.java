package mc.alk.tracker.executors;

import mc.alk.tracker.TrackerInterface;
import mc.alk.tracker.objects.Stat;
import mc.alk.tracker.objects.WLT;
import mc.alk.tracker.objects.VersusRecords.VersusRecord;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.alk.executors.MCCommand;

public class PVPExecutor extends TrackerExecutor {
	
	public PVPExecutor(TrackerInterface ti) {
		super(ti);
	}

	@MCCommand(cmds={"addKill"},op=true,min=3,usage="addkill <player1> <player2>: this is a debugging method")
	public boolean addKill(CommandSender sender, Command cmd, String commandLabel, Object[] args){
		Stat stat = ti.getRecord((String)args[1]);
		Stat stat2 = ti.getRecord((String)args[2]);
		if (stat == null || stat2 == null){
			sender.sendMessage("Player not found");
			return true;}
		
		ti.addPlayerRecord((String)args[1], (String)args[2], WLT.WIN);
		VersusRecord or = stat.getRecordVersus(stat2.getKey());		
		sendMessage(sender, stat.getName()+ " versus " + stat2.getName()+" (&4"+or.wins +"&e:&8"+or.losses+"&e)");
		return true;
	}

	
}
