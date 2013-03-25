package mc.alk.tracker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import mc.alk.battleCore.MCPlugin;
import mc.alk.battleCore.Version;
import mc.alk.tracker.controllers.ConfigController;
import mc.alk.tracker.controllers.MessageController;
import mc.alk.tracker.controllers.SignController;
import mc.alk.tracker.controllers.TrackerController;
import mc.alk.tracker.controllers.TrackerImpl;
import mc.alk.tracker.controllers.TrackerImpl.DBConnectionException;
import mc.alk.tracker.executors.BattleTrackerExecutor;
import mc.alk.tracker.executors.TrackerExecutor;
import mc.alk.tracker.listeners.BTEntityListener;
import mc.alk.tracker.listeners.BTPluginListener;
import mc.alk.tracker.listeners.SignListener;
import mc.alk.tracker.objects.StatSign;
import mc.alk.tracker.serializers.SignSerializer;
import mc.alk.tracker.serializers.YamlConfigUpdater;
import mc.alk.tracker.serializers.YamlMessageUpdater;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;


public class Tracker extends MCPlugin{
	static Tracker plugin;
	TrackerController sc;
	static Map<String, TrackerInterface> interfaces = new ConcurrentHashMap<String,TrackerInterface>();
	SignController signController = new SignController();
	SignSerializer signSerializer;

	@Override
	public void onEnable() {
		super.onEnable();
		plugin = this;
		createPluginFolder();
		loadConfigs();
		getServer().getPluginManager().registerEvents(new BTEntityListener(), this);
		getServer().getPluginManager().registerEvents(new BTPluginListener(), this);
		getServer().getPluginManager().registerEvents(new SignListener(signController), this);

		getCommand("battleTracker").setExecutor(new BattleTrackerExecutor());
		getCommand("btpvp").setExecutor(new TrackerExecutor(getInterface(Defaults.PVP_INTERFACE)));
		getCommand("btpve").setExecutor(new TrackerExecutor(getInterface(Defaults.PVE_INTERFACE)));

		BTPluginListener.loadPlugins();

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			@Override
			public void run() {
				signController.updateSigns();
			}
		}, 20, 1000);
	}

	@Override
	public void onDisable(){
		saveConfigs();
	}
	@Override
	public void onLoad(){
		ConfigurationSerialization.registerClass(StatSign.class);
	}

	public void loadConfigs(){
		/// Load
		ConfigController.setConfig(load("/default_files/config.yml",getDataFolder().getPath() +"/config.yml"));
		MessageController.setConfig(load("/default_files/messages.yml",getDataFolder().getPath() +"/messages.yml"));
		signSerializer = new SignSerializer(signController);
		signSerializer.setConfig(getDataFolder().getPath()+"/signs.yml");
		signSerializer.loadAll();

		/// Update
		YamlConfigUpdater cu = new YamlConfigUpdater();
		cu.update(ConfigController.getConfig(), ConfigController.getFile());

		YamlMessageUpdater mu = new YamlMessageUpdater();
		mu.update(MessageController.getConfig(), MessageController.getFile());

		/// Reload
		ConfigController.setConfig(ConfigController.getFile());
		MessageController.setConfig(MessageController.getFile());
	}

	public static Tracker getSelf() {
		return plugin;
	}

	private void saveConfigs() {
		synchronized(interfaces){
			for (TrackerInterface ti: interfaces.values()){
				ti.flush();
			}
		}
		signSerializer.saveAll();
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

	public static Collection<TrackerInterface> getAllInterfaces() {
		return new ArrayList<TrackerInterface>(interfaces.values());
	}
}
