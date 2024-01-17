package me.bcawley1.rootwars.shop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class UpgradableItem extends ActionItem{
    private int stage;
    private final int numUpgrades;
    private List<ItemStack> cost;
    private String upgradeName;
    public UpgradableItem(Material type, BiConsumer<Player, ActionItem> action, String upgradeName, int numUpgrades, List<ItemStack> cost) {
        super(type, action);
        this.cost = new ArrayList<>(cost);
        this.numUpgrades = numUpgrades;
        this.upgradeName = upgradeName;
        stage = 0;
        updateItemMeta();
    }
    public void upgrade(){
        stage++;
        updateItemMeta();
    }
    private void updateItemMeta(){
        ItemMeta meta = getItemMeta();
        if(stage>=numUpgrades-1){
            String description = "%sYou have all of the %s upgrades.".formatted(ChatColor.RED,upgradeName.toLowerCase());
            meta.setLore(List.of(description));
            meta.setDisplayName(ChatColor.WHITE + upgradeName + " Upgrade: %sMAX".formatted(ChatColor.RED));
        } else {
            String description = "%s%sCost: %s%s %s\n%sClick to buy!!!!".formatted(ChatColor.RESET, ChatColor.GRAY, ChatColor.WHITE, cost.get(stage).getAmount(), ShopItem.getFormattedName(cost.get(0).getType()), ChatColor.YELLOW);
            meta.setLore(List.of(description.split("\n")));
            meta.setDisplayName(ChatColor.WHITE + upgradeName + " Upgrade: Tier " + (stage + 2));
        }
        setItemMeta(meta);
    }

    public ItemStack getCost() {
        return cost.get(stage);
    }

    public int getStage() {
        return stage;
    }
    public boolean isMax(){
        return stage>=numUpgrades-1;
    }
}
