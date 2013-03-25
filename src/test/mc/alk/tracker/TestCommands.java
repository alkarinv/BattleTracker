package test.mc.alk.tracker;
import java.io.File;

import junit.framework.TestCase;
import mc.alk.mc.MCServer;
import mc.alk.tracker.controllers.ConfigController;
import mc.alk.tracker.controllers.TrackerImpl;
import mc.alk.tracker.executors.TrackerExecutor;
import test.mc.alk.TestServer;
import test.mc.alk.core.TestCommand;
import test.mc.alk.testbukkit.TestBukkitPlayer;

public class TestCommands extends TestCase{
	static TestServer api = new TestServer();

	@Override
	public void setUp(){
		MCServer.setInstance(api);
		File cf = new File("default_files/config.yml");
		assertTrue(cf.exists());
		ConfigController.setConfig(cf);


	}
	public void testTopCommand(){
		TestBukkitPlayer tp = new TestBukkitPlayer("testplayer1");
//		String type, String db, String urlOrPath, String table, String port, String user, String password) {
		TrackerImpl ti = new TrackerImpl("mysql", "minecraft","localhost",
				"pvp", "3306", "root","");
		TrackerExecutor exe = new TrackerExecutor(ti);
		TestCommand cmd = new TestCommand("top");
		exe.onCommand(tp, cmd, "top", new String[]{"top"});
	}
}
