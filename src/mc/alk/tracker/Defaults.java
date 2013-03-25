package mc.alk.tracker;

public class Defaults {
	public static final int SAVE_EVERY_X_SECONDS = 200;

	public static final String PVP_INTERFACE = "PvP";
	public static final String PVE_INTERFACE = "PvE";

	public static int STREAK_EVERY = 15;
	public static int RAMPAGE_TIME = 7000;

	public static boolean DISABLE_PVP_MESSAGES = false;
	public static boolean DISABLE_PVE_MESSAGES = false;

	public final static boolean DEBUG = false;
	public static boolean DEBUG_ADD_RECORDS = false;

	public static int RADIUS = -1;

	/// Message Defaults

	public static String MSG_TOP_HEADER = "&4Top &6{interfaceName}&4 {stat} TeamSize:{teamSize}";
	public static String MSG_TOP_BODY ="&e#{rank}&4 {name} - {wins}:{losses}&6[{rating}]";

	public static final String ADMIN_PERM = "tracker.admin";
}
