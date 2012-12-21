package mc.alk.tracker.executors;

import mc.alk.tracker.Defaults;
import mc.alk.tracker.Tracker;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.alk.executors.CustomCommandExecutor;

public class BattleTrackerExecutor extends CustomCommandExecutor {

	public BattleTrackerExecutor() {
		super();
	}

	@MCCommand( cmds = {"enableDebugging"}, op=true, usage="enableDebugging <code section> <true | false>")
	public void enableDebugging(CommandSender sender, String section, Boolean on){
		if (section.equalsIgnoreCase("records")){
			Defaults.DEBUG_ADD_RECORDS = on;
		} else {
			sendMessage(sender, "&cDebugging couldnt find code section &6"+ section);
			return;
		}
		sendMessage(sender, "&a[BattleTracker]&2 debugging for &6" + section +"&2 now &6" + on);
	}

	@MCCommand( cmds = {"set"}, perm="tracker.admin", usage="set <pvp | pve> <section> <true | false>")
	public void pvpToggle(CommandSender sender, String pvp, String section, Boolean on){
		boolean ispvp = pvp.equalsIgnoreCase("pvp");
		String type = ispvp ? "PvP" : "PvE";
		if (section.equalsIgnoreCase("msg") || section.equalsIgnoreCase("message")){
			if (ispvp){
				Defaults.DISABLE_PVP_MESSAGES = on;
			} else {
				Defaults.DISABLE_PVE_MESSAGES = on;
			}
			sendMessage(sender, "&a[BattleTracker]&2 "+type+" messages now &6" + on);
		} else {
			sendMessage(sender, "&cDebugging couldnt find section &6"+ section);
			sendMessage(sender, "&cValid sections: &6msg");
			return;
		}
	}

	@MCCommand(cmds={"spawn"},op=true, inGame=true,usage="addkill <player1> <player2>: this is a debugging method")
	public boolean spawn(Player sender, Integer n){
		World w = Bukkit.getWorld("world");
		for (int i=0;i<n;i++){
			w.spawnEntity(sender.getLocation(), EntityType.ZOMBIE);
		}
		return true;
	}

	@MCCommand(cmds={"reload"},perm="tracker.admin")
	public boolean reload(CommandSender sender){
		Tracker.getSelf().loadConfigs();

		return sendMessage(sender, "&2Configs reloaded for BattleTracker");
	}

}
