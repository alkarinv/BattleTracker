package mc.alk.tracker.controllers;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
/**
 * 
 * @author alkarin
 *
 */
public class ConfigController {
	static YamlConfiguration config;
	static File f = null;
	
	public static boolean getBoolean(String node) {return config.getBoolean(node, false);}
	public static boolean getBoolean(String node,boolean b) {return config.getBoolean(node, b);}
	public static  String getString(String node) {return config.getString(node,null);}
	public static  String getString(String node,String def) {return config.getString(node,def);}
	public static int getInt(String node,int i) {return config.getInt(node, i);}
	public static double getDouble(String node, double d) {return config.getDouble(node, d);}

	public static void setConfig(File f){
		ConfigController.f = f;
		config = new YamlConfiguration();
		loadAll();
	}

	public static void loadAll(){
		try {config.load(f);} catch (Exception e){e.printStackTrace();}
	}
}
