package me.tuke.sktuke.hooks.marriage;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.MarriageAPI;

import javax.annotation.Nullable;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

public class ExprPartnerOf extends SimplePropertyExpression <Player, OfflinePlayer>{

	@Override
	public Class<OfflinePlayer> getReturnType() {
		return OfflinePlayer.class;
	}

	@Override
	@Nullable
	public OfflinePlayer convert(Player p) {
		Marriage marry = (Marriage) MarriageAPI.getInstance();
		if (p != null){
			if (marry.getMPlayer(p.getUniqueId()).isMarried())
				return (OfflinePlayer) Bukkit.getOfflinePlayer(marry.getMPlayer(p.getUniqueId()).getPartner().getUniqueId());
			return null;
		}
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "partner";
	}

}
