package mc.alk.tracker.controllers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mc.alk.tracker.objects.StatSign;

public class SignController {
	Map<String,StatSign> personalSigns = new HashMap<String,StatSign>();
	Map<String,StatSign> topSigns = new HashMap<String,StatSign>();

	public void addSign(StatSign sign){
		switch(sign.getSignType()){
		case TOP:
			topSigns.put(sign.getLocationString(), sign);
			break;
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
}
