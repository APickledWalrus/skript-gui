package me.tuke.sktuke.listeners;

import java.util.HashMap;
import java.util.UUID;

import ch.njol.skript.bukkitutil.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.tuke.sktuke.TuSKe;

public class OnlineStatusCheck implements Listener{
	

	private static HashMap<UUID, Long> players = new HashMap<>();
	private TuSKe instance;
	public OnlineStatusCheck(TuSKe instance){
		this.instance = instance;
		if (!instance.getConfig().isSet("disabled.online time check") || !instance.getConfig().getBoolean("disabled.online time check"))
			for (Player p: PlayerUtils.getOnlinePlayers())
				players.put(p.getUniqueId(), System.currentTimeMillis());
	}
	
	@EventHandler
	public void OnPlayerLogin(final PlayerLoginEvent e){
		UUID u = e.getPlayer().getUniqueId();
		if (!players.containsKey(u))
			players.put(u, System.currentTimeMillis());
		
	}

	@EventHandler
	public void OnPlayerQuit(final PlayerQuitEvent e){
		UUID u = e.getPlayer().getUniqueId();
		if (players.containsKey(u))
			Bukkit.getScheduler().runTaskLater(instance, () -> players.remove(u), 1L);
		
	}
	public static Long getTime(Player p){
		if (p == null)
			return 0L;
		UUID u = p.getUniqueId();
		if (players.containsKey(u))
			return players.get(u);
		Long now = System.currentTimeMillis();
		players.put(u, now);
		return now;
	}
	
	public static void setTime(Player p, Long time){
		if (p == null || time == null)
			return;
		if (time <= System.currentTimeMillis())
			players.put(p.getUniqueId(), time);
	}

}
