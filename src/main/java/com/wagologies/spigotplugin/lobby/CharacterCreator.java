package com.wagologies.spigotplugin.lobby;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.campaign.PointOfInterest;
import com.wagologies.spigotplugin.entity.AbilityScores;
import com.wagologies.spigotplugin.player.OfflinePlayer;
import com.wagologies.spigotplugin.player.StarterKit;
import com.wagologies.spigotplugin.utils.ItemHelper;
import com.wagologies.spigotplugin.utils.StringHelper;
import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Stream;

public class CharacterCreator extends InventoryView implements Listener {
    private static final Map<Integer, AbilityScores.AbilityScore> ABILITY_SCORE_SLOT_MAP = new HashMap<>() {{
        put(10, AbilityScores.AbilityScore.STRENGTH);
        put(13, AbilityScores.AbilityScore.DEXTERITY);
        put(16, AbilityScores.AbilityScore.CONSTITUTION);
        put(29, AbilityScores.AbilityScore.INTELLIGENCE);
        put(31, AbilityScores.AbilityScore.WISDOM);
        put(33, AbilityScores.AbilityScore.CHARISMA);
    }};
    private static final int NameChangeSlot = 52;
    private static final int NextArrowSlot = 53;
    private final SpigotPlugin plugin;
    private final Player player;
    private String name = "";
    private final Inventory topInventory;
    private final BiMap<AbilityScores.AbilityScore, Integer> abilityScorePointMap = HashBiMap.create();
    private final List<Integer> pointValues = new ArrayList<>();
    private boolean pageIsItemSelector = false;
    private StarterKit starterKit = null;
    private Campaign campaign;

    public CharacterCreator(SpigotPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        for (int i = 0; i < 6; i++) {
            pointValues.add(rollPointScore());
        }
        topInventory = Bukkit.createInventory(null, 54);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public int rollPointScore() {
        int[] rolls = new int[4];
        Random random = new Random();
        for (int i = 0; i < rolls.length; i++) {
            rolls[i] = random.nextInt(6) + 1;
        }
        return Arrays.stream(rolls).sum() - Arrays.stream(rolls).min().getAsInt();
    }

    public void setupInventory() {
        topInventory.clear();
        for (int i = 0; i < topInventory.getSize(); i++) {
            topInventory.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        }

        PlayerInventory playerInventory = (PlayerInventory) getBottomInventory();
        playerInventory.clear();
        for (int slot = 0; slot < 36; slot ++) {
            playerInventory.setItem(slot, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        }

        if(!pageIsItemSelector) {
            updateAbilityScores(false);
            ItemStack changeName = new ItemStack(Material.NAME_TAG);
            ItemMeta changeNameMeta = changeName.getItemMeta();
            assert changeNameMeta != null;
            changeNameMeta.setDisplayName(ChatColor.RESET.toString() + ChatColor.WHITE + "Rename Character");
            changeNameMeta.setLore(StringHelper.prependWithColor(StringHelper.wrapItemLore("\nClick here to change your character's name."), ChatColor.GRAY.toString()));
            changeName.setItemMeta(changeNameMeta);
            topInventory.setItem(NameChangeSlot, changeName);


            int[] playerGoldHotbarSlots = new int[] {1, 2, 3, 5, 6, 7};

            for (int i = 0; i < playerGoldHotbarSlots.length; i++) {
                if(!abilityScorePointMap.containsValue(i)) {
                    playerInventory.setItem(playerGoldHotbarSlots[i], createPointItem(i));
                } else {
                    playerInventory.setItem(playerGoldHotbarSlots[i], null);
                }
            }
        } else {
            topInventory.setItem(NextArrowSlot, createNextArrow());
            for (int i = 0; i < StarterKit.StarterKits.length; i++) {
                StarterKit kit = StarterKit.StarterKits[i];
                ItemStack displayItem = kit.getDisplayItem();
                if(kit.equals(starterKit)) {
                    ItemMeta kitMeta = displayItem.getItemMeta();
                    assert kitMeta != null;
                    kitMeta.setDisplayName(ChatColor.RESET.toString() + ChatColor.GREEN + "[>] " + kitMeta.getDisplayName());
                    displayItem.setItemMeta(kitMeta);
                    ItemHelper.AddGlow(displayItem);
                }
                topInventory.setItem(i + 10, displayItem);
            }
        }

        if(isOpen()) {
            player.updateInventory();
        }
    }

    public boolean canGoNext() {
        return (!pageIsItemSelector && abilityScorePointMap.size() == 6 && !name.isEmpty()) || (pageIsItemSelector && starterKit != null);
    }

    private void updateAbilityScores() {
        this.updateAbilityScores(true);
    }

    private void updateAbilityScores(boolean updateInventory) {
        ABILITY_SCORE_SLOT_MAP.forEach(this::setupAbilityScore);
        topInventory.setItem(NextArrowSlot, createNextArrow());
        if(updateInventory && isOpen()) {
            player.updateInventory();
        }
    }

    public void setupAbilityScore(int topSlot, AbilityScores.AbilityScore scoreType) {
        ItemStack topStack = scoreType.getItemStack();
        Integer pointIndex = abilityScorePointMap.get(scoreType);
        ItemStack scoreItem = null;
        if (pointIndex != null) {
            scoreItem = createPointItem(pointIndex);
            ItemMeta meta = topStack.getItemMeta();
            assert meta != null;
            meta.setDisplayName(meta.getDisplayName() + ": " + ChatColor.GOLD + pointValues.get(pointIndex));
            topStack.setItemMeta(meta);
        }
        topInventory.setItem(topSlot, topStack);

        int bottomSlot = topSlot + 9;
        topInventory.setItem(bottomSlot, scoreItem);
    }

    private ItemStack createNextArrow() {
        boolean isNextDisabled = !canGoNext();
        ItemStack nextArrow = new ItemStack(isNextDisabled ? Material.BARRIER : Material.ARROW);
        ItemMeta nextArrowMeta = nextArrow.getItemMeta();
        assert nextArrowMeta != null;
        nextArrowMeta.setDisplayName(ChatColor.RESET.toString() + (isNextDisabled ? ChatColor.RED : ChatColor.GREEN) + (pageIsItemSelector ? "Create Character" : "Next Step"));
        nextArrowMeta.setLore(
                Stream.concat(
                        StringHelper.prependWithColor(StringHelper.wrapItemLore("\nClick here to proceed!"), ChatColor.GRAY.toString()).stream(),
                        StringHelper.prependWithColor(StringHelper.wrapItemLore("\nYou will not be able to return!"), ChatColor.RED.toString() + ChatColor.BOLD).stream()
                ).toList()
        );
        nextArrow.setItemMeta(nextArrowMeta);
        return nextArrow;
    }

    public ItemStack createPointItem(int pointIndex) {
        int pointValue = pointValues.get(pointIndex);
        ItemStack stack = new ItemStack(Material.GOLD_NUGGET, pointValue);
        ItemMeta meta = stack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.RESET.toString() + ChatColor.GOLD + pointValue + " Points");
        meta.setLore(StringHelper.prependWithColor(StringHelper.wrapItemLore("\nPlace this under an ability to assign it!"), ChatColor.YELLOW.toString()));
        PersistentDataContainer persistentDataContainer = meta.getPersistentDataContainer();
        persistentDataContainer.set(getPointsKey(), PersistentDataType.INTEGER, pointIndex);
        stack.setItemMeta(meta);
        return stack;
    }

    public NamespacedKey getPointsKey() {
        return new NamespacedKey(plugin, "character-points");
    }

    public int getAbilityScore(AbilityScores.AbilityScore scoreType) {
        if(!abilityScorePointMap.containsKey(scoreType)) {
            return -1;
        }
        return pointValues.get(abilityScorePointMap.get(scoreType));
    }

    public boolean isOpen() {
        return this.player.getOpenInventory().equals(this);
    }

    public void open() {
        if(name.isEmpty()) {
            openNameGUI();
            return;
        }
        player.openInventory(this);
        setupInventory();
    }

    public void openNameGUI() {
        if(isOpen()) {
            player.closeInventory();
        }
        SignGUI gui = SignGUI.builder()
                .setLines(name, "-------------", "Name your", "character")
                .setHandler((p, result) -> {
                    String characterName = result.getLine(0);
                    if(characterName.isEmpty()) {
                        return Collections.emptyList();
                    }
                    setName(characterName);
                    return List.of(
                            SignGUIAction.runSync(plugin, this::open)
                    );
                })
                .build();
        gui.open(player);
    }

    public void setName(String newName) {
        this.name = newName;
        if(isOpen()) {
            player.updateInventory();
        }
    }

    public AbilityScores createAbilityScores() {
        if(abilityScorePointMap.size() != 6) {
            throw new IllegalStateException("Missing ability scores!");
        }
        AbilityScores abilityScores = new AbilityScores();
        for (AbilityScores.AbilityScore abilityScore : AbilityScores.AbilityScore.values()) {
            abilityScores.setScore(abilityScore, getAbilityScore(abilityScore));
        }
        return abilityScores;
    }

    public OfflinePlayer createCharacter() {
        return new OfflinePlayer(player.getUniqueId().toString(), name, createAbilityScores()).setLocation(
                PointOfInterest.NEW_CAMPAIGN.toLocation(campaign.getWorld())).setStarterKit(this.starterKit);
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public CharacterCreator setCampaign(Campaign campaign) {
        this.campaign = campaign;
        return this;
    }

    @Override
    @Nonnull
    public Inventory getTopInventory() {
        return topInventory;
    }

    @Override
    @Nonnull
    public Inventory getBottomInventory() {
        return player.getInventory();
    }

    @Override
    @Nonnull
    public HumanEntity getPlayer() {
        return player;
    }

    @Override
    @Nonnull
    public InventoryType getType() {
        return InventoryType.CHEST;
    }

    @Override
    @Nonnull
    public String getTitle() {
        if(name.isEmpty()) {
            return "Character Creator";
        } else {
            return "Character Creator: " + ChatColor.BOLD + name;
        }
    }

    @Override
    @Nonnull
    public String getOriginalTitle() {
        return "Character Creator";
    }

    @Override
    public void setTitle(@Nonnull String s) {}

    public void remove() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onHotkey(InventoryClickEvent event) {
        if(event.getClick().isKeyboardClick() && event.getWhoClicked().equals(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDisabledClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if(event.getWhoClicked().equals(player)) {
            if(!event.isLeftClick() || event.isShiftClick() || (clickedItem != null && !clickedItem.getType().equals(Material.GOLD_NUGGET))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if(event.getWhoClicked().equals(player)) {
            event.setCancelled(true);
        }
    }

    public boolean clickedSlotInTop(InventoryClickEvent event, int slot) {
        if(event.getWhoClicked().equals(player)) {
            Inventory clickedInventory = event.getClickedInventory();
            if(clickedInventory == null || !clickedInventory.equals(getTopInventory())) {
                return false;
            }
            if(event.getCursor() != null && !event.getCursor().getType().isAir()) {
                return false;
            }
            if(event.getSlot() == slot) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onNextPage(InventoryClickEvent event) {
        if(clickedSlotInTop(event, NextArrowSlot) && canGoNext()) {
            if(!pageIsItemSelector) {
                pageIsItemSelector = true;
                setupInventory();
            } else {
                plugin.getLobbyManager().createNewCharacter(player, createCharacter(), campaign);
            }
        }
    }

    @EventHandler
    public void onClickNameChange(InventoryClickEvent event) {
        if(clickedSlotInTop(event, NameChangeSlot) && !pageIsItemSelector) {
            Bukkit.getScheduler().runTask(plugin, this::openNameGUI);
        }
    }

    @EventHandler
    public void onDropPoint(InventoryClickEvent event) {
        if(event.getWhoClicked().equals(player) && !pageIsItemSelector) {
            ItemStack droppedItem = event.getCursor();
            if(droppedItem == null) {
                return;
            }
            ItemMeta meta = droppedItem.getItemMeta();
            if(meta == null) {
                return;
            }
            PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
            Integer pointIndex = dataContainer.get(getPointsKey(), PersistentDataType.INTEGER);
            if(pointIndex == null) {
                return;
            }
            Inventory clickedInventory = event.getClickedInventory();
            if(clickedInventory == null) {
                return;
            }
            if(event.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) {
                event.setCancelled(true);
                return;
            }
            int slot = event.getSlot();
            int adjustedSlotValue = slot - 9;
            if(abilityScorePointMap.containsValue(pointIndex)) {
                abilityScorePointMap.inverse().remove(pointIndex);
            }
            if(clickedInventory.equals(getTopInventory()) && ABILITY_SCORE_SLOT_MAP.containsKey(adjustedSlotValue)) {
                AbilityScores.AbilityScore scoreType = ABILITY_SCORE_SLOT_MAP.get(adjustedSlotValue);
                abilityScorePointMap.put(scoreType, pointIndex);
            }
            Bukkit.getScheduler().runTask(plugin, () -> updateAbilityScores());
        }
    }

    @EventHandler
    public void onSelectKit(InventoryClickEvent event) {
        if(pageIsItemSelector) {
            for (int i = 0; i < StarterKit.StarterKits.length; i++) {
                if(clickedSlotInTop(event, i + 10)) {
                    if(starterKit != StarterKit.StarterKits[i]) {
                        starterKit = StarterKit.StarterKits[i];
                        setupInventory();
                    }
                    return;
                }
            }
        }
    }
}
