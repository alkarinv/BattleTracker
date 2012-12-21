package mc.alk.tracker;

import java.util.HashMap;

import mc.alk.tracker.controllers.ConfigController;
import mc.alk.tracker.controllers.MessageController;
import mc.alk.tracker.controllers.TrackerController;
import mc.alk.tracker.controllers.TrackerImpl;
import mc.alk.tracker.controllers.TrackerImpl.DBConnectionException;
import mc.alk.tracker.executors.BattleTrackerExecutor;
import mc.alk.tracker.executors.TrackerExecutor;
import mc.alk.tracker.listeners.BTEntityListener;
import mc.alk.tracker.listeners.BTPluginListener;
import mc.alk.tracker.ranking.RankingCalculator;

import com.alk.battleCore.MCPlugin;
import com.alk.battleCore.Version;

public class Tracker extends MCPlugin{
	static Tracker plugin;
	TrackerController sc;
	static HashMap<String, TrackerInterface> interfaces = new HashMap<String,TrackerInterface>();

	@Override
	public void onEnable() {
		super.onEnable();
		plugin = this;
		createPluginFolder();
		loadConfigs();
		getServer().getPluginManager().registerEvents(new BTEntityListener(), this);
		getServer().getPluginManager().registerEvents(new BTPluginListener(), this);

		getCommand("battleTracker").setExecutor(new BattleTrackerExecutor());
		getCommand("pvp").setExecutor(new TrackerExecutor(getInterface(Defaults.PVP_INTERFACE)));
		getCommand("pve").setExecutor(new TrackerExecutor(getInterface(Defaults.PVE_INTERFACE)));

		BTPluginListener.loadPlugins();
	}

	@Override
	public void onDisable(){
		saveStats();
	}

	public void loadConfigs(){
		ConfigController.setConfig(load("/default_files/config.yml",getDataFolder().getPath() +"/config.yml"));
		MessageController.setConfig(load("/default_files/messages.yml",getDataFolder().getPath() +"/messages.yml"));
	}

	public static Tracker getSelf() {
		return plugin;
	}

	private void saveStats() {
		synchronized(interfaces){
			for (TrackerInterface ti: interfaces.values()){
				ti.flush();
			}
		}
	}

	public static TrackerInterface getInterface(String interfaceName){
		return getInterface(interfaceName,null);
	}

	public static TrackerInterface getInterface(String interfaceName, RankingCalculator rankingCalculator){
		try {
			String iname = interfaceName.toLowerCase();
			if (!interfaces.containsKey(iname)){
				if (rankingCalculator == null)
					interfaces.put(iname, new TrackerImpl(interfaceName));
				else
					interfaces.put(iname, new TrackerImpl(interfaceName,rankingCalculator));
			}
			return interfaces.get(iname);
		} catch (DBConnectionException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Version getVersionObject(){
		String strv = getSelf().getDescription().getVersion();
		return new Version(strv);
	}
}
