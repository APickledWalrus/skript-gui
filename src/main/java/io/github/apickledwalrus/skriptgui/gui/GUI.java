package io.github.apickledwalrus.skriptgui.gui;

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

	private Map<Character, Consumer<InventoryClickEvent>> slots = new HashMap<>();
	private Consumer<InventoryCloseEvent> onClose;

	private Map<Character, ItemStack> items = new HashMap<>();

	/*
	 * Constructors
	 */

	/**
	 * Creates a new {@link GUI} with the given inventory
	 * @param inv A {@link Inventory}
	 * @see ExprVirtualInventory
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
	 * @see ExprVirtualInventory
	 */
	public GUI(Inventory inv, boolean stealableItems) {
		this.guiInventory = inv;
		this.name = inv.getType().getDefaultTitle();
		this.stealableItems = stealableItems;
	}

	/*
	 * General Methods
	 */

	/**
	 * @return The {@link Inventory} of this {@link GUI}
	 */
	public Inventory getInventory() {
		if (!getListener().isStarted()) {
			getListener().start();
			if (items.size() > 0) {
				int x = 0;
				for (char ch : rawShape.toCharArray()) {
					ItemStack item = items.get(ch);
					if (item != null && item.getType() != Material.AIR)
						guiInventory.setItem(x, item);
					x++;
				}
				// It won't be necessary anymore, so just cleaning it up
				items.clear();
			}
		}
		return guiInventory;
	}

	/**
	 * Converts an integer to a char.
	 * This method is for {@link GUI} shapes.
	 * @param slot
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
	 * @return The modified {@link GUI}
	 * @see GUI#getName()
	 */
	public GUI setName(String newName) {
		if (newName == null)
			return null;

		Inventory inv = InventoryUtils.newInventory(getInventory().getType(), getInventory().getSize(), newName);
		inv.setContents(getInventory().getContents());

		getInventory().getViewers().forEach(viewer -> {
			ItemStack cursor = viewer.getItemOnCursor();
			viewer.setItemOnCursor(null);
			viewer.openInventory(inv);
			viewer.setItemOnCursor(cursor);
		});

		guiInventory = inv;

		return this;
	}

	/**
	 * @return The name of this {@link GUI}.
	 * @see {@link GUI#setName(String)}
	 * @see {@link GUI#setNameAndSize(String, int)}
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Changes the size of this {@link GUI}.
	 * If the {@link GUI} currently has viewers, it will be reopened for them to update the size.
	 * @param newSize The new size for this {@link GUI}
	 * @return The modified {@link GUI}
	 * @see GUI#getSize()
	 */
	public GUI setSize(int newSize) {
		Inventory inv = InventoryUtils.newInventory(getInventory().getType(), newSize, getName());
		inv.setContents(getInventory().getContents());

		getInventory().getViewers().forEach(viewer -> {
			ItemStack cursor = viewer.getItemOnCursor();
			viewer.setItemOnCursor(null);
			viewer.openInventory(inv);
			viewer.setItemOnCursor(cursor);
		});

		guiInventory = inv;

		return this;
	}

	/**
	 * @return The size of this {@link GUI}.
	 * @see GUI#setSize(int)
	 * @see GUI#setNameAndSize(String, int)
	 */
	public int getSize() {
		return getInventory().getSize();
	}

	/**
	 * Changes the name and size of this {@link GUI}.
	 * If the {@link GUI} currently has viewers, it will be reopened for them to update the name and size.
	 * @param newName The new name for this {@link GUI}
	 * @param newSize The new size for this {@link GUI}
	 * @return The modified {@link GUI}
	 * @see GUI#setName(String)
	 * @see GUI#getName()
	 * @see GUI#setSize(int)
	 * @see GUI#getSize()
	 */
	public GUI setNameAndSize(String newName, int newSize) {
		if (newName == null)
			return null;

		Inventory inv = InventoryUtils.newInventory(getInventory().getType(), newSize, newName);
		inv.setContents(getInventory().getContents());

		getInventory().getViewers().forEach(viewer -> {
			ItemStack cursor = viewer.getItemOnCursor();
			viewer.setItemOnCursor(null);
			viewer.openInventory(inv);
			viewer.setItemOnCursor(cursor);
		});

		guiInventory = inv;

		return this;
	}

	/**
	 * Sets the shape of this {@link GUI}
	 * @param defaultShape If true, the {@link GUI}'s shape will be reset to default
	 * @param actions If true, the shape will be changed for actions. If false, it will be changed for items.
	 * @param shapes The new shape patterns for this {@link GUI}
	 * @return The modified {@link GUI}
	 * @see GUI#getRawShape()
	 */
	public GUI setShape(Boolean defaultShape, boolean actions, String... shapes) {
		if (defaultShape) {
			StringBuilder sb = new StringBuilder();
			for (char c = 'A'; c < getInventory().getSize() + 'A'; c++)
				sb.append(c);
			this.rawShape = sb.toString();
		} else if (shapes.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (String shape : shapes)
				sb.append(shape);
			while (sb.length() < getInventory().getSize())
				sb.append(' ');
			if (actions) {
				this.rawShape = sb.toString();
			} else {
				updateShape(getInventory().getContents(), sb.toString());
			}
		}
		return this;
	}

	public void updateShape(ItemStack[] contents, String newRawShape) {
		ItemStack[] newItems = contents.clone();
		int length = getInventory().getType() == InventoryType.CHEST ? getInventory().getSize() : newItems.length;
		contents = new ItemStack[length];
		int x = 0;
		Map<Character, ItemStack> items = new HashMap<>();
		for (char ch1 : rawShape.toCharArray()) {
			if (x < newItems.length)
				items.put(ch1, newItems[x++]);
		}
		x = 0;
		for (char ch : newRawShape.toCharArray()) {
			ItemStack item = items.get(ch);
			if (item != null && x < contents.length)
				contents[x] = item;
			x++;
		}
		getInventory().setContents(contents);
	}

	/**
	 * @return The raw shape of this {@link GUI}.
	 * @see GUI#setShape(Boolean, String...)
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
	 * @param slot The slot to put the item in. It will be converted by {@link GUI#convert(Object)}
	 * @param item The {@link ItemStack} to put in the slot.
	 * @param consumer The {@link Consumer} that the slot will run when clicked. Put as null if the slot should not run anything when clicked.
	 * @return The modified {@link GUI}
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
	 * If the {@link Inventory} is a {@linkplain ExprVirtualInventory}, has no viewers, or if the listener is null, it will set the slot later.
	 * If it is an {@link Inventory} from somewhere (like a block) or has someone already viewing it, it will set the slot then.
	 * @param ch The char for the slot(s). It is assumed that this char has already been converted.
	 * @param item The {@link ItemStack} to be put in the slot.
	 * @see GUI#setItem(Object, ItemStack, Consumer)
	 */
	private void setItem(char ch, ItemStack item) {
		if (listener == null && (getInventory().getHolder() == null || getInventory().getViewers().size() == 0)) {
			items.put(ch, item);
		} else { // Set the slot now.
			int x = -1;
			for (char ch1 : rawShape.toCharArray()) {
				if (++x < getInventory().getSize() && ch1 == ch) {
					getInventory().setItem(x, item);
				}
			}
		}
	}

	/**
	 * @param slot The slot in integer form. It will be converted by {@link GUI#convert(Object)}.
	 * @return The slot's {@link Consumer}, or null if it does not have one.
	 * @see GUI#getSlot(char)
	 */
	@Nullable
	public Consumer<InventoryClickEvent> getSlot(int slot) {
		return slot >= 0 ? getSlot(convertSlot(slot)) : null;
	}

	/**
	 * @param slot The slot in char form. It is assumed that this char was already converted.
	 * @return The slot's {@link Consumer}, or null if it does not have one.
	 * @see GUI#getSlot(int)
	 */
	@Nullable
	public Consumer<InventoryClickEvent> getSlot(char ch) {
		return ch > 0 ? slots.get(ch) : null;
	}

	public GUIListener getListener() {
		if (listener == null) {
			listener = new GUIListener(guiInventory) {
				@Override
				public void onClick(InventoryClickEvent e, int slot) {
					Consumer<InventoryClickEvent> run = getSlot(slot);
					e.setCancelled(run != null || !getStealable());
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
					if (hasOnClose()){
						SkriptGUI.getGUIManager().setGUIEvent(e, GUI.this);
						try {
							getOnClose().accept(e);
						} catch (Exception ex){
							Skript.exception(ex, "An error occurred while closing a GUI. If you are unsure why this occured, please report the error on the skript-gui GitHub.");
						}
					}
				}

				@Override
				public void onDrag(InventoryDragEvent e, int slot) {
					if (getSlot(slot) != null)
						e.setCancelled(!getStealable());
				}
			};
		}
		return listener;
	}

}