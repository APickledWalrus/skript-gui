package me.tuke.sktuke.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.tuke.sktuke.TuSKe;

public class OnlineStatusCheck implements Listener{
	

	private static HashMap<Player, Long> players = new HashMap<Player, Long>();
	private TuSKe instance;
	public OnlineStatusCheck(TuSKe instance){
		this.instance = instance;
	}
	
	@EventHandler
	public void OnPlayerLogin(final PlayerLoginEvent e){
		if (!players.containsKey(e.getPlayer()))
			players.put(e.getPlayer(), System.currentTimeMillis());
		
	}

	@EventHandler
	public void OnPlayerQuit(final PlayerQuitEvent e){
		if (players.containsKey(e.getPlayer()))
			Bukkit.getScheduler().runTaskLater(instance, new Runnable(){

				@Override
				public void run() {
					players.remove(e.getPlayer());
					
				}}, 1L);
		
	}
	public static Long getTime(Player p){
		if (p != null && players.containsKey(p))
			return players.get(p);
		return System.currentTimeMillis();
	}
	
	public static void setTime(Player p, Long time){
		if (p != null && time <= System.currentTimeMillis())
			players.put(p, time);
	}

}
