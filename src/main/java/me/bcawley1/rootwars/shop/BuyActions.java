package me.bcawley1.rootwars.shop;

import me.bcawley1.rootwars.RootWars;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.function.BiConsumer;

public enum BuyActions {
    DEFAULT((p, i) -> {
        if (i instanceof ShopItem shopItem && shopItem.defaultBuyCheck(p)) {
            p.getInventory().addItem(shopItem.getPurchasedItem());
        }
    }),

    CHAINMAIL_ARMOR((p, i) -> {
        if (i instanceof ShopItem shopItem && shopItem.defaultBuyCheck(p)) {
            if(p.getInventory().getHelmet()==null || p.getInventory().getHelmet().getType()!=Material.DIAMOND_HELMET && p.getInventory().getHelmet().getType()!=Material.IRON_HELMET && p.getInventory().getHelmet().getType()!=Material.CHAINMAIL_HELMET){
                ItemStack helmet = new ItemStack(Material.IRON_HELMET);
                ItemMeta meta = helmet.getItemMeta();
                meta.setUnbreakable(true);
                meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
                if (RootWars.getPlayer(p).getTeam().getProtection() > 0) {
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
            }
        }
    }),
    IRON_ARMOR((p, i) -> {
        if (i instanceof ShopItem shopItem && shopItem.defaultBuyCheck(p)) {
            if(p.getInventory().getHelmet()==null || p.getInventory().getHelmet().getType()!=Material.DIAMOND_HELMET && p.getInventory().getHelmet().getType()!=Material.IRON_HELMET){
                ItemStack helmet = new ItemStack(Material.IRON_HELMET);
                ItemMeta meta = helmet.getItemMeta();
                meta.setUnbreakable(true);
                meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
                if (RootWars.getPlayer(p).getTeam().getProtection() > 0) {
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
            }
        }
    }),
    DIAMOND_ARMOR((p, i) -> {
        if (i instanceof ShopItem shopItem && shopItem.defaultBuyCheck(p)) {
            if(p.getInventory().getHelmet()==null || p.getInventory().getHelmet().getType()!=Material.DIAMOND_HELMET){
                ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
                ItemMeta meta = helmet.getItemMeta();
                meta.setUnbreakable(true);
                meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
                if (RootWars.getPlayer(p).getTeam().getProtection() > 0) {
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
            }
        }
    }),
    WOOL((p, i) -> {
        if (i instanceof ShopItem shopItem && shopItem.defaultBuyCheck(p)) {
            p.getInventory().addItem(new ItemStack(Material.valueOf(RootWars.getPlayer(p).getTeam().getName().toUpperCase() + "_WOOL"), 16));
        }
    }),
    TERRACOTTA((p, i) -> {
        if (i instanceof ShopItem shopItem && shopItem.defaultBuyCheck(p)) {
            p.getInventory().addItem(new ItemStack(Material.valueOf(RootWars.getPlayer(p).getTeam().getName().toUpperCase() + "_TERRACOTTA"), 16));
        }
    }),
    KNOCKBACK_STICK((p, i) -> {
        if (i instanceof ShopItem shopItem && shopItem.defaultBuyCheck(p)) {
            ItemStack item = new ItemStack(Material.STICK);
            ItemMeta meta = item.getItemMeta();
            meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
            item.setItemMeta(meta);
            p.getInventory().addItem(item);
        }
    }),
    SWORD((p, i) -> {
        if (i instanceof ShopItem shopItem && shopItem.defaultBuyCheck(p)) {
            ItemStack item = shopItem.getPurchasedItem();
            if (RootWars.getPlayer(p).getTeam().getSharpness() > 0) {
                item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, RootWars.getPlayer(p).getTeam().getProtection());
            }
            p.getInventory().addItem(item);
        }
    }),
    PROTECTION((p, i) -> {
        if(i instanceof UpgradableItem item && item.defaultBuyCheck(p)){
            RootWars.getPlayer(p).getTeam().upgradeProtection();
        }
    }),
    SHARPNESS((p, i) -> {
        if(i instanceof UpgradableItem item && item.defaultBuyCheck(p)){
            RootWars.getPlayer(p).getTeam().upgradeSharpness();
        }
    }),
    GENERATOR((p, i) -> {
        if(i instanceof UpgradableItem item && item.defaultBuyCheck(p)){
            RootWars.getPlayer(p).getTeam().getGenerator().upgradeGenerator();
        }
    }),
    JUMP((p, i) -> {
        if (i instanceof ShopItem shopItem && shopItem.defaultBuyCheck(p)) {
            ItemStack item = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, 900, 4, true, true), true);
            item.setItemMeta(meta);
            p.getInventory().addItem(item);
        }
    }),
    SPEED((p, i) -> {
        if (i instanceof ShopItem shopItem && shopItem.defaultBuyCheck(p)) {
            ItemStack item = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 900, 1, true, true), true);
            item.setItemMeta(meta);
            p.getInventory().addItem(item);
        }
    }),
    INVIS((p, i) -> {
        if(i instanceof ShopItem shopItem && shopItem.defaultBuyCheck(p)){
            ItemStack item = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2400, 1, true, true), true);
            item.setItemMeta(meta);
            p.getInventory().addItem(item);
        }
    }),
    FIREWORK((p, i) -> {
        if(i instanceof ShopItem shopItem && shopItem.defaultBuyCheck(p)){
            ItemStack item = new ItemStack(Material.FIREWORK_ROCKET, 2);
            FireworkMeta meta = (FireworkMeta) item.getItemMeta();
            meta.addEffect(FireworkEffect.builder().trail(true).withColor(Color.AQUA).build());
            meta.addEffect(FireworkEffect.builder().trail(true).withColor(Color.LIME).build());
            item.setItemMeta(meta);
            p.getInventory().addItem(item);
        }
    }),
    TAB_QUICK((p, i) -> {
        Shop shop = RootWars.getPlayer(p).getTeam().getShop();
        p.openInventory(shop.getInventoryTab(p, Shop.ShopTab.QUICK));
    }),
    TAB_BLOCKS((p, i) -> {
        Shop shop = RootWars.getPlayer(p).getTeam().getShop();
        p.openInventory(shop.getInventoryTab(p, Shop.ShopTab.BLOCKS));
    }),
    TAB_MELEE((p, i) -> {
        Shop shop = RootWars.getPlayer(p).getTeam().getShop();
        p.openInventory(shop.getInventoryTab(p, Shop.ShopTab.MELEE));
    }),
    TAB_ARMOR((p, i) -> {
        Shop shop = RootWars.getPlayer(p).getTeam().getShop();
        p.openInventory(shop.getInventoryTab(p, Shop.ShopTab.ARMOR));
    }),
    TAB_TOOLS((p, i) -> {
        Shop shop = RootWars.getPlayer(p).getTeam().getShop();
        p.openInventory(shop.getInventoryTab(p, Shop.ShopTab.TOOLS));
    }),
    TAB_RANGED((p, i) -> {
        Shop shop = RootWars.getPlayer(p).getTeam().getShop();
        p.openInventory(shop.getInventoryTab(p, Shop.ShopTab.RANGED));
    }),
    TAB_POTION((p, i) -> {
        Shop shop = RootWars.getPlayer(p).getTeam().getShop();
        p.openInventory(shop.getInventoryTab(p, Shop.ShopTab.POTION));
    }),
    TAB_UTILITY((p, i) -> {
        Shop shop = RootWars.getPlayer(p).getTeam().getShop();
        p.openInventory(shop.getInventoryTab(p, Shop.ShopTab.UTILITY));
    });

    BuyActions(final BiConsumer<Player, ActionItem> action) {
        this.action = action;
    }

    private BiConsumer<Player, ActionItem> action;

    public BiConsumer<Player, ActionItem> getAction() {
        return action;
    }
}