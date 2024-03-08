package me.bcawley1.rootwars.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.Map;

public abstract class ItemStackMixin {
    @JsonProperty("material")
    private Material type;
    @JsonIgnore
    private MaterialData data;
    @JsonIgnore
    private ItemMeta meta;

    @JsonIgnore
    public abstract MaterialData getData();

    @JsonIgnore
    public abstract short getDurability();

    @JsonIgnore
    public abstract int getMaxStackSize();

    @JsonIgnore
    public abstract int getEnchantmentLevel();

    @JsonIgnore
    public abstract Map<Enchantment, Integer> getEnchantments();

    @JsonIgnore
    public abstract ItemMeta getItemMeta();

    @JsonIgnore
    public abstract String getTranslationKey();
}
