package me.tuke.sktuke.hooks.simpleclans.expressions;

import me.tuke.sktuke.util.Registry;
import org.bukkit.entity.Player;
import javax.annotation.Nullable;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class ExprPlayerClan  extends SimplePropertyExpression<Player, Clan>{
	{
		Registry.newProperty(ExprPlayerClan.class, "clan", "player");
	}

	@Override
	public Class<? extends Clan> getReturnType() {
		return Clan.class;
	}

	@Override
	@Nullable
	public Clan convert(Player p) {
		ClanPlayer clan = SimpleClans.getInstance().getClanManager().getClanPlayer(p);
	    if (clan != null) 
	      return clan.getClan();
	    return null;
	}

	@Override
	protected String getPropertyName() {
		return "clan";
	}
}
