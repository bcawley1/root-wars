package me.bcawley1.rootwars.gamemodes;

public class Overgrowth extends GameMode{
    public Overgrowth() {
        super();
    }
    public static void registerGameMode(){
        GameMode.registerGameMode(Overgrowth.class);
    }
}
