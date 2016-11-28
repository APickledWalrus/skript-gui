package me.tuke.sktuke.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.MarriageAPI;
import com.lenis0012.bukkit.marriage2.config.Settings;

import me.tuke.sktuke.events.customevent.DivorceEvent;
import me.tuke.sktuke.events.customevent.MarryEvent;

public class MarryCommand implements Listener{
	
	private Marriage marriage = MarriageAPI.getInstance();
	@EventHandler
	public void onMarryCommand(PlayerCommandPreprocessEvent e){
		String[] arg = e.getMessage().replaceAll("/", "").split(" ");
		Player player = e.getPlayer();
		String cmd = arg[0].toLowerCase();
		if (arg.length > 1){
			if (player != null && cmd == "marry"){
				Player player1 = Bukkit.getPlayer(arg[1]);
			    MPlayer mp1 = this.marriage.getMPlayer(player1.getUniqueId());
				if (((Boolean)Settings.ENABLE_PRIEST.value()).booleanValue())
			    {
					Player player2 = Bukkit.getPlayer(arg[2]);
					if (player2 == null)
						return;
					MPlayer mp2 = this.marriage.getMPlayer(player2.getUniqueId());
			      
					if ((mp1.isMarried()) || (mp2.isMarried()))
						return;
			      
					MPlayer mp = this.marriage.getMPlayer(player.getUniqueId());
					if (!mp.isPriest())
						return;
			      
					MarryEvent me = new MarryEvent(player1, player2, player);
		    		Bukkit.getServer().getPluginManager().callEvent(me);
					if (me.isCancelled()){
						e.setCancelled(true);
						return;
					}
			    }
			    else
			    {
			    	Player target = player1;
			    	if (target.getName().equalsIgnoreCase(player.getName()))
			    		return;
			    	MPlayer mPlayer = this.marriage.getMPlayer(player.getUniqueId());
			    	if (mPlayer.isMarried())
			    		return;
			    	MPlayer mTarget = this.marriage.getMPlayer(target.getUniqueId());
			    	if (mTarget.isMarried())
			    		return;
			    	if (mPlayer.isMarriageRequested(target.getUniqueId()))
			    	{
			    		if (this.marriage.dependencies().isEconomyEnabled() )
			    			return;
			    		MarryEvent me = new MarryEvent(player, player1);
			    		Bukkit.getServer().getPluginManager().callEvent(me);
						if (me.isCancelled()){
							e.setCancelled(true);
							return;
						}
			    	}
			    }
			} else if (!e.isCancelled() && player != null && arg[1].toLowerCase().equals("divorce") &&  cmd.equals("marry")){
				MPlayer mPlayer = this.marriage.getMPlayer(player.getUniqueId());
			    if (!mPlayer.isMarried())
			    	return;
			    if (this.marriage.dependencies().isEconomyEnabled())
			    	return;
	    		DivorceEvent de = new DivorceEvent(player);
	    		Bukkit.getServer().getPluginManager().callEvent(de);
				if (de.isCancelled()){
					e.setCancelled(true);
					return;
				}
				
			}
		}
	}
}
	

