package me.bcawley1.rootwars;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.BiConsumer;

public enum BuyActions {

    IRON_ARMOR_ACTION((p, i) -> {
        if(!p.getInventory().getHelmet().equals(new ItemStack(Material.DIAMOND_HELMET))||p.getInventory().getHelmet().equals(new ItemStack(Material.IRON_HELMET))) {
            ItemStack helmet = new ItemStack(Material.IRON_HELMET);
            ItemMeta helmetMeta = helmet.getItemMeta();
            helmetMeta.setUnbreakable(true);
            ItemStack boots = new ItemStack(Material.IRON_BOOTS);
            ItemMeta bootsMeta = boots.getItemMeta();
            helmetMeta.setUnbreakable(true);
            ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
            ItemMeta leggingsMeta = leggings.getItemMeta();
            helmetMeta.setUnbreakable(true);
//                p.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS).setItemMeta(meta));
            p.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            p.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
        } else {
            p.sendMessage(ChatColor.RED + "You already have armor that is better or equal to iron.");
        }
    }),
    DIAMOND_ARMOR_ACTION((p, i) -> { /* doh! */}),
    WOOL((p, i) -> {
        if (p.getInventory().containsAtLeast(i.getCostItem(), i.getCostItem().getAmount())) {
            if(ShopItem.getBuyCooldown().getCooldown(p.getUniqueId())==0) {
                ShopItem.getBuyCooldown().setCooldown(p.getUniqueId(), 200);
                p.getInventory().removeItem(i.getCostItem());
                p.getInventory().addItem(new ItemStack(Material.valueOf(RootWars.getTeamFromPlayer(p).getName().toUpperCase()+"_WOOL"), 16));
            }
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
    }),
    TERRACOTTA((p, i) -> {
        if (p.getInventory().containsAtLeast(i.getCostItem(), i.getCostItem().getAmount())) {
            if(ShopItem.getBuyCooldown().getCooldown(p.getUniqueId())==0) {
                ShopItem.getBuyCooldown().setCooldown(p.getUniqueId(), 200);
                p.getInventory().removeItem(i.getCostItem());
                p.getInventory().addItem(new ItemStack(Material.valueOf(RootWars.getTeamFromPlayer(p).getName().toUpperCase()+"_TERRACOTTA"), 16));
            }
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
    }),
    TAB_BLOCKS((p, i) -> {
        p.openInventory(Shop.getInventoryTab(p, "Blocks"));
    }),
    TAB_MELEE((p, i) -> {
        p.openInventory(Shop.getInventoryTab(p, "Melee"));
    });

    BuyActions(final BiConsumer<Player, ShopItem> action) {
        this.action = action;
    }

    private BiConsumer<Player, ShopItem> action;

    public BiConsumer<Player, ShopItem> getAction() {
        return action;
    }
}