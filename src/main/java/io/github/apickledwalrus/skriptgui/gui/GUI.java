package io.github.apickledwalrus.skriptgui.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.util.InventoryUtils;

public class GUI {

	private final Consumer<InventoryClickEvent> NULL_CONSUMER = e -> {};

	private Inventory guiInventory;
	private GUIListener listener;

	// Whether the player can take items from this GUI.
	private boolean stealableItems;
	private String name;
	private String rawShape;

	private final Map<Character, Consumer<InventoryClickEvent>> slots = new HashMap<>();
	private Consumer<InventoryCloseEvent> onClose;

	/*
	 * Constructors
	 */

	/**
	 * Creates a new {@link GUI} with the given inventory
	 * @param inv A {@link Inventory}
	 * @see io.github.apickledwalrus.skriptgui.elements.expressions.ExprVirtualInventory
	 */
	public GUI(Inventory inv) {
		this.guiInventory = inv;
		this.name = inv.getType().getDefaultTitle();
		this.stealableItems = false;
	}

	/**
	 * Creates a new {@link GUI} with the given inventory and lock status.
	 * @param inv A {@link Inventory}
	 * @param stealableItems Whether items can be taken from this {@link GUI} by the viewer
	 * @see io.github.apickledwalrus.skriptgui.elements.expressions.ExprVirtualInventory
	 */
	public GUI(Inventory inv, boolean stealableItems) {
		this.guiInventory = inv;
		this.name = inv.getType().getDefaultTitle();
		this.stealableItems = stealableItems;
	}

	/**
	 * Creates a new {@link GUI} with the given inventory and lock status.
	 * @param inv A {@link Inventory}
	 * @param stealableItems Whether items can be taken from this {@link GUI} by the viewer
	 * @param name The name of this GUI. This SHOULD be the name of the created inventory used in {@link io.github.apickledwalrus.skriptgui.elements.sections.SecCreateGUI}.
	 * @see io.github.apickledwalrus.skriptgui.elements.expressions.ExprVirtualInventory
	 */
	public GUI(Inventory inv, boolean stealableItems, String name) {
		this.guiInventory = inv;
		this.name = name;
		this.stealableItems = stealableItems;
	}

	/*
	 * General Methods
	 */

	/**
	 * @return The {@link Inventory} of this {@link GUI}
	 */
	public Inventory getInventory() {
		if (!getListener().isStarted())
			getListener().start();
		return guiInventory;
	}

	/**
	 * Converts an integer to a char.
	 * This method is for {@link GUI} shapes.
	 * @param slot Usually the slot from an inventory event.
	 * @return The converted slot integer. This char is the key to what item the slot holds.
	 */
	public char convertSlot(int slot) {
		if (slot < getRawShape().length())
			return getRawShape().charAt(slot);
		return ' ';
	}

	/**
	 * The returned char is for the 'items' map.
	 * @return The next available slot in this {@link GUI}
	 */
	public char nextSlot() {
		for (char ch : rawShape.toCharArray()) {
			if (!slots.containsKey(ch))
				return ch;
		}
		return 0;
	}

	/**
	 * The returned char is for the 'items' map.
	 * @return The newest slot that is filled in this {@link GUI}
	 */
	public char nextInvertedSlot() {
		for (char ch2 : rawShape.toCharArray()) {
			if (slots.containsKey(ch2))
				return ch2;
		}
		return 0;
	}

	/**
	 * @param slot The object to convert to char form
	 * @return A char that is usable in the item and slot maps.
	 */
	private char convert(Object slot) {
		char ch;
		if (slot instanceof Number)
			ch = convertSlot(((Number) slot).intValue());
		else if (slot instanceof String && !((String) slot).isEmpty())
			ch = ((String) slot).charAt(0);
		else if (slot instanceof Character)
			ch = (Character) slot;
		else { // It will get the next free slot
			ch = nextSlot();
		}
		return ch;
	}

	/**
	 * Clears all slots of the {@link GUI}
	 * @return The modified {@link GUI}.
	 */
	public GUI clear() {
		int x = -1;
		for (char ch : rawShape.toCharArray()) {
			if (++x < getInventory().getSize() && slots.containsKey(ch)) {
				setItem(ch, new ItemStack(Material.AIR));
			}
		}
		slots.clear();
		return this;
	}

	/**
	 * Clears the specified slots.
	 * If the given char array is null or empty, nothing will happen.
	 * @param chars The slots to clear. They will be converted by {@link GUI#convert(Object)}
	 * @return The modified {@link GUI}.
	 */
	@SuppressWarnings("null")
	public GUI clearSlots(Object... chars){
		if (chars != null || chars.length > 0) {
			for (Object ch : chars) {
				char ch1 = convert(ch);
				int x = -1;
				for (char ch2 : rawShape.toCharArray()) {
					if (++x < getInventory().getSize() && ch1 == ch2)
						setItem(ch, new ItemStack(Material.AIR), null);
				}
				slots.remove(ch1);
			}
		}
		return this;
	}

	/*
	 * GUI Properties Methods
	 */

	/**
	 * Changes the name of this {@link GUI}.
	 * If the {@link GUI} currently has viewers, it will be reopened for them to update the name.
	 * @param newName The new name for this {@link GUI}
	 * @return The modified {@link GUI}. No modifications will occur if the new name is null.
	 * @see GUI#getName()
	 */
	public GUI setName(String newName) {
		if (newName == null)
			return this;

		Inventory inv = InventoryUtils.newInventory(getInventory().getType(), getInventory().getSize() / 9, newName);
		inv.setContents(getInventory().getContents());

		// Create clone to avoid a CME
		new ArrayList<>(getInventory().getViewers()).forEach(viewer -> {
			ItemStack cursor = viewer.getItemOnCursor();
			viewer.setItemOnCursor(null);
			viewer.openInventory(inv);
			viewer.setItemOnCursor(cursor);
		});

		guiInventory = inv;
		getListener().setInventory(guiInventory);
		this.name = newName;

		return this;
	}

	/**
	 * @return The name of this {@link GUI}.
	 * @see GUI#setName(String)
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Changes the size of this {@link GUI}.
	 * If the {@link GUI} currently has viewers, it will be reopened for them to update the size.
	 * @param newSize The new size for this {@link GUI}
	 * @return The modified {@link GUI}. If this GUI is not a chest GUI, no modifications will occur.
	 * @see GUI#getSize()
	 */
	public GUI setSize(int newSize) {
		if (getInventory().getType() != InventoryType.CHEST)
			return this;
		Inventory inv = InventoryUtils.newInventory(getInventory().getType(), newSize, getName());

		// Check if the new inventory is smaller - avoid issues.
		if (newSize < getSize()) {
			for (int i = 0; i < inv.getSize(); i++)
				inv.setItem(i, getInventory().getItem(i));
			rawShape = rawShape.substring(0, inv.getSize());
		} else {
			inv.setContents(getInventory().getContents());
		}

		// Create clone to avoid a CME
		new ArrayList<>(getInventory().getViewers()).forEach(viewer -> {
			ItemStack cursor = viewer.getItemOnCursor();
			viewer.setItemOnCursor(null);
			viewer.openInventory(inv);
			viewer.setItemOnCursor(cursor);
		});

		guiInventory = inv;
		getListener().setInventory(guiInventory);

		return this;
	}

	/**
	 * @return The size of this {@link GUI}.
	 * @see GUI#setSize(int)
	 */
	public int getSize() {
		return getInventory().getSize();
	}

	/**
	 * The type of shape change to be applied
	 * @see GUI#setShape(Boolean, ShapeMode, String...)
	 */
	public enum ShapeMode {

		/**
		 * Update shape for items
		 */
		ITEMS,

		/**
		 * Update shape for actions
		 */
		ACTIONS,

		/**
		 * Update shape for items and actions
		 */
		BOTH
	}

	/**
	 * Sets the shape of this {@link GUI}
	 * @param defaultShape If true, the {@link GUI}'s shape will be reset to default
	 * @param shapeMode If true, the shape will be changed for actions. If false, it will be changed for items.
	 * @param shapes The new shape patterns for this {@link GUI}
	 * @return The modified {@link GUI}
	 * @see GUI#getRawShape()
	 */
	public GUI setShape(Boolean defaultShape, ShapeMode shapeMode, String... shapes) {
		if (defaultShape) {
			StringBuilder sb = new StringBuilder();
			for (char c = 'A'; c < getSize() + 'A'; c++)
				sb.append(c);
			this.rawShape = sb.toString();
		} else if (shapes.length > 0 && shapeMode != null) {
			StringBuilder sb = new StringBuilder();
			for (String shape : shapes)
				sb.append(shape);
			while (sb.length() < getSize())
				sb.append(' ');
			String newRawShape = sb.toString();
			if (shapeMode == ShapeMode.ITEMS || shapeMode == ShapeMode.BOTH) // In case it's both, we MUST do this first.
				updateShape(newRawShape);
			if (shapeMode == ShapeMode.ACTIONS || shapeMode == ShapeMode.BOTH)
				this.rawShape = newRawShape;
		}
		return this;
	}

	/**
	 * Used to make the items match the new shape of the GUI.
	 * @param newRawShape The new shape.
	 * @see GUI#setShape(Boolean, ShapeMode, String...)
	 */
	private void updateShape(String newRawShape) {

		// Get a map of the current shape for contents.
		int x = 0;
		Map<Character, ItemStack> items = new HashMap<>();
		for (char ch : rawShape.toCharArray()) {
			if (x >= getSize())
				break;
			items.put(ch, getInventory().getItem(x));
			x++;
		}

		// Set the contents
		ItemStack[] newContents = new ItemStack[getSize()];
		x = 0;
		for (char ch : newRawShape.toCharArray()) {
			ItemStack item = items.get(ch);
			if (item != null && x < getSize())
				newContents[x] = item;
			x++;
		}

		getInventory().setContents(newContents);
	}

	/**
	 * @return The raw shape of this {@link GUI}.
	 * @see GUI#setShape(Boolean, ShapeMode, String...)
	 */
	public String getRawShape() {
		return this.rawShape;
	}

	/**
	 * @param stealable Whether items in this {@link GUI} should be stealable
	 * @return The modified {@link GUI}
	 * @see GUI#getStealable()
	 */
	public GUI setStealable(Boolean stealable) {
		this.stealableItems = stealable;
		return this;
	}

	/**
	 * @return Whether the items in this {@link GUI} can be stolen.
	 * @see GUI#setStealable(Boolean)
	 */
	public boolean getStealable() {
		return this.stealableItems;
	}

	/*
	 * Action Methods
	 */

	/**
	 * Sets the consumer to be run when this {@link GUI} is closed
	 * @param consumer The {@link Consumer} to be run
	 * @return The modified {@link GUI}
	 * @see GUI#getOnClose()
	 * @see GUI#hasOnClose()
	 */
	public GUI setOnClose(Consumer<InventoryCloseEvent> consumer) {
		this.onClose = consumer;
		return this;
	}

	/**
	 * @return The {@link Consumer} that will run when this {@link GUI} is closed.
	 * @see GUI#setOnClose(Consumer)
	 * @see GUI#hasOnClose()
	 */
	@Nullable
	public Consumer<InventoryCloseEvent> getOnClose() {
		return this.onClose;
	}

	/**
	 * @return Whether this {@link GUI} has a {@link Consumer} that will run when it is closed.
	 * @see GUI#setOnClose(Consumer)
	 * @see GUI#getOnClose()
	 */
	public boolean hasOnClose() {
		return this.onClose != null;
	}

	/**
	 * Sets a slot's item.
	 * @param slot The slot to put the item in. It will be converted by {@link GUI#convert(Object)}.
	 * @param item The {@link ItemStack} to put in the slot.
	 * @param consumer The {@link Consumer} that the slot will run when clicked. Put as null if the slot should not run anything when clicked.
	 * @return The modified {@link GUI}.
	 */
	public GUI setItem(Object slot, ItemStack item, @Nullable Consumer<InventoryClickEvent> consumer) {
		char ch = convert(slot);
		if (consumer == null)
			consumer = NULL_CONSUMER;
		if (ch == 0)
			return this;
		if (ch == '+' && rawShape.contains("+")) {
			char ch2 = 'A';
			while (rawShape.indexOf(ch2) >= 0)
				ch2++;
			rawShape = rawShape.replaceFirst("\\+", "" + ch2);
			ch = ch2;
		}
		slots.put(ch, consumer);
		setItem(ch, item);
		return this;
	}

	/**
	 * Set's the char slot's ItemStack in the GUI inventory.
	 * @param ch The char for the slot(s). It is assumed that this char has already been converted.
	 * @param item The {@link ItemStack} to be put in the slot.
	 * @see GUI#setItem(Object, ItemStack, Consumer)
	 */
	private void setItem(char ch, ItemStack item) {
		int x = 0;
		for (char ch1 : rawShape.toCharArray()) {
			if (ch == ch1 && x < getSize())
				getInventory().setItem(x, item);
			x++;
		}
	}

	/**
	 * @param slot The slot in integer form. It will be converted by {@link GUI#convert(Object)}.
	 * @return The slot's {@link Consumer}, or {@link GUI#NULL_CONSUMER} if it does not have one.
	 * @see GUI#getSlot(char)
	 */
	@Nullable
	public Consumer<InventoryClickEvent> getSlot(int slot) {
		return slot >= 0 ? getSlot(convertSlot(slot)) : NULL_CONSUMER;
	}

	/**
	 * @param ch The slot in char form. It is assumed that this char was already converted.
	 * @return The slot's {@link Consumer}, or {@link GUI#NULL_CONSUMER} if it does not have one.
	 * @see GUI#getSlot(int)
	 */
	public Consumer<InventoryClickEvent> getSlot(char ch) {
		if (ch > 0 && slots.containsKey(ch))
			return slots.get(ch);
		return NULL_CONSUMER;
	}

	public GUIListener getListener() {
		if (listener == null) {
			listener = new GUIListener(guiInventory) {
				@Override
				public void onClick(InventoryClickEvent e, int slot) {
					Consumer<InventoryClickEvent> run = getSlot(slot);
					// Cancel the event if this GUI slot runs something
					// If it doesn't, check whether items are stealable in this GUI
					e.setCancelled(run != NULL_CONSUMER || !getStealable());
					if (run != null && slot == e.getSlot() && guiInventory.equals(e.getClickedInventory())) {
						run.accept(e);
					}
				}

				@Override
				public void onOpen(InventoryOpenEvent e) {
					SkriptGUI.getGUIManager().setGUI((Player) e.getPlayer(), GUI.this);
				}

				@Override
				public void onClose(InventoryCloseEvent e) {
					SkriptGUI.getGUIManager().removeGUI((Player) e.getPlayer());
					if (hasOnClose()) {
						SkriptGUI.getGUIManager().setGUIEvent(e, GUI.this);
						if (getOnClose() != null) {
							try {
								getOnClose().accept(e);
							} catch (Exception ex) {
								Skript.exception(ex, "An error occurred while closing a GUI. If you are unsure why this occured, please report the error on the skript-gui GitHub.");
							}
						}
					}
				}

				@Override
				public void onDrag(InventoryDragEvent e, int slot) {
					Consumer<InventoryClickEvent> run = getSlot(slot);
					// Cancel the event if this GUI slot runs something
					// If it doesn't, check whether items are stealable in this GUI
					e.setCancelled(run != NULL_CONSUMER || !getStealable());
				}
			};
		}
		return listener;
	}

}
