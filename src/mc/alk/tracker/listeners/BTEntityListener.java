package mc.alk.tracker.listeners;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import mc.alk.tracker.Defaults;
import mc.alk.tracker.Tracker;
import mc.alk.tracker.TrackerInterface;
import mc.alk.tracker.controllers.ConfigController;
import mc.alk.tracker.controllers.MessageController;
import mc.alk.tracker.controllers.TrackerController;
import mc.alk.tracker.objects.Stat;
import mc.alk.tracker.objects.WLT;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


public class BTEntityListener implements Listener{
	//	private static final int DAMAGE_TIMEOUT = 60000;
	static JavaPlugin plugin;

	ConcurrentHashMap<String,Long> lastDamageTime = new ConcurrentHashMap<String,Long>();
	ConcurrentHashMap<String,RampageStreak> lastKillTime = new ConcurrentHashMap<String,RampageStreak>();
	static int streakEvery = 15;
	static int rampageTime = 7000;
	Random r = new Random();
	TrackerInterface playerTi;
	TrackerInterface worldTi;
	int count = 0;

	class RampageStreak{
		Long time; int nkills;
		public RampageStreak(Long t, int nk){this.time = t; this.nkills = nk;}
	}

	public BTEntityListener(){
		playerTi = Tracker.getInterface(Defaults.PVP_INTERFACE);
		worldTi = Tracker.getInterface(Defaults.PVE_INTERFACE);
		plugin = Tracker.getSelf();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDeath(EntityDeathEvent event) {
		String target, killer;
		boolean targetPlayer = false, killerPlayer = false;
		boolean isMelee = true;
		ItemStack killingWeapon = null;
		if (!ConfigController.getBoolean("showBukkitDeathMessages") && event instanceof PlayerDeathEvent){
			PlayerDeathEvent pde = (PlayerDeathEvent) event;
			pde.setDeathMessage(""); /// Set to none, will cancel all non pvp messages
		}
		/// Get our hapless target
		Entity targetEntity = event.getEntity();
		if (targetEntity instanceof Player){
			target = ((Player)targetEntity).getName();
			targetPlayer = true;
		} else if (targetEntity instanceof Tameable){
			target = "Tamed" + targetEntity.getType().getName();
		} else {
			target = targetEntity.getType().getName();
		}
		//		FileLogger.log("onEntityDeath " + target +" targetPlayer=" + targetPlayer +"   tracking=" + TrackerController.dontTrack(target));
		/// Should we be tracking this person
		if (targetPlayer && TrackerController.dontTrack(target))
			return;

		EntityDamageEvent lastDamageCause = event.getEntity().getLastDamageCause();

		/// Get our killer
		if (lastDamageCause instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) lastDamageCause;
			if (edbee.getDamager() instanceof Player) { /// killer is player
				Player pk = ((Player) edbee.getDamager());
				killer = pk.getName();
				killerPlayer = true;
				killingWeapon = pk.getItemInHand();
			} else if (edbee.getDamager() instanceof Projectile) { /// we have some sort of projectile
				isMelee = false;
				Projectile proj = (Projectile) edbee.getDamager();
				if (proj.getShooter() instanceof Player){ /// projectile was shot by a player
					killerPlayer = true;
					killer = ((Player) proj.getShooter()).getName();
				} else if (proj.getShooter() != null){ /// projectile shot by some mob, or other source
					killer = proj.getShooter().getType().getName();
				} else {
					killer = "unknown"; /// projectile was null?
				}
			} else { /// Killer is not a player
				killer = edbee.getDamager().getType().getName();
			}			
		} else {
			if (lastDamageCause == null || lastDamageCause.getCause() == null)
				killer  = "unknown";
			else 
				killer = lastDamageCause.getCause().name();
		}
		//		FileLogger.log("onEntityDeath " + target +" targetPlayer=" + targetPlayer +"   tracking=" + TrackerController.dontTrack(target)+
		//				"  killerPlayer=" + killerPlayer +"   killer=" + killer);

		if (killerPlayer && TrackerController.dontTrack(killer))
			return;
		/// Decide what to do
		if (targetPlayer && killerPlayer){
			if (ConfigController.getBoolean("trackPvP",true))
				addRecord(playerTi,killer,target,WLT.WIN,true);

			if (ConfigController.getBoolean("sendPVPDeathMessages",true))
				MessageController.sendDeathMessage(getDeathMessage(killer,target,isMelee,playerTi,killingWeapon));
		} else if (!targetPlayer && !killerPlayer){ /// mobs killing each other, or dying by traps
			/// Do nothing
		} else if (ConfigController.getBoolean("trackPvE",true)){ /// One player, One other
			if (!killerPlayer){
				killer = killer.replaceAll("Craft", "");}
			if (!targetPlayer){
				target = target.replaceAll("Craft", "");}
			addRecord(worldTi, killer,target,WLT.WIN, false);
		}		
	}

	public String getDeathMessage(String p1, String p2, boolean isMeleeDeath, TrackerInterface ti, ItemStack killingWeapon){
		/// Check for a rampage 
		try {
			RampageStreak lastKill = lastKillTime.get(p1);
			long now = System.currentTimeMillis() ;
			if (lastKill != null && now - lastKill.time < rampageTime){
				lastKill.nkills++;
				lastKill.time = now;
				return MessageController.getMessage("onARoll", p1,p2, lastKill.nkills);
			} else {
				lastKillTime.put(p1, new RampageStreak(now, 1));
			}
		} catch (Exception e){
			/// in the unlikely case of a Concurrent modification exception, lets not crash 
			/// but instead send a normal message
		}

		/// If a normal streak
		/// Player has a multiple of x, send they are on a streak message
		Stat stat = ti.loadPlayerRecord(p1);
		final int streak = stat.getStreak();
		if (streak != 0 && streakEvery != 0 && streak % streakEvery == 0){ /// they are on a streak
			return MessageController.getMessage("onAStreak", p1, p2, streak);
		} else { /// Display a normal kill message
			return isMeleeDeath ? MessageController.getMeleeMessage(p1, p2) : MessageController.getRangeMessage(p1, p2);  
		}
	}


	public static void addRecord(final TrackerInterface ti,final String e1, final String e2, 
			final WLT record, final boolean saveIndividualRecord){
		Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
			@Override
			public void run() {
				ti.addPlayerRecord(e1, e2, record, saveIndividualRecord);
			}
		});
	}
}
