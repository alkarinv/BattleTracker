package mc.alk.tracker.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import mc.alk.tracker.Tracker;
import mc.alk.tracker.TrackerInterface;
import mc.alk.tracker.objects.Stat;
import mc.alk.tracker.objects.StatSign;
import mc.alk.tracker.objects.StatType;
import mc.alk.util.SignUtil;
import mc.alk.v1r5.util.Log;
import mc.alk.v1r5.util.SerializerUtil;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class SignController {
	Map<String,StatSign> personalSigns = new HashMap<String,StatSign>();
	Map<String,StatSign> topSigns = new ConcurrentHashMap<String,StatSign>();
	Map<String, Map<String,StatSign>> allSigns = new ConcurrentHashMap<String,Map<String,StatSign>>();
	boolean updating = false;
	public SignController(){
		//		allSigns.put(key, value)
	}
	public void addSign(StatSign sign){
		switch(sign.getSignType()){
		case TOP:
			topSigns.put(sign.getLocationString(), sign);
			break;
		case PERSONAL:
			personalSigns.put(sign.getLocationString(), sign);
		}
	}

	public Map<String,StatSign> getPersonalSigns(){
		return personalSigns;
	}

	public Map<String,StatSign> getTopSigns(){
		return topSigns;
	}

	public void addSigns(Collection<StatSign> signs) {
		for (StatSign sign: signs){
			addSign(sign);}
	}

	public void updateSigns(){
		if (updating)
			return;
		updating = true;
		final Map<String,StatSign> map;
		synchronized(topSigns){
			map = new HashMap<String,StatSign>(topSigns);
		}
		Collection<String> badSigns = new HashSet<String>();
		for (TrackerInterface ti : Tracker.getAllInterfaces()){
			final String tiName = ti.getInterfaceName().toUpperCase();
			Map<StatType, List<Stat>> stats = new HashMap<StatType, List<Stat>>();

			for (String loc : map.keySet()){
				StatSign ss = map.get(loc);
				if (!ss.getDBName().toUpperCase().equals(tiName)){
					continue;}
				Sign s = getSign(loc);
				if (s == null){
					badSigns.add(loc);
					continue;
				}

				switch(ss.getSignType()){
				case TOP:
					doTopSign(ss,s,ti,stats);
					break;
				default:
					break;
				}

			}
		}
		synchronized(topSigns){
			for (String s: badSigns){
				topSigns.remove(s);
			}
		}
		updating = false;
	}

	private void doTopSign(StatSign ss, Sign s, TrackerInterface ti, Map<StatType,List<Stat>> stats) {
		World w = s.getLocation().getWorld();
		Sign sign = null;
		List<Sign> signList = new ArrayList<Sign>();

		boolean foundUpStatSign = false;
		LinkedList<Sign> upsignList = new LinkedList<Sign>();
		/// Search up
		int x = s.getLocation().getBlockX();
		int y = s.getLocation().getBlockY();
		int z = s.getLocation().getBlockZ();
		while ((sign = getSign(w,x,++y,z)) != null){
			/// another command sign, don't continue
			if (breakLine(sign.getLine(0))){
				foundUpStatSign = true;
				break;
			}
			upsignList.addFirst(sign);
		}
		/// If there isnt a conflicting sign above, then add all those signs as well
		if (!foundUpStatSign){
			signList.addAll(upsignList);}

		/// Add self
		int originalSignIndex = signList.size();
		signList.add(s);

		sign = null;
		/// Search down
		x = s.getLocation().getBlockX();
		y = s.getLocation().getBlockY();
		z = s.getLocation().getBlockZ();
		while ((sign = getSign(w,x,--y,z)) != null){
			String line = sign.getLine(0);
			/// another command sign, don't continue
			if (breakLine(line))
				break;
			signList.add(sign);
		}

		List<Stat> toplist= stats.get(ss.getStatType());
		if (toplist == null || toplist.size() < signList.size()*4){ /// Load List
			toplist = ti.getTopX(ss.getStatType(), signList.size() * 4);
			if (toplist == null){ /// some error happened, cant update this type
				Log.warn("[BattleTracker] TopList wasnt found for " + ss.getStatType() +"  at " + s.getLocation());
				return;
			}
			stats.put(ss.getStatType(), toplist);
		}

		boolean quit = false;
		int curTop = 0;
		for (int i =0;i< signList.size() && !quit;i++){
			int startIndex = 0;
			s = signList.get(i);
			String line = s.getLine(0);
			if (i == originalSignIndex){
				s.setLine(0, MessageController.colorChat("[&e"+StringUtils.capitalize(ti.getInterfaceName())+"&0]"));
				s.setLine(1, MessageController.colorChat("["+ss.getStatType().color()+ss.getStatType()+"&0]"));
				startIndex = 2;
			}

			for (int j=startIndex;j< 4;j++){
				if (curTop >= toplist.size()){
					quit = true;
					break;
				}
				String name = toplist.get(curTop).getName();
				int val = (int) toplist.get(curTop).getStat(ss.getStatType());
				String sval = val +"";
				int length = ((curTop+1)+"").length() + sval.length() + 1; // 1 delimiters
				if (name.length() + length > 16){
					name = name.substring(0, Math.min(name.length(), length));
				}

				String spacing = StringUtils.repeat(" ", 15-(length + name.length()));
				line = (curTop+1)+"."+ name+ spacing +sval;
				s.setLine(j, line+"         ");
				curTop++;
			}
			s.update(true);
		}
	}
	private boolean breakLine(String line) {
		return line != null && (!line.isEmpty() && line.startsWith("["));
	}
	private Sign getSign(World w, int x, int y, int z) {
		Block b = w.getBlockAt(x, y, z);
		Material t = b.getType();
		return t == Material.SIGN || t == Material.SIGN_POST || t==Material.WALL_SIGN ? (Sign)b.getState(): null;
	}

	private Sign getSign(String loc) {
		Location l = SerializerUtil.getLocation(loc);
		if (l == null)
			return null;
		Material t = l.getBlock().getType();
		return t == Material.SIGN || t == Material.SIGN_POST || t==Material.WALL_SIGN ? (Sign)l.getBlock().getState(): null;
	}

	public StatSign getSign(Location location) {
		String key = StatSign.getLocationString(location);
		if (personalSigns.containsKey(key))
			return personalSigns.get(key);
		else
			return topSigns.get(key);
	}

	public void clickedSign(Player player, Sign s, StatSign ss) {
		switch(ss.getSignType()){
		case TOP:
			updateTopSign(player, s, ss);
			break;
		case PERSONAL:
			updatePersonalSign(player, s, ss);
			break;
		}
	}
	private void updatePersonalSign(Player player, Sign s, StatSign ss) {
		updateSign(player,s,ss);
	}

	private void updateTopSign(Player player, Sign s, StatSign ss) {
		updateSign(player,s,ss);
	}
	private void updateSign(Player player, Sign s, StatSign ss){
		String[] lines = s.getLines();
		TrackerInterface ti = Tracker.getInterface(ss.getDBName());
		Stat stat = ti.getRecord(player);
		if (stat == null)
			return;

		lines[0] = "&eYour Stats";
		lines[1] = "[&6"+stat.getRating() +"&0]";
		int l = (stat.getWins() +"/" + stat.getLosses()).length();
		lines[2] = l <= 10 ?
			"&2"+stat.getWins() +"&0/&4" + stat.getLosses() :
			stat.getWins() +"/" + stat.getLosses();
		if (lines[2].length() <= 12)
			lines[2] = "W/L " + lines[2];
		lines[3] = "Streak: &6" + stat.getStreak() +"";
		for (int i=0;i<lines.length;i++){
			lines[i] = MessageController.colorChat(lines[i]);
		}
		SignUtil.sendLines(player, s, lines);
	}
	public void clearSigns() {
		topSigns.clear();
		personalSigns.clear();

	}
	public void removeSignAt(Location location) {
		String l = StatSign.getLocationString(location);
		topSigns.remove(l);
		personalSigns.remove(l);
	}
}
