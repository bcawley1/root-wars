package me.bcawley1.rootwars;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public enum BuyActions {

    CHAINMAIL_ARMOR((p, i) -> {
        if (p.getInventory().getHelmet() == null || !p.getInventory().getHelmet().getType().equals(Material.DIAMOND_HELMET) && !p.getInventory().getHelmet().getType().equals(Material.IRON_HELMET) && !p.getInventory().getHelmet().getType().equals(Material.CHAINMAIL_HELMET)) {
            if (p.getInventory().containsAtLeast(i.getCostItem(), i.getCostItem().getAmount())) {
                ItemStack helmet = new ItemStack(Material.CHAINMAIL_HELMET);
                ItemMeta meta = helmet.getItemMeta();
                meta.setUnbreakable(true);
                meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
                if (RootWars.getPlayer(p).getTeam().isProtection()) {
                    meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);
                }
                helmet.setItemMeta(meta);
                ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS);
                boots.setItemMeta(meta);
                ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
                leggings.setItemMeta(meta);
                p.getInventory().setLeggings(leggings);
                p.getInventory().setHelmet(helmet);
                p.getInventory().setBoots(boots);
                p.getInventory().removeItem(i.getCostItem());
                p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(i.getPurchasedItem().getItemMeta().getDisplayName()));
            } else {
                p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
            }
        } else {
            p.sendMessage(ChatColor.RED + "You already have armor that is better or equal to chainmail.");
        }
    }),
    IRON_ARMOR((p, i) -> {
        if (p.getInventory().getHelmet() == null || !p.getInventory().getHelmet().getType().equals(Material.DIAMOND_HELMET) && !p.getInventory().getHelmet().getType().equals(Material.IRON_HELMET)) {
            if (p.getInventory().containsAtLeast(i.getCostItem(), i.getCostItem().getAmount())) {
                ItemStack helmet = new ItemStack(Material.IRON_HELMET);
                ItemMeta meta = helmet.getItemMeta();
                meta.setUnbreakable(true);
                meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
                if (RootWars.getPlayer(p).getTeam().isProtection()) {
                    meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);
                }
                helmet.setItemMeta(meta);
                ItemStack boots = new ItemStack(Material.IRON_BOOTS);
                boots.setItemMeta(meta);
                ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
                leggings.setItemMeta(meta);
                p.getInventory().setLeggings(leggings);
                p.getInventory().setHelmet(helmet);
                p.getInventory().setBoots(boots);
                p.getInventory().removeItem(i.getCostItem());
                p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(i.getPurchasedItem().getItemMeta().getDisplayName()));
            } else {
                p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
            }
        } else {
            p.sendMessage(ChatColor.RED + "You already have armor that is better or equal to iron.");
        }
    }),
    DIAMOND_ARMOR((p, i) -> {
        if (p.getInventory().getHelmet() == null || !p.getInventory().getHelmet().getType().equals(Material.DIAMOND_HELMET)) {
            if (p.getInventory().containsAtLeast(i.getCostItem(), i.getCostItem().getAmount())) {
                ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
                ItemMeta meta = helmet.getItemMeta();
                meta.setUnbreakable(true);
                meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
                if (RootWars.getPlayer(p).getTeam().isProtection()) {
                    meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);
                }
                helmet.setItemMeta(meta);
                ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
                boots.setItemMeta(meta);
                ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
                leggings.setItemMeta(meta);
                p.getInventory().setLeggings(leggings);
                p.getInventory().setHelmet(helmet);
                p.getInventory().setBoots(boots);
                p.getInventory().removeItem(i.getCostItem());
                p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(i.getPurchasedItem().getItemMeta().getDisplayName()));
            } else {
                p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
            }
        } else {
            p.sendMessage(ChatColor.RED + "You already have armor that is better or equal to diamond.");
        }
    }),
    WOOL((p, i) -> {
        if (p.getInventory().containsAtLeast(i.getCostItem(), i.getCostItem().getAmount())) {
            if (ShopItem.getBuyCooldown().getCooldown(p.getUniqueId()) == 0) {
                ShopItem.getBuyCooldown().setCooldown(p.getUniqueId(), 200);
                p.getInventory().removeItem(i.getCostItem());
                p.getInventory().addItem(new ItemStack(Material.valueOf(RootWars.getPlayer(p).getTeam().getName().toUpperCase() + "_WOOL"), 16));
                p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(i.getPurchasedItem().getItemMeta().getDisplayName()));
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
                p.getInventory().addItem(new ItemStack(Material.valueOf(RootWars.getPlayer(p).getTeam().getName().toUpperCase() + "_TERRACOTTA"), 16));
                p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(i.getPurchasedItem().getItemMeta().getDisplayName()));
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
                p.getInventory().removeItem(i.getCostItem());
                p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(i.getPurchasedItem().getItemMeta().getDisplayName()));
            }
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
    }),
    SWORD((p, i) -> {
        if (p.getInventory().containsAtLeast(i.getCostItem(), i.getCostItem().getAmount())) {
            if (ShopItem.getBuyCooldown().getCooldown(p.getUniqueId()) == 0) {
                ShopItem.getBuyCooldown().setCooldown(p.getUniqueId(), 200);
                p.getInventory().removeItem(i.getCostItem());
                ItemStack item = i.getPurchasedItem();
                p.sendMessage(String.valueOf(RootWars.getPlayer(p).getTeam().isSharpness()));
                if (RootWars.getPlayer(p).getTeam().isSharpness()) {
                    item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
                }
                p.getInventory().addItem(item);
                p.sendMessage(ChatColor.GREEN + "You purchased %s!!!".formatted(i.getPurchasedItem().getItemMeta().getDisplayName()));
            }
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
    }),
    PROTECTION((p, i) -> {
        GameTeam team = RootWars.getPlayer(p).getTeam();
        if (p.getInventory().containsAtLeast(i.getCostItem(), i.getCostItem().getAmount())) {
            if (team.isProtection()) {
                p.sendMessage(ChatColor.RED + "You already have this.");
            } else {
                p.getInventory().removeItem(i.getCostItem());
                team.setProtection(true);
                for (Player player : team.getPlayersInTeam()) {
                    player.sendMessage(ChatColor.GREEN + "Purchased reinforced armor.");
                    player.getInventory().getLeggings().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                    player.getInventory().getHelmet().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                    player.getInventory().getBoots().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                    player.getInventory().getChestplate().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                }
            }
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
    }),
    SHARPNESS((p, i) -> {
        GameTeam team = RootWars.getPlayer(p).getTeam();
        if (p.getInventory().containsAtLeast(i.getCostItem(), i.getCostItem().getAmount())) {
            if (team.isSharpness()) {
                p.sendMessage(ChatColor.RED + "You already have this.");
            } else {
                p.getInventory().removeItem(i.getCostItem());
                team.setSharpness(true);
                for (Player player : team.getPlayersInTeam()) {
                    player.sendMessage(ChatColor.GREEN + "Purchased sharper swords.");
                    for (ItemStack item : player.getInventory().getStorageContents()) {
                        if (item != null) {
                            if (item.getType().equals(Material.WOODEN_SWORD) || item.getType().equals(Material.STONE_SWORD) || item.getType().equals(Material.IRON_SWORD) || item.getType().equals(Material.DIAMOND_SWORD)) {
                                item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
                            }
                        }
                    }

                }
            }
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
    }),
    GENERATOR((p, i) -> {
        GameTeam team = RootWars.getPlayer(p).getTeam();
        if (p.getInventory().containsAtLeast(i.getCostItem(), i.getCostItem().getAmount())) {
            if (team.isGenUpgrade()) {
                p.sendMessage(ChatColor.RED + "You already have this.");
            } else {
                for (Player player : team.getPlayersInTeam()) {
                    player.sendMessage(ChatColor.GREEN + "Purchased upgraded generator.");
                }
                p.getInventory().removeItem(i.getCostItem());
                team.setGenUpgrade(true);
                team.upgradeGenerator(new ArrayList<GeneratorItem>(List.of(
                        new GeneratorItem(new ItemStack(Material.IRON_INGOT), 79),
                        new GeneratorItem(new ItemStack(Material.GOLD_INGOT), 20),
                        new GeneratorItem(new ItemStack(Material.EMERALD), 1))), 5);
            }
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
    }),
    JUMP((p, i) -> {
        if (p.getInventory().containsAtLeast(i.getCostItem(), i.getCostItem().getAmount())) {
            ItemStack item = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, 900, 4, true, true), true);
            item.setItemMeta(meta);
            p.getInventory().addItem(item);
            p.getInventory().removeItem(i.getCostItem());
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
    }),
    SPEED((p, i) -> {
        if (p.getInventory().containsAtLeast(i.getCostItem(), i.getCostItem().getAmount())) {
            ItemStack item = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 900, 1, true, true), true);
            item.setItemMeta(meta);
            p.getInventory().addItem(item);
            p.getInventory().removeItem(i.getCostItem());
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough to purchase this item.");
        }
    }),
    TAB_QUICK((p, i) -> {
        p.openInventory(RootWars.getCurrentGameMode().getShop().getInventoryTab(p, "Quick Buy"));
    }),
    TAB_BLOCKS((p, i) -> {
        p.openInventory(RootWars.getCurrentGameMode().getShop().getInventoryTab(p, "Blocks"));
    }),
    TAB_MELEE((p, i) -> {
        p.openInventory(RootWars.getCurrentGameMode().getShop().getInventoryTab(p, "Melee"));
    }),
    TAB_ARMOR((p, i) -> {
        p.openInventory(RootWars.getCurrentGameMode().getShop().getInventoryTab(p, "Armor"));
    }),
    TAB_TOOLS((p, i) -> {
        p.openInventory(RootWars.getCurrentGameMode().getShop().getInventoryTab(p, "Tools"));
    }),
    TAB_RANGED((p, i) -> {
        p.openInventory(RootWars.getCurrentGameMode().getShop().getInventoryTab(p, "Ranged"));
    }),
    TAB_POTION((p, i) -> {
        p.openInventory(RootWars.getCurrentGameMode().getShop().getInventoryTab(p, "Potions"));
    }),
    TAB_UTILITY((p, i) -> {
        p.openInventory(RootWars.getCurrentGameMode().getShop().getInventoryTab(p, "Utility"));
    });

    BuyActions(final BiConsumer<Player, ShopItem> action) {
        this.action = action;
    }

    private BiConsumer<Player, ShopItem> action;

    public BiConsumer<Player, ShopItem> getAction() {
        return action;
    }
}