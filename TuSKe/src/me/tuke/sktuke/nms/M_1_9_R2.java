package me.tuke.sktuke.nms;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R2.CraftServer;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.MinecraftServer;
import net.minecraft.server.v1_9_R2.PlayerInteractManager;

public class M_1_9_R2 implements NMS{

	@Override
	public Player getToPlayer(OfflinePlayer p) {
		if (!p.isOnline() && p.hasPlayedBefore()){
			MinecraftServer sv = ((CraftServer)Bukkit.getServer()).getServer();
			EntityPlayer newPlayer = new EntityPlayer(sv, sv.getWorldServer(0), new GameProfile(p.getUniqueId(), p.getName()), new PlayerInteractManager(sv.getWorldServer(0)));
			Player player = newPlayer.getBukkitEntity();
			if (player != null){
				player.loadData();
				return player;
			}
		}
		return null;
	}

	@Override
	public void makeDrop(Player p, ItemStack i) {
		if (p != null && i != null){
			net.minecraft.server.v1_9_R2.ItemStack Item = CraftItemStack.asNMSCopy(i);
			EntityPlayer entity = ((CraftPlayer) p).getHandle();
			entity.drop(Item, false);
		}
		
	}

	@Override
	public void setFastBlock(World world, int x, int y, int z, int blockId, byte data) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void updateChunk(org.bukkit.Chunk c){
		
	}

}