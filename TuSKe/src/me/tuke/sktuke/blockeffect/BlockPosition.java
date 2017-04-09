package me.tuke.sktuke.blockeffect;

import org.bukkit.Location;
public class BlockPosition {
	
	public double x;
	public double y;
	public double z;
	public int id;
	public byte data;
	
	public BlockPosition(double x, double y, double z, int id, byte data){
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
		this.data = data;
		
	}
	
	@SuppressWarnings("deprecation")
	public void setBlock(Location loc){
		loc.add(x, y, z);
		loc.getBlock().setTypeIdAndData(id, data, false);
	}
	@Override
	public boolean equals(Object bp){
		if (bp instanceof BlockPosition)
			return Double.compare(x, ((BlockPosition) bp).x) == 0 && Double.compare(y, ((BlockPosition) bp).y) == 0 && Double.compare(z, ((BlockPosition) bp).z) == 0 && id == ((BlockPosition) bp).id && data == ((BlockPosition) bp).data;
		return false;
		
	}

}
