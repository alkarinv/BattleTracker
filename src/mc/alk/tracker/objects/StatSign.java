package mc.alk.tracker.objects;

import java.util.HashMap;
import java.util.Map;

import mc.alk.util.SerializerUtil;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;


public class StatSign implements ConfigurationSerializable{
	final Location location;
	final SignType type;
	StatType statType;

	static {
		ConfigurationSerialization.registerClass(StatSign.class);
	}

	public StatSign(Location location, SignType type){
		this.location = location;
		this.type = type;
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("location", SerializerUtil.getLocString(location));
		map.put("signType", type.toString());
		if (statType != null)
			map.put("statType", statType.toString());
		return map;
	}

	public static StatSign deserialize(Map<String, Object> map) {
		Object signTypeStr = map.get("signType");
		Object locStr = map.get("location");
		Object statStr = map.get("statType");
		if (signTypeStr == null || locStr == null)
			return null;
		SignType type = SignType.valueOf(signTypeStr.toString());
		Location l = SerializerUtil.getLocation(locStr.toString());
		if (type == null || l == null)
			return null;
		StatSign ss = new StatSign(l,type);
		if (statStr != null){
			StatType statType = StatType.valueOf(statStr.toString());
			ss.setStatType(statType);
		}
		return ss;
	}

	public void setStatType(StatType statType) {
		this.statType = statType;
	}

	public SignType getSignType() {
		return type;
	}

	public String getLocationString() {
		return SerializerUtil.getLocString(location);
	}

	@Override
	public String toString(){
		return "["+SerializerUtil.getLocString(location)+ " : "+type+"]";
	}
}
