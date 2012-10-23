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
}
