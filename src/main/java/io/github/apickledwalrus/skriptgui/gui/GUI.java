package io.github.apickledwalrus.skriptgui.gui;

import io.github.apickledwalrus.skriptgui.SkriptGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class GUI {

	private Inventory inventory;
	private String name;

	private final GUIEventHandler eventHandler = new GUIEventHandler() {
		@Override
		public void onClick(InventoryClickEvent e) {
			if (isPaused() || isPaused((Player) e.getWhoClicked())) {
				e.setCancelled(true); // Just in case
				return;
			}

			SlotData slotData = getSlotData(convert(e.getSlot()));
			if (slotData != null) {
				// Only cancel if this slot can't be removed AND all items aren't removable
				e.setCancelled(!isRemovable(slotData));

				// Call onChange if the slot is being changed
				if (!e.isCancelled() && (e.getCursor() != null || e.getCurrentItem() != null)) {
					if (e.getCursor() == null || e.getCurrentItem() == null ||
							!e.getCursor().isSimilar(e.getCurrentItem()) ||
							e.getCurrentItem().getAmount() < e.getCurrentItem().getMaxStackSize()) {
						onChange(e);
					}
				}

				Consumer<InventoryClickEvent> runOnClick = slotData.getRunOnClick();
				if (runOnClick != null) {
					SkriptGUI.getGUIManager().setGUI(e, GUI.this);
					runOnClick.accept(e);
				}
			} else { // If there is no slot data, cancel if this GUI doesn't have stealable items
				e.setCancelled(!isRemovable());
			}
		}

		@Override
		public void onChange(InventoryClickEvent e) {
			if (isPaused() || isPaused((Player) e.getWhoClicked())) {
				e.setCancelled(true); // Just in case
				return;
			}

			SlotData slotData = getSlotData(convert(e.getSlot()));
			if (slotData != null) {
				// Only cancel if this slot can't be removed AND all items aren't removable
				e.setCancelled(!isRemovable(slotData));

				Consumer<InventoryClickEvent> runOnChange = slotData.getRunOnChange();
				if (!e.isCancelled() && runOnChange != null) {
					SkriptGUI.getGUIManager().setGUI(e, GUI.this);
					runOnChange.accept(e);
				}
			} else { // If there is no slot data, cancel if this GUI doesn't have stealable items
				e.setCancelled(!isRemovable());
			}
		}

		@Override
		public void onDrag(InventoryDragEvent e) {
			if (isPaused() || isPaused((Player) e.getWhoClicked())) {
				e.setCancelled(true); // Just in case
				return;
			}

			for (int slot : e.getRawSlots()) {
				if (!isRemovable(convert(slot))) {
					e.setCancelled(true);
					break;
				}
			}
			onChange(e);
		}

		@Override
		public void onOpen(InventoryOpenEvent e) {
			if (isPaused() || isPaused((Player) e.getPlayer())) {
				return;
			}

			if (onOpen != null) {
				SkriptGUI.getGUIManager().setGUI(e, GUI.this);
				onOpen.accept(e);
			}
		}

		@Override
		public void onClose(InventoryCloseEvent e) {
			if (isPaused() || isPaused((Player) e.getPlayer())) {
				return;
			}

			if (onClose != null) {
				SkriptGUI.getGUIManager().setGUI(e, GUI.this);
				onClose.accept(e);
				if (closeCancelled) {
					Bukkit.getScheduler().runTaskLater(SkriptGUI.getInstance(), () -> {
						// Reset behavior (it shouldn't persist)
						setCloseCancelled(false);

						Player closer = (Player) e.getPlayer();
						pause(closer); // Avoid calling any open sections
						closer.openInventory(inventory);
						resume(closer);
					}, 1);
					return;
				}
			}

			if (id == null && inventory.getViewers().size() == 1) { // Only stop tracking if it isn't a global GUI
				Bukkit.getScheduler().runTaskLater(SkriptGUI.getInstance(), () -> SkriptGUI.getGUIManager().unregister(GUI.this), 1);
			}

			// To combat issues like https://github.com/APickledWalrus/skript-gui/issues/60
			Bukkit.getScheduler().runTaskLater(SkriptGUI.getInstance(), () -> ((Player) e.getPlayer()).updateInventory(), 1);
		}
	};

	private final Map<Character, SlotData> slots = new HashMap<>();
	@Nullable
	private String rawShape;

	// Whether all items of this GUI (excluding buttons) can be taken.
	private boolean removableItems;

	// To be run when this inventory is opened.
	@Nullable
	private Consumer<InventoryOpenEvent> onOpen;
	// To be run when this inventory is closed.
	@Nullable
	private Consumer<InventoryCloseEvent> onClose;
	// Whether the inventory close event for this event handler is cancelled.
	private boolean closeCancelled;

	@Nullable
	private String id;

	public GUI(Inventory inventory, boolean stealableItems, @Nullable String name) {
		this.inventory = inventory;
		this.removableItems = stealableItems;
		this.name = name != null ? name : inventory.getType().getDefaultTitle();
		SkriptGUI.getGUIManager().register(this);
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

	public String getName() {
		return name;
	}

	public void setName(@Nullable String name) {
		changeInventory(inventory.getSize(), name);
	}

	public void clear(Object slot) {
		Character realSlot = convert(slot);
		setItem(realSlot, new ItemStack(Material.AIR), false, null);
		slots.remove(realSlot);
	}

	public void clear() {
		inventory.clear();
		slots.clear();
	}

	private void changeInventory(int size, @Nullable String name) {
		if (name == null) {
			name = inventory.getType().getDefaultTitle();
		} else if (size < 9 ) { // Minimum size
			size = 9;
		} else if (size > 54) { // Maximum size
			size = 54;
		}

		if (size == inventory.getSize() && name.equals(this.name)) { // Nothing is actually changing
			return;
		}

		Inventory newInventory;
		if (inventory.getType() == InventoryType.CHEST) {
			newInventory = Bukkit.getServer().createInventory(null, size, name);
		} else {
			newInventory = Bukkit.getServer().createInventory(null, inventory.getType(), name);
		}

		if (size >= inventory.getSize()) {
			newInventory.setContents(inventory.getContents());
		} else { // The inventory is shrinking
			for (int slot = 0; slot < size; slot++) {
				newInventory.setItem(slot, inventory.getItem(slot));
			}
		}

		eventHandler.pause(); // Don't process any events as we transfer data and players

		for (HumanEntity viewer : new ArrayList<>(inventory.getViewers())) {
			ItemStack cursor = viewer.getItemOnCursor();
			viewer.setItemOnCursor(null);
			viewer.openInventory(newInventory);
			viewer.setItemOnCursor(cursor);
		}
		SkriptGUI.getGUIManager().transferRegistration(this, newInventory);
		inventory = newInventory;
		this.name = name;

		eventHandler.resume(); // It is safe to resume operations
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
			// Make sure inventory slot is at least 0 (see https://github.com/APickledWalrus/skript-gui/issues/48)
			if (rawShape != null && invSlot >= 0 && invSlot < rawShape.length()) {
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
		if (rawShape != null) {
			for (char ch : rawShape.toCharArray()) {
				if (slots.containsKey(ch)) {
					return ch;
				}
			}
		}
		return 0;
	}

	/**
	 * Sets a slot's item.
	 * @param slot The slot to put the item in. It will be converted by {@link GUI#convert(Object)}.
	 * @param item The {@link ItemStack} to put in the slot.
	 * @param removable Whether this {@link ItemStack} can be removed from its slot.
	 * @param consumer The {@link Consumer} that the slot will run when clicked. Put as null if the slot should not run anything when clicked.
	 */
	public void setItem(Object slot, @Nullable ItemStack item, boolean removable, @Nullable Consumer<InventoryClickEvent> consumer) {
		if (rawShape == null) {
			SkriptGUI.getInstance().getLogger().warning("Unable to set the item in a gui named '" + getName() + "' as it has a null shape.");
			return;
		}

		char ch = convert(slot);
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

		// Although we may be adding null consumers, it lets us track what slots have been set
		slots.put(ch, new SlotData(consumer, removable));

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
		char ch = convert(slot);
		if (ch == 0) {
			return new ItemStack(Material.AIR);
		}
		ItemStack item = inventory.getItem(rawShape.indexOf(ch));
		return item != null ? item : new ItemStack(Material.AIR);
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

		String[] shape = new String[size / 9];

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

		String newShape = sb.toString();
		Map<Character, ItemStack> movedCharacters = new HashMap<>();

		if (rawShape != null) {
			int pos = 0;
			for (char ch : rawShape.toCharArray()) {
				if (rawShape.indexOf(ch) == pos) { // Only check a character once
					if (newShape.indexOf(ch) == -1) { // This character IS NOT in the new shape
						clear(ch);
					} else { // This character IS in the new shape
						movedCharacters.put(ch, getItem(ch));
					}
				}
				pos++;
			}
		}

		// Clear out the slots of characters that are new to the shape (just in case they were occupied before)
		// We only need to clear the slot of the item as actions (clicking, stealing, etc.) will already have been changed
		if (rawShape != null) {
			for (int i = 0; i < inventory.getSize(); i++) {
				if (rawShape.indexOf(newShape.charAt(i)) == -1) { // This character was NOT in the old shape
					inventory.clear(i);
				}
			}
		}

		rawShape = newShape;

		// Move around items for the moved characters
		for (Entry<Character, ItemStack> movedCharacter : movedCharacters.entrySet()) {
			Character ch = movedCharacter.getKey();
			SlotData slotData = getSlotData(ch);
			if (slotData != null) { // Make sure the character was actually used, see https://github.com/APickledWalrus/skript-gui/issues/133
				setItem(ch, movedCharacter.getValue(), slotData.isRemovable(), slotData.getRunOnClick());
			}
		}

	}

	/**
	 * @return Whether the items in this GUI can be removed by default.
	 * It's important to note that items with consumers/click triggers can <b>never</b> be removed, regardless of this setting.
	 */
	public boolean isRemovable() {
		return removableItems;
	}

	/**
	 * @return Whether the given slot in this GUI can have its item removed.
	 * Will always return true if {@link #isRemovable()}} is true and the slot does not have a click consumer associated with it.
	 */
	public boolean isRemovable(Character slot) {
		SlotData slotData = slots.get(slot);
		return slotData != null ? isRemovable(slotData) : removableItems;
	}

	/**
	 * Internal method for determining whether a slot can have its item removed.
	 */
	private boolean isRemovable(SlotData slotData) {
		// Removable IF all GUI items are removable and this item does not have a click consumer OR if the SlotData is marked as removable
		return (removableItems && slotData.getRunOnClick() == null) || slotData.isRemovable();
	}

	/**
	 * @param stealableItems Whether items in this GUI can be removed by default.
	 */
	public void setRemovable(boolean stealableItems) {
		this.removableItems = stealableItems;
	}

	/**
	 * Sets the consumer to be run when this GUI is opened.
	 * @param onOpen The consumer to be run when this GUI is opened.
	 */
	public void setOnOpen(Consumer<InventoryOpenEvent> onOpen) {
		this.onOpen = onOpen;
	}

	/**
	 * Sets the consumer to be run when this GUI is closed.
	 * @param onClose The consumer to be run when this GUI is closed.
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
	 * @param id The new id for this GUI. If null, it will be removed from the {@link GUIManager} and cleared unless it has viewers.
	 */
	public void setID(@Nullable String id) {
		this.id = id;
		if (id == null && inventory.getViewers().isEmpty()) {
			SkriptGUI.getGUIManager().unregister(this);
			clear();
		}
	}

	/**
	 * Returns the SlotData for the provided slot. SlotData contains properties of a GUI slot.
	 * @param slot The slot to find data for.
	 * @return The SlotData for the provided slot, or null if no SlotData exists.
	 */
	@Nullable
	public SlotData getSlotData(Character slot) {
		return slots.get(slot);
	}

	/**
	 * SlotData contains the properties of a GUI slot.
	 */
	public static final class SlotData {

		@Nullable
		private Consumer<InventoryClickEvent> runOnClick;
		@Nullable
		private Consumer<InventoryClickEvent> runOnChange;
		private boolean removable;

		public SlotData(@Nullable Consumer<InventoryClickEvent> runOnClick, boolean removable) {
			this.runOnClick = runOnClick;
			this.removable = removable;
		}

		/**
		 * @return The consumer to run when a slot with this data is clicked.
		 */
		@Nullable
		public Consumer<InventoryClickEvent> getRunOnClick() {
			return runOnClick;
		}

		@Nullable
		public Consumer<InventoryClickEvent> getRunOnChange() {
			return runOnChange;
		}

		/**
		 * Updates the consumer to run when a slot with this data is clicked. A null value may be used to remove the consumer.
		 * @param runOnClick The consumer to run when a slot with this data is clicked.
		 */
		public void setRunOnClick(@Nullable Consumer<InventoryClickEvent> runOnClick) {
			this.runOnClick = runOnClick;
		}

		public void setRunOnChange(@Nullable Consumer<InventoryClickEvent> runOnChange) {
			this.runOnChange = runOnChange;
		}

		/**
		 * @return Whether this item can be removed from its slot, regardless of {@link GUI#isRemovable()}.
		 * 	Please note that if {@link #getRunOnClick()} returns a non-null value, this method will <b>always</b> return false.
		 */
		public boolean isRemovable() {
			return removable;
		}

		/**
		 * Updates whether this item can be removed from its slot.
		 * Please note that if {@link #getRunOnClick()} returns a non-null value, this method will have no effect.
		 * @param removable Whether this item can be removed from its slot.
		 */
		public void setRemovable(boolean removable) {
			this.removable = removable;
		}

	}

}
