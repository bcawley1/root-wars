package me.bcawley1.rootwars.gamemodes;

public class Standard extends GameMode {
    public Standard() {
        super();
    }
    public static void registerGameMode(){
        GameMode.registerGameMode(Standard.class);
    }
}
