package me.tuke.sktuke.gui;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Functions;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.TuSKe;
import me.tuke.sktuke.util.EvalFunction;

public class EffFormatGUI extends Effect{
	private int Type;
	private boolean toClose;
	private EvalFunction func;
	private Expression<String> perm;
	private Expression<String> cmd;
	private Expression<CommandSender> sender;
	private Expression<Player> p;
	private Expression<Number> s;
	private Expression<ItemStack> i;
	private Expression<?> ct;
	private Expression<ItemStack> i2;
	private Expression<Inventory> inv = null;
	private boolean runEvent;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		int max = arg.length;
		s = (Expression<Number>) arg[0];
		p = (Expression<Player>) arg[1];
		i = (Expression<ItemStack>) arg[2]/*.getConvertedExpression(ItemStack.class)*/;
		toClose = arg3.mark > 0;
		Type = arg1;
		if (arg3.mark == 2)
			inv = (Expression<Inventory>) arg[3];
		switch (arg1){
		case 4: 
			runEvent = true;
			break;
		case 1: 
			toClose = true;
			ct = arg[4];
		case 0:  break;
		case 2: 
			sender = (Expression<CommandSender>) arg[4];
			cmd = (Expression<String>) arg[5];
			perm =  arg[6] != null ? (Expression<String>) arg[6] : null;
			break;
		case 3:
			String name = arg3.regexes.get(0).group(0).replaceAll(" ","");
			String exprs = arg3.regexes.size() > 1 ? arg3.regexes.get(1).group(0) : "";
			Function<?> f = Functions.getFunction(name);
			if (f != null)
				func = new EvalFunction(f, exprs);
			else
				func = new EvalFunction(name, exprs);
		}
		if (arg1 > 1 && arg1 != 4){
			ct = arg[max - 2] != null ? arg[max - 2].getConvertedExpression(Object.class): null;
			i2 = arg[max - 1] != null ? (Expression<ItemStack>) arg[max - 1] : null;
		}
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {		
		return "format a gui slot " +s.toString(e, arg1) + " of " + p.toString(e, arg1) + " with " + i.toString(e, arg1);
	}

	@Override
	protected void execute(Event e) {
		if (this.p.getArray(e) != null && this.s.getArray(e) != null && this.i.getSingle(e) != null){
			Player[] p = this.p.getArray(e);
			Number[] slots = this.s.getArray(e);
			for (int x = 0; x < slots.length; x++)
				for(int y = 0; y < p.length; y++){
					if (p[y] == null)
						break;
					Inventory inv = p[y].getOpenInventory().getTopInventory();
					if (slots[x] != null && slots[x].intValue() >= 0 && slots[x].intValue() < inv.getSize()){
						Runnable rn = null;
						switch(Type){
						case 2: 
							final CommandSender s = sender != null ? sender.getSingle(e) : (Player)p[y];
							final String pe = perm != null ? perm.getSingle(e) : null;
							final String c = cmd.getSingle(e);
							rn = new Runnable(){
								@Override
								public void run() {
									TuSKe.getGUIManager().runCommand(s, c, pe);
								}};
							break;
						case 3:
							final EvalFunction f = func.getParemetersValues(e);
							rn = new Runnable(){
								
								@Override
								public void run() {
									f.run();
								}};
							break;
						}
						GUI gui = new GUI(rn, (i2 != null && i2.getSingle(e) != null ? i2.getSingle(e) : null), (ct != null ? getFromObject(ct.getSingle(e)) : null));
						
						if (slots[x] != null && slots[x].intValue() >= 0 && slots[x].intValue() < inv.getSize() /*&& !inv.getType().equals(InventoryType.PLAYER)*/ && !inv.getType().equals(InventoryType.CRAFTING)){
							
							if (runEvent)
								gui.toCallEvent(runEvent);
							else
								gui.toClose(toClose);
							if (this.inv != null)
								gui.toOpenInventory(this.inv.getSingle(e));								
							TuSKe.getGUIManager().newGUI(inv, slots[x].intValue(), i.getSingle(e), gui);
						}		
						
					}
				}
		}
	}
	private ClickType getFromObject(Object ct){
		if (ct == null)
			return null;
		if (ct instanceof String){
			try {
				return ClickType.valueOf(((String)ct).toUpperCase().replaceAll(" ", "_"));
			} catch(Exception ee){}
			return null;
		} 
		return (ClickType) ct;
	}
}
