package com.github.tukenuke.tuske.hooks.legendchat;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import br.com.devpaulo.legendchat.api.events.PrivateMessageEvent;
import br.com.devpaulo.legendchat.channels.ChannelManager;
import br.com.devpaulo.legendchat.channels.types.Channel;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.classes.Comparator;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Comparators;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.github.tukenuke.tuske.TuSKe;
import com.github.tukenuke.tuske.util.SimpleType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * @author Tuke_Nuke on 10/04/2017
 */
public class LegendchatRegister {
	public static LegendchatConfig config;
	public LegendchatRegister(SkriptAddon tuske) {
		types();
		eventValues();
		comparators();
		try {
			config =new LegendchatConfig((TuSKe) tuske.plugin);
			tuske.loadClasses(this.getClass().getPackage().getName(), "events", "conditions", "effects", "expressions");
		} catch (Exception e) {

		}

	}

	private void types() {

		new SimpleType<Channel>(Channel.class, "channel"){
			@Override
			@Nullable
			public Channel parse(String s, ParseContext context) {
				ChannelManager cm = Legendchat.getChannelManager();
				if (cm.existsChannel(s.toLowerCase()))
					return cm.getChannelByName(s.toLowerCase());
				return null;
			}
			@Override
			public String toString(Channel c, int flags) {
				return c.getName().toLowerCase();
			}
			@Override
			public String toVariableNameString(Channel c) {
				return c.toString().toLowerCase();
			}};
	}
	private void eventValues() {

		EventValues.registerEventValue(ChatMessageEvent.class, Player.class,
				new Getter<Player, ChatMessageEvent>() {
					@Override
					public Player get(ChatMessageEvent event) {
						return event.getSender();
					}
				}, 0);
		EventValues.registerEventValue(PrivateMessageEvent.class, CommandSender.class,
				new Getter<CommandSender, PrivateMessageEvent>() {
					@Override
					public CommandSender get(PrivateMessageEvent event) {
						return event.getSender();
					}
				}, 0);
	}
	private void comparators() {
		Comparators.registerComparator(Channel.class, Channel.class, new Comparator<Channel, Channel>(){

			@Override
			public Relation compare(Channel arg0, Channel arg1) {
				return Relation.get(arg0.equals(arg1));
			}

			@Override
			public boolean supportsOrdering() {
				return true;
			}
		});

	}
}
