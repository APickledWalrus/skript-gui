package com.github.tukenuke.tuske.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import javax.annotation.Nullable;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

@Name("Minecraft Version")
@Description("Returns the minecraft version of {{types|Player|player}}.")
@Examples({
		"on join:",
		"if minecraft version of player is \"1.9\":",
		"send \"You're joining with version %mc version of player%!\""})
@Since("1.0 (ProtocolSupport), 1.0.5 (ViaVersion)")
public class ExprPlayerVersion extends SimplePropertyExpression<Player, String>{
	private static final boolean isViaversion = Bukkit.getServer().getPluginManager().isPluginEnabled("ViaVersion");
	private static final boolean isProtocolSupport = Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolSupport");

	static {
		if (isViaversion || isProtocolSupport)
			Registry.newProperty(ExprPlayerVersion.class, "(mc|minecraft) version", "player");
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	@Nullable
	public String convert(Player p) {
		if (isViaversion){
			//int i = us.myles.ViaVersion.api.ViaVersionAPI.getPlayerVersion(p);
			//return us.myles.ViaVersion.api.protocol.ProtocolVersion.getProtocol(i).getName().replace(".x", "");
		} else if (isProtocolSupport)
			return protocolsupport.api.ProtocolSupportAPI.getProtocolVersion(p).getName();
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "minecraft version";
	}

}
