package mc.alk.tracker.objects;

public enum WLT {
	LOSS,WIN,TIE;

	public static WLT valueOf(int value) {
		if (value >= WLT.values().length || value < 0)
			return null;
		return WLT.values()[value];
	}

	public WLT reverse() {
		switch(this){
		case LOSS: return WIN;
		case WIN : return LOSS;
		case TIE: return TIE;
		}
		return null;
	}
}
