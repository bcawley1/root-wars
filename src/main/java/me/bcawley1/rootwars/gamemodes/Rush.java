package me.bcawley1.rootwars.gamemodes;

public class Rush extends GameMode {
    public Rush() {
        super();
    }
    public static void registerGameMode(){
        GameMode.registerGameMode(Rush.class);
    }
}
