package mc.alk.tracker.serializers;

import java.io.File;
import java.io.IOException;

import mc.alk.plugin.updater.v1r2.FileUpdater;
import mc.alk.plugin.updater.v1r2.Version;
import mc.alk.tracker.Tracker;

import org.bukkit.configuration.file.YamlConfiguration;

public class YamlMessageUpdater {


	public void update(YamlConfiguration config, File configFile) {
		File backupDir = new File(Tracker.getSelf().getDataFolder()+"/backups");
		if (!backupDir.exists()) backupDir.mkdir();

		Version curVersion = new Version(config.getString("version", "0"));
		Version newVersion = new Version("1.0");
		try{
			if (curVersion.compareTo(newVersion) < 0){
				curVersion = updateTo1Point0(configFile,backupDir, curVersion, newVersion);}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private Version updateTo1Point0(File oldFile, File backupDir, Version newVersion, Version oldVersion) throws IOException {
		FileUpdater fu = new FileUpdater(oldFile,backupDir,newVersion,oldVersion);
		fu.addAfter(".*# &f  White.*", "version: 1.0");
		fu.addBefore(".*wolf:.*",
				"  snowman:",
				"    - '&8snowmen&f have fought back against &6%d&f'",
				"  ozelot:",
				"    - 'The cute ocelot showed its power to &6%d&f'",
				"    - '&6%d&f discovered that ocelots have fangs'",
				"  anvil:",
				"    - '&6%d&f was crushed by an anvil'",
				"    - '&6%d&f found to use anvils, not stand under them'");
		return fu.update();
	}

}
