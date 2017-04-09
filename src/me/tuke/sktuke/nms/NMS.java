package me.tuke.sktuke.nms;

import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface NMS {
	
	Player getToPlayer(OfflinePlayer OfflinePlayer);
	
	void makeDrop(Player Player, ItemStack ItemStack);
	
	//The source of the code: https://www.spigotmc.org/threads/solved-nms-settingblock-fast-spigot-1-8.45061/#post-511020
	void setFastBlock(World world, int x, int y, int z, int blockId, byte data);
	
	//This also has a source of this, I didn't find it yet.
	void updateChunk(Chunk c);
}
