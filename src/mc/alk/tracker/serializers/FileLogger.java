package mc.alk.tracker.serializers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Vector;

import mc.alk.tracker.Tracker;


public class FileLogger {
	static final String version ="1.0.1";
	static Vector<String> msgs = new Vector<String>();

	public FileLogger() {}

	public static synchronized int log(String msg) {
		try {
			Calendar cal = new GregorianCalendar();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd,hh:mm:ss");
			msgs.add(sdf.format(cal.getTime()).toString() + ","+msg+"\n");
		} catch(Exception e){
			e.printStackTrace();
		}
		return -1;
	}	
	public static synchronized int log(String node, Object... varArgs) {
		try {
			Calendar cal = new GregorianCalendar();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd,hh:mm:ss");
			StringBuilder buf = new StringBuilder();
			Formatter form = new Formatter(buf);
			form.format(node, varArgs);
			msgs.add(sdf.format(cal.getTime()).toString() + "," + buf.toString() +"\n");
			return msgs.size();
		} catch(Exception e){
			e.printStackTrace();
		}
		return -1;
	}

	public static synchronized void saveAll() {
		try {
			FileWriter fstream = new FileWriter(new File(Tracker.getSelf().getDataFolder()+"/test.log"),true);
			BufferedWriter out = new BufferedWriter(fstream);
			for (String msg : msgs){
				out.write(msg);	
			}
			msgs.clear();
			out.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}

