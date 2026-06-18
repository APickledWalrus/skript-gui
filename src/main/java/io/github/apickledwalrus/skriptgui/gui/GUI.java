package io.github.apickledwalrus.skriptgui.gui;

import io.github.apickledwalrus.skriptgui.SkriptGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

public class GUI {

	public static final char UNKNOWN_LAYOUT_SLOT = ' ';

	private Inventory inventory;
	private Component name;

	private final GUIEventHandler eventHandler = new GUIEventHandler() {

		@Override
		public void onClick(InventoryClickEvent clickEvent) {
			if (!(clickEvent.getWhoClicked() instanceof Player player) || isPaused(player)) {
				clickEvent.setCancelled(true);
				return;
			}

			SlotData slotData = getSlotData(convert(clickEvent.getSlot()));
			if (slotData == null) { // if there is no slot data, cancel if this GUI doesn't have stealable items
				clickEvent.setCancelled(!isChangeable());
				return;
			}

			// only cancel if this slot can't be removed AND all items aren't removable
			clickEvent.setCancelled(!isChangeable(slotData));

			// Call onChange if the slot is being changed
			if (!clickEvent.isCancelled()) {
				if (clickEvent.getCurrentItem() == null ||
					!clickEvent.getCursor().isSimilar(clickEvent.getCurrentItem()) ||
					clickEvent.getCurrentItem().getAmount() < clickEvent.getCurrentItem().getMaxStackSize()) {
					onChange(clickEvent);
				}
			}

			Consumer<InventoryClickEvent> runOnClick = slotData.getRunOnClick();
			if (runOnClick != null) {
				SkriptGUI.getGUIManager().setGUI(clickEvent, GUI.this);
				runOnClick.accept(clickEvent);
			}
		}

		@Override
		public void onChange(InventoryClickEvent clickEvent) {
			if (!(clickEvent.getWhoClicked() instanceof Player player) || isPaused(player)) {
				clickEvent.setCancelled(true);
				return;
			}

			SlotData slotData = getSlotData(convert(clickEvent.getSlot()));
			if (slotData != null) {
				// Only cancel if this slot can't be removed AND all items aren't removable
				clickEvent.setCancelled(!isChangeable(slotData));

				Consumer<InventoryClickEvent> runOnChange = slotData.getRunOnChange();
				if (!clickEvent.isCancelled() && runOnChange != null) {
					SkriptGUI.getGUIManager().setGUI(clickEvent, GUI.this);
					runOnChange.accept(clickEvent);
				}
			} else { // If there is no slot data, cancel if this GUI doesn't have stealable items
				clickEvent.setCancelled(!isChangeable());
			}
		}

		@Override
		public void onDrag(InventoryDragEvent dragEvent) {
			if (!(dragEvent.getWhoClicked() instanceof Player player) || isPaused(player)) {
				dragEvent.setCancelled(true);
				return;
			}

			for (int slot : dragEvent.getRawSlots()) {
				if (!isChangeable(convert(slot))) {
					dragEvent.setCancelled(true);
					return;
				}
			}

			// if the drag is permitted, process slot changes
			int guiEnd = inventory.getSize();
			for (int slot : dragEvent.getRawSlots()) {
				if (slot < guiEnd) {
					InventoryClickEvent clickEvent = new InventoryClickEvent(
						dragEvent.getView(),
						dragEvent.getView().getSlotType(slot),
						slot,
						ClickType.UNKNOWN,
						InventoryAction.UNKNOWN
					);
					eventHandler.onChange(clickEvent);
				}
			}
		}

		@Override
		public void onOpen(InventoryOpenEvent openEvent) {
			if (!(openEvent.getPlayer() instanceof Player player) || isPaused(player)) {
				openEvent.setCancelled(true);
				return;
			}

			if (onOpen != null) {
				SkriptGUI.getGUIManager().setGUI(openEvent, GUI.this);
				onOpen.accept(openEvent);
			}
		}

		@Override
		public void onClose(InventoryCloseEvent closeEvent) {
			if (!(closeEvent.getPlayer() instanceof Player player) || isPaused(player)) {
				return;
			}

			if (onClose != null) {
				SkriptGUI.getGUIManager().setGUI(closeEvent, GUI.this);
				onClose.accept(closeEvent);
				if (isCloseCanceled(closeEvent)) {
					Bukkit.getScheduler().runTaskLater(SkriptGUI.getInstance(), () -> {
						// Reset behavior (it shouldn't persist)
						setCloseCanceled(closeEvent, false);

						pause(player); // Avoid calling any open sections
						player.openInventory(inventory);
						resume(player);
					}, 1);
					return;
				}
			}

			if (id == null && inventory.getViewers().size() == 1) { // Only stop tracking if it isn't a global GUI
				Bukkit.getScheduler().runTaskLater(SkriptGUI.getInstance(),
					() -> SkriptGUI.getGUIManager().unregister(GUI.this), 1);
			}

			// To combat issues like https://github.com/APickledWalrus/skript-gui/issues/60
			Bukkit.getScheduler().runTaskLater(SkriptGUI.getInstance(), player::updateInventory, 1);
		}
	};

	public GUI(Inventory inventory, boolean changeable, @Nullable Component name, @Nullable String layout) {
		this.inventory = inventory;
		setChangeable(changeable);
		this.name = name == null ? inventory.getType().defaultTitle() : name;
		setLayout(layout);

		SkriptGUI.getGUIManager().register(this);
	}

	public Inventory getInventory() {
		return inventory;
	}

	public GUIEventHandler getEventHandler() {
		return eventHandler;
	}

	public Component getName() {
		return name;
	}

	public void setName(@Nullable Component name) {
		changeInventory(inventory.getSize(), name);
	}

	public int getSize() {
		return inventory.getSize();
	}

	public void setSize(int size) {
		changeInventory(size, getName());
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

	private void changeInventory(int size, @Nullable Component name) {
		if (name == null) {
			name = inventory.getType().defaultTitle();
		} else if (size < 9) { // Minimum size
			size = 9;
		} else if (size > 54) { // Maximum size
			size = 54;
		} else if (size % 9 != 0) {
			return;
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
	 * Sets a slot's item.
	 * @param slot The slot to put the item in. It will be converted by {@link GUI#convert(Object)}.
	 * @param item The {@link ItemStack} to put in the slot.
	 * @param changeable Whether the item in {@code slot} can be changed.
	 * @param consumer The {@link Consumer} that the slot will run when clicked.
	 * Put as null if the slot should not run anything when clicked.
	 */
	public void setItem(Object slot, @Nullable ItemStack item, boolean changeable,
						@Nullable Consumer<InventoryClickEvent> consumer) {
		char ch = convert(slot);
		if (ch == UNKNOWN_LAYOUT_SLOT) {
			return;
		}

		// Although we may be adding null consumers, it lets us track what slots have been set
		SlotData slotData = new SlotData();
		slotData.setRunOnClick(consumer);
		slotData.setChangeable(changeable);
		slots.put(ch, slotData);

		int i = 0;
		for (char ch1 : layout.toCharArray()) {
			if (ch == ch1 && i < inventory.getSize()) {
				inventory.setItem(i, item);
			}
			i++;
		}
	}

	/*
	 * Layout
	 */

	private String layout;

	/**
	 * @return The layout of this GUI.
	 * @see #setLayout(String)
	 */
	public String getLayout() {
		return layout;
	}

	/**
	 * Sets the layout of this GUI.
	 * @param layout The new layout for this GUI.
	 * @see GUI#getLayout()
	 */
	public void setLayout(@Nullable String layout) {
		if (layout == null) {
			StringBuilder layoutBuilder = new StringBuilder();
			// Create a layout with only unique characters
			int end = 'A' + getSize();
			for (char c = 'A'; c < end; c++) {
				layoutBuilder.append(c);
			}
			layout = layoutBuilder.toString();
		} else if (layout.length() < getSize()) { // Append to empty
			layout = layout + String.valueOf(UNKNOWN_LAYOUT_SLOT).repeat(getSize() - layout.length());
		}

		if (this.layout == null) { // Should only be true during initialization
			this.layout = layout;
			return;
		}

		// Remove data for characters no longer part of the layout
		Map<Character, ItemStack> retained = new HashMap<>();
		int pos = 0;
		for (char ch : this.layout.toCharArray()) {
			if (this.layout.indexOf(ch) == pos) { // Only check a character once
				int index = layout.indexOf(ch);
				if (index == -1) { // This character IS NOT in the new layout
					clear(ch);
				} else { // This character IS in the new layout
					retained.put(ch, inventory.getItem(index));
				}
			}
			pos++;
		}
		// Reposition items for retained characters
		for (Entry<Character, ItemStack> movedCharacter : retained.entrySet()) {
			Character ch = movedCharacter.getKey();
			SlotData slotData = getSlotData(ch);
			if (slotData != null) { // In case the moved character was not actually used
				setItem(ch, movedCharacter.getValue(), slotData.isChangeable(), slotData.getRunOnClick());
			}
		}

		this.layout = layout;
	}

	/**
	 * Converters a {@link Character}, {@link Number}, or {@link String} into a character recognized by the layout.
	 * @param slot The object to convert.
	 * @return A character from the {@link #getLayout()} or {@link #UNKNOWN_LAYOUT_SLOT} if not recognized.
	 */
	public Character convert(Object slot) {
		return switch (slot) {
			case Character character -> character;
			case Number number -> {
				int invSlot = number.intValue();
				// Make sure inventory slot is at least 0 (see https://github.com/APickledWalrus/skript-gui/issues/48)
				if (invSlot >= 0 && invSlot < layout.length()) {
					yield layout.charAt(invSlot);
				}
				yield UNKNOWN_LAYOUT_SLOT;
			}
			case String string -> layout.contains(string) ? string.charAt(0) : UNKNOWN_LAYOUT_SLOT;
			default -> UNKNOWN_LAYOUT_SLOT;
		};
	}

	/**
	 * Obtains the next available slot in this GUI.
	 * @return A character from the {@link #getLayout()} or {@link #UNKNOWN_LAYOUT_SLOT} if not recognized.
	 */
	public Character nextSlot() {
		for (char ch : layout.toCharArray()) {
			if (!slots.containsKey(ch)) {
				return ch;
			}
		}
		return UNKNOWN_LAYOUT_SLOT;
	}

	/**
	 * Obtains the last character of the layout that has been used.
	 * @return A character from the {@link #getLayout()} or {@link #UNKNOWN_LAYOUT_SLOT} if not recognized.
	 */
	public Character nextSlotInverted() {
		char[] chars = layout.toCharArray();
		for (int i = chars.length - 1; i >= 0; i--) {
			char ch = chars[i];
			if (slots.containsKey(ch)) {
				return ch;
			}
		}
		return UNKNOWN_LAYOUT_SLOT;
	}

	/*
	 * Standard Properties
	 */

	private @Nullable String id;

	/**
	 * @return The ID of this GUI if it is a global GUI
	 * @see GUIManager
	 */
	public @Nullable String getID() {
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
	 * Whether all slots of this GUI (excluding those with actions by default) can be changed.
	 */
	private boolean changeable;

	/**
	 * @return Whether slots in this GUI can be changed.
	 * @see #setChangeable(boolean)
	 * @see #isChangeable(Character)
	 * @see #isChangeable(SlotData)
	 */
	public boolean isChangeable() {
		return changeable;
	}

	/**
	 * @param changeable Whether slots in this GUI can be changed.
	 * @see SlotData#setChangeable(boolean)
	 */
	public void setChangeable(boolean changeable) {
		this.changeable = changeable;
	}

	/*
	 * General GUI actions
	 */

	/**
	 * A consumer to run when this GUI is opened (viewed).
	 */
	private @Nullable Consumer<InventoryOpenEvent> onOpen;

	/**
	 * Sets the consumer to be run when this GUI is opened.
	 * @param onOpen The consumer to be run when this GUI is opened.
	 */
	public void setOnOpen(@Nullable Consumer<InventoryOpenEvent> onOpen) {
		this.onOpen = onOpen;
	}

	/**
	 * A consumer to run when this GUI is closed.
	 */
	private @Nullable Consumer<InventoryCloseEvent> onClose;

	/**
	 * Sets the consumer to be run when this GUI is closed.
	 * @param onClose The consumer to be run when this GUI is closed.
	 */
	public void setOnClose(@Nullable Consumer<InventoryCloseEvent> onClose) {
		this.onClose = onClose;
	}

	/**
	 * Tracking for canceling GUI closes.
	 */
	private final Set<Event> closeCanceled = new HashSet<>();

	/**
	 * @param event The event to check.
	 * @return Whether the closing of this GUI (represented through {@code event}) should be canceled.
	 */
	public boolean isCloseCanceled(Event event) {
		return closeCanceled.contains(event);
	}

	/**
	 * Sets whether this GUI's close event should be canceling.
	 * @param cancel Whether the closing of this GUI (represented through {@code event}) should be canceled.
	 */
	public void setCloseCanceled(Event event, boolean cancel) {
		if (cancel) {
			closeCanceled.add(event);
		} else {
			closeCanceled.remove(event);
		}
	}

	/*
	 * Slot Control
	 */

	private final Map<Character, SlotData> slots = new HashMap<>();

	/**
	 * SlotData contains the properties of a GUI slot.
	 */
	public static final class SlotData {

		private @Nullable Consumer<InventoryClickEvent> runOnClick;
		private @Nullable Consumer<InventoryClickEvent> runOnChange;
		private boolean changeable;

		/**
		 * @return The consumer to run when a slot with this data is clicked.
		 */
		public @Nullable Consumer<InventoryClickEvent> getRunOnClick() {
			return runOnClick;
		}

		/**
		 * Updates the consumer to run when a slot with this data is clicked.
		 * A null value may be used to remove the consumer.
		 * @param runOnClick The consumer to run when a slot with this data is clicked.
		 */
		public void setRunOnClick(@Nullable Consumer<InventoryClickEvent> runOnClick) {
			this.runOnClick = runOnClick;
		}

		/**
		 * @return The consumer to run when a slot with this data is changed.
		 */
		public @Nullable Consumer<InventoryClickEvent> getRunOnChange() {
			return runOnChange;
		}

		/**
		 * Updates the consumer to run when a slot with this data is changed.
		 * A null value may be used to remove the consumer.
		 * @param runOnChange The consumer to run when a slot with this data is changed.
		 */
		public void setRunOnChange(@Nullable Consumer<InventoryClickEvent> runOnChange) {
			this.runOnChange = runOnChange;
		}

		/**
		 * @return Whether the item contained in this slot can be changed.
		 */
		public boolean isChangeable() {
			return changeable;
		}

		/**
		 * Updates whether the item contained in this slot can be changed.
		 * @param changeable Whether the item contained in this slot can be changed.
		 */
		public void setChangeable(boolean changeable) {
			this.changeable = changeable;
		}

	}

	/**
	 * Returns the SlotData for the provided slot. SlotData contains properties of a GUI slot.
	 * @param slot The slot to find data for.
	 * @return The SlotData for the provided slot, or null if no SlotData exists.
	 */
	public @Nullable SlotData getSlotData(Character slot) {
		return slots.get(slot);
	}

	/**
	 * @param slot The slot to check.
	 * @return Whether the given slot in this GUI can have its item changed.
	 * If there is no data associated with this slot, this method defers to {@link #isChangeable()}.
	 * Otherwise, see {@link #isChangeable(SlotData)} for detailed behavior.
	 */
	public boolean isChangeable(Character slot) {
		SlotData slotData = getSlotData(slot);
		return slotData == null ? isChangeable() : isChangeable(slotData);
	}

	/**
	 * @param slotData The slot data to check.
	 * @return Whether the given slot in this GUI can have its item changed.
	 * If the slot data has been explicitly marked as changeable, it is always changeable.
	 * Otherwise, the slot is only changeable if the GUI is marked as changeable ({@link #isChangeable()})
	 *  and the slot does not have a click action.
	 */
	private boolean isChangeable(SlotData slotData) {
		return slotData.isChangeable() || (isChangeable() && slotData.getRunOnClick() == null);
	}

}
