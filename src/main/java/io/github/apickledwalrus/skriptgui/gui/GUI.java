package io.github.apickledwalrus.skriptgui.gui;

import ch.njol.skript.Skript;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GUI {

	private Inventory inventory;
	private String name;
	private final GUIEventHandler eventHandler = new GUIEventHandler(this) {
		@Override
		public void onClick(InventoryClickEvent e, int slot) {
			Character realSlot = convert(slot);
			Consumer<InventoryClickEvent> run = getSlot(realSlot);
			/*
			 * Cancel the event if this GUI slot is a button (it runs a consumer)
			 * If it isn't, check whether items are stealable in this GUI, or if the specific slot is stealable
			 */
			e.setCancelled(run != null || (!isStealable() && !isStealable(realSlot)));
			if (run != null && slot == e.getSlot()) {
				run.accept(e);
			}
		}

		@Override
		public void onDrag(InventoryDragEvent e, int slot) {
			Character realSlot = convert(slot);
			Consumer<InventoryClickEvent> run = getSlot(realSlot);
			/*
			 * Cancel the event if this GUI slot is a button (it runs a consumer)
			 * If it isn't, check whether items are stealable in this GUI, or if the specific slot is stealable
			 */
			e.setCancelled(run != null || (!isStealable() && !isStealable(realSlot)));
		}

		@Override
		public void onOpen(InventoryOpenEvent e) {
			SkriptGUI.getGUIManager().setGUI((Player) e.getPlayer(), GUI.this);
			if (onOpen != null)
				onOpen.accept(e);
		}

		@Override
		public void onClose(InventoryCloseEvent e) {
			if (onClose == null) { // If this GUI does not run anything when it is closed, it will not be able to cancel its closing
				SkriptGUI.getGUIManager().removeGUI((Player) e.getPlayer());
				return;
			}

			SkriptGUI.getGUIManager().setGUIEvent(e, GUI.this);
			try {
				onClose.accept(e);
				if (closeCancelled) {
					Bukkit.getScheduler().runTaskLater(SkriptGUI.getInstance(), () -> {
						// Reset behavior (it shouldn't persist)
						setCloseCancelled(false);

						e.getPlayer().openInventory(inventory);
					}, 1);
				} else { // Event isn't being "cancelled"
					SkriptGUI.getGUIManager().removeGUI((Player) e.getPlayer());
				}
			} catch (Exception ex) {
				throw Skript.exception(ex, "An error occurred while closing a GUI. If you are unsure why this occurred, please report the error on the skript-gui GitHub.");
			}
		}
	};

	private final Map<Character, Consumer<InventoryClickEvent>> slots = new HashMap<>();
	@Nullable
	private String rawShape;

	// Whether all items of this GUI (excluding buttons) can be taken.
	private boolean stealableItems;
	/*
	 * The individual slots of this GUI that can be stolen.
	 * Even if stealableItems is false, slots in this list will be stealable.
	 * Ignored if 'stealableItems' is true.
	 */
	private final List<Character> stealableSlots = new ArrayList<>();

	// To be ran when this inventory is opened.
	private Consumer<InventoryOpenEvent> onOpen;
	// To be ran when this inventory is closed.
	private Consumer<InventoryCloseEvent> onClose;
	// Whether the inventory close event for this event handler is cancelled.
	private boolean closeCancelled;

	private String id;

	public GUI(Inventory inventory, boolean stealableItems, @Nullable String name) {
		this.inventory = inventory;
		this.stealableItems = stealableItems;
		this.name = name != null ? name : inventory.getType().getDefaultTitle();
		eventHandler.start();
	}

	public Inventory getInventory() {
		return inventory;
	}

	public GUIEventHandler getEventHandler() {
		return eventHandler;
	}

	public void setSize(int size) {
		changeInventory(size, getName());
	}

	@NotNull
	public String getName() {
		return name;
	}

	public void setName(@Nullable String name) {
		changeInventory(inventory.getSize(), name);
	}

	public void clear(Object slot) {
		inventory.clear();
		Character realSlot = convert(slot);
		setItem(realSlot, new ItemStack(Material.AIR), false, null); // It's okay to convert again (it won't really convert)
		slots.remove(realSlot);
		stealableSlots.remove(realSlot);
	}

	public void clear() {
		inventory.clear();
		slots.clear();
		stealableSlots.clear();
	}

	private void changeInventory(int size, @Nullable String name) {
		Inventory newInventory = InventoryUtils.newInventory(inventory.getType(), size, name);
		newInventory.setContents(inventory.getContents());

		Iterator<HumanEntity> viewerIterator = inventory.getViewers().iterator();
		while (viewerIterator.hasNext()) {
			HumanEntity viewer = viewerIterator.next();
			ItemStack cursor = viewer.getItemOnCursor();
			viewer.setItemOnCursor(null);
			viewer.openInventory(newInventory);
			viewer.setItemOnCursor(cursor);
			viewerIterator.remove();
		}

		inventory = newInventory;
		this.name = name;
	}

	/**
	 * @param slot The object to convert to Character form
	 * @return A Character that is usable in the item and slot maps.
	 */
	public Character convert(Object slot) {
		if (slot instanceof Character) {
			return (Character) slot;
		}

		if (slot instanceof Number) {
			int invSlot = ((Number) slot).intValue();
			if (rawShape != null && invSlot < rawShape.length()) {
				return rawShape.charAt(invSlot);
			}
			return ' ';
		}

		if (slot instanceof String && !((String) slot).isEmpty()) {
			char strSlot = ((String) slot).charAt(0);
			return (rawShape != null && rawShape.contains(Character.toString(strSlot))) ? strSlot : ' ';
		}

		return nextSlot();
	}

	/**
	 * @return The next available slot in this GUI.
	 */
	public Character nextSlot() {
		if (rawShape != null) {
			for (char ch : rawShape.toCharArray()) {
				if (!slots.containsKey(ch)) {
					return ch;
				}
			}
		}
		return 0;
	}

	/**
	 * @return The newest slot that has been filled in this GUI.
	 */
	public Character nextSlotInverted() {
		return convert(inventory.firstEmpty() - 1);
	}

	/**
	 * @param ch The slot in Character form. It is assumed that this Character was already converted through {@link GUI#convert(Object)}.
	 * @return The slot's button consumer, or an emtpty consumer if it does not have one.
	 */
	@Nullable
	public Consumer<InventoryClickEvent> getSlot(Character ch) {
		if (ch > 0 && slots.containsKey(ch)) {
			return slots.get(ch);
		}
		return null;
	}

	/**
	 * Sets a slot's item.
	 * @param slot The slot to put the item in. It will be converted by {@link GUI#convert(Object)}.
	 * @param item The {@link ItemStack} to put in the slot.
	 * @param stealable Whether this {@link ItemStack} can be stolen.
	 * @param consumer The {@link Consumer} that the slot will run when clicked. Put as null if the slot should not run anything when clicked.
	 */
	public void setItem(Object slot, @Nullable ItemStack item, boolean stealable, @Nullable Consumer<InventoryClickEvent> consumer) {
		if (rawShape == null) {
			SkriptGUI.getInstance().getLogger().warning("Unable to set the item in a gui named '" + getName() + "' as it has a null shape.");
			return;
		}

		Character ch = convert(slot);
		if (ch == ' ') {
			return;
		}
		if (ch == '+' && rawShape.contains("+")) {
			char ch2 = 'A';
			while (rawShape.indexOf(ch2) >= 0) {
				ch2++;
			}
			rawShape = rawShape.replaceFirst("\\+", "" + ch2);
			ch = ch2;
		}
		slots.put(ch, consumer);

		if (stealable) {
			stealableSlots.add(ch);
		} else { // Just in case as we may be updating a slot.
			stealableSlots.remove(ch);
		}

		int i = 0;
		for (char ch1 : rawShape.toCharArray()) {
			if (ch == ch1 && i < inventory.getSize()) {
				inventory.setItem(i, item);
			}
			i++;
		}
	}

	/**
	 * @param slot The slot to get the item from. It will be converted.
	 * @return The item at this slot, or AIR if the slot has no item, or the slot is not valid for this GUI.
	 */
	public ItemStack getItem(Object slot) {
		if (rawShape == null) {
			return new ItemStack(Material.AIR);
		}
		Character ch = convert(slot);
		if (ch == 0) {
			return new ItemStack(Material.AIR);
		}
		return inventory.getItem(rawShape.indexOf(ch));
	}

	/**
	 * @return The raw shape of this GUI. May be null if the shape has not yet been initialized.
	 * @see #setShape(String...) 
	 */
	@Nullable
	public String getRawShape() {
		return rawShape;
	}

	/**
	 * Resets the shape of this {@link GUI}
	 */
	public void resetShape() {
		int size = 54; // Max inventory size

		String[] shape = new String[size];

		int position = 0;
		StringBuilder sb = new StringBuilder();
		for (char c = 'A'; c < size + 'A'; c++) { // Create the default shape in String form.
			sb.append(c);
			if (sb.length() == 9) {
				shape[position] = sb.toString();
				sb = new StringBuilder();
				position++;
			}
		}

		setShape(shape);
	}

	/**
	 * Sets the shape of this {@link GUI}
	 * @param shapes The new shape patterns for this {@link GUI}
	 * @see GUI#getRawShape()
	 */
	public void setShape(String... shapes) {
		if (shapes.length == 0) {
			return;
		}

		int size = inventory.getSize();

		StringBuilder sb = new StringBuilder();
		for (String shape : shapes) {
			sb.append(shape);
		}
		while (sb.length() < size) { // Fill it in if it's too small
			sb.append(' ');
		}

		String newRawShape = sb.toString();

		// Get a map of the current shape for contents.
		if (rawShape != null) {
			char lastChar = ' '; // Spaces are not valid for a shape
			for (char ch : rawShape.toCharArray()) {
				if (ch == lastChar) {
					continue;
				}
				setItem(ch, getItem(ch), isStealable(ch), slots.get(ch));
			}

			// Remove invalid characters
			slots.keySet().removeIf(ch -> !rawShape.contains(ch.toString()));
			stealableSlots.removeIf(ch -> !rawShape.contains(ch.toString()));
		}

		rawShape = newRawShape;
	}

	/**
	 * @return Whether the items in this GUI can be stolen.
	 */
	public boolean isStealable() {
		return stealableItems;
	}

	/**
	 * @return Whether the given slot in this GUI can have its item stolen.
	 */
	public boolean isStealable(Character slot) {
		return stealableSlots.contains(slot);
	}

	/**
	 * @param stealableItems Whether items in this GUI should be stealable.
	 */
	public void setStealableItems(Boolean stealableItems) {
		this.stealableItems = stealableItems;
	}

	/**
	 * Sets the consumer to be ran when this GUI is opened.
	 * @param onOpen The consumer to be ran when this GUI is opened.
	 */
	public void setOnOpen(Consumer<InventoryOpenEvent> onOpen) {
		this.onOpen = onOpen;
	}

	/**
	 * Sets the consumer to be ran when this GUI is closed.
	 * @param onClose The consumer to be ran when this GUI is closed.
	 */
	public void setOnClose(Consumer<InventoryCloseEvent> onClose) {
		this.onClose = onClose;
	}

	/**
	 * Sets whether this GUI's close event should be cancelled.
	 * @param cancel Whether this GUI's close event should be cancelled.
	 */
	public void setCloseCancelled(boolean cancel) {
		closeCancelled = cancel;
	}

	/**
	 * @return The ID of this GUI if it is a global GUI
	 * @see GUIManager
	 */
	@Nullable
	public String getID() {
		return id;
	}

	/**
	 * Updates the ID of this GUI. Updates will be made in the {@link GUIManager} too.
	 * @param id The new id for this GUI. If null, it will be removed from the {@link GUIManager}.
	 */
	public void setID(@Nullable String id) {
		if (this.id != null) { // This GUI can be removed from the manager
			SkriptGUI.getGUIManager().removeGlobalGUI(this.id);
		}
		this.id = id;
		if (id != null) { // This GUI can be added to the manager
			SkriptGUI.getGUIManager().addGlobalGUI(id, this);
		}
	}

}
