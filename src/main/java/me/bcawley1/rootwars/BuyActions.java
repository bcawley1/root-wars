package me.bcawley1.rootwars;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.BiConsumer;

public enum BuyActions {

    CHAINMAIL_ARMOR((p, i) -> {
        if (p.getInventory().getHelmet() == null || !p.getInventory().getHelmet().getType().equals(Material.DIAMOND_HELMET) || !p.getInventory().getHelmet().getType().equals(Material.IRON_HELMET) || !p.getInventory().getHelmet().getType().equals(Material.CHAINMAIL_HELMET)) {
            if (p.getInventory().containsAtLeast(i.getCostItem(), i.getCostItem().getAmount())) {
                ItemStack helmet = new ItemStack(Material.CHAINMAIL_HELMET);
                ItemMeta meta = helmet.getItemMeta();
                meta.setUnbreakable(true);
                meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
                helmet.setItemMeta(meta);
                ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS);
                boots.setItemMeta(meta);
                ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
                leggings.setItemMeta(meta);
                p.getInventory().setLeggings(leggings);
                p.getInventory().setHelmet(helmet);
                p.getInventory().setBoots(boots);
                p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(i.getPurchasedItem().getI18NDisplayName()));
            } else {
                p.sendMessage(ChatColor.RED + "You already have armor that is better or equal to chainmail.");
            }
        }
    }),
    IRON_ARMOR((p, i) -> {
        if (p.getInventory().getHelmet() == null || !p.getInventory().getHelmet().getType().equals(Material.DIAMOND_HELMET) || !p.getInventory().getHelmet().getType().equals(Material.IRON_HELMET)) {
            if (p.getInventory().containsAtLeast(i.getCostItem(), i.getCostItem().getAmount())) {
                ItemStack helmet = new ItemStack(Material.IRON_HELMET);
                ItemMeta meta = helmet.getItemMeta();
                meta.setUnbreakable(true);
                meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
                helmet.setItemMeta(meta);
                ItemStack boots = new ItemStack(Material.IRON_BOOTS);
                boots.setItemMeta(meta);
                ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
                leggings.setItemMeta(meta);
                p.getInventory().setLeggings(leggings);
                p.getInventory().setHelmet(helmet);
                p.getInventory().setBoots(boots);
                p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(i.getPurchasedItem().getI18NDisplayName()));
            } else {
                p.sendMessage(ChatColor.RED + "You already have armor that is better or equal to iron.");
            }
        }
    }),
    DIAMOND_ARMOR((p, i) -> {
        if (p.getInventory().getHelmet() == null || !p.getInventory().getHelmet().getType().equals(Material.DIAMOND_HELMET)) {
            if (p.getInventory().containsAtLeast(i.getCostItem(), i.getCostItem().getAmount())) {
                ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
                ItemMeta meta = helmet.getItemMeta();
                meta.setUnbreakable(true);
                meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
                helmet.setItemMeta(meta);
                ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
                boots.setItemMeta(meta);
                ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
                leggings.setItemMeta(meta);
                p.getInventory().setLeggings(leggings);
                p.getInventory().setHelmet(helmet);
                p.getInventory().setBoots(boots);
                p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(i.getPurchasedItem().getI18NDisplayName()));
            } else {
                p.sendMessage(ChatColor.RED + "You already have armor that is better or equal to diamond.");
            }
        }
    }),
    WOOL((p, i) -> {
        if (p.getInventory().containsAtLeast(i.getCostItem(), i.getCostItem().getAmount())) {
            if (ShopItem.getBuyCooldown().getCooldown(p.getUniqueId()) == 0) {
                ShopItem.getBuyCooldown().setCooldown(p.getUniqueId(), 200);
                p.getInventory().removeItem(i.getCostItem());
                p.getInventory().addItem(new ItemStack(Material.valueOf(RootWars.getTeamFromPlayer(p).getName().toUpperCase() + "_WOOL"), 16));
                p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(i.getPurchasedItem().getI18NDisplayName()));
            }
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
    }),
    TERRACOTTA((p, i) -> {
        if (p.getInventory().containsAtLeast(i.getCostItem(), i.getCostItem().getAmount())) {
            if (ShopItem.getBuyCooldown().getCooldown(p.getUniqueId()) == 0) {
                ShopItem.getBuyCooldown().setCooldown(p.getUniqueId(), 200);
                p.getInventory().removeItem(i.getCostItem());
                p.getInventory().addItem(new ItemStack(Material.valueOf(RootWars.getTeamFromPlayer(p).getName().toUpperCase() + "_TERRACOTTA"), 16));
                p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(i.getPurchasedItem().getI18NDisplayName()));
            }
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
    }),
    KNOCKBACK_STICK((p, i) -> {
        if (p.getInventory().containsAtLeast(i.getCostItem(), i.getCostItem().getAmount())) {
            if (ShopItem.getBuyCooldown().getCooldown(p.getUniqueId()) == 0) {
                ShopItem.getBuyCooldown().setCooldown(p.getUniqueId(), 200);
                p.getInventory().removeItem(i.getCostItem());
                ItemStack item = new ItemStack(Material.STICK);
                ItemMeta meta = item.getItemMeta();
                meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
                item.setItemMeta(meta);
                p.getInventory().addItem(item);
                p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(i.getPurchasedItem().getI18NDisplayName()));
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
    }),
    TAB_ARMOR((p, i) -> {
        p.openInventory(Shop.getInventoryTab(p, "Armor"));
    });

    BuyActions(final BiConsumer<Player, ShopItem> action) {
        this.action = action;
    }

    private BiConsumer<Player, ShopItem> action;

    public BiConsumer<Player, ShopItem> getAction() {
        return action;
    }
}