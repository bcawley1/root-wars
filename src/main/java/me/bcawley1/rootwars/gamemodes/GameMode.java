package me.bcawley1.rootwars.gamemodes;

import me.bcawley1.rootwars.runnables.Generator;
import me.bcawley1.rootwars.util.*;
import me.bcawley1.rootwars.RootWars;
import me.bcawley1.rootwars.events.LobbyEvent;
import me.bcawley1.rootwars.runnables.Regen;
import me.bcawley1.rootwars.runnables.Respawn;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import me.bcawley1.rootwars.shop.Shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GameMode implements Listener, Votable {
    protected final String[] teamColors;
    public List<GameTeam> teams;
    protected String gameModeName;
    protected String description;

    protected Material material;
    protected Scoreboard scoreboard;
    protected static Map<String, GameMode> gameModes = new HashMap<>();
    protected int respawnTime;
    protected int playerHealth;
    protected List<PotionEffect> effects;
    protected int diamondCooldown;
    protected int emeraldCooldown;
    protected Regen regen;
    protected Shop shop;
    protected final List<GeneratorData> generatorUpgradeData;
    protected List<Generator> emeraldGenerators;
    protected List<Generator> diamondGenerators;


    protected GameMode(String gameModeName, String description, Material material, int respawnTime, String[] teamColors, int playerHealth) {
        this.gameModeName = gameModeName;
        this.description = description;
        this.material = material;
        this.respawnTime = respawnTime;
        this.teamColors = teamColors;
        this.playerHealth = playerHealth;
        this.shop = new Shop();
        effects = new ArrayList<>();
        generatorUpgradeData = new ArrayList<>(List.of());
        effects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, 9999999, 255, false, false, false));
    }

    public void startGame() {
        //Creates and starts the emerald and diamond generators.
        emeraldGenerators = new ArrayList<>();
        diamondGenerators = new ArrayList<>();
        createGenerators();
        diamondGenerators.forEach(Generator::startGenerator);
        emeraldGenerators.forEach(Generator::startGenerator);

        //Starts a health regeneration timer.
        regen = new Regen();
        regen.runTaskTimer(RootWars.getPlugin(), 0, 20);

        //Creates the teams listed in the teamColors array.
        teams = new ArrayList<>();
        for (String s : teamColors) {
            teams.add(new GameTeam(RootWars.getCurrentMap(), s, generatorUpgradeData.get(0)));
        }

        //Initializes players.
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(playerHealth);
            p.setFoodLevel(20);
            p.setHealth(playerHealth);
            p.addPotionEffects(effects);
            initializePlayer(p);
        });


        //Assigns players to teams equally.
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (int i = 0; i < players.size(); i++) {
            teams.get(i % teams.size()).addPlayer(players.get(i));
            players.get(i).setGameMode(org.bukkit.GameMode.SURVIVAL);
        }

        //Initializes things related to teams.
        teams.forEach(t -> {
            t.spawnVillagers();
            t.getGenerator().runTaskTimer(RootWars.getPlugin(), 0, generatorUpgradeData.get(0).delay());
        });

        //Creates the map and registers the events defined in this class.
        RootWars.getCurrentMap().buildMap();
        Bukkit.getPluginManager().registerEvents(this, RootWars.getPlugin());
    }

    public void endGame() {
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

        //Removes the generator for each team.
        for (GameTeam team : teams) {
            team.removeGenerator();
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

    public Shop getShop() {
        return shop;
    }

    public void updateScoreboard() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("game", Criteria.DUMMY, "%s%sRoot Wars".formatted(ChatColor.YELLOW, ChatColor.BOLD));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        objective.getScore("Teams:").setScore(teams.size() + 3);
        teams.forEach(t -> {
            objective.getScore(ChatColor.valueOf(t.getName().toUpperCase()) + "%s%s: %s".formatted(t.getName().substring(0, 1).toUpperCase(), t.getName().substring(1), t.hasRoot() && t.numPlayersInTeam() != 0 ? "✔" : t.numPlayersInTeam())).setScore(teams.indexOf(t) + 3);
        });
        objective.getScore(" ").setScore(2);
        objective.getScore(ChatColor.LIGHT_PURPLE + "Root Wars " + ChatColor.WHITE + "on " + ChatColor.YELLOW + "Lopixel").setScore(1);
        //sets new scoreboard to players
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(scoreboard);
        }
    }

    public Material getMaterial() {
        return material;
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
        if (!(blockLocation.getX() > currentMap.getMapBorder().get("negX") && blockLocation.getX() < currentMap.getMapBorder().get("posX") &&
                blockLocation.getY() > currentMap.getMapBorder().get("negY") && blockLocation.getY() < currentMap.getMapBorder().get("posY") &&
                blockLocation.getZ() > currentMap.getMapBorder().get("negZ") && blockLocation.getZ() < currentMap.getMapBorder().get("posZ"))) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot place blocks outside of the map.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        GamePlayer gp = RootWars.getPlayer(event.getPlayer());
        for (GameTeam team : teams) {
            if (event.getBlock().getLocation().equals(team.getRootLocation()) && gp.getTeam().equals(team)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot break your own root anymore \uD83D\uDE14");
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (shop.containsTab(event.getView().getOriginalTitle())) {
            if (shop.isTopBar(event.getCurrentItem())) {
                shop.getTopBarItem(event.getCurrentItem()).getAction().accept((Player) event.getWhoClicked(), null);
            } else if(event.getCurrentItem()!=null) {
                shop.getShopItem(event.getCurrentItem()).onItemClick((Player) event.getWhoClicked());
            }
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && ((Player) event.getEntity()).getHealth() - event.getFinalDamage() <= 0) {
            Player p = (Player) event.getEntity();
            event.setCancelled(true);
            respawnPlayer(p);
            if (RootWars.getPlayer((Player) event.getEntity()).getTeam().hasRoot()) {
                p.setGameMode(org.bukkit.GameMode.SPECTATOR);
                p.teleport(RootWars.getPlayer(p).getTeam().getSpawnLoc());
                startRespawnTimer(respawnTime, p);
            } else {
                p.setGameMode(org.bukkit.GameMode.SPECTATOR);
                p.teleport(RootWars.getPlayer(p).getTeam().getSpawnLoc());
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
        if (event.getRightClicked() instanceof Villager) {
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
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            endGame();
        }
    }

    @EventHandler
    public void hungerChangeEvent(FoodLevelChangeEvent event) {
        event.setCancelled(true);
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
    protected void createGenerators(){
        RootWars.getCurrentMap().getEmeraldGeneratorLocations().forEach(l -> emeraldGenerators.add(
                new Generator(l, new GeneratorData(300, new GeneratorItem(Material.EMERALD, 100)))));
        RootWars.getCurrentMap().getDiamondGeneratorLocations().forEach(l -> diamondGenerators.add(
                new Generator(l, new GeneratorData(150, new GeneratorItem(Material.DIAMOND, 100)))));
    }
    protected void initializePlayer(Player p){
        GamePlayer gp = RootWars.getPlayer(p);
        if (gp.getTeam() != null) {
            gp.respawnPlayer();
        } else {
            p.setGameMode(org.bukkit.GameMode.SPECTATOR);
            p.teleport(RootWars.getCurrentMap().getEmeraldGeneratorLocations().get(0));
        }
        p.setPlayerListName(ChatColor.valueOf(gp.getTeam().getName().toUpperCase()) + p.getName());
        p.getInventory().setChestplate(getTeamChestplate(p));
        p.setScoreboard(scoreboard);
    }
    protected ItemStack getTeamChestplate(Player p){
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
}
