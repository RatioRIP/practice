package cc.ratio.practice.lobby;

import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class LobbyItems {

    public static final ItemStack UNRANKED_QUEUE_ITEM = ItemStackBuilder.of(Material.IRON_SWORD)
            .name("&7Unranked Queue")
            .lore("&7Rick click to open the Unranked Queue menu")
            .build();

    public static final ItemStack RANKED_QUEUE_ITEM = ItemStackBuilder.of(Material.DIAMOND_SWORD)
            .name("&cUnranked Queue")
            .lore("&7Rick click to open the Ranked Queue menu")
            .build();

    public static final Map<Integer, ItemStack> ITEMS = new HashMap<>() {{
        put(0, UNRANKED_QUEUE_ITEM);
        put(1, RANKED_QUEUE_ITEM);
    }};

}
