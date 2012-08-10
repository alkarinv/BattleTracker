package mc.alk.tracker;

import java.util.HashMap;

import mc.alk.tracker.TrackerInterface.DBConnectionException;
import mc.alk.tracker.controllers.ConfigController;
import mc.alk.tracker.controllers.MessageController;
import mc.alk.tracker.controllers.TrackerController;
import mc.alk.tracker.executors.PVPExecutor;
import mc.alk.tracker.listeners.BTEntityListener;
import mc.alk.tracker.listeners.BTPlayerListener;
import mc.alk.tracker.listeners.BTPluginListener;

import com.alk.battleCore.MCPlugin;

public class Tracker extends MCPlugin{
	static Tracker plugin;
	TrackerController sc;
	static HashMap<String, TrackerInterface> interfaces = new HashMap<String,TrackerInterface>();
	@Override
	public void onEnable() {
		super.onEnable();
		plugin = this;
		createPluginFolder();
		ConfigController.setConfig(load("/default_files/config.yml",getDataFolder().getPath() +"/config.yml"));
		getServer().getPluginManager().registerEvents(new BTEntityListener(), this);
		getServer().getPluginManager().registerEvents(new BTPlayerListener(), this);
		getServer().getPluginManager().registerEvents(new BTPluginListener(), this);
		MessageController.setConfig(load("/default_files/messages.yml",getDataFolder().getPath() +"/messages.yml"));

		getCommand("battleTracker").setExecutor(new PVPExecutor(getInterface(Defaults.PVP_INTERFACE)));
		getCommand("pvp").setExecutor(new PVPExecutor(getInterface(Defaults.PVP_INTERFACE)));
		getCommand("pve").setExecutor(new PVPExecutor(getInterface(Defaults.PVE_INTERFACE)));
		BTPluginListener.loadPlugins();
//		Timer timer = new Timer();
//		timer.scheduleAtFixedRate(new TimerTask(){
//			public void run(){
//				saveStats();
//			}
//		}, 0, Defaults.SAVE_EVERY_X_SECONDS * 1000);

	}
	@Override
	public void onDisable(){
		saveStats();
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

	public static TrackerInterface getInterface(String name){
		try {
			name = name.toLowerCase();
			if (!interfaces.containsKey(name)){
				interfaces.put(name, new TrackerInterface(name));}
			return interfaces.get(name);
		} catch (DBConnectionException e) {
			e.printStackTrace();
		}
		return null;
	}
}
