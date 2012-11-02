package mc.alk.tracker.events;

import mc.alk.tracker.objects.Stat;

public class MaxRatingChangeEvent extends TrackerEvent{
	final Stat stat;
	final double oldMaxRating;
	public MaxRatingChangeEvent(Stat stat, double oldMaxRating){
		this.stat = stat;
		this.oldMaxRating = oldMaxRating;
	}

	public Stat getStat(){
		return stat;
	}

	public double getOldMaxRating(){
		return oldMaxRating;
	}
}
