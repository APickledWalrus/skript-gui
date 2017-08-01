package com.github.tukenuke.tuske.listeners;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import com.github.tukenuke.tuske.hooks.legendchat.LegendchatConfig;

public class TagChat implements Listener{
	
	private LegendchatConfig config;
	public TagChat(LegendchatConfig config){
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
