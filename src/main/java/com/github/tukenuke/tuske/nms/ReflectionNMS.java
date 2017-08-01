package com.github.tukenuke.tuske.nms;

import java.lang.reflect.Constructor;
import java.util.logging.Level;

import com.github.tukenuke.tuske.util.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mojang.authlib.GameProfile;

import com.github.tukenuke.tuske.TuSKe;

public class ReflectionNMS implements NMS {

	private String version = ReflectionUtils.packageVersion;
	public ReflectionNMS(){
	}

	@Override
	public Player getToPlayer(OfflinePlayer p) {
		if (!p.isOnline() && p.hasPlayedBefore()){
			try {
				Object sv = Bukkit.getServer().getClass().getDeclaredMethod("getServer").invoke(Bukkit.getServer());
				Class<?> entityPlayerClz = Class.forName("net.minecraft.server.v" + version + ".EntityPlayer");
				Class<?> nms = Class.forName("net.minecraft.server.v"+version+".MinecraftServer");
				Object worldServer = nms.getDeclaredMethod("getWorldServer", int.class).invoke(sv, 0);
				Object playerInteractManager = Class.forName("net.minecraft.server.v" + version + ".PlayerInteractManager").getConstructors()[0].newInstance(worldServer);
				GameProfile profile = new GameProfile(p.getUniqueId(), p.getName());
				Constructor<?> constructor = entityPlayerClz.getConstructors()[0];
				Object newPlayer = constructor.newInstance(sv, worldServer, profile, playerInteractManager);
				Player player = (Player) newPlayer.getClass().getDeclaredMethod("getBukkitEntity").invoke(newPlayer);
				if (player != null){
					player.loadData();
					return player;
				}
			} catch (Exception e){
				TuSKe.log(Level.WARNING,
					"An error occured with expression to get player data. It is because your server version isn't supported yet.",
					"So, report it somewhere, in Spigot or GitHub, to the developer with following details:",
					"Running version: v" + version,
					"Error details:");
				e.printStackTrace();
			}
			
		}
		return null;
	}

	@Override
	public void makeDrop(Player p, ItemStack i) {
		if (p != null && i != null){
			try {
				Class<?> craftItemClz = Class.forName("org.bukkit.craftbukkit.v"+version+".inventory.CraftItemStack");
				Object nmsItem = craftItemClz.getDeclaredMethod("asNMSCopy", ItemStack.class).invoke(null, i);
				Class<?> craftPlayerClz = Class.forName("org.bukkit.craftbukkit.v"+version+".entity.CraftPlayer");
				Object entity = craftPlayerClz.getDeclaredMethod("getHandle").invoke(p);
				Class.forName("net.minecraft.server.v"+version+".EntityHuman").getDeclaredMethod("drop", nmsItem.getClass(), boolean.class).invoke(entity, nmsItem, true);
	
			} catch (Exception e){
				TuSKe.log(Level.WARNING,
					"An error occured with effect to force a player to drop a item. It is because your server version isn't supported yet.",
					"So, report it somewhere, in Spigot or GitHub, to the developer with following details:",
					"Running version: v" + version,
					"Error details:");
				e.printStackTrace();
				
			}
		}
		
	}

	@Override
	public void setFastBlock(World world, int x, int y, int z, int blockId, byte data) {		
	}

	@Override
	public void updateChunk(Chunk c) {
	}

}
