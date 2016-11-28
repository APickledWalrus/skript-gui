package me.tuke.sktuke.listeners;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import me.tuke.sktuke.util.LegendConfig;

public class TagChat implements Listener{
	
	private LegendConfig config;
	public TagChat(LegendConfig config){
		this.config = config;
	}
	
	@EventHandler
	public void onChat(ChatMessageEvent e){
		if (config.getFile().exists())
			for (String s : config.getPlayerTags(e.getSender()).keySet()){
				e.setTagValue(s, config.getPlayerTags(e.getSender()).get(s));
			}
		
	}	
}
