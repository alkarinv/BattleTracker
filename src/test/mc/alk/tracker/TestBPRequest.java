package test.mc.alk.tracker;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import junit.framework.TestCase;
import mc.alk.mc.MCServer;
import mc.alk.tracker.controllers.ConfigController;
import mc.alk.tracker.controllers.TrackerImpl;
import mc.alk.tracker.executors.TrackerExecutor;
import mc.alk.tracker.objects.Stat;
import mc.alk.v1r7.util.Log;
import test.mc.alk.TestServer;
import test.mc.alk.core.TestCommand;
import test.mc.alk.testbukkit.TestBukkitPlayer;

public class TestBPRequest extends TestCase{
	static TestServer api = new TestServer();
	int readTimeout = 5000;
	int conTimeout = 7000;

	@Override
	public void setUp(){
		MCServer.setInstance(api);
		File cf = new File("default_files/config.yml");
		assertTrue(cf.exists());
		ConfigController.setConfig(cf);
	}

	public void testBP() throws IOException{
		TestBukkitPlayer tp = new TestBukkitPlayer("testplayer1");
		TrackerImpl ti = new TrackerImpl("mysql", "minecraft","localhost",
				"pvp", "3306", "root","");
		TrackerExecutor exe = new TrackerExecutor(ti);
		TestCommand cmd = new TestCommand("top");
		exe.onCommand(tp, cmd, "top", new String[]{"top"});
		URL url = new URL("http://tracker.battleplugins.com/grabber/overall.php");
		Stat s = ti.loadPlayerRecord("p1");
		String data = getStatPostString(s);
		URLConnection connection = getPOSTConnection(url, data);
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		br.close();

		url = new URL("http://tracker.battleplugins.com/grabber/kill.php");
//		Stat s = ti.loadPlayerRecord("p1");
		data = getDeathPostString(s, s.getName(), "p2", "DIAMOND_SWORD");
		connection = getPOSTConnection(url, data);
		br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		br.close();

	}

	private String getStatPostString(Stat s) {
		String hostname;
		try {
			 hostname = java.net.InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			hostname = "unknown";
		}

		return
				"port=25565&"+
				"player="+s.getName()+"&"+
				"rating="+s.getRating()+"&"+
				"maxrating="+s.getMaxRating()+"&"+
				"wins="+s.getWins()+"&"+
				"ties="+s.getTies()+"&"+
				"losses="+s.getLosses()+"&"+
				"streak="+s.getStreak()+"&"+
				"maxstreak="+s.getMaxStreak()+"&"+
				"count="+s.getCount()+"&"+
				"flags="+s.getFlags()+"&"
				;
	}


	private String getDeathPostString(Stat s, String killer, String killee, String weapon) {
		return
				"port=25565&"+
				"killer="+s.getName()+"&"+
				"killee="+killee+"&"+
				"weapon="+weapon
				;
	}
	private HttpURLConnection getPOSTConnection(URL dataurl, String postdata) throws IOException {
		Log.debug("daturl=" +dataurl);
		Log.debug("postdata=" +postdata);
		final HttpURLConnection connection = (HttpURLConnection) dataurl.openConnection();
		/// Set the POST settings
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setAllowUserInteraction(false);
//		String postdata = this.getDataString();
		connection.setRequestProperty("Content-type", "text/xml; charset=" + "UTF-8");
		connection.setRequestProperty("Content-length",String.valueOf(postdata.length()));
		/// Make sure we can timeout eventually
		connection.setConnectTimeout(conTimeout);
		connection.setReadTimeout(readTimeout);

		DataOutputStream out = new DataOutputStream(connection.getOutputStream());
		out.writeBytes(postdata);
		out.flush();
		out.close();
		return connection;
	}

	public void sleep(long millis){
		try {Thread.sleep(millis);} catch (InterruptedException e) {e.printStackTrace();}
	}
}
