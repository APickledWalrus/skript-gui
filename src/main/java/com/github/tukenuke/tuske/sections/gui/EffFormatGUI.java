package com.github.tukenuke.tuske.sections.gui;

import ch.njol.skript.Skript;
import ch.njol.skript.registrations.Classes;
import com.github.tukenuke.tuske.TuSKe;
import com.github.tukenuke.tuske.manager.gui.GUI;
import com.github.tukenuke.tuske.util.EffectSection;
import com.github.tukenuke.tuske.util.EvalFunction;
import com.github.tukenuke.tuske.util.VariableUtil;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Functions;
import ch.njol.util.Kleenean;

import java.util.function.Consumer;

public class EffFormatGUI extends EffectSection {
	static {
		String cr = "string/" + Classes.getExactClassInfo(ClickType.class).getCodeName();
		Registry.newEffect(EffFormatGUI.class,
				"(format|create|make) [a] gui slot [%-numbers%] of %players% with %itemstack% [to [do] nothing]",
				"(format|create|make) [a] gui slot [%-numbers%] of %players% with %itemstack% to (1¦close|2¦open %-inventory%) [(using|with) %-" + cr + "% [(button|click|action)]]",
				"(format|create|make) [a] gui slot [%-numbers%] of %players% with %itemstack% to (run|exe[cute]) [(using|with) %-" + cr + "% [(button|click|action)]]",
				"(format|create|make) [a] gui slot [%-numbers%] of %players% with %itemstack% to [(1¦close|2¦open %-inventory%) then] (run|exe[cute]) %commandsender% command %string% [(using|with) perm[ission] %-string%][[(,| and)] (using|with) %-" + cr + "% [(button|click|action)]][[(,| and)] (using|with) cursor [item] %-itemstack%]",
				"(format|create|make) [a] gui slot [%-numbers%] of %players% with %itemstack% to [(1¦close|2¦open %-inventory%) then] (run|exe[cute]) function <(.+)>\\([<.*?>]\\)[[(,| and)] (using|with) %-" + cr + "% [(button|click|action)]][[(,| and)] (using|with) cursor [item] %-itemstack%]",
				//"(format|create|make) [a] gui slot %numbers% of %players% with %itemstack% to [(1¦close|2¦open %-inventoy%) then] (run|exe[cute]) function <(.+)>\\([%-objects%[, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%]]\\)[[(,| and)] (using|with) %-" + cr + "% [(button|click|action)]][[(,| and)] (using|with) cursor [item] %-itemstack%]",
				"(format|create|make) [a] gui slot [%-numbers%] of %players% with %itemstack% to (run|exe[cute]) [gui [click]] event");
	}

	public static EffFormatGUI lastInstance = null;
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
		if (checkIfCondition())
			return false;
		if (hasSection()) {
			if ((arg1 == 0 || arg1 == 5)) {
				Skript.error("You can't execute a code in this effect. Use 'format gui slot .... to run:' instead.");
				return false;
			}
			loadSection("format gui effect", false, InventoryClickEvent.class);
		} else if (!hasSection() && arg1 == 2) {
			Skript.error("You can't execute a blank code in this effect. In case you want to format a unstealable item, use 'format gui slot ... to do nothing' instead.");
			return false;
		}
		int max = arg.length;
		s = (Expression<Number>) arg[0];
		p = (Expression<Player>) arg[1];
		i = (Expression<ItemStack>) arg[2]/*.getConvertedExpression(ItemStack.class)*/;
		toClose = arg3.mark > 0;
		Type = arg1;
		if (arg3.mark == 2)
			inv = (Expression<Inventory>) arg[3];
		switch (arg1){
		case 5:
			runEvent = true;
			break;
		case 1: 
			toClose = true;
		case 2:
			ct = arg[arg.length -1] != null ? arg[arg.length -1].getConvertedExpression(Object.class) : null;
		case 0:  break;
		case 3:
			sender = (Expression<CommandSender>) arg[4];
			cmd = (Expression<String>) arg[5];
			perm =  arg[6] != null ? (Expression<String>) arg[6] : null;
			break;
		case 4:
			String name = arg3.regexes.get(0).group(0).replaceAll(" ","");
			String exprs = arg3.regexes.size() > 1 ? arg3.regexes.get(1).group(0) : "";
			Function<?> f = Functions.getFunction(name);
			EvalFunction.setParserInstance(this);
			if (f != null)
				func = new EvalFunction(f, exprs);
			else
				func = new EvalFunction(name, exprs);
			
		}
		if (arg1 > 2 && arg1 != 5){
			ct = arg[max - 2] != null ? arg[max - 2].getConvertedExpression(Object.class): null;
			i2 = arg[max - 1] != null ? (Expression<ItemStack>) arg[max - 1] : null;
		}
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {		
		return "format a gui slot " + (s != null ? s.toString(e, arg1) : -1) + " of " + p.toString(e, arg1) + " with " + i.toString(e, arg1);
	}

	@Override
	protected void execute(Event e) {
		if (this.p.getArray(e) != null && this.i.getSingle(e) != null){
			Player[] p = this.p.getArray(e);
			Number[] slots = s != null ? s.getArray(e) : new Number[]{-2};
			for (int x = 0; x < slots.length; x++)
				for(int y = 0; y < p.length; y++){
					if (p[y] == null || slots[x] == null)
						continue;
					Inventory inv = p[y].getOpenInventory().getTopInventory();
					if (slots[x].intValue() >= -2 && slots[x].intValue() < inv.getSize() && !inv.getType().equals(InventoryType.CRAFTING)) {
						Object rn = null;
						switch (Type) {
							case 3:
								final CommandSender s = sender != null ? sender.getSingle(e) : p[y];
								final String pe = perm != null ? perm.getSingle(e) : null;
								final String c = cmd.getSingle(e);
								rn = (Runnable) () -> TuSKe.getGUIManager().runCommand(s, c, pe);
								break;
							case 4:
								final EvalFunction f = func.getParemetersValues(e);
								rn = (Runnable) f::run;
								break;
						}
						if (hasSection()) {
							Object copy = rn;
							Object variablesMap = VariableUtil.getInstance().copyVariables(e);
							rn = (Consumer<Event>) event -> {
								if (copy != null)
									((Runnable) copy).run();
								VariableUtil.getInstance().pasteVariables(event, variablesMap);
								runSection(event);
							};
						}
						GUI gui = new GUI(rn, (i2 != null && i2.getSingle(e) != null ? i2.getSingle(e) : null), (ct != null ? getFromObject(ct.getSingle(e)) : null));
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
