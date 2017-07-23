package me.tuke.sktuke.hooks.simpleclans.events;

import ch.njol.skript.SkriptEventHandler;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SelfRegisteringSkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import me.tuke.sktuke.TuSKe;
import me.tuke.sktuke.hooks.simpleclans.ClanChatEvent;
import me.tuke.sktuke.util.ReflectionUtils;
import me.tuke.sktuke.util.Registry;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tuke_Nuke on 27/04/2017
 */
public class EvtChatClan extends SelfRegisteringSkriptEvent implements Listener{
	static {
		Registry.newEvent(EvtChatClan.class, ClanChatEvent.class, "Clan Chat", "clan chat");
	}
	private static List<Trigger> triggers = new ArrayList<>();
	private static final SimpleClans plugin = SimpleClans.getInstance();
	private static boolean lastConfigValue = true;

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		if (e.getMessage().isEmpty() || e.getPlayer() == null || plugin.getSettingsManager().isBlacklistedWorld(e.getPlayer().getWorld().getName()))
			return;
		String[] sp = e.getMessage().substring(1).split("\\s+");
		String cmd = sp[0];
		ClanPlayer cp = null;
		if ((plugin.getSettingsManager().isTagBasedClanChat())) {
			cp = plugin.getClanManager().getClanPlayer(e.getPlayer());
			if (cp == null && !cp.getTag().equalsIgnoreCase(cmd))
				return;
		} else if (!cmd.equals("."))
			return;
		if (cp == null)
			cp = plugin.getClanManager().getClanPlayer(e.getPlayer());
		if (cp != null && sp.length > 1) {
			ClanChatEvent cce = new ClanChatEvent(cp, Helper.toMessage(Helper.removeFirst(sp)));
			SkriptEventHandler.logEventStart(cce);
			for (Trigger t : triggers) {
				SkriptEventHandler.logTriggerStart(t);
				t.execute(cce);
				SkriptEventHandler.logTriggerEnd(t);
			}
			SkriptEventHandler.logEventEnd();
			if (!cce.isCancelled()) {
				e.setCancelled(true);
				plugin.getClanManager().processClanChat(cce.getClanPlayer().toPlayer(), cce.getMessage());
			}
		}
	}

	public void init() {
		if (triggers.size() == 1) {
			lastConfigValue = ReflectionUtils.getField(plugin.getSettingsManager().getClass(), plugin.getSettingsManager(), "clanChatEnable");
			if (lastConfigValue) {
				Bukkit.getPluginManager().registerEvents(this, TuSKe.getInstance());
				ReflectionUtils.setField(plugin.getSettingsManager().getClass(), plugin.getSettingsManager(), "clanChatEnable", false);
			}
		}
	}
	public void finalize() {
		if (triggers.size() == 0) {
			ReflectionUtils.setField(plugin.getSettingsManager().getClass(), plugin.getSettingsManager(), "clanChatEnable", lastConfigValue);
			HandlerList.unregisterAll(this);
		}
	}


	@Override
	public void register(Trigger trigger) {
		triggers.add(trigger);
		init();
	}

	@Override
	public void unregister(Trigger trigger) {
		triggers.remove(trigger);
		finalize();
	}

	@Override
	public void unregisterAll() {
		triggers.clear();
		finalize();
	}

	@Override
	public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
		return true;
	}

	@Override
	public String toString(Event event, boolean b) {
		return "clan chat event";
	}
}
