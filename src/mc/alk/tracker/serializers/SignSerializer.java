package mc.alk.tracker.serializers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mc.alk.serializers.BaseConfig;
import mc.alk.tracker.controllers.SignController;
import mc.alk.tracker.objects.StatSign;

public class SignSerializer extends BaseConfig{
	SignController sc;

	public SignSerializer(SignController sc) {
		this.sc = sc;
	}


	public void saveAll() {
		Map<String,StatSign> map = sc.getTopSigns();
		if (map != null){
			List<StatSign> l = new ArrayList<StatSign>(map.values());
			config.set("topSigns", l);
		}
		map = sc.getPersonalSigns();
		if (map != null){
			List<StatSign> l = new ArrayList<StatSign>(map.values());
			config.set("personalSigns", l);
		}

		save();
	}

	public void loadAll(){
		String[] types = new String[]{"topSigns","personalSigns"};
		for (String type: types){
			List<?> signs = config.getList(type);
			if (signs == null)
				continue;
			for (Object o : signs){
				if (o == null || !(o instanceof StatSign))
					continue;
				sc.addSign((StatSign) o);
			}
		}
	}
}
