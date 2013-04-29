package mc.alk.tracker.objects;

import java.util.HashMap;
import java.util.Map;

import mc.alk.v1r5.util.Log;
import mc.alk.v1r5.util.SerializerUtil;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;


public class StatSign implements ConfigurationSerializable{
	final String dbName;
	final Location location;
	final SignType type;
	StatType statType = StatType.RATING;

	public StatSign(String dbName, Location location, SignType type){
		this.dbName = dbName;
		this.location = location;
		this.type = type;
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("location", SerializerUtil.getLocString(location));
		map.put("signType", type.toString());
		map.put("dbName", dbName);
		if (statType != null)
			map.put("statType", statType.toString());
		return map;
	}

	public static StatSign valueOf(Map<String, Object> map) {
		return deserialize(map);
	}

	public static StatSign deserialize(Map<String, Object> map) {
		Object signTypeStr = map.get("signType");
		Object locStr = map.get("location");
		Object statStr = map.get("statType");
		Object dbStr = map.get("dbName");
		if (signTypeStr == null || locStr == null || dbStr == null)
			return null;
		SignType type = SignType.valueOf(signTypeStr.toString());
		Location l = null;
		try{
			l = SerializerUtil.getLocation(locStr.toString());
		} catch (IllegalArgumentException e){
			Log.warn("BattleTracker error retrieving sign at " + locStr);
		}
		if (type == null || l == null)
			return null;
		StatSign ss = new StatSign(dbStr.toString(),l,type);
		if (statStr != null){
			StatType statType = StatType.fromName(statStr.toString());
			ss.setStatType(statType);
		}
		return ss;
	}

	public void setStatType(StatType statType) {
		this.statType = statType;
	}
	public StatType getStatType(){
		return statType;
	}
	public SignType getSignType() {
		return type;
	}

	public String getLocationString() {
		return SerializerUtil.getLocString(location);
	}

	public static String getLocationString(Location location){
		return SerializerUtil.getLocString(location);
	}
	public Location getLocation(){
		return location;
	}
	@Override
	public String toString(){
		return "["+SerializerUtil.getLocString(location)+ " : "+type+"]";
	}

	public String getDBName() {
		return dbName;
	}
}
