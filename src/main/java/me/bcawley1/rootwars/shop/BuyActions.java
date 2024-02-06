package me.bcawley1.rootwars.shop;

import me.bcawley1.rootwars.RootWars;
import me.bcawley1.rootwars.util.GameTeam;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.function.BiConsumer;

public enum BuyActions {
    DEFAULT((p, i) -> {
        ShopItem shopItem = (ShopItem) i;
        if (p.getInventory().containsAtLeast(shopItem.getCostItem(), shopItem.getCostItem().getAmount())) {
            if (ShopItem.getBuyCooldown().getCooldown(p.getUniqueId()) == 0) {
                ShopItem.getBuyCooldown().setCooldown(p.getUniqueId(), 200);
                p.getInventory().removeItem(shopItem.getCostItem());
                p.getInventory().addItem(shopItem.getPurchasedItem());
                p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(ShopItem.getFormattedName(shopItem.getPurchasedItem().getType())));
            }
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
    }),

    CHAINMAIL_ARMOR((p, i) -> {
        ShopItem shopItem = (ShopItem) i;
        if (p.getInventory().getHelmet() == null || !p.getInventory().getHelmet().getType().equals(Material.DIAMOND_HELMET) && !p.getInventory().getHelmet().getType().equals(Material.IRON_HELMET) && !p.getInventory().getHelmet().getType().equals(Material.CHAINMAIL_HELMET)) {
            if (p.getInventory().containsAtLeast(shopItem.getCostItem(), shopItem.getCostItem().getAmount())) {
                ItemStack helmet = new ItemStack(Material.CHAINMAIL_HELMET);
                ItemMeta meta = helmet.getItemMeta();
                meta.setUnbreakable(true);
                meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
                if(RootWars.getPlayer(p).getTeam().getProtection()>0) {
                    meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, RootWars.getPlayer(p).getTeam().getProtection(), true);
                }
                helmet.setItemMeta(meta);
                ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS);
                boots.setItemMeta(meta);
                ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
                leggings.setItemMeta(meta);
                p.getInventory().setLeggings(leggings);
                p.getInventory().setHelmet(helmet);
                p.getInventory().setBoots(boots);
                p.getInventory().removeItem(shopItem.getCostItem());
                p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(ShopItem.getFormattedName(shopItem.getPurchasedItem().getType())));
            } else {
                p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
            }
        } else {
            p.sendMessage(ChatColor.RED + "You already have armor that is better or equal to chainmail.");
        }
    }),
    IRON_ARMOR((p, i) -> {
        ShopItem shopItem = (ShopItem) i;
        if (p.getInventory().getHelmet() == null || !p.getInventory().getHelmet().getType().equals(Material.DIAMOND_HELMET) && !p.getInventory().getHelmet().getType().equals(Material.IRON_HELMET)) {
            if (p.getInventory().containsAtLeast(shopItem.getCostItem(), shopItem.getCostItem().getAmount())) {
                ItemStack helmet = new ItemStack(Material.IRON_HELMET);
                ItemMeta meta = helmet.getItemMeta();
                meta.setUnbreakable(true);
                meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
                if(RootWars.getPlayer(p).getTeam().getProtection()>0) {
                    meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, RootWars.getPlayer(p).getTeam().getProtection(), true);
                }
                helmet.setItemMeta(meta);
                ItemStack boots = new ItemStack(Material.IRON_BOOTS);
                boots.setItemMeta(meta);
                ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
                leggings.setItemMeta(meta);
                p.getInventory().setLeggings(leggings);
                p.getInventory().setHelmet(helmet);
                p.getInventory().setBoots(boots);
                p.getInventory().removeItem(shopItem.getCostItem());
                p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(ShopItem.getFormattedName(shopItem.getPurchasedItem().getType())));
            } else {
                p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
            }
        } else {
            p.sendMessage(ChatColor.RED + "You already have armor that is better or equal to iron.");
        }
    }),
    DIAMOND_ARMOR((p, i) -> {
        ShopItem shopItem = (ShopItem) i;
        if (p.getInventory().getHelmet() == null || !p.getInventory().getHelmet().getType().equals(Material.DIAMOND_HELMET)) {
            if (p.getInventory().containsAtLeast(shopItem.getCostItem(), shopItem.getCostItem().getAmount())) {
                ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
                ItemMeta meta = helmet.getItemMeta();
                meta.setUnbreakable(true);
                meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
                if(RootWars.getPlayer(p).getTeam().getProtection()>0) {
                    meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, RootWars.getPlayer(p).getTeam().getProtection(), true);
                }
                helmet.setItemMeta(meta);
                ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
                boots.setItemMeta(meta);
                ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
                leggings.setItemMeta(meta);
                p.getInventory().setLeggings(leggings);
                p.getInventory().setHelmet(helmet);
                p.getInventory().setBoots(boots);
                p.getInventory().removeItem(shopItem.getCostItem());
                p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(ShopItem.getFormattedName(shopItem.getPurchasedItem().getType())));
            } else {
                p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
            }
        } else {
            p.sendMessage(ChatColor.RED + "You already have armor that is better or equal to diamond.");
        }
    }),
    WOOL((p, i) -> {
        ShopItem shopItem = (ShopItem) i;
        if (p.getInventory().containsAtLeast(shopItem.getCostItem(), shopItem.getCostItem().getAmount())) {
            if (ShopItem.getBuyCooldown().getCooldown(p.getUniqueId()) == 0) {
                ShopItem.getBuyCooldown().setCooldown(p.getUniqueId(), 200);
                p.getInventory().removeItem(shopItem.getCostItem());
                p.getInventory().addItem(new ItemStack(Material.valueOf(RootWars.getPlayer(p).getTeam().getName().toUpperCase() + "_WOOL"), 16));
                p.sendMessage(ChatColor.GREEN + "You purchased wool!!!");
            }
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
    }),
    TERRACOTTA((p, i) -> {
        ShopItem shopItem = (ShopItem) i;
        if (p.getInventory().containsAtLeast(shopItem.getCostItem(), shopItem.getCostItem().getAmount())) {
            if (ShopItem.getBuyCooldown().getCooldown(p.getUniqueId()) == 0) {
                ShopItem.getBuyCooldown().setCooldown(p.getUniqueId(), 200);
                p.getInventory().removeItem(shopItem.getCostItem());
                p.getInventory().addItem(new ItemStack(Material.valueOf(RootWars.getPlayer(p).getTeam().getName().toUpperCase() + "_TERRACOTTA"), 16));
                p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(ShopItem.getFormattedName(shopItem.getPurchasedItem().getType())));
            }
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
    }),
    KNOCKBACK_STICK((p, i) -> {
        ShopItem shopItem = (ShopItem) i;
        if (p.getInventory().containsAtLeast(shopItem.getCostItem(), shopItem.getCostItem().getAmount())) {
            if (ShopItem.getBuyCooldown().getCooldown(p.getUniqueId()) == 0) {
                ShopItem.getBuyCooldown().setCooldown(p.getUniqueId(), 200);
                p.getInventory().removeItem(shopItem.getCostItem());
                ItemStack item = new ItemStack(Material.STICK);
                ItemMeta meta = item.getItemMeta();
                meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
                item.setItemMeta(meta);
                p.getInventory().addItem(item);
                p.getInventory().removeItem(shopItem.getCostItem());
                p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(ShopItem.getFormattedName(shopItem.getPurchasedItem().getType())));
            }
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
    }),
    SWORD((p, i) -> {
        ShopItem shopItem = (ShopItem) i;
        if (p.getInventory().containsAtLeast(shopItem.getCostItem(), shopItem.getCostItem().getAmount())) {
            if (ShopItem.getBuyCooldown().getCooldown(p.getUniqueId()) == 0) {
                ShopItem.getBuyCooldown().setCooldown(p.getUniqueId(), 200);
                p.getInventory().removeItem(shopItem.getCostItem());
                ItemStack item = shopItem.getPurchasedItem();
                if(RootWars.getPlayer(p).getTeam().getSharpness()>0) {
                    item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, RootWars.getPlayer(p).getTeam().getProtection());
                }
                p.getInventory().addItem(item);
                p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(ShopItem.getFormattedName(shopItem.getPurchasedItem().getType())));
            }
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
    }),
    PROTECTION((p, i) -> {
        UpgradableItem item = (UpgradableItem) i;
        GameTeam team = RootWars.getPlayer(p).getTeam();
        if (item.isMax()) {
            p.sendMessage(ChatColor.RED + "You cannot buy anymore protection upgrades.");
        } else if (p.getInventory().containsAtLeast(item.getCost(), item.getCost().getAmount())) {
            for (Player player : team.getPlayersInTeam()) {
                player.sendMessage(ChatColor.GREEN + "Purchased protection tier %s!".formatted(item.getStage() + 2));
            }
            p.getInventory().removeItem(item.getCost());
            item.upgrade();
            team.upgradeProtection();
            p.openInventory(RootWars.getPlayer(p).getTeam().getShop().getUpgradeTab(p));
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
    }),
    SHARPNESS((p, i) -> {
        UpgradableItem item = (UpgradableItem) i;
        GameTeam team = RootWars.getPlayer(p).getTeam();
        if (item.isMax()) {
            p.sendMessage(ChatColor.RED + "You cannot buy anymore sharpness upgrades.");
        } else if (p.getInventory().containsAtLeast(item.getCost(), item.getCost().getAmount())) {
            for (Player player : team.getPlayersInTeam()) {
                player.sendMessage(ChatColor.GREEN + "Purchased sharpness tier %s!".formatted(item.getStage() + 2));
            }
            p.getInventory().removeItem(item.getCost());
            item.upgrade();
            team.upgradeSharpness();
            p.openInventory(RootWars.getPlayer(p).getTeam().getShop().getUpgradeTab(p));
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
    }),
    GENERATOR((p, i) -> {
        UpgradableItem item = (UpgradableItem) i;
        GameTeam team = RootWars.getPlayer(p).getTeam();
        if (item.isMax()) {
            p.sendMessage(ChatColor.RED + "You cannot buy anymore generator upgrades.");
        } else if (p.getInventory().containsAtLeast(item.getCost(), item.getCost().getAmount())) {
            for (Player player : team.getPlayersInTeam()) {
                player.sendMessage(ChatColor.GREEN + "Purchased generator tier %s!".formatted(item.getStage() + 2));
            }
            p.getInventory().removeItem(item.getCost());
            item.upgrade();
            team.getGenerator().upgradeGenerator();
            p.openInventory(RootWars.getPlayer(p).getTeam().getShop().getUpgradeTab(p));
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
    }),
    JUMP((p, i) -> {
        ShopItem shopItem = (ShopItem) i;
        if (p.getInventory().containsAtLeast(shopItem.getCostItem(), shopItem.getCostItem().getAmount())) {
            ItemStack item = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, 900, 4, true, true), true);
            item.setItemMeta(meta);
            p.getInventory().addItem(item);
            p.getInventory().removeItem(shopItem.getCostItem());
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
    }),
    SPEED((p, i) -> {
        ShopItem shopItem = (ShopItem) i;
        if (p.getInventory().containsAtLeast(shopItem.getCostItem(), shopItem.getCostItem().getAmount())) {
            ItemStack item = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 900, 1, true, true), true);
            item.setItemMeta(meta);
            p.getInventory().addItem(item);
            p.getInventory().removeItem(shopItem.getCostItem());
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
    }),
    TAB_QUICK((p, i) -> {
        Shop shop = RootWars.getPlayer(p).getTeam().getShop();
        p.openInventory(shop.getInventoryTab(p, "Quick Buy"));
    }),
    TAB_BLOCKS((p, i) -> {
        Shop shop = RootWars.getPlayer(p).getTeam().getShop();
        p.openInventory(shop.getInventoryTab(p, "Blocks"));
    }),
    TAB_MELEE((p, i) -> {
        Shop shop = RootWars.getPlayer(p).getTeam().getShop();
        p.openInventory(shop.getInventoryTab(p, "Melee"));
    }),
    TAB_ARMOR((p, i) -> {
        Shop shop = RootWars.getPlayer(p).getTeam().getShop();
        p.openInventory(shop.getInventoryTab(p, "Armor"));
    }),
    TAB_TOOLS((p, i) -> {
        Shop shop = RootWars.getPlayer(p).getTeam().getShop();
        p.openInventory(shop.getInventoryTab(p, "Tools"));
    }),
    TAB_RANGED((p, i) -> {
        Shop shop = RootWars.getPlayer(p).getTeam().getShop();
        p.openInventory(shop.getInventoryTab(p, "Ranged"));
    }),
    TAB_POTION((p, i) -> {
        Shop shop = RootWars.getPlayer(p).getTeam().getShop();
        p.openInventory(shop.getInventoryTab(p, "Potions"));
    }),
    TAB_UTILITY((p, i) -> {
        Shop shop = RootWars.getPlayer(p).getTeam().getShop();
        p.openInventory(shop.getInventoryTab(p, "Utility"));
    });

    BuyActions(final BiConsumer<Player, ActionItem> action) {
        this.action = action;
    }

    private BiConsumer<Player, ActionItem> action;

    public BiConsumer<Player, ActionItem> getAction() {
        return action;
    }
}