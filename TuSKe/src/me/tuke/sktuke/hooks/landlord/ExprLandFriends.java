package me.tuke.sktuke.hooks.landlord;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;

import java.util.ArrayList;

import javax.annotation.Nullable;

import com.jcdesimp.landlord.persistantData.Friend;
import com.jcdesimp.landlord.persistantData.LowOwnedLand;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprLandFriends extends SimpleExpression<OfflinePlayer>{

	private Expression<LowOwnedLand> ol;
	@Override
	public Class<? extends OfflinePlayer> getReturnType() {
		return OfflinePlayer.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.ol = (Expression<LowOwnedLand>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "land friends of " + this.ol;
	}

	@SuppressWarnings("deprecation")
	@Override
	@Nullable
	protected OfflinePlayer[] get(Event e) {
		LowOwnedLand ol = this.ol.getSingle(e);
		ArrayList<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
		if (ol != null){
			for (Friend f : ol.getFriends())
				players.add(Bukkit.getOfflinePlayer(f.getName()));
			return players.toArray(new OfflinePlayer[players.size()]);
		}
		return null;
	}
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		LowOwnedLand ol = this.ol.getSingle(e);
		//List<Friend> friends = ol.getFriends();
		OfflinePlayer[] ob = null;
		if (mode != ChangeMode.RESET || mode != ChangeMode.DELETE)
			ob = (OfflinePlayer[]) delta;
		for (Friend f : ol.getFriends())
			Bukkit.broadcastMessage(f.getName());
		if (ol != null){
			switch (mode){
				case RESET:
				case DELETE: 
					for (Friend f : ol.getFriends())
						ol.removeFriend(f); break;
				case SET:
					for (Friend f : ol.getFriends())
						ol.removeFriend(f);
				case ADD:
					if (ob != null)
						for (OfflinePlayer oop : ob)
							ol.addFriend(Friend.friendFromOfflinePlayer(oop.getName())); 
					break;
							
				case REMOVE:
					for (OfflinePlayer oop : ob)
						ol.removeFriend(Friend.friendFromOfflinePlayer(oop.getName())); break;
				default:
					break;
			}
			Bukkit.broadcastMessage("Hei");
			for (Friend f : ol.getFriends())
				Bukkit.broadcastMessage(f.getName());
			ol.save();
			for (Friend f : ol.getFriends())
				Bukkit.broadcastMessage(f.getName() + " dps");
			
		}
	}

	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode != ChangeMode.REMOVE_ALL)
			return CollectionUtils.array(OfflinePlayer[].class);
		return null;
		
	}
}
