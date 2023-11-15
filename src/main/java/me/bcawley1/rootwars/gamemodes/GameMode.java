package me.bcawley1.rootwars.gamemodes;

import me.bcawley1.rootwars.*;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

public abstract class GameMode implements Listener {
    protected Map<Player, Integer> respawnTimers = new HashMap<>();
    protected Map<Player, Integer> respawnTimerID = new HashMap<>();
    protected String gameModeName;
    protected String description;
    protected int invSlot;
    protected Material material;
    protected Scoreboard scoreboard;
    protected static Map<String, GameMode> gameModes = new HashMap<>();

    public GameMode(String gameModeName) {
        this.gameModeName = gameModeName;
    }

    public abstract void startGame();
    public abstract void endGame();
    public void onRootBreak(GameTeam team){
        updateScoreboard();
        for(Player p : team.getPlayersInTeam()){
            p.playSound(net.kyori.adventure.sound.Sound.sound(Key.key("minecraft:entity.warden.roar"), Sound.Source.MASTER, 1f, 1f));
            p.sendTitle("Your Root Broke", "");
        }
    }

    public static Map<String, GameMode> getGameModes() {
        return gameModes;
    }

    public String getGameModeName() {
        return gameModeName;
    }

    public abstract void updateScoreboard();

    public String getDescription() {
        return description;
    }

    public int getInvSlot() {
        return invSlot;
    }

    public Material getMaterial() {
        return material;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        GameMap currentMap = RootWars.getCurrentMap();
        Location blockLocation = event.getBlock().getLocation();
        if(event.getBlock().getType().equals(Material.TNT)){
            Bukkit.getWorld("world").spawnEntity(event.getBlock().getLocation().add(0.5, 0, 0.5), EntityType.PRIMED_TNT);
            event.getPlayer().getInventory().removeItem(new ItemStack(Material.TNT));
            event.setCancelled(true);
        }
        if(!(blockLocation.getX()>currentMap.getMapBorder().get("negX")&&blockLocation.getX()<currentMap.getMapBorder().get("posX")&&
                blockLocation.getY()>currentMap.getMapBorder().get("negY")&&blockLocation.getY()<currentMap.getMapBorder().get("posY")&&
                blockLocation.getZ()>currentMap.getMapBorder().get("negZ")&&blockLocation.getZ()<currentMap.getMapBorder().get("posZ"))){
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot place blocks outside of the map.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        for(GameTeam team : RootWars.getTeams().values()){
            if(event.getBlock().getLocation().equals(team.getRootLocation())){
                if(RootWars.getTeamFromPlayer(event.getPlayer()).equals(team)){
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "You cannot break your own root anymore \uD83D\uDE14");
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if(Shop.containsTab(event.getView().getOriginalTitle())) {
            if(Shop.isTopBar(event.getCurrentItem())){
                Shop.getTopBarAction(event.getCurrentItem()).getAction().accept((Player) event.getWhoClicked(), new ShopItem(Material.IRON_GOLEM_SPAWN_EGG, 1, Material.IRON_INGOT, 1, "not null"));
            }
            if (ShopItem.hasShopItem(event.getCurrentItem())) {
                ShopItem.getShopItemFromItem(event.getCurrentItem()).onItemClick((Player) event.getWhoClicked());
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        event.setCancelled(true);
        Bukkit.broadcastMessage(event.getPlayer().getName() + " has died lmao");
        event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
        event.getPlayer().setGameMode(org.bukkit.GameMode.SPECTATOR);
        RootWars.getTeamFromPlayer(event.getPlayer()).respawnPlayer(event.getPlayer());
        if(RootWars.getTeamFromPlayer(event.getPlayer()).isRoot()){
            respawnTimers.put(event.getPlayer(), 6);
            respawnTimerID.put(event.getPlayer(), Bukkit.getScheduler().runTaskTimer(RootWars.getPlugin(),() -> {
                respawnTimers.merge(event.getPlayer(), -1, Integer::sum);
                event.getPlayer().sendTitle(String.valueOf(respawnTimers.get(event.getPlayer())),"");
                if(respawnTimers.get(event.getPlayer())<=0){
                    event.getPlayer().setGameMode(org.bukkit.GameMode.SURVIVAL);
                    RootWars.getTeamFromPlayer(event.getPlayer()).respawnPlayer(event.getPlayer());
                    Bukkit.getScheduler().cancelTask(respawnTimerID.get(event.getPlayer()));
                }
            }, 0, 20).getTaskId());
        } else {
            RootWars.getTeamFromPlayer(event.getPlayer()).removePlayersAlive();
            updateScoreboard();
            int teamsDead = 0;
            for(GameTeam team : RootWars.getTeams().values()){
                if(team.getPlayersAlive()==0){
                    teamsDead++;
                }
            }
            if(teamsDead>=3){
                endGame();
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event){
        if(event.getRightClicked() instanceof Villager){
            for(GameTeam team : RootWars.getTeams().values()) {
                if(team.isItemVillager(event.getRightClicked().getLocation())) {
                    event.getPlayer().openInventory(Shop.getInventoryTab(event.getPlayer(), "Quick Buy"));
                } else if(team.isUpgradeVillager(event.getRightClicked().getLocation())){
                    event.getPlayer().openInventory(Shop.getUpgradeTab(event.getPlayer()));
                }
            }
        }
    }
    @EventHandler
    public void playerInteract(PlayerInteractEvent event){
        if(event.getPlayer().getItemInHand().getType().equals(Material.FIRE_CHARGE)){
            Player p = event.getPlayer();
            Location loc = p.getEyeLocation().toVector().add(p.getLocation().getDirection().multiply(2)).
                    toLocation(p.getWorld(), p.getLocation().getYaw(), p.getLocation().getPitch());

            Fireball fireball = event.getPlayer().getWorld().spawn(loc, Fireball.class);
            fireball.setYield(2);
            p.getInventory().removeItem(new ItemStack(Material.FIRE_CHARGE, 1));
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event){
        if(Generator.droppedByGenerator(event.getItem())){
            Generator.deleteGeneratorItem(event.getItem());
            for(Player p : Bukkit.getServer().getOnlinePlayers()){
                if(Math.abs(p.getLocation().getX()-event.getEntity().getLocation().getX())<1.3&&Math.abs(p.getLocation().getZ()-event.getEntity().getLocation().getZ())<1.3){
                    p.getInventory().addItem(event.getItem().getItemStack());
                }
            }

            event.setCancelled(true);
        }
    }
    @EventHandler
    public void playerJoin(PlayerJoinEvent event){
        GameTeam.replacePlayer(event.getPlayer());
        GameTeam team = RootWars.getTeamFromPlayer(event.getPlayer());
        event.getPlayer().setScoreboard(scoreboard);
        if(team!=null){
            team.respawnPlayer(event.getPlayer());
        } else {
            event.getPlayer().setGameMode(org.bukkit.GameMode.SPECTATOR);
            event.getPlayer().teleport(RootWars.getCurrentMap().getEmeraldGeneratorLocations().get(0));
        }
    }
    @EventHandler
    public void regenEvent(EntityRegainHealthEvent event){
        if(event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.REGEN)||event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)){
            event.setCancelled(true);
        }

    }
    @EventHandler
    public void playerLeave(PlayerQuitEvent event){
        if(Bukkit.getOnlinePlayers().isEmpty()){
            endGame();
        }
    }
}
