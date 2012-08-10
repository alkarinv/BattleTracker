package mc.alk.tracker.executors;

import mc.alk.tracker.TrackerInterface;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alk.executors.MCCommand;

public class PVPExecutor extends TrackerExecutor {
	
	public PVPExecutor(TrackerInterface ti) {
		super(ti);
	}

	@MCCommand(cmds={"addKill"},op=true,min=3,usage="/pvp addkill <player1> <player2>: this is a debugging method")
	public boolean addKill(CommandSender sender, Player p, Object[] args){
		return super.addKill(sender,p,args);
	}

	
}
