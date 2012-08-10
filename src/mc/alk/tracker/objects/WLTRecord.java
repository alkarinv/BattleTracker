package mc.alk.tracker.objects;


public class WLTRecord {
	public final Long date;
	public WLT wlt;

	public WLTRecord(WLT wlt, Long date) {
		this.wlt = wlt;
		this.date = date;
	}

	public WLTRecord(WLT wlt) {
		this.wlt = wlt;
		this.date = System.currentTimeMillis();
	}
	
	public int compareTo(WLTRecord o) {
		return this.date.compareTo(o.date);
	}
	public String toString(){
		return "[WLT " + wlt + " " + date+"]";
	}

	public void reverse() {
		wlt = wlt.reverse(); 
	}
}
