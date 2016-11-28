package me.tuke.sktuke.expressions;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprVirtualInv extends SimpleExpression<Inventory>{

	private Expression<InventoryType> it;
	private Expression<Number> size;
	private Expression<String> name;
	
	@Override
	public Class<? extends Inventory> getReturnType() {
		return Inventory.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		it = (Expression<InventoryType>) arg[0];
		size = (Expression<Number>) arg[1];
		name = (Expression<String>) arg[2];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "virtual inventory";
	}

	@Override
	@Nullable
	protected Inventory[] get(Event e) {
		InventoryType type = it.getSingle(e);
		if (type != null){
			int size = this.size != null && this.size.getSingle(e) != null ? this.size.getSingle(e).intValue()*9 : type.getDefaultSize(); 
			String name = this.name != null && this.name.getSingle(e) != null? (this.name.getSingle(e).length() > 32 ? this.name.getSingle(e).substring(0, 32) : this.name.getSingle(e)) : type.getDefaultTitle();
			return new Inventory[]{getInventory(type, size, name)};
		}
		return null;
	}
	
	public Inventory getInventory(InventoryType type, int size, String name){
		switch (type){
		//case ANVIL:
		case BEACON:
		case MERCHANT:
		case CRAFTING:
		case CREATIVE: return null;
		case CHEST:	return Bukkit.getServer().createInventory(null, size, name);
		case DROPPER: type = InventoryType.DISPENSER;
		default: return Bukkit.getServer().createInventory(null, type, name);
		}
	}
	/*public static void openAnvil(Player player){

        //Get our EntityPlayer
        EntityPlayer p = ((CraftPlayer) player).getHandle();

        //Create the AnvilContainer
        AnvilContainer container = new AnvilContainer(p);

        //Set the items to the items from the inventory given
        //container.getBukkitView().getTopInventory().setItem(0, inventory.getItem(0));
        //container.getBukkitView().getTopInventory().setItem(1, inventory.getItem(1));
        //container.getBukkitView().getTopInventory().setItem(2, inventory.getItem(2));

        //Counter stuff that the game uses to keep track of inventories
        int c = p.nextContainerCounter();

        //Send the packet
        p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, 8, "Repairing", 9, true));

        //Set their active container to the container
        p.activeContainer = container;

        //Set their active container window id to that counter stuff
        p.activeContainer.windowId = c;

        //Add the slot listener
        p.activeContainer.addSlotListener(p);

    }*/

}
