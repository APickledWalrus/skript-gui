package me.tuke.sktuke.hooks.simpleclans;

import ch.njol.skript.SkriptAddon;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import me.tuke.sktuke.util.SimpleType;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.events.*;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * @author Tuke_Nuke on 10/04/2017
 */
public class SimpleClansRegister {
	public SimpleClansRegister(SkriptAddon tuske) {
		types();
		eventValues();
		try {
			tuske.loadClasses(this.getClass().getPackage().getName(), "events", "conditions", "effects", "expressions");
		} catch (Exception e) {

		}
	}
	private void types() {
		new SimpleType<Clan>(Clan.class, "clan"){
			@Override
			@Nullable
			public Clan parse(String s, ParseContext context) {
				return null;
			}
			@Override
			public String toString(Clan c, int flags) {
				return c.getName().toString();
			}
			@Override
			public String toVariableNameString(Clan c) {
				return c.toString().toLowerCase();

			}};
	}
	private void eventValues() {
		EventValues.registerEventValue(CreateClanEvent.class, Player.class,
				new Getter<Player, CreateClanEvent>() {
					@Override
					public Player get(CreateClanEvent event) {
						return event.getClan().getLeaders().get(0).toPlayer();
					}
				}, 0);
		EventValues.registerEventValue(CreateClanEvent.class, Clan.class,
				new Getter<Clan, CreateClanEvent>() {
					@Override
					public Clan get(CreateClanEvent event) {
						return event.getClan();
					}
				}, 0);

		EventValues.registerEventValue(DisbandClanEvent.class, Clan.class,
				new Getter<Clan, DisbandClanEvent>() {
					@Override
					public Clan get(DisbandClanEvent event) {
						return event.getClan();
					}
				}, 0);

		EventValues.registerEventValue(AllyClanAddEvent.class, Clan.class,
				new Getter<Clan, AllyClanAddEvent>() {
					@Override
					public Clan get(AllyClanAddEvent event) {
						return event.getClanFirst();
					}
				}, 0);

		EventValues.registerEventValue(AllyClanRemoveEvent.class, Clan.class,
				new Getter<Clan, AllyClanRemoveEvent>() {
					@Override
					public Clan get(AllyClanRemoveEvent event) {
						return event.getClanFirst();
					}
				}, 0);

		EventValues.registerEventValue(RivalClanAddEvent.class, Clan.class,
				new Getter<Clan, RivalClanAddEvent>() {
					@Override
					public Clan get(RivalClanAddEvent event) {
						return event.getClanFirst();
					}
				}, 0);

		EventValues.registerEventValue(RivalClanRemoveEvent.class, Clan.class,
				new Getter<Clan, RivalClanRemoveEvent>() {
					@Override
					public Clan get(RivalClanRemoveEvent event) {
						return event.getClanFirst();
					}
				}, 0);

		EventValues.registerEventValue(PlayerPromoteEvent.class, Player.class,
				new Getter<Player, PlayerPromoteEvent>() {
					@Override
					public Player get(PlayerPromoteEvent event) {
						return event.getClanPlayer().toPlayer();
					}
				}, 0);

		EventValues.registerEventValue(PlayerDemoteEvent.class, Player.class,
				new Getter<Player, PlayerDemoteEvent>() {
					@Override
					public Player get(PlayerDemoteEvent event) {
						return event.getClanPlayer().toPlayer();
					}
				}, 0);
	}
}
