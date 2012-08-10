package mc.alk.tracker.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;


public class TeamStat extends Stat implements Comparable<TeamStat>{

	public TeamStat(){
		
	}

	public TeamStat(String name, boolean id){
		this(name,id,0);
	}
	public int charCount(final String s, final Character testc){
		int count =0;
		for (int i=0;i< s.length();i++)
			if (testc.equals(s.charAt(i)))
				count++;
		return count;
	}

	public TeamStat(String name, boolean id, int teamSize) {
		if (id){
			this.strid = name;
			count = teamSize;
		} else {
			this.name = name;
			this.strid = name;
			/// If we have a comma delimited list, then we know the # of players, otherwise its a mystery(0)
			int c = charCount(name,',');
			count = c == 0 ? 0: c+1; 
		}
//		System.out.println(id + "  this=" + this);
	}

	public TeamStat(Set<String> p){
		this.p = new ArrayList<String>(p);
		createName();
		strid = TeamStat.getKey(this.p);
		count = p.size();
	}

	public void setMembers(Collection<String> players){
//		System.out.println("Setting members = " + players);
		if (players == null)
			return;
		this.p = new LinkedList<String>(players);
		createName();
	}


}
