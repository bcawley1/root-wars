package me.bcawley1.rootwars.shop;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import me.bcawley1.rootwars.util.GameTeam;
import org.bukkit.Bukkit;
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

import java.util.UUID;
import java.util.function.BiConsumer;

public enum BuyActions {
    @JsonEnumDefaultValue
    DEFAULT((p, i) -> {
        if (i instanceof ShopItem shopItem && shopItem.defaultBuyCheck(p)) {
            p.getInventory().addItem(shopItem.getPurchasedItem());
        }
    }),

    CHAINMAIL_ARMOR((p, i) -> {
        if (i instanceof ShopItem shopItem && shopItem.defaultBuyCheck(p)) {
            if(p.getInventory().getHelmet()==null || p.getInventory().getHelmet().getType()!=Material.DIAMOND_HELMET && p.getInventory().getHelmet().getType()!=Material.IRON_HELMET && p.getInventory().getHelmet().getType()!=Material.CHAINMAIL_HELMET){
                ItemStack helmet = new ItemStack(Material.CHAINMAIL_HELMET);
                ItemMeta meta = helmet.getItemMeta();
                meta.setUnbreakable(true);
                meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
                if (GameTeam.getTeam(p.getUniqueId()).getUpgrade("Protection") > 0) {
                    meta.addEnchant(Enchantment.PROTECTION, GameTeam.getTeam(p.getUniqueId()).getUpgrade("Protection"), true);
                }
                helmet.setItemMeta(meta);
                ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS);
                boots.setItemMeta(meta);
                ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
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
                if (GameTeam.getTeam(p.getUniqueId()).getUpgrade("Protection") > 0) {
                    meta.addEnchant(Enchantment.PROTECTION, GameTeam.getTeam(p.getUniqueId()).getUpgrade("Protection"), true);
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
                if (GameTeam.getTeam(p.getUniqueId()).getUpgrade("Protection") > 0) {
                    meta.addEnchant(Enchantment.PROTECTION, GameTeam.getTeam(p.getUniqueId()).getUpgrade("Protection"), true);
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
            p.getInventory().addItem(new ItemStack(Material.valueOf(GameTeam.getTeam(p.getUniqueId()).getColor().chatColor.name().toUpperCase() + "_WOOL"), 16));
        }
    }),
    TERRACOTTA((p, i) -> {
        if (i instanceof ShopItem shopItem && shopItem.defaultBuyCheck(p)) {
            p.getInventory().addItem(new ItemStack(Material.valueOf(GameTeam.getTeam(p.getUniqueId()).getColor().chatColor.name().toUpperCase() + "_TERRACOTTA"), 16));
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
            if (GameTeam.getTeam(p.getUniqueId()).getUpgrade("Sharpness") > 0) {
                item.addEnchantment(Enchantment.SHARPNESS, GameTeam.getTeam(p.getUniqueId()).getUpgrade("Sharpness"));
            }
            p.getInventory().addItem(item);
        }
    }),
    PROTECTION((p, i) -> {
        if(i instanceof UpgradableItem item && item.defaultBuyCheck(p)){
            GameTeam team = GameTeam.getTeam(p.getUniqueId());
            for (UUID id : team.getPlayersInTeam()) {
                Player player = Bukkit.getPlayer(id);
                for (ItemStack armor : player.getInventory().getArmorContents()) {
                    if (armor != null) {
                        armor.addEnchantment(Enchantment.PROTECTION, team.getUpgrade("Protection"));
                    }
                }
            }
        }
    }),
    SHARPNESS((p, i) -> {
        if(i instanceof UpgradableItem item && item.defaultBuyCheck(p)){
            GameTeam team = GameTeam.getTeam(p.getUniqueId());
            for (UUID id : team.getPlayersInTeam()) {
                for (ItemStack itemStack : Bukkit.getPlayer(id).getInventory()) {
                    if (itemStack != null) {
                        switch (itemStack.getType()) {
                            case WOODEN_SWORD, STONE_SWORD, IRON_SWORD, GOLDEN_SWORD, DIAMOND_SWORD, NETHERITE_SWORD ->
                                    itemStack.addEnchantment(Enchantment.SHARPNESS, team.getUpgrade("Sharpness"));
                        }
                    }
                }
            }
        }
    }),
    GENERATOR((p, i) -> {
        if(i instanceof UpgradableItem item && item.defaultBuyCheck(p)){
            GameTeam team = GameTeam.getTeam(p.getUniqueId());
            team.getGenerator().upgradeGenerator();
        }
    }),
    JUMP((p, i) -> {
        if (i instanceof ShopItem shopItem && shopItem.defaultBuyCheck(p)) {
            ItemStack item = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 900, 4, true, true), true);
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
    });

    BuyActions(final BiConsumer<Player, ActionItem> action) {
        this.action = action;
    }

    private final BiConsumer<Player, ActionItem> action;

    public BiConsumer<Player, ActionItem> getAction() {
        return action;
    }
}