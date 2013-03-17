package test.mc.alk.tracker;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import junit.framework.TestCase;
import mc.alk.tracker.controllers.SignController;
import mc.alk.tracker.objects.SignType;
import mc.alk.tracker.objects.StatSign;
import mc.alk.tracker.serializers.SignSerializer;
import mc.alk.util.SerializerUtil;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.junit.AfterClass;

/**
 *
 * To use this you must supply the test version of getLocation for the parsing of the location
 *
 */
public class TestSignConfig extends TestCase{
	File testDir = new File("test_files");
	File signFile = new File("test_files/signs.yml");
	final int n = 10;

	@Override
	public void setUp(){
		ConfigurationSerialization.registerClass(StatSign.class);
		SerializerUtil.TESTING = true;
		if (!testDir.exists()){
			assertTrue(testDir.mkdir());
		}
		if (!signFile.exists()){
			try {
				signFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				fail();
			}
		}
	}

	@AfterClass
	public void tearDownAfterClass(){
		signFile.delete();
		testDir.delete();
	}

	public void testSaveConfig(){
		SignController sc = new SignController();
		SignSerializer ser = new SignSerializer(sc);
		for (int i=0;i<n;i++){
			sc.addSign(new StatSign(new Location(null,i,i,i), SignType.TOP));
		}

		ser.setConfig(signFile);
		ser.saveAll();
	}

	public void testLoadConfig(){
		SignController sc = new SignController();
		SignSerializer ser = new SignSerializer(sc);
		ser.setConfig(signFile);
		ser.loadAll();
		Map<String,StatSign> signs = sc.getTopSigns();
		assertEquals(n,signs.size());
	}
}
