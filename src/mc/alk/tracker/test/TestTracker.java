package mc.alk.tracker.test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import mc.alk.tracker.TrackerInterface;
import mc.alk.tracker.objects.Stat;
import mc.alk.tracker.objects.TeamStat;
import mc.alk.tracker.objects.WLT;

import org.junit.Test;

public class TestTracker {
	Random rand = new Random();
	
	@Test
	public void test() {
		String db ="tracker";
		String table = "pvp";
		String user = "root";
		String password = "";
		TrackerInterface bti = new TrackerInterface(db,table,user,password);
		bti.onlyTrackOverallStats(false);
		Set<String> players = new HashSet<String>();
		Set<String> players2 = new HashSet<String>();
		final int nTeamMembers = 7;
		for (int i=0;i< nTeamMembers;i++){
			players.add("p" +i);
			players2.add("player" +(i+20));
		}
		Stat s = bti.getPlayerRecord("p1");
		System.out.println("p2 = "  + s);
		s = bti.loadPlayerRecord("p2");
		System.out.println("p2 = "  + s);
//		bti.addTeamRecord(players, players2, WLTIE.WIN);
//		bti.addTeamRecord(players2, players, WLTIE.WIN);
		bti.addPlayerRecord("p2", "p1", WLT.WIN);
		bti.addPlayerRecord("p1", "p2", WLT.WIN);
		bti.addPlayerRecord("p1", "p2", WLT.WIN);
		bti.addPlayerRecord("p1", "p2", WLT.WIN);
		bti.addPlayerRecord("p1", "p3", WLT.WIN);
		bti.addPlayerRecord("p3", "p4", WLT.WIN);
		System.out.println("p1 = "  + bti.getPlayerRecord("p1"));
		System.out.println("p2 = "  + s);
//		printList(bti.getTopXKDRatio(15));
//		printList(bti.getTopXWins(35));
		TeamStat ts = bti.getTeamRecord("p2");
		System.out.println("p2 = "  + ts);
//		VersusRecord or = ts.getRecordVersus("p1");
//		System.out.println("Stat = " + ts);
//		System.out.println("OR = " + or);
//		assert(ts.getCount() == nTeamMembers);
		String p1,p2;
		final int NPLAYERS = 50;
//		for (int i=0;i< NPLAYERS;i++){
//			p1 = "p"+rand.nextInt(NPLAYERS) ;
//			p2 = "p" + rand.nextInt(NPLAYERS);
//			bti.addPlayerRecord(p1, p2, WLTIE.WIN);
//		}
//		for (int i=0;i< NPLAYERS;i++){
//			s = bti.loadPlayerRecord("p" + i);
//			System.out.println(s);
//		}
		bti.saveAll();
		bti.flush();
	}

	private void printList(List<Stat> stats){
		System.out.println("----- Top = " + stats.size());
		for (int i=0;i<stats.size();i++){
			System.out.println(i+" stat = " + stats.get(i));			
		}
		
	}
}
