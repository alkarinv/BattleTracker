package mc.alk.tracker;

import mc.alk.tracker.controllers.ConfigController;
import mc.alk.tracker.ranking.EloCalculator;
import mc.alk.tracker.ranking.RatingCalculator;

public class TrackerOptions {
	boolean saveIndividualRecords = false;
	RatingCalculator ratingCalculator;

	public TrackerOptions(){
		EloCalculator ec = new EloCalculator();
		ec.setDefaultRating((float) ConfigController.getDouble("elo.default",1250));
		ec.setEloSpread((float) ConfigController.getDouble("elo.spread",400));
		ratingCalculator = ec;
	}

	public RatingCalculator getRatingCalculator() {
		return ratingCalculator;
	}

	public void setRatingCalculator(RatingCalculator ratingCalculator) {
		this.ratingCalculator = ratingCalculator;
	}

	public boolean savesIndividualRecords() {
		return saveIndividualRecords;
	}

	public void setSaveIndividualRecords(boolean saveIndividualRecords) {
		this.saveIndividualRecords = saveIndividualRecords;
	}

}
