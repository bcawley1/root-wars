package me.bcawley1.rootwars.gamemodes;

import me.bcawley1.rootwars.RootWars;
import me.bcawley1.rootwars.events.LobbyEvent;
import me.bcawley1.rootwars.maps.GameMap;
import me.bcawley1.rootwars.runnables.Generator;
import me.bcawley1.rootwars.runnables.Regen;
import me.bcawley1.rootwars.runnables.Respawn;
import me.bcawley1.rootwars.shop.Shop;
import me.bcawley1.rootwars.util.*;
import me.bcawley1.rootwars.vote.Votable;
import me.bcawley1.rootwars.vote.Vote;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public abstract class GameMode implements Listener, Votable {
    protected final String[] teamColors;
    protected List<GameTeam> teams;
    protected String gameModeName;
    protected String description;

    protected Material material;
    protected static Map<String, GameMode> gameModes = new HashMap<>();
    protected int respawnTime;
    protected int playerHealth;
    protected int regenTime;
    protected List<PotionEffect> effects;
    protected Regen regen;
    //Generator Variables
    protected final GeneratorData[] playerGeneratorUpgradeData;
    protected List<Generator> emeraldGenerators;
    protected final GeneratorData[] emeraldUpgradeData;
    protected List<Generator> diamondGenerators;
    protected final GeneratorData[] diamondUpgradeData;
    protected final List<ScheduledEvent> events;
    protected final List<RepeatableEvent> repeatableEvents;
    protected BukkitTask scoreboardUpdateTask;
    protected long startTick;

    protected GameMode(String jsonName) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObj = null;
        try (FileReader reader = new FileReader(RootWars.getPlugin().getDataFolder().getAbsoluteFile() + "/GameModes/%s.json".formatted(jsonName))) {
            Object obj = jsonParser.parse(reader);
            jsonObj = (JSONObject) obj;
        } catch (Exception e) {
            RootWars.getPlugin().getLogger().log(new LogRecord(Level.SEVERE, "Error while trying to parse %s.json.".formatted(jsonName)));
        }
        HashMap<String, Object> data = jsonObj;

        gameModeName = (String) data.get("name");
        description = (String) data.get("description");
        material = Material.valueOf((String) data.get("voteItem"));
        respawnTime = Math.toIntExact((long) data.get("respawnTime"));
        playerHealth = Math.toIntExact((long) data.get("playerHealth"));
        regenTime = Math.toIntExact((long) data.get("regenTimer"));

        playerGeneratorUpgradeData = getGeneratorData((List<HashMap<String, Object>>) data.get("teamGeneratorTiers"));
        emeraldUpgradeData = getGeneratorData((List<HashMap<String, Object>>) data.get("emeraldGeneratorTiers"));
        diamondUpgradeData = getGeneratorData((List<HashMap<String, Object>>) data.get("diamondGeneratorTiers"));

        events = new ArrayList<>();
        for (Map<String, Object> map : (List<Map<String, Object>>) data.get("events")) {
            events.add(new ScheduledEvent((String) map.get("name"), Math.toIntExact((long) map.get("delay")), getRunnable((String) map.get("runnable"))));
        }

        repeatableEvents = new ArrayList<>();
        for (Map<String, Object> map : (List<Map<String, Object>>) data.get("repeatingEvents")) {
            repeatableEvents.add(new RepeatableEvent((String) map.get("name"), Math.toIntExact((long) map.get("delay")), Math.toIntExact((long) map.get("repeatTime")), getRunnable((String) map.get("runnable"))));
        }

        effects = new ArrayList<>();
        for (Map<String, Object> map : (List<Map<String, Object>>) data.get("effects")) {
            effects.add(new PotionEffect(PotionEffectType.getByName((String) map.get("name")), -1, Math.toIntExact((long) map.get("amplifier")), false, false, false));
        }

        teamColors = ((List<String>) data.get("teams")).toArray(new String[0]);
        gameModes.put(gameModeName, this);
    }

    private GeneratorData[] getGeneratorData(List<HashMap<String, Object>> data) {
        GeneratorData[] dataArray = new GeneratorData[data.size()];
        for (int i = 0; i < data.size(); i++) {
            List<HashMap<String, Object>> items = (List<HashMap<String, Object>>) data.get(i).get("items");
            GeneratorItem[] itemsButCooler = new GeneratorItem[items.size()];
            for (int j = 0; j < items.size(); j++) {
                itemsButCooler[j] = new GeneratorItem(Material.valueOf((String) items.get(j).get("item")), Math.toIntExact((long) items.get(j).get("chance")));
            }
            dataArray[i] = new GeneratorData(Math.toIntExact((long) data.get(i).get("delay")), itemsButCooler);
        }
        return dataArray;
    }

    public void startGame() {
        scoreboardUpdateTask = Bukkit.getScheduler().runTaskTimer(RootWars.getPlugin(), this::updateScoreboard, 0, 20);
        Collections.sort(events);
        startTick = RootWars.getWorld().getGameTime();
        Bukkit.getOnlinePlayers().forEach(RootWars::addPlayer);
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
        teams = new ArrayList<>();
        for (String s : teamColors) {
            teams.add(new GameTeam(s, playerGeneratorUpgradeData));
        }


        //Assigns players to teams equally.
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (int i = 0; i < players.size(); i++) {
            teams.get(i % teams.size()).addPlayer(players.get(i));
            players.get(i).setGameMode(org.bukkit.GameMode.SURVIVAL);
        }

        //Initializes players.
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(playerHealth);
            p.setFoodLevel(20);
            p.setHealth(playerHealth);
            p.addPotionEffects(effects);
            initializePlayer(p);
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
                lobbyEvent.putPlayerInLobby(player);
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

    protected Runnable getRunnable(String s) {
        try {
            return Events.valueOf(s).getRunnable();
        } catch (Exception e) {
            return null;
        }
    }


    public void onRootBreak(GameTeam team) {
        updateScoreboard();
        for (Player p : team.getPlayersInTeam()) {
            p.playSound(p, Sound.ENTITY_WARDEN_ROAR, SoundCategory.MASTER, 1f, 1f);
            p.sendTitle("Your Root Broke", "", 10, 70, 20);
        }
    }

    public static Map<String, GameMode> getGameModes() {
        return gameModes;
    }

    @Override
    public ItemStack getItem() {
        return Vote.getItem(material, gameModeName, description);
    }

    @Override
    public String getName() {
        return gameModeName;
    }

    public void updateScoreboard() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("game", Criteria.DUMMY, "%s%sRoot Wars".formatted(ChatColor.YELLOW, ChatColor.BOLD));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        objective.getScore("Teams:").setScore(teams.size() + 5);
        teams.forEach(t -> {
            objective.getScore(ChatColor.valueOf(t.getName().toUpperCase()) + "%s%s: %s".formatted(t.getName().substring(0, 1).toUpperCase(), t.getName().substring(1), t.hasRoot() && t.numPlayersInTeam() != 0 ? "✔" : t.numPlayersInTeam())).setScore(teams.indexOf(t) + 5);
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
        GameMap currentMap = RootWars.getCurrentMap();
        Location blockLocation = event.getBlock().getLocation();
        if (event.getBlock().getType().equals(Material.TNT)) {
            RootWars.getWorld().spawnEntity(event.getBlock().getLocation().add(0.5, 0, 0.5), EntityType.PRIMED_TNT);
            event.getPlayer().getInventory().removeItem(new ItemStack(Material.TNT));
            event.setCancelled(true);
        }
        if (!(blockLocation.getX() > currentMap.getMapBorder().getNegativeX() && blockLocation.getX() < currentMap.getMapBorder().getPositiveX() &&
                blockLocation.getY() > currentMap.getMapBorder().getNegativeY() && blockLocation.getY() < currentMap.getMapBorder().getPositiveY() &&
                blockLocation.getZ() > currentMap.getMapBorder().getNegativeZ() && blockLocation.getZ() < currentMap.getMapBorder().getPositiveZ())) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot place blocks outside of the map.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        GamePlayer gp = RootWars.getPlayer(event.getPlayer());
        for (GameTeam team : teams) {
            if (event.getBlock().getLocation().equals(team.getTeamData().getRootLocation()) && gp.getTeam().equals(team)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot break your own root anymore \uD83D\uDE14");
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Shop shop = RootWars.getPlayer((Player) event.getWhoClicked()).getTeam().getShop();
        if (event.getCurrentItem() == null) {
            return;
        }
        if (shop.containsTab(event.getView().getOriginalTitle())) {
            if (shop.isTopBar(event.getCurrentItem())) {
                shop.getTopBarItem(event.getCurrentItem()).onItemClick((Player) event.getWhoClicked());
            } else if (event.getCurrentItem() != null) {
                shop.getActionItem(event.getCurrentItem()).onItemClick((Player) event.getWhoClicked());
            }
            event.setCancelled(true);
        } else if (event.getView().getOriginalTitle().equalsIgnoreCase("Upgrades") && event.getCurrentItem() != null) {
            System.out.println(event.getCurrentItem().getItemMeta().getDisplayName().split(" Upgrade:")[0].substring(2));
            shop.getActionItemFromString(event.getCurrentItem().getItemMeta().getDisplayName().split(" Upgrade:")[0].substring(2)).onItemClick((Player) event.getWhoClicked());
            event.setCancelled(true);
        }
    }

    //On player death
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player p && p.getHealth() - event.getFinalDamage() <= 0) {
            event.setCancelled(true);
            respawnPlayer(p);
            if (RootWars.getPlayer((Player) event.getEntity()).getTeam().hasRoot()) {
                p.setGameMode(org.bukkit.GameMode.SPECTATOR);
                p.teleport(RootWars.getPlayer(p).getTeam().getTeamData().getSpawnPoint());
                startRespawnTimer(respawnTime, p);
            } else {
                p.setGameMode(org.bukkit.GameMode.SPECTATOR);
                p.teleport(RootWars.getPlayer(p).getTeam().getTeamData().getSpawnPoint());
                RootWars.getPlayer(p).getTeam().removePlayer(p);

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
        if (event.getRightClicked() instanceof Villager && RootWars.getPlayer(event.getPlayer()).getTeam() != null) {
            Shop shop = RootWars.getPlayer(event.getPlayer()).getTeam().getShop();
            for (GameTeam team : teams) {
                if (team.isItemVillager(event.getRightClicked().getLocation())) {
                    event.getPlayer().openInventory(shop.getInventoryTab(event.getPlayer(), "Quick Buy"));
                } else if (team.isUpgradeVillager(event.getRightClicked().getLocation())) {
                    event.getPlayer().openInventory(shop.getUpgradeTab(event.getPlayer()));
                }
            }
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getItemInHand().getType().equals(Material.FIRE_CHARGE)) {
            throwFireball(event.getPlayer());
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
        initializePlayer(event.getPlayer());
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
        }
    }

    @EventHandler
    public void hungerChangeEvent(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void entityLoadEvent(EntitiesLoadEvent event) {
        event.getEntities().forEach(entity -> {
            if (entity.getType() == EntityType.DROPPED_ITEM && !Generator.droppedByGenerator((Item) entity)) {
                entity.remove();
            }
        });
    }

    protected static void throwFireball(Player p) {
        Location loc = p.getEyeLocation().toVector().add(p.getLocation().getDirection().multiply(2)).
                toLocation(p.getWorld(), p.getLocation().getYaw(), p.getLocation().getPitch());

        Fireball fireball = p.getWorld().spawn(loc, Fireball.class);
        fireball.setYield(RootWars.getPlugin().getConfig().getInt("fireball-strength"));
        p.getInventory().removeItem(new ItemStack(Material.FIRE_CHARGE, 1));
    }

    protected static void respawnPlayer(Player p) {
        GamePlayer gp = RootWars.getPlayer(p);
        Bukkit.broadcastMessage(RootWars.getPlugin().getConfig().getString("death-message").replace("{player}", p.getName()));
        p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
    }

    protected static void startRespawnTimer(int time, Player p) {
        new Respawn(time, p).runTaskTimer(RootWars.getPlugin(), 0, 20);
    }

    protected void initializePlayer(Player p) {
        GamePlayer gp = RootWars.getPlayer(p);
        if (gp.getTeam() != null) {
            gp.respawnPlayer();
            p.setPlayerListName(ChatColor.valueOf(gp.getTeam().getName().toUpperCase()) + p.getName());
            p.getInventory().setChestplate(getTeamChestplate(p));
        } else {
            p.setGameMode(org.bukkit.GameMode.SPECTATOR);
            p.teleport(RootWars.getCurrentMap().getDiamondGenerators().get(0));
        }
        updateScoreboard();
    }

    protected ItemStack getTeamChestplate(Player p) {
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemMeta meta = chestplate.getItemMeta();
        switch (RootWars.getPlayer(p).getTeam().getName()) {
            case "blue" -> ((LeatherArmorMeta) meta).setColor(Color.BLUE);
            case "red" -> ((LeatherArmorMeta) meta).setColor(Color.RED);
            case "yellow" -> ((LeatherArmorMeta) meta).setColor(Color.YELLOW);
            case "green" -> ((LeatherArmorMeta) meta).setColor(Color.GREEN);
        }
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta.setUnbreakable(true);
        chestplate.setItemMeta(meta);
        return chestplate;
    }

    public boolean isGlobalEffect(PotionEffectType effectType) {
        for (PotionEffect effect : effects) {
            if (effect.getType() == effectType) {
                return true;
            }
        }
        return false;
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
