package mc.alk.tracker.serializers;

import java.io.File;
import java.io.IOException;

import mc.alk.plugin.updater.v1r3.FileUpdater;
import mc.alk.plugin.updater.v1r3.Version;
import mc.alk.tracker.Tracker;

import org.bukkit.configuration.file.YamlConfiguration;

public class YamlConfigUpdater {


	public void update(YamlConfiguration config, File configFile) {
		File backupDir = new File(Tracker.getSelf().getDataFolder()+"/backups");
		if (!backupDir.exists()) backupDir.mkdir();

		Version curVersion = new Version(config.getString("version", "0"));
		Version newVersion = new Version("1.0");
		try{
			if (curVersion.compareTo(newVersion) < 0){
				curVersion = updateTo1Point0(configFile,backupDir, curVersion, newVersion);}
			newVersion = new Version("1.0.1");
			if (curVersion.compareTo(newVersion) < 0){
				curVersion = updateTo1Point01(configFile,backupDir, curVersion, newVersion);}
			newVersion = new Version("1.0.2");
			if (curVersion.compareTo(newVersion) < 0){
				curVersion = updateTo1Point02(configFile,backupDir, curVersion, newVersion);}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private Version updateTo1Point0(File oldFile, File backupDir, Version newVersion, Version oldVersion) throws IOException {
		FileUpdater fu = new FileUpdater(oldFile,backupDir,newVersion,oldVersion);
		fu.addBefore(".*showBukkitPVPMessages.*", "version: 1.0");
		fu.addAfter(".*trackPvE:.*", "",
				"### The default messages when top command is used",
				"topHeaderMsg: '&4Top &6{interfaceName}&4 {stat} TeamSize:{teamSize}'",
				"topBodyMsg: '&e#{rank}&4 {name} - {wins}:{losses}&6[{rating}]'",
				"",
				"### ignore the following entities when they kill or are killed",
				"ignoreEntities: []");
		return fu.update();
	}
	private Version updateTo1Point01(File oldFile, File backupDir, Version newVersion, Version oldVersion) throws IOException {
		FileUpdater fu = new FileUpdater(oldFile,backupDir,newVersion,oldVersion);
		fu.replace(".*version:.*", "version: 1.0.1");
		fu.addAfter(".*sendPVEDeathMessages:.*", "",
				"### If server wide PvP or Pve messages are disabled you can turn",
				"### these to true to allow the killer and the dead person",
				"### to still see the messages",
				"sendInvolvedPvPMessages: false",
				"sendInvolvedPvEMessages: false");
		fu.addAfter(".*ignoreEntities:.*", "",
				"### Should we even use leaderboard signs?",
				"allowLeaderboardSigns: true");
		return fu.update();
	}

	private Version updateTo1Point02(File oldFile, File backupDir, Version newVersion, Version oldVersion) throws IOException {
		FileUpdater fu = new FileUpdater(oldFile,backupDir,newVersion,oldVersion);
		fu.replace(".*version:.*", "version: 1.0.2");
		fu.addAfter(".*ignoreEntities:.*", "",
				"### ignore the following worlds",
				"ignoreWorlds: []");
		return fu.update();
	}
}
