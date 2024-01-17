package me.bcawley1.rootwars.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public class ActionItem extends ItemStack {
    protected BiConsumer<Player, ActionItem> action;


    public ActionItem(Material type, int amount, BiConsumer<Player, ActionItem> action) {
        super(type, amount);
        this.action = action;
    }

    public ActionItem(Material type, BiConsumer<Player, ActionItem> action) {
        super(type);
        this.action = action;
    }

    public void onItemClick(Player p) {
        action.accept(p, this);
    }
}
