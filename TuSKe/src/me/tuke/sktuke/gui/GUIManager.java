package me.tuke.sktuke.gui;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private HashMap<Inventory, Map<Integer, Set<GUI>>> invs = new HashMap<>();
	private HashMap<String, GroupGUI> groups = new HashMap<>();
	
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
		if (!isAllowedType(gui.getClickType()))
			gui.withClickType(null);
		//GUI gui = new GUI(rn, item2, ct); 
		addToListener(inv, slot, item, gui);
	}
	public void addToGroupGUI(String id, int slot, Expression<ItemStack> item, GUI gui){
		GroupGUI gg = groups.containsKey(id.toLowerCase()) ? groups.get(id.toLowerCase()) : new GroupGUI();
		gg.newSlot(slot, item, gui);
		if (!groups.containsKey(id.toLowerCase()))
			groups.put(id.toLowerCase(), gg);
	}
	/*public void formatGroupGUI(Event e, String id, Player p){
		if (groups.containsKey(id.toLowerCase())){
			GroupGUI gg = groups.get(id.toLowerCase());
			Inventory inv = p.getOpenInventory().getTopInventory();
			invs.put(inv, gg.getGUIs());
			//for (Set<GUI> gui : gg.getGUIs().values())
			//	for (GUI gui2 : gui)
			//		gui2.setEvent(e);
			ItemStack[] items = gg.getArrayItems(e);
			if(items.length > inv.getSize()){
				items = Arrays.copyOfRange(items, 0, inv.getSize());
			}
			
			inv.setContents(items);
		}
	}*/
	
	private GUI getGUI(Set<GUI> guis, ClickType ct){
		for (GUI gui : guis){
			if ((gui.getClickType() == null && ct == null) || (gui.getClickType() != null && ct != null && gui.getClickType().equals(ct)))
				return gui;
		}
		if (ct != null)
			return getGUI(guis, null);
		return null;
	}
	private void addToListener(Inventory inv, int slot, ItemStack item, GUI gui){
		Map<Integer, Set<GUI>> guislot = new HashMap<Integer, Set<GUI>>();
		Set<GUI> guis = new HashSet<GUI>();
		if (invs.containsKey(inv)){
			guislot = invs.get(inv);
			invs.remove(inv);
			if (guislot.containsKey(slot))
				guis = guislot.get(slot);
		}
		GUI gui2 = getGUI(guis, gui.getClickType());
		if (gui2 != null)
			guis.remove(gui2);
		guis.add(gui);
		guislot.put(slot, guis);
		invs.put(inv, guislot);
		inv.setItem(slot, item);
	}
	public void remove(Inventory inv, int slot){
		inv.setItem(slot, new ItemStack(Material.AIR));
		Map<Integer, Set<GUI>> map = invs.get(inv);
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
}
