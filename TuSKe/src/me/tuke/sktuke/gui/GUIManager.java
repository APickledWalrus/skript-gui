package me.tuke.sktuke.gui;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Parameter;
import me.tuke.sktuke.TuSKe;

public class GUIManager {
	private HashMap<Inventory, HashMap<Integer, GUI[]>> invs = new HashMap<>();
	
	public boolean isGUI(Inventory inv, int slot){
		return invs.containsKey(inv) && invs.get(inv).containsKey(slot);
	}
	public boolean hasGUI(Inventory inv){
		return invs.containsKey(inv);
	}
	public GUI getGUI(Inventory inv, int slot, ClickType ct){
		return isGUI(inv, slot) ? getGUI(invs.get(inv).get(slot), ct) : null;
	}
	public void newGUI(Inventory inv, int slot, ItemStack item, GUI gui){
		addToListener(inv, slot, item, gui);
	}	
	private GUI getGUI(GUI[] guis, ClickType ct){
		int index = getIndex(ct);
		return guis[index] != null? guis[index] : guis[0];
	}
	private void addToListener(Inventory inv, int slot, ItemStack item, GUI gui){
		GUI[] guis2 = null;
		HashMap<Integer,GUI[]> guislot2 = new HashMap<>();
		if (invs.containsKey(inv) && (guislot2 = invs.get(inv)).containsKey(slot)){
			guislot2.get(slot)[getIndex(gui.getClickType())] = gui;
		} else {
			guis2 = new GUI[ClickType.values().length - 2];
			guis2[getIndex(gui.getClickType())] = gui;
			guislot2.put(slot, guis2);
			
		}
		invs.put(inv, guislot2);
		inv.setItem(slot, item);
	}
	public void remove(Inventory inv, int slot){
		inv.setItem(slot, new ItemStack(Material.AIR));
		HashMap<Integer, GUI[]> map = invs.get(inv);
		map.remove(slot);
		if (map.size() > 0)
			invs.put(inv, map);
		else
			invs.remove(inv);
	}
	public void removeAll(Inventory inv){
		for (int slot : invs.get(inv).keySet())
			inv.setItem(slot, new ItemStack(Material.AIR));
		invs.remove(inv);
		
	}
	public void clearAll(){
		for (Inventory inv : invs.keySet())
			for (Integer slot : invs.get(inv).keySet())
				inv.setItem(slot, new ItemStack(Material.AIR));
		
				
		invs.clear();
	}
	public Object[][] getParam(Function<?> f, List<Expression<?>> param, Event e){
		int max = f.getParameters().length < param.size() ? f.getParameters().length : param.size() < 1 ? 1: param.size();
		Object[][] params = new Object[max][];
		if (param.size() > 0)
			for (int x = 0; x < max; x++)
				if (x < param.size() && param.get(x) != null)
					params[x] = param.get(x).getArray(e);
		if (params[0] == null){
			Expression<?> def = getDefault(f.getParameter(0));
			if (def != null)
				params[0] = def.getArray(e);
		}
		return params;
	}
	public boolean isAllowedType(ClickType ct){
		if (ct != null)
			switch(ct){
			case UNKNOWN:
			case WINDOW_BORDER_RIGHT:
			case WINDOW_BORDER_LEFT:
			case CREATIVE:
				return false;
			default:
				break;
		}
		return true;
	}
	private Expression<?> getDefault(Parameter<?> param){
		Field field = null;
		
		try {
			field = Parameter.class.getDeclaredField("def");
			field.setAccessible(true);
			return (Expression<?>) field.get(param);
		}catch(Exception e){
			return null;
		}
		
	}
	public void runCommand(final CommandSender sender, String cmd, String perm){
		if (sender != null && cmd != null){
			if (sender instanceof Player && perm != null && !sender.isOp()){
				sender.addAttachment(TuSKe.getInstance(), perm, true, 0);
			}
			if (cmd.startsWith("/"))
				cmd = cmd.substring(1);
			Skript.dispatchCommand(sender, cmd);
		}
	}
	private int getIndex(ClickType ct){
		if (ct == null)
			return 0;
		int index = ct.ordinal() + 1;
		if (index > 6)
			index -=2;
		return index;
	}
}
