package cc.ratio.practice.lobby;

import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class LobbyItems {

    public static final ItemStack UNRANKED_QUEUE_ITEM = ItemStackBuilder.of(Material.IRON_SWORD)
            .name("&bUnranked Queue")
            .lore("&7Right click to open the &bUnranked Queue &7menu")
            .build();

    public static final ItemStack RANKED_QUEUE_ITEM = ItemStackBuilder.of(Material.DIAMOND_SWORD)
            .name("&cRanked Queue")
            .lore("&7Right click to open the &cRanked Queue &7menu")
            .build();

    public static final ItemStack LEAVE_QUEUE_ITEM = ItemStackBuilder.of(Material.INK_SACK)
            .durability(1)
            .name("&cLeave Q")
            .lore("&7Right click to leave the queue")
            .build();

    public static final Map<Integer, ItemStack> LOBBY_ITEMS = new HashMap<Integer, ItemStack>() {{
        this.put(0, UNRANKED_QUEUE_ITEM);
        this.put(1, RANKED_QUEUE_ITEM);
    }};

    public static final Map<Integer, ItemStack> QUEUE_ITEMS = new HashMap<Integer, ItemStack>() {{
        this.put(8, LEAVE_QUEUE_ITEM);
    }};

}
