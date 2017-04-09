package me.tuke.sktuke.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.tuke.sktuke.TuSKe;
import me.tuke.sktuke.events.customevent.PlayerStartsMoveEvent;
import me.tuke.sktuke.events.customevent.PlayerStopsMoveEvent;
import me.tuke.sktuke.util.PlayerMoves;

public class PlayerMovesCheck implements Listener{
	
	static boolean loaded;
	private final TuSKe pl;
	public PlayerMovesCheck(TuSKe pl){
		this.pl = pl;
		loaded = false;
		
	}
	
	@EventHandler
	public void OnPlayerMoves(PlayerMoveEvent e){
		if (e.isCancelled() || !isLoaded())
			return;
		final Player p = e.getPlayer();
		final Location l = e.getTo();
		final PlayerMoves pm = PlayerMoves.getPlayerM(p);
		if (pm.isStopped()){
			if ( l.distance(p.getLocation()) > 0.01D && (Double.compare(l.getX(), p.getLocation().getX()) != 0 || Double.compare(l.getY(), p.getLocation().getY()) != 0 || Double.compare(l.getZ(), p.getLocation().getZ()) != 0)){
				
				PlayerStartsMoveEvent ps = new PlayerStartsMoveEvent(p, e.getFrom());
				Bukkit.getPluginManager().callEvent(ps);		
				pm.setStopped(false);
				int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(pl, new Runnable(){
				int max = 0;
				Location lee = l;
					@Override
					public void run() {
						if (!pm.isStopped() && Double.compare(p.getLocation().getX(), lee.getX()) == 0 && Double.compare(p.getLocation().getY(), lee.getY()) == 0 && Double.compare(p.getLocation().getZ(), lee.getZ()) == 0){
							max++;
						} else if (max != 0){
							max = 0;
						}
						if (max > 4){	
							pm.setStopped(true);
							PlayerStopsMoveEvent pst = new PlayerStopsMoveEvent(p);
							Bukkit.getPluginManager().callEvent(pst);
							Bukkit.getScheduler().cancelTask(pm.getID());
							pm.setID(0);
						}
						lee = p.getLocation();
						
					}}, 2, 2);
				pm.setID(id);
			}
		}		
	}
	public static void setLoaded(boolean value){
		loaded = value;
	}
	public static boolean isLoaded(){
		return loaded;
	}
}
