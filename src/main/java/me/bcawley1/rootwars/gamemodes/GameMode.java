package me.bcawley1.rootwars.gamemodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.bcawley1.rootwars.RootWars;
import me.bcawley1.rootwars.events.LobbyEvent;
import me.bcawley1.rootwars.generator.Generator;
import me.bcawley1.rootwars.generator.GeneratorData;
import me.bcawley1.rootwars.mixin.ItemStackMixin;
import me.bcawley1.rootwars.runnables.Regen;
import me.bcawley1.rootwars.runnables.Respawn;
import me.bcawley1.rootwars.shop.Shop;
import me.bcawley1.rootwars.util.GameTeam;
import me.bcawley1.rootwars.util.Potion;
import me.bcawley1.rootwars.util.RepeatableEvent;
import me.bcawley1.rootwars.util.ScheduledEvent;
import me.bcawley1.rootwars.vote.Votable;
import me.bcawley1.rootwars.vote.Vote;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class GameMode implements Listener, Votable {
    @JsonIgnore
    protected static Map<String, GameMode> gameModes = new HashMap<>();
    @JsonProperty("name")
    protected String name;
    @JsonProperty("description")
    protected String description;
    @JsonProperty
    protected int respawnTime;
    @JsonProperty("playerHealth")
    protected int playerHealth;
    @JsonProperty("teams")
    protected List<GameTeam> teams;
    @JsonProperty
    protected int regenTime;
    @JsonProperty("effects")
    protected List<Potion> effects;
    @JsonIgnore
    protected Regen regen;
    @JsonProperty("teamGeneratorTiers")
    protected GeneratorData[] playerGeneratorUpgradeData;
    @JsonIgnore
    protected List<Generator> emeraldGenerators;
    @JsonProperty("emeraldGeneratorTiers")
    protected GeneratorData[] emeraldUpgradeData;
    @JsonIgnore
    protected List<Generator> diamondGenerators;
    @JsonProperty("diamondGeneratorTiers")
    protected GeneratorData[] diamondUpgradeData;
    @JsonProperty
    protected List<ScheduledEvent> events;
    @JsonProperty("repeatingEvents")
    protected List<RepeatableEvent> repeatableEvents;
    @JsonIgnore
    protected BukkitTask scoreboardUpdateTask;
    @JsonIgnore
    protected long startTick;
    @JsonIgnore
    protected boolean gameOn;
    @JsonProperty
    protected Shop shop;
    @JsonIgnore
    private ItemStack item;

    protected static void registerGameMode(Class<? extends GameMode> classToSer) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.addMixIn(ItemStack.class, ItemStackMixin.class);
        try {
            GameMode gameMode = objectMapper.readValue(new File(RootWars.getPlugin().getDataFolder() + "/GameModes/%s.json".formatted(classToSer.getSimpleName().toLowerCase())), classToSer);
            gameMode.gameOn = false;
            gameModes.put(gameMode.name, gameMode);
            gameMode.item = Vote.getItem(Material.valueOf(RootWars.COLORS[gameModes.size() % RootWars.COLORS.length] + "_WOOL"), gameMode.name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected GameMode() {
    }

    public void startGame() {
        gameOn = true;
        scoreboardUpdateTask = Bukkit.getScheduler().runTaskTimer(RootWars.getPlugin(), this::updateScoreboard, 0, 20);
        Collections.sort(events);
        startTick = RootWars.getWorld().getGameTime();
        RootWars.getCurrentMap().buildMap();

        //Creates and starts the emerald and diamond generators.
        emeraldGenerators = new ArrayList<>();
        diamondGenerators = new ArrayList<>();
        RootWars.getCurrentMap().getEmeraldGenerators().forEach(loc -> emeraldGenerators.add(new Generator(loc, emeraldUpgradeData)));
        RootWars.getCurrentMap().getDiamondGenerators().forEach(loc -> diamondGenerators.add(new Generator(loc, diamondUpgradeData)));
        diamondGenerators.forEach(Generator::startGenerator);
        emeraldGenerators.forEach(Generator::startGenerator);

        //Starts a health regeneration timer.
        regen = new Regen();
        regen.runTaskTimer(RootWars.getPlugin(), 0, regenTime);

        //Creates the teams listed in the teamColors array.
        teams.forEach(team -> team.resetTeam(playerGeneratorUpgradeData));


        //Assigns players to teams equally.
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (int i = 0; i < players.size(); i++) {
            teams.get(i % teams.size()).addPlayer(players.get(i).getUniqueId());
            players.get(i).setGameMode(org.bukkit.GameMode.SURVIVAL);
        }

        //Initializes players.
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(playerHealth);
            p.setFoodLevel(20);
            p.setHealth(playerHealth);
            effects.forEach(e -> p.addPotionEffect(e.getPotionEffect()));
            initializePlayer(p.getUniqueId());
        });

        //Initializes things related to teams.
        teams.forEach(t -> {
            t.spawnVillagers();
            t.getGenerator().startGenerator();
        });

        //Sets the ScheduledEvents to run at their specific time
        events.forEach(ScheduledEvent::scheduleEvent);

        //Sets the RepeatableEvents to run
        repeatableEvents.forEach(RepeatableEvent::scheduleEvent);

        //registers the events defined in this class.
        Bukkit.getPluginManager().registerEvents(this, RootWars.getPlugin());
    }

    public void endGame() {
        gameOn = false;
        scoreboardUpdateTask.cancel();
        regen.cancel();

        //Creates a new instance of the LobbyEvent class and registers the events defined in it.
        HandlerList.unregisterAll(this);
        LobbyEvent lobbyEvent = new LobbyEvent();
        Bukkit.getPluginManager().registerEvents(lobbyEvent, RootWars.getPlugin());

        //Loops through every entity and removes it if it's not a player. If it is a player, it will be reset back to the lobby.
        for (Entity entity : RootWars.getWorld().getEntities()) {
            if (!entity.getType().equals(EntityType.PLAYER)) {
                entity.remove();
            } else {
                Player player = (Player) entity;
                lobbyEvent.putPlayerInLobby(player.getUniqueId());
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                player.setHealth(20);
                player.getActivePotionEffects().forEach(p -> player.removePotionEffect(p.getType()));
            }
        }

        //removes scheduled events
        events.forEach(ScheduledEvent::cancelEvent);
        repeatableEvents.forEach(RepeatableEvent::cancelEvent);

        //Removes the generator for each team.
        for (GameTeam team : teams) {
            team.getGenerator().stopGenerator();
        }
        emeraldGenerators.forEach(Generator::stopGenerator);
        diamondGenerators.forEach(Generator::stopGenerator);
    }

    @JsonIgnore
    public Runnable getRunnable(String s) {
        try {
            return Events.valueOf(s).getRunnable();
        } catch (Exception e) {
            return null;
        }
    }


    public void onRootBreak(GameTeam team) {
        updateScoreboard();
        for (UUID id : team.getPlayersInTeam()) {
            Player p = Bukkit.getPlayer(id);
            p.playSound(p, Sound.ENTITY_WARDEN_ROAR, SoundCategory.MASTER, 1f, 1f);
            p.sendTitle("Your Root Broke", "", 10, 70, 20);
        }
    }

    @JsonIgnore
    public static Map<String, GameMode> getGameModes() {
        return gameModes;
    }

    @JsonIgnore
    public boolean isGameOn() {
        return gameOn;
    }

    @JsonIgnore
    @Override
    public ItemStack getItem() {
        return item.clone();
    }

    @JsonIgnore
    @Override
    public String getName() {
        return name;
    }

    public void updateScoreboard() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("game", Criteria.DUMMY, "%s%sRoot Wars".formatted(ChatColor.YELLOW, ChatColor.BOLD));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        objective.getScore("Teams:").setScore(teams.size() + 5);
        teams.forEach(t -> {
            objective.getScore(t.getColor().chatColor + "%s%s: %s".formatted(t.getName().substring(0, 1).toUpperCase(), t.getName().substring(1), t.hasRoot() && t.numPlayersInTeam() != 0 ? "✔" : t.numPlayersInTeam())).setScore(teams.indexOf(t) + 5);
        });
        objective.getScore("  ").setScore(4);
        long currentGameTick = RootWars.getWorld().getGameTime() - startTick;
        for (ScheduledEvent event : events) {
            if (event.getDelay() > currentGameTick) {
                long ticksUntilEvent = event.getDelay() - currentGameTick;
                objective.getScore(ChatColor.AQUA + event.getName() + ": " + ChatColor.DARK_GREEN + (ticksUntilEvent > 1200 ? "%s Min".formatted(ticksUntilEvent / 1200) : "%s Sec".formatted(ticksUntilEvent / 20))).setScore(3);
                break;
            }
        }
        objective.getScore(" ").setScore(2);
        objective.getScore(ChatColor.LIGHT_PURPLE + "Root Wars " + ChatColor.WHITE + "on " + ChatColor.YELLOW + "Lopixel").setScore(1);
        //sets new scoreboard to players
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(scoreboard);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType().equals(Material.TNT)) {
            RootWars.getWorld().spawnEntity(event.getBlock().getLocation().add(0.5, 0, 0.5), EntityType.TNT);
            event.getPlayer().getInventory().removeItem(new ItemStack(Material.TNT));
            event.setCancelled(true);
        }
        if (!RootWars.getCurrentMap().isInsideBorders(event.getBlock().getLocation())) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot place blocks outside of the map.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }
        try {
            if (shop.containsTab(event.getView().getOriginalTitle())) {
                if (shop.isTopBar(event.getCurrentItem())) {
                    shop.getTopBarItem(event.getCurrentItem()).onItemClick((Player) event.getWhoClicked());
                } else {
                    shop.getActionItem(event.getCurrentItem()).onItemClick((Player) event.getWhoClicked());
                }
                event.setCancelled(true);
            } else if (event.getView().getOriginalTitle().equalsIgnoreCase("Upgrades") && event.getCurrentItem() != null) {
                shop.getActionItemFromString(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName().split(" Upgrade:")[0])).onItemClick((Player) event.getWhoClicked());
                event.setCancelled(true);
            }
        } catch (NullPointerException e){
            event.setCancelled(true);
        }
    }

    //On player death
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player p && p.getHealth() - event.getFinalDamage() <= 0) {
            event.setCancelled(true);
            respawnPlayer(p.getUniqueId());
            if (GameTeam.getTeam(p.getUniqueId()).hasRoot()) {
                p.setGameMode(org.bukkit.GameMode.SPECTATOR);
                p.teleport(GameTeam.getTeam(p.getUniqueId()).getTeamData().getSpawnPoint());
                startRespawnTimer(respawnTime, p.getUniqueId());
            } else {
                p.setGameMode(org.bukkit.GameMode.SPECTATOR);
                p.teleport(GameTeam.getTeam(p.getUniqueId()).getTeamData().getSpawnPoint());
                GameTeam.getTeam(p.getUniqueId()).removePlayer(p.getUniqueId());

                int teamsAlive = 0;
                for (GameTeam team : teams) {
                    if (team.numPlayersInTeam() > 0) {
                        teamsAlive++;
                    }
                }

                if (teamsAlive <= 1) {
                    endGame();
                }
                updateScoreboard();
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Villager && GameTeam.getTeam(event.getPlayer().getUniqueId()) != null) {
            for (GameTeam team : teams) {
                if (team.isItemVillager(event.getRightClicked().getLocation())) {
                    event.getPlayer().openInventory(shop.getTabs().get(0).getInventoryTab(event.getPlayer()));
                } else if (team.isUpgradeVillager(event.getRightClicked().getLocation())) {
                    event.getPlayer().openInventory(shop.getUpgradeTab(event.getPlayer()));
                }
            }
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getItemInHand().getType().equals(Material.FIRE_CHARGE)) {
            throwFireball(event.getPlayer().getUniqueId());
        }
    }


    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (Generator.droppedByGenerator(event.getItem())) {
            Generator.deleteGeneratorItem(event.getItem());
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (Math.abs(p.getLocation().getX() - event.getEntity().getLocation().getX()) < 1.3 && Math.abs(p.getLocation().getZ() - event.getEntity().getLocation().getZ()) < 1.3) {
                    p.getInventory().addItem(event.getItem().getItemStack());
                }
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        RootWars.defaultJoin(event.getPlayer());
        initializePlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void regenEvent(EntityRegainHealthEvent event) {
        if (event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.REGEN) || event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent event) {
        if (Bukkit.getOnlinePlayers().size() == 1 && Bukkit.getOnlinePlayers().contains(event.getPlayer())) {
            endGame();
        } else if(Bukkit.getOnlinePlayers().stream().filter(p -> p.getGameMode()== org.bukkit.GameMode.SPECTATOR).count() >= Bukkit.getOnlinePlayers().size()-1){
            endGame();
        }
    }

    @EventHandler
    public void hungerChangeEvent(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void entityLoadEvent(EntitiesLoadEvent event) {
        event.getEntities().forEach(entity -> {
            if (entity.getType() == EntityType.ITEM && !Generator.droppedByGenerator((Item) entity)) {
                entity.remove();
            }
        });
    }

    @EventHandler
    public void playerConsumeEvent(PlayerItemConsumeEvent event) {
        if (((PotionMeta) event.getItem().getItemMeta()).getCustomEffects().get(0).getType().equals(PotionEffectType.INVISIBILITY)) {
            ItemStack[] armor = event.getPlayer().getInventory().getArmorContents();
            event.getPlayer().getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
            Bukkit.getScheduler().runTaskLater(RootWars.getPlugin(), () -> event.getPlayer().getInventory().setArmorContents(armor), 2400);
        }
    }

    @EventHandler
    public void blockFormedEvent(BlockFormEvent event) {
        if (!RootWars.getCurrentMap().isInsideBorders(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void bucketUsedEvent(PlayerBucketEmptyEvent event) {
        if (!RootWars.getCurrentMap().isInsideBorders(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    protected static void throwFireball(UUID id) {
        Player p = Bukkit.getPlayer(id);
        Location loc = p.getEyeLocation().toVector().add(p.getLocation().getDirection().multiply(2)).
                toLocation(p.getWorld(), p.getLocation().getYaw(), p.getLocation().getPitch());

        Fireball fireball = p.getWorld().spawn(loc, Fireball.class);
        fireball.setYield(RootWars.getPlugin().getConfig().getInt("fireball-strength"));
        p.getInventory().removeItem(new ItemStack(Material.FIRE_CHARGE, 1));
    }

    protected static void respawnPlayer(UUID id) {
        Player p = Bukkit.getPlayer(id);
        Bukkit.broadcastMessage(RootWars.getPlugin().getConfig().getString("death-message").replace("{player}", p.getName()));
        p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
    }

    protected static void startRespawnTimer(int time, UUID id) {
        new Respawn(time, id).runTaskTimer(RootWars.getPlugin(), 0, 20);
    }

    protected void initializePlayer(UUID id) {
        Player p = Bukkit.getPlayer(id);
        GameTeam team = GameTeam.getTeam(id);
        if (team != null) {
            RootWars.respawnPlayer(id);
            p.setPlayerListName(team.getColor().chatColor + p.getName());
            p.getInventory().setChestplate(getTeamChestplate(id));
        } else {
            p.setGameMode(org.bukkit.GameMode.SPECTATOR);
            p.teleport(RootWars.getCurrentMap().getDiamondGenerators().get(0));
        }
        updateScoreboard();
    }

    @JsonIgnore
    protected ItemStack getTeamChestplate(UUID id) {
        Player p = Bukkit.getPlayer(id);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemMeta meta = chestplate.getItemMeta();
        ((LeatherArmorMeta) meta).setColor(GameTeam.getTeam(p.getUniqueId()).getColor().color);
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta.setUnbreakable(true);
        chestplate.setItemMeta(meta);
        return chestplate;
    }

    @JsonIgnore
    public boolean isGlobalEffect(PotionEffectType effectType) {
        for (Potion effect : effects) {
            if (effect.getPotionEffect().getType() == effectType) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public Shop getShop() {
        return shop;
    }

    private enum Events {
        EMERALD_II(() -> RootWars.getCurrentGameMode().emeraldGenerators.forEach(Generator::upgradeGenerator)),
        DIAMOND_II(() -> RootWars.getCurrentGameMode().diamondGenerators.forEach(Generator::upgradeGenerator)),
        EMERALD_III(() -> RootWars.getCurrentGameMode().emeraldGenerators.forEach(Generator::upgradeGenerator)),
        DIAMOND_III(() -> RootWars.getCurrentGameMode().diamondGenerators.forEach(Generator::upgradeGenerator)),
        ROOTS_BREAK(() -> RootWars.getCurrentGameMode().teams.forEach(GameTeam::breakRoot));

        private final Runnable runnable;

        Events(Runnable runnable) {
            this.runnable = runnable;
        }

        public Runnable getRunnable() {
            return runnable;
        }
    }
}