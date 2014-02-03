package mc.alk.tracker.objects;

public enum SignType {
	TOP, PERSONAL;

	public static SignType fromName(String name) {
		if (name ==null)
			return null;
		name = name.toUpperCase();
		SignType gt = null;
		try{
			gt = SignType.valueOf(name);
		} catch (Exception e){}
		if (gt == null){
			StatType st = StatType.fromName(name);
			if (st != null)
				gt = SignType.PERSONAL;
		}
		return gt;
	}

}
