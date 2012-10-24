package mc.alk.tracker.executors;

import mc.alk.tracker.Defaults;

import org.bukkit.command.CommandSender;

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

}
