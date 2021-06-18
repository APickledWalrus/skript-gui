package io.github.apickledwalrus.skriptgui.gui;

import ch.njol.skript.Skript;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Consumer;

public class GUI implements Inventory {

	private Inventory inventory;
	private String name;
	private final GUIEventHandler eventHandler = new GUIEventHandler(this) {
		@Override
		public void onClick(InventoryClickEvent e, int slot) {
			char realSlot = convert(slot);
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
			char realSlot = convert(slot);
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
						e.getPlayer().openInventory(getInventory());
						// Reset behavior (it shouldn't persist)
						setCloseCancelled(false);
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

	public GUI(Inventory inventory, boolean stealableItems, @Nullable String name) {
		this.inventory = inventory;
		this.stealableItems = stealableItems;
		this.name = name != null ? name : inventory.getType().getDefaultTitle();
		getEventHandler().start();
	}

	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public int getSize() {
		return inventory.getSize();
	}

	public void setSize(int size) {
		changeInventory(size, getName());
	}

	@Override
	public int getMaxStackSize() {
		return inventory.getMaxStackSize();
	}

	@Override
	public void setMaxStackSize(int i) {
		inventory.setMaxStackSize(i);
	}

	@NotNull
	public String getName() {
		return name;
	}

	@NotNull
	public String getTitle() {
		return name;
	}

	public void setName(@Nullable String name) {
		changeInventory(getSize(), name);
	}

	@Override
	public ItemStack getItem(int i) {
		return inventory.getItem(i);
	}

	@Override
	public void setItem(int i, @Nullable ItemStack itemStack) {
		setItem(i, itemStack, false, null);
	}

	@Override
	@NotNull
	public HashMap<Integer, ItemStack> addItem(ItemStack... itemStacks) throws IllegalArgumentException {
		return inventory.addItem(itemStacks);
	}

	@Override
	@NotNull
	public HashMap<Integer, ItemStack> removeItem(ItemStack... itemStacks) throws IllegalArgumentException {
		return inventory.removeItem(itemStacks);
	}

	@Override
	public ItemStack[] getContents() {
		return inventory.getContents();
	}

	@Override
	public void setContents(ItemStack[] itemStacks) throws IllegalArgumentException {
		inventory.setContents(itemStacks);
	}

	@Override
	public ItemStack[] getStorageContents() {
		return inventory.getStorageContents();
	}

	@Override
	public void setStorageContents(ItemStack[] itemStacks) throws IllegalArgumentException {
		inventory.setStorageContents(itemStacks);
	}

	@Override
	public boolean contains(@NotNull Material material) throws IllegalArgumentException {
		return inventory.contains(material);
	}

	@Override
	public boolean contains(@Nullable ItemStack itemStack) {
		return inventory.contains(itemStack);
	}

	@Override
	public boolean contains(@NotNull Material material, int i) throws IllegalArgumentException {
		return inventory.contains(material, i);
	}

	@Override
	public boolean contains(@Nullable ItemStack itemStack, int i) {
		return inventory.contains(itemStack, i);
	}

	@Override
	public boolean containsAtLeast(@Nullable ItemStack itemStack, int i) {
		return inventory.containsAtLeast(itemStack, i);
	}

	@Override
	@NotNull
	public HashMap<Integer, ? extends ItemStack> all(@NotNull Material material) throws IllegalArgumentException {
		return inventory.all(material);
	}

	@Override
	@NotNull
	public HashMap<Integer, ? extends ItemStack> all(@Nullable ItemStack itemStack) {
		return inventory.all(itemStack);
	}

	@Override
	public int first(@NotNull Material material) throws IllegalArgumentException {
		return inventory.first(material);
	}

	@Override
	public int first(@NotNull ItemStack itemStack) {
		return inventory.first(itemStack);
	}

	@Override
	public int firstEmpty() {
		return inventory.firstEmpty();
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@Override
	public void remove(@NotNull Material material) throws IllegalArgumentException {
		inventory.remove(material);
	}

	@Override
	public void remove(@NotNull ItemStack itemStack) {
		inventory.remove(itemStack);
	}

	/**
	 * @param slot The slot(s) to remove. This will be converted through {@link GUI#convert(Object)}.
	 *          If the returned character covers multiple slots, each of those slots will be cleared.
	 */
	@Override
	public void clear(int slot) {
		char realSlot = convert(slot);
		setItem(realSlot, new ItemStack(Material.AIR));
		slots.remove(realSlot);
		stealableSlots.remove(realSlot);
	}

	public void clear(Object slot) {
		char realSlot = convert(slot);
		setItem(realSlot, new ItemStack(Material.AIR));
		slots.remove(realSlot);
		stealableSlots.remove(realSlot);
	}

	@Override
	public void clear() {
		inventory.clear();
		slots.clear();
		stealableSlots.clear();
	}

	@Override
	@NotNull
	public List<HumanEntity> getViewers() {
		return inventory.getViewers();
	}

	@Override
	@NotNull
	public InventoryType getType() {
		return inventory.getType();
	}

	@Override
	public InventoryHolder getHolder() {
		return inventory.getHolder();
	}

	@Override
	@NotNull
	public ListIterator<ItemStack> iterator() {
		return inventory.iterator();
	}

	@Override
	@NotNull
	public ListIterator<ItemStack> iterator(int i) {
		return inventory.iterator(i);
	}

	@Override
	public Location getLocation() {
		return inventory.getLocation();
	}

	private void changeInventory(int size, @Nullable String name) {
		Inventory newInventory = InventoryUtils.newInventory(getType(), size, name);
		newInventory.setContents(getContents());

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
	 * @param slot The object to convert to char form
	 * @return A char that is usable in the item and slot maps.
	 */
	public char convert(Object slot) {
		if (slot instanceof Number) {
			int invSlot = ((Number) slot).intValue();
			if (invSlot < rawShape.length())
				return rawShape.charAt(invSlot);
			return ' ';
		}

		if (slot instanceof String && !((String) slot).isEmpty()) {
			return ((String) slot).charAt(0);
		}

		if (slot instanceof Character) {
			return (Character) slot;
		}

		return nextSlot();
	}

	/**
	 * @return The next available slot in this GUI.
	 */
	public char nextSlot() {
		for (char ch : rawShape.toCharArray()) {
			if (!slots.containsKey(ch)) {
				return ch;
			}
		}
		return 0;
	}

	/**
	 * @return The newest slot that has been filled in this GUI.
	 */
	public char nextSlotInverted() {
		return convert(firstEmpty());
	}

	/**
	 * @param ch The slot in char form. It is assumed that this char was already converted through {@link GUI#convert(Object)}.
	 * @return The slot's button consumer, or an emtpty consumer if it does not have one.
	 */
	@Nullable
	public Consumer<InventoryClickEvent> getSlot(char ch) {
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
		char ch = convert(slot);
		if (ch == 0) {
			return;
		}
		if (ch == '+' && rawShape.contains("+")) {
			char ch2 = 'A';
			while (rawShape.indexOf(ch2) >= 0)
				ch2++;
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
			if (ch == ch1 && i < getSize()) {
				inventory.setItem(i, item);
			}
			i++;
		}
	}

	/**
	 * @return The raw shape of this GUI.
	 */
	public String getRawShape() {
		return rawShape;
	}

	/**
	 * The type of shape change to be applied
	 * @see GUI#setShape(ShapeMode, String...)
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
	 * Resets the shape of this {@link GUI}
	 */
	public void resetShape() {
		StringBuilder sb = new StringBuilder();
		for (char c = 'A'; c < getSize() + 'A'; c++) {
			sb.append(c);
		}
		rawShape = sb.toString();
	}

	/**
	 * Sets the shape of this {@link GUI}
	 * @param shapeMode If true, the shape will be changed for actions. If false, it will be changed for items.
	 * @param shapes The new shape patterns for this {@link GUI}
	 * @see GUI#getRawShape()
	 */
	public void setShape(ShapeMode shapeMode, String... shapes) {
		if (shapes.length == 0) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		for (String shape : shapes) {
			sb.append(shape);
		}
		while (sb.length() < getSize()) { // Fill it in if it's too small
			sb.append(' ');
		}

		String newRawShape = sb.toString();
		if (shapeMode == ShapeMode.ITEMS || shapeMode == ShapeMode.BOTH) { // In case it's both, we MUST do this first.
			// Get a map of the current shape for contents.
			int x = 0;
			Map<Character, ItemStack> items = new HashMap<>();
			for (char ch : rawShape.toCharArray()) {
				if (x >= getSize())
					break;
				items.put(ch, getItem(x));
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

			setContents(newContents);
		}

		if (shapeMode == ShapeMode.ACTIONS || shapeMode == ShapeMode.BOTH) {
			rawShape = newRawShape;
		}
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
	public boolean isStealable(char slot) {
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

	public GUIEventHandler getEventHandler() {
		return eventHandler;
	}

}
