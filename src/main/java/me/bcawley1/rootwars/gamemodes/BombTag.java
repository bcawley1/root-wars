package me.bcawley1.rootwars.gamemodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.bcawley1.rootwars.RootWars;
import me.bcawley1.rootwars.util.GameTeam;
import me.bcawley1.rootwars.util.ScoreboardInsert;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class BombTag extends GameMode {
    @JsonProperty
    private int explodeTime;
    private List<UUID> playersWithBomb;
    private String playerBombName;
    private long bombSelectTime;

    public BombTag() {
        super();
    }

    public static void registerGameMode() {
        GameMode.registerGameMode(BombTag.class);
    }

    @Override
    public void startGame() {
        super.startGame();
        playersWithBomb = new ArrayList<>();
    }

    @Override
    protected void updateScores() {
        ScoreboardInsert.filter(scoreboardInserts, "bombPlayer").setName(playerBombName + " has the bomb!");
        ScoreboardInsert.filter(scoreboardInserts, "timeUntilExplode").setName("Explosion: " + ((bombSelectTime + bombSelectTime - RootWars.getWorld().getGameTime()) / 20) + " Sec");
        super.updateScores();
        scores.remove(2);
    }

    @Override
    @JsonIgnore
    public Runnable getRunnable(String s) {
        Runnable runnable = super.getRunnable(s);
        if (runnable == null) {
            try {
                return Events.valueOf(s).getRunnable();
            } catch (Exception e) {
                return null;
            }
        }
        return runnable;
    }

    @Override
    public void onRootBreak(GameTeam team) {
        super.onRootBreak(team);
        Bukkit.broadcastMessage("The entire %s team has the bomb!".formatted(team.getName()));
        playersWithBomb.clear();
        playersWithBomb.addAll(team.getPlayersInTeam());
        playerBombName = "%s Team".formatted(team.getName());
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (playersWithBomb.contains(event.getDamager().getUniqueId()) && !GameTeam.getTeam(event.getDamager().getUniqueId()).equals(GameTeam.getTeam(event.getEntity().getUniqueId()))) {
            playersWithBomb.remove(event.getDamager().getUniqueId());
            playersWithBomb.add(event.getEntity().getUniqueId());
            playerBombName = event.getEntity().getName();
            updateScoreboard();
        }
    }

    @Override
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player p && p.getHealth() - event.getFinalDamage() <= 0 && !playersWithBomb.contains(p.getUniqueId())) {
            if(GameTeam.getTeam(p.getUniqueId()).hasRoot()) {
                event.setCancelled(true);
                respawnPlayer(p.getUniqueId());
                p.setGameMode(org.bukkit.GameMode.SPECTATOR);
                p.teleport(GameTeam.getTeam(p.getUniqueId()).getTeamData().getSpawnPoint());
                startRespawnTimer(respawnTime, p.getUniqueId());
            }
        }
    }

    private enum Events {
        BOMB_SELECT(() -> {
            ((BombTag) RootWars.getCurrentGameMode()).bombSelectTime = RootWars.getWorld().getGameTime();
            Random random = new Random();
            Player selectedPlayer = new ArrayList<>(Bukkit.getOnlinePlayers()).get(random.nextInt(Bukkit.getOnlinePlayers().size()));
            ((BombTag) RootWars.getCurrentGameMode()).playersWithBomb.add(selectedPlayer.getUniqueId());
            ((BombTag) RootWars.getCurrentGameMode()).playerBombName = selectedPlayer.getName();
            selectedPlayer.sendMessage("§4You have the bomb. §6Hit another player to give them the bomb, or break another team's root to give their entire team the bomb!");
            Bukkit.getScheduler().runTaskLater(RootWars.getPlugin(), () -> {
                ((BombTag) RootWars.getCurrentGameMode()).playersWithBomb.forEach(p -> {
                    RootWars.getWorld().createExplosion(Bukkit.getPlayer(p).getLocation(), 10, true);
                });
                respawnPlayer(selectedPlayer.getUniqueId());
                selectedPlayer.setGameMode(org.bukkit.GameMode.SPECTATOR);
                selectedPlayer.teleport(GameTeam.getTeam(selectedPlayer.getUniqueId()).getTeamData().getSpawnPoint());
                GameTeam.getTeam(selectedPlayer.getUniqueId()).removePlayer(selectedPlayer.getUniqueId());

                int teamsAlive = 0;
                for (GameTeam team : RootWars.getCurrentGameMode().teams) {
                    if (team.numPlayersInTeam() > 0) {
                        teamsAlive++;
                    }
                }

                if (teamsAlive <= 1) {
                    RootWars.getCurrentGameMode().endGame();
                }

                RootWars.getCurrentGameMode().updateScoreboard();
            }, 2400-5);
        });

        private final Runnable runnable;

        Events(Runnable runnable) {
            this.runnable = runnable;
        }

        public Runnable getRunnable() {
            return runnable;
        }
    }
}
