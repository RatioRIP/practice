package cc.ratio.practice.lobby;

import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class LobbyItem {

    public static final ItemStack UNRANKED_QUEUE_ITEM = ItemStackBuilder.of(Material.STONE_SWORD)
            .name("&7Unranked Queue")
            .lore("&7Rick click to open the Unranked Queue menu")
            .build();

}
