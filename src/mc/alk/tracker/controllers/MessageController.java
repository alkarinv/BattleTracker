package mc.alk.tracker.controllers;

import java.io.File;
import java.util.Formatter;
import java.util.List;
import java.util.Random;

import mc.alk.tracker.Defaults;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.alk.controllers.MC;
import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Herochat;

/**
 * 
 * @author alkarin
 *
 */
public class MessageController {

	private static YamlConfiguration config = new YamlConfiguration();
	static File f;
	static Herochat hc;
	static MessageController mc;
	static List<String> meleeMsgs = null;
	static List<String> rangeMsgs = null;
	static Random r = new Random();
	public static void sendDeathMessage(String msg) {
		/// Send to global or to a specified herochat/battlechat channel
		boolean useHero = ConfigController.getBoolean("useHeroChat");
		if (useHero && hc != null ){
			String hcchannel = ConfigController.getString("chatChannel");
			Channel ch = Herochat.getChannelManager().getChannel(hcchannel);
			if (ch != null){
			}
		} else {
			Bukkit.broadcastMessage(MessageController.colorChat(msg));				
		}

	}

	public static Herochat getHeroChat() {
		return hc;
	}
	public static void setHeroChat(Herochat hc) {
		MessageController.hc = hc;
	}
	public static String getMessage(String node, Object... varArgs) {
		return getMsg(Defaults.LANGUAGE,node,varArgs);
	}

	public static String getMessageNP(String node, Object... varArgs) {
		return getMsgNP(Defaults.LANGUAGE,node,varArgs);
	}


	private static String getMsg(String prefix,String node, Object... varArgs) {
		try{
			ConfigurationSection n = config.getConfigurationSection(prefix);

			StringBuilder buf = new StringBuilder(n.getString("prefix", "[PVP]"));
			String msg = n.getString(node, "No translation for " + node);
			Formatter form = new Formatter(buf);
			form.format(msg, varArgs);
			form.close();
			return colorChat(buf.toString());
		} catch(Exception e){
			System.err.println("Error getting message " + prefix + "." + node);
			for (Object o: varArgs){ System.err.println("argument=" + o);}
			e.printStackTrace();
			return "Error getting message " + prefix + "." + node;
		}
	}
	private static String getMsgNP(String prefix,String node, Object... varArgs) {
		ConfigurationSection n = config.getConfigurationSection(prefix);
		StringBuilder buf = new StringBuilder();
		String msg = n.getString(node, "No translation for " + node);
		Formatter form = new Formatter(buf);
		try{
			form.format(msg, varArgs);
			form.close();
		} catch(Exception e){
			System.err.println("Error getting message " + prefix + "." + node);
			for (Object o: varArgs){ System.err.println("argument=" + o);}
			e.printStackTrace();
		}
		return colorChat(buf.toString());
	}

	public static String colorChat(String msg) {
		return msg.replaceAll("&", Character.toString((char) 167));
	}

	public static boolean setConfig(File f){
		MessageController.f = f;
		return load();
	}

	public static boolean sendMessage(Player p, String message){
		if (message ==null) return true;
		String[] msgs = message.split("\n");
		for (String msg: msgs){
			if (p == null){
				System.out.println(MC.colorChat(msg));
			} else {
				p.sendMessage(MC.colorChat(msg));			
			}			
		}
		return true;
	}
	public static boolean sendMessage(CommandSender p, String message){
		if (message ==null) return true;
		if (p instanceof Player){
			if (((Player) p).isOnline())
				p.sendMessage(MC.colorChat(message));			
		} else {
			p.sendMessage(MC.colorChat(message));
		}
		return true;
	}
	public static String getMeleeMessage(Object... varArgs){
		String msg = meleeMsgs.get(r.nextInt(meleeMsgs.size()));
		StringBuilder buf = new StringBuilder();
		Formatter form = new Formatter(buf);
		try{
			form.format(msg, varArgs);
			form.close();
		} catch(Exception e){
			System.err.println("Error getting melee message ");
			for (Object o: varArgs){ System.err.println("argument=" + o);}
			e.printStackTrace();
		}
		return colorChat(buf.toString());
	}
	public static String getRangeMessage(Object... varArgs){
		String msg = rangeMsgs.get(r.nextInt(rangeMsgs.size()));
		StringBuilder buf = new StringBuilder();
		Formatter form = new Formatter(buf);
		try{
			form.format(msg, varArgs);
			form.close();
		} catch(Exception e){
			System.err.println("Error getting range message ");
			for (Object o: varArgs){ System.err.println("argument=" + o);}
			e.printStackTrace();
		}
		return colorChat(buf.toString());
	}

	public static boolean load() {
		try {
			config.load(f);
			String prefix = config.getString(Defaults.LANGUAGE+".prefix", "&4[PVP] ");
			meleeMsgs = config.getStringList(Defaults.LANGUAGE+".meleeDeaths");
			rangeMsgs = config.getStringList(Defaults.LANGUAGE+".rangeDeaths");
			for (int i=0;i<meleeMsgs.size();i++){
				meleeMsgs.set(i, prefix+meleeMsgs.get(i));}
			for (int i=0;i<rangeMsgs.size();i++){
				rangeMsgs.set(i, prefix+rangeMsgs.get(i));}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
