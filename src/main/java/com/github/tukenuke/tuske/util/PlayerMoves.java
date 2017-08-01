package com.github.tukenuke.tuske.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class PlayerMoves {
	
	private Player p;
	private boolean isStopped = true;
	private int id;
	static List<PlayerMoves> pmm = new ArrayList<PlayerMoves>();
	
	private PlayerMoves(Player p, int i, boolean isStopped){
		this.p = p;
		this.isStopped = isStopped;
		this.id = i;
	}
	public int getID(){
		return id;
	}
	public boolean isStopped(){
		return isStopped;
	}
	public void setID(int ID){
		id = ID;
	}
	public void setStopped(boolean b){
		this.isStopped = b;
	}
	public Player getPlayer(){
		return this.p;
	}	
	public static PlayerMoves getPlayerM(Player p){
		if (p != null){
			for (PlayerMoves pm: pmm){
				if (pm.getPlayer().equals(p))
					return pm;
			}
			PlayerMoves pm = new PlayerMoves(p, 0, true);
			pmm.add(pm);
			return pm;
			
		}
		return null;
		
	}

}
