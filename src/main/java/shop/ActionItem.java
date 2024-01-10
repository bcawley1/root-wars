package shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public class ActionItem extends ItemStack {
    protected BiConsumer<Player, ShopItem> action;

    public ActionItem(Material type, int amount, BiConsumer<Player, ShopItem> action) {
        super(type, amount);
        this.action = action;
    }

    public BiConsumer<Player, ShopItem> getAction() {
        return action;
    }

    public ActionItem(Material type, BiConsumer<Player, ShopItem> action) {
        super(type);
        this.action = action;
    }
}
