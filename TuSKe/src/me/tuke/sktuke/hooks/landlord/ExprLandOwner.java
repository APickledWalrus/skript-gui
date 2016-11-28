package me.tuke.sktuke.hooks.landlord;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nullable;

import com.jcdesimp.landlord.persistantData.LowOwnedLand;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

public class ExprLandOwner extends SimplePropertyExpression<LowOwnedLand, OfflinePlayer>{

	@Override
	public Class<? extends OfflinePlayer> getReturnType() {
		return OfflinePlayer.class;
	}

	@SuppressWarnings("deprecation")
	@Override
	@Nullable
	public OfflinePlayer convert(LowOwnedLand ol) {
		return Bukkit.getOfflinePlayer(ol.getOwnerUsername());
	}

	@Override
	protected String getPropertyName() {
		return "land[lord] owner";
	}

}
