package me.tuke.sktuke.expressions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import javax.annotation.Nullable;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

public class ExprPlayerVersion extends SimplePropertyExpression<Player, String>{
	
	final boolean isViaversion = Bukkit.getServer().getPluginManager().isPluginEnabled("ViaVersion");
	final boolean isProtocolSupport = Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolSupport");

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	@Nullable
	public String convert(Player p) {
		if (p == null)
			return null;
		if (isViaversion){
			int i = us.myles.ViaVersion.api.ViaVersion.getInstance().getPlayerVersion(p);
			return us.myles.ViaVersion.api.protocol.ProtocolVersion.getProtocol(i).getName().replace(".x", "");
		} else if (isProtocolSupport)
			return protocolsupport.api.ProtocolSupportAPI.getProtocolVersion(p).getName();

		
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "minecraft version";
	}

}
