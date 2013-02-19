package mc.alk.tracker;

import java.util.HashMap;

import mc.alk.battleCore.MCPlugin;
import mc.alk.battleCore.Version;
import mc.alk.tracker.controllers.ConfigController;
import mc.alk.tracker.controllers.MessageController;
import mc.alk.tracker.controllers.TrackerController;
import mc.alk.tracker.controllers.TrackerImpl;
import mc.alk.tracker.controllers.TrackerImpl.DBConnectionException;
import mc.alk.tracker.executors.BattleTrackerExecutor;
import mc.alk.tracker.executors.TrackerExecutor;
import mc.alk.tracker.listeners.BTEntityListener;
import mc.alk.tracker.listeners.BTPluginListener;


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
		getCommand("btpvp").setExecutor(new TrackerExecutor(getInterface(Defaults.PVP_INTERFACE)));
		getCommand("btpve").setExecutor(new TrackerExecutor(getInterface(Defaults.PVE_INTERFACE)));

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

	public static TrackerInterface getPVPInterface(){
		return getInterface(Defaults.PVP_INTERFACE,new TrackerOptions());
	}

	public static TrackerInterface getPVEInterface(){
		return getInterface(Defaults.PVE_INTERFACE,new TrackerOptions());
	}

	public static TrackerInterface getInterface(String interfaceName){
		return getInterface(interfaceName,new TrackerOptions());
	}

	public static TrackerInterface getInterface(String interfaceName, TrackerOptions trackerOptions){
		try {
			String iname = interfaceName.toLowerCase();
			if (!interfaces.containsKey(iname)){
				interfaces.put(iname, new TrackerImpl(interfaceName,trackerOptions));}
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

	public static boolean hasInterface(String interfaceName) {
		String iname = interfaceName.toLowerCase();
		return interfaces.containsKey(iname);
	}
}
