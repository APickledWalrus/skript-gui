package me.tuke.sktuke.nms;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.ChunkCoordIntPair;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;

public class M_1_8_R3 implements NMS{

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
			net.minecraft.server.v1_8_R3.ItemStack Item = CraftItemStack.asNMSCopy(i);
			EntityPlayer entity = ((CraftPlayer) p).getHandle();
			entity.drop(Item, false);
		}
		
	}

	@Override
	public void setFastBlock(World world, int x, int y, int z, int blockId, byte data) {
		net.minecraft.server.v1_8_R3.World w = ((CraftWorld) world).getHandle();
        net.minecraft.server.v1_8_R3.Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
        BlockPosition bp = new BlockPosition(x, y, z);
        int combined = blockId + (data << 12);
        IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(combined);
        chunk.a(bp, ibd);
		
	}
	@Override
	public void updateChunk(Chunk c){
        int diffx, diffz;
		int view = Bukkit.getServer().getViewDistance() << 4;
		net.minecraft.server.v1_8_R3.World world2 = ((CraftChunk) c).getHandle().world;
		for (EntityHuman ep : world2.players) {
			diffx = (int) Math.abs(ep.locX - (c.getX() << 4));
			diffz = (int) Math.abs(ep.locZ - (c.getZ() << 4));
			if (diffx <= view && diffz <= view) {
				((EntityPlayer)ep).chunkCoordIntPairQueue.add(new ChunkCoordIntPair(c.getX(), c.getZ()));
			}
		}
	}

}
