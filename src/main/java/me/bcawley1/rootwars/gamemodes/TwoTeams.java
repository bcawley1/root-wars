package me.bcawley1.rootwars.gamemodes;

public class TwoTeams extends GameMode {
    public TwoTeams() {
        super();
    }
    public static void registerGameMode(){
        GameMode.registerGameMode(TwoTeams.class);
    }
}
