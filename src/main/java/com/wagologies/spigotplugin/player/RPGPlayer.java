package com.wagologies.spigotplugin.player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.base.Strings;
import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.campaign.PointOfInterest;
import com.wagologies.spigotplugin.campaign.QuestManager;
import com.wagologies.spigotplugin.entity.AbilityScores;
import com.wagologies.spigotplugin.entity.DamageSource;
import com.wagologies.spigotplugin.entity.RPGEntity;
import com.wagologies.spigotplugin.event.CastSpellEvent;
import com.wagologies.spigotplugin.event.player.RPGPlayerDeathEvent;
import com.wagologies.spigotplugin.item.Armor;
import com.wagologies.spigotplugin.item.RPGItem;
import com.wagologies.spigotplugin.item.ItemType;
import com.wagologies.spigotplugin.item.Wand;
import com.wagologies.spigotplugin.spell.SpellCast;
import com.wagologies.spigotplugin.spell.SpellType;
import com.wagologies.spigotplugin.utils.SerializeInventory;
import com.wagologies.spigotplugin.utils.StringHelper;
import com.xism4.sternalboard.SternalBoard;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RPGPlayer extends RPGEntity {
    private final Player player;
    private final PlayerListener playerListener;
    private SpellCast spellCast = null;
    private int mana = 100;
    private int coins = 100;
    private String actionBarCenter = "";
    private final Campaign campaign;
    private StarterKit starterKit;
    private boolean isInConversation = false;
    private boolean isInArena = false;
    private boolean isInDungeon = false;
    private boolean isInBattle = false;
    private List<SpellType> knownSpells = new ArrayList<>();
    private SternalBoard scoreboard;

    public RPGPlayer(Player player, SpigotPlugin plugin, Campaign campaign) {
        super(plugin);
        this.player = player;
        this.playerListener = new PlayerListener(plugin, this);
        this.campaign = campaign;
        this.player.setGameMode(GameMode.ADVENTURE);
        setupScoreboard();
        setHealth(getMaxHealth());
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount % 4 == 0) {
            mana = Math.clamp(mana + 1, 0, getMaxMana());
        }
        if(tickCount % 80 == 0 && !isInCombat()) {
            int regenAmount = (int) Math.round(((float)getMaxHealth()/60) * Math.exp(-((double) getHealth()/1000)));
            if(getHealth() < getMaxHealth()) {
                setHealth(getHealth() + regenAmount);
            }
        }

        if(isInConversation()) {
            if(!player.hasPotionEffect(PotionEffectType.SLOW)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, -1, 3, false, false));
            }
        } else {
            if(player.hasPotionEffect(PotionEffectType.SLOW)) {
                player.removePotionEffect(PotionEffectType.SLOW);
            }
        }

        sendDefaultActionBar();
        if (spellCast != null) {
            spellCast.tick();
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        boolean wasInArena = isInArena;

        RPGPlayerDeathEvent deathEvent = new RPGPlayerDeathEvent(this, damageSource);
        Bukkit.getPluginManager().callEvent(deathEvent);
        player.sendMessage(ChatColor.RED + "You died!");

        if(wasInArena) {
            player.teleport(PointOfInterest.ARENA_RESPAWN.toLocation(player.getWorld()));
            setHealth(getMaxHealth());
            isInArena = false;
            return;
        }
        super.die(damageSource);

        setHealth(getMaxHealth());
        setMana(getMaxMana());
        QuestManager.Type quest = campaign.getQuestManager().getCurrentQuest();
        player.teleport(quest.getRespawnLocation().toLocation(campaign.getWorld()));
    }

    @Override
    public void remove(boolean isDead) {
        if(!isDead) {
            super.remove(false);
            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getName());
            updateOfflinePlayer();
            removeScoreboard();
            playerListener.removeListener();
        }
    }

    public void updateOfflinePlayer() {
        OfflinePlayer offlinePlayer = campaign.getCharacter(player);
        if(offlinePlayer == null) {
            throw new IllegalStateException("Player doesn't exist in this campaign");
        }
        offlinePlayer
                .setInventoryString(SerializeInventory.playerInventoryToBase64(player.getInventory()))
                .setAbilityScores(getAbilityScores())
                .setLocation(getSaveLocation())
                .setStarterKit(starterKit)
                .setCoins(coins)
                .setKnownSpells(this.knownSpells.toArray(new SpellType[0]));
    }

    @Override
    public void setHealth(int newHealth) {
        super.setHealth(newHealth);
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert attribute != null;
        double maxHealth = getScaledMaxHealth();
        if(attribute.getBaseValue() != maxHealth) {
            attribute.setBaseValue(maxHealth);
        }
        double newScaledHealth = getScaledHealth();
        if(newHealth == 0) {
            newScaledHealth = getScaledMaxHealth();
        }
        player.setHealth(newScaledHealth);
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.UPDATE_HEALTH);
        packet.getFloat().write(0, (float) player.getHealth()).write(1, player.getSaturation());
        packet.getIntegers().write(0, player.getFoodLevel());
        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            plugin.getLogger().warning("Failed to send update health packet!");
        }
    }

    public double getScaledHealth() {
        return ((double) getHealth()/getMaxHealth()) * getScaledMaxHealth();
    }

    public double getScaledMaxHealth() {
        return Math.log10(getMaxHealth()) * 10;
    }

    public void sendDefaultActionBar() {
        int middleSize = 20;
        int centerLength = actionBarCenter.length();
        String paddedCenter = actionBarCenter;
        int neededSpaces = middleSize - centerLength;
        if(neededSpaces > 0) {
            paddedCenter = Strings.repeat(" ", Math.ceilDiv(neededSpaces, 2)) + actionBarCenter + Strings.repeat(" ", Math.floorDiv(neededSpaces, 2));
        }
        String actionBarString = ChatColor.RED.toString() + getHealth() + " / " + getMaxHealth() + " " + paddedCenter + " " + ChatColor.BLUE + mana + " / " + getMaxMana();
        plugin.getActionBar().sendActionBar(player, actionBarString);
    }

    public void sendSpellActionBar(SpellType spellType) {
        int duration = 40;
        actionBarCenter = spellType.getColoredNameAndCost();
        Bukkit.getScheduler().runTaskLater(plugin, () -> actionBarCenter = "", duration + 1);
    }

    public boolean canChargeSpell() {
        if (player.isFlying()) {
            return false;
        }
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType().isAir() || !itemStack.getType().isItem()) {
            return false;
        }
        return RPGItem.GetItemType(itemStack).equals(ItemType.WAND);
    }

    protected void beginChargingSpell() {
        this.spellCast = new SpellCast(this, this.player.getLocation());
    }

    protected void cancelChargingSpell() {
        if (this.spellCast != null) {
            this.spellCast = null;
        }
    }

    protected void chargeSpell() {
        if (spellCast != null) {
            List<SpellCast.SpellLine> spellLines = spellCast.getPatternLines();
            ItemStack holdingItem = player.getInventory().getItemInMainHand();
            if (!spellLines.isEmpty() && RPGItem.GetItemType(holdingItem) == ItemType.WAND) {
                Wand wand = (Wand) RPGItem.ConvertToCustomItem(plugin, holdingItem);
                assert wand != null;

                SpellType spellType = SpellType.fromLines(spellLines);

                boolean success = spellType != null && this.knownSpells.contains(spellType);

                List<Location> castingPoints = spellCast.getCastingPoints();
                Particle.DustOptions dustOptions = new Particle.DustOptions(success ? Color.fromRGB(0x00aa14) : Color.fromRGB(0xe02626), 1.0F);

                for (Location castingPoint : castingPoints) {
                    player.getWorld().spawnParticle(Particle.REDSTONE, castingPoint, 1, dustOptions);
                }

                if (success) {
                    wand.setLoadedSpell(spellType);
                    player.playSound(player, Sound.ENTITY_BREEZE_INHALE, 10, 1);
//                    player.sendMessage(ChatColor.GREEN + "Your wand is now charged with " + spellType.getColoredName());
                }
            }
            cancelChargingSpell();
        }
    }

    protected void clearSpell(ItemStack itemStack) {
        if (itemStack.getType().isAir() || RPGItem.GetItemType(itemStack) != ItemType.WAND) {
            return;
        }

        Wand wand = ((Wand) RPGItem.ConvertToCustomItem(plugin, itemStack));
        assert wand != null;
        if (wand.isSpellLoaded()) {
            wand.setLoadedSpell(null);
            player.sendMessage(ChatColor.RED + "You lost focused and discharged your spell!");
        }
    }

    protected void castSpell(SpellType spellType, Wand wand) {
        int manaCost = spellType.getManaCost();
        if (manaCost > mana) {
            player.sendMessage(ChatColor.RED + "You are too exhausted to cast " + spellType.getColoredNameAndCost());
            return;
        }
        CastSpellEvent event = new CastSpellEvent(this, spellType);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        mana -= manaCost;
        plugin.getSpellManager().castSpell(this, spellType);
        sendSpellActionBar(spellType);
        wand.setLoadedSpell(null);
    }
    public void updateSpellBook() {
        updateSpellBook(false);
    }

    public void updateSpellBook(boolean giveIfAbsent) {
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if(itemStack != null && itemStack.getType().equals(Material.WRITTEN_BOOK)) {
                inventory.setItem(i, getSpellBookItem());
                return;
            }
        }
        if(giveIfAbsent) {
            inventory.addItem(getSpellBookItem());
        }
    }

    public void loadFromOffline(OfflinePlayer character) {
        AbilityScores loadedScores = character.getAbilityScores();
        this.setAbilityScores(loadedScores.getStrength(), loadedScores.getDexterity(), loadedScores.getConstitution(), loadedScores.getIntelligence(), loadedScores.getWisdom(), loadedScores.getCharisma());
        this.starterKit = character.getStarterKit();
        this.coins = character.getCoins();
        this.knownSpells = Arrays.stream(character.getKnownSpells()).collect(Collectors.toList());
        player.getInventory().setContents(character.getInventoryContents());
        player.setDisplayName(character.getName());
        player.setPlayerListName(character.getName());
        player.teleport(character.getLocation());
        updateScoreboard();
    }

    private void setupScoreboard() {
        scoreboard = new SternalBoard(this.player);
        scoreboard.updateTitle("Avalan Quest");
        updateScoreboard();
    }

    private void removeScoreboard() {
        scoreboard.delete();
    }

    public void updateScoreboard() {
        List<String> lines = new ArrayList<>(List.of(
                "Welcome: " + ChatColor.AQUA + getName(),
                " ",
                "Coins: " + ChatColor.GOLD + coins,
                " ",
                "Current Quest:"));
        QuestManager.Type quest = campaign.getQuestManager().getCurrentQuest();
        lines.addAll(StringHelper.prependWithColor(StringHelper.wrapItemLore(quest.getTitle(), 30), " " + ChatColor.YELLOW, false));
        scoreboard.updateLines(lines);
    }

    @Override
    public boolean doDamageTarget(RPGEntity target) {
        this.player.getAttackCooldown();
        return super.doDamageTarget(target);
    }

    @Override
    public boolean canTarget(RPGEntity entity) {
        if(isInArena) {
            return !entity.isInvulnerable();
        } else {
            return super.canTarget(entity);
        }
    }

    public Location getSaveLocation() {
        if(isInArena) {
            return PointOfInterest.ARENA_RESPAWN.toLocation(getWorld());
        }
        if(isInDungeon) {
            return PointOfInterest.DUNGEON_EXIT_POINT.toLocation(getWorld());
        }
        return player.getLocation();
    }

    public PlayerListener getPlayerListener() {
        return playerListener;
    }

    public SpellCast getSpellCast() {
        return spellCast;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getMaxMana() {
        return 100;
    }

    public int getMaxHealth() {
        return 100;
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
    }

    @Override
    public void playHurtAnimation() {
        player.playHurtAnimation(0);
    }

    @Override
    public Location getEyeLocation() {
        return player.getEyeLocation();
    }

    @Override
    public World getWorld() {
        return player.getWorld();
    }

    @Override
    public BoundingBox getBoundingBox() {
        return player.getBoundingBox();
    }

    @Override
    public Entity getMainEntity() {
        return player;
    }

    @Override
    public RPGItem getHeldItem() {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        return RPGItem.ConvertToCustomItem(plugin, itemStack);
    }

    @Override
    public boolean isInvulnerable() {
        return player.getGameMode() == GameMode.CREATIVE || player.isInvulnerable() || isInConversation();
    }

    @Override
    public Armor[] getArmor() {
        PlayerInventory inventory = player.getInventory();
        Armor[] armorSet = new Armor[4];
        ItemStack[] minecraftArmor = inventory.getArmorContents();
        for (int i = 0; i < minecraftArmor.length; i++) {
            ItemStack armorPiece = minecraftArmor[i];
            if(armorPiece == null) {
                continue;
            }
            RPGItem item = RPGItem.ConvertToCustomItem(plugin, armorPiece);
            if(item instanceof Armor armor) {
                armorSet[i] = armor;
            }
        }
        return armorSet;
    }

    public ItemStack getSpellBookItem() {
        ItemStack bookStack = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) bookStack.getItemMeta();
        assert bookMeta != null;
        List<String> spellPages = new ArrayList<>();
        if(getKnownSpells().size() == 0) {
            spellPages.add("Empty Spell Book");
        }
        for (SpellType knownSpell : getKnownSpells()) {
            StringBuilder sb = new StringBuilder();
            sb.append(ChatColor.LIGHT_PURPLE)
                    .append(ChatColor.BOLD)
                    .append(knownSpell.getName())
                    .append(ChatColor.RESET)
                    .append("\n\n")
                    .append(ChatColor.BLUE)
                    .append("Mana Cost: ")
                    .append(ChatColor.BOLD)
                    .append(knownSpell.getManaCost())
                    .append("\n")
                    .append(ChatColor.BOLD)
                    .append(ChatColor.DARK_GRAY)
                    .append("Description:\n")
                    .append(ChatColor.RESET)
                    .append(ChatColor.GRAY)
                    .append(knownSpell.getDescription())
                    .append(ChatColor.RESET)
                    .append(ChatColor.DARK_GREEN)
                    .append(ChatColor.BOLD)
                    .append("\nIncantation:\n")
                    .append(ChatColor.RESET);
            List<SpellCast.SpellLine> spellLines = knownSpell.getIncantation();
            for (SpellCast.SpellLine spellLine : spellLines) {
                sb.append(spellLine.getColor())
                        .append(spellLine.getLineName()).append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
            spellPages.add(sb.toString());
        }
        bookMeta.setPages(spellPages);
        bookMeta.setTitle(ChatColor.LIGHT_PURPLE + "Spell Book");
        bookMeta.setAuthor(getName());
        bookStack.setItemMeta(bookMeta);
        return bookStack;
    }

    @Override
    public String getName() {
        return player.getDisplayName();
    }

    public Campaign getCampaign() {
        return campaign;
    }
    public StarterKit getStarterKit() {
        return starterKit;
    }

    public boolean isInConversation() {
        return isInConversation;
    }

    public void setInConversation(boolean inConversation) {
        isInConversation = inConversation;
    }

    public boolean isInArena() {
        return isInArena;
    }

    public void setInArena(boolean inArena) {
        isInArena = inArena;
    }

    public boolean isInDungeon() {
        return isInDungeon;
    }

    public RPGPlayer setInDungeon(boolean inDungeon) {
        isInDungeon = inDungeon;
        return this;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
        updateScoreboard();
    }

    public void gainCoins(int coins) {
        this.setCoins(getCoins() + coins);
    }

    public boolean payCoins(int payment) {
        if(getCoins() >= payment) {
            setCoins(getCoins() - payment);
            return true;
        }
        return false;
    }

    public List<SpellType> getKnownSpells() {
        return this.knownSpells;
    }

    public void learnSpell(SpellType type) {
        if(!knownSpells.contains(type)) {
            knownSpells.add(type);
            updateSpellBook();
        }
    }

    public void unlearnSpell(SpellType type) {
        if(knownSpells.contains(type)) {
            knownSpells.remove(type);
            updateSpellBook();
        }
    }
}
