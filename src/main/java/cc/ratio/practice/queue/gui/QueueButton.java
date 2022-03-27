package cc.ratio.practice.queue.gui;

import cc.ratio.practice.profile.Profile;
import cc.ratio.practice.profile.ProfileRepository;
import cc.ratio.practice.queue.Queue;
import me.lucko.helper.Services;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.function.Consumer;

public class QueueButton extends Item {

    private static final ProfileRepository profileRepository = Services.get(ProfileRepository.class).get();

    public QueueButton(Queue queue) {
        super(

                new HashMap<ClickType, Consumer<InventoryClickEvent>>() {{
                    this.put(ClickType.LEFT, createClickHandler(queue));
                    this.put(ClickType.RIGHT, createClickHandler(queue));
                }},

                createItem(queue)

        );
    }

    /**
     * Creates a click handler for a given {@link Queue}
     * @param queue the queue
     * @return the click handler
     */
    private static Consumer<InventoryClickEvent> createClickHandler(Queue queue) {
        return event -> {
            Player player = (Player) event.getWhoClicked();
            Profile profile = profileRepository.findOrNull(player.getUniqueId());

            profile.queueInit(queue);

            player.closeInventory();
        };
    }

    /**
     * Creates an {@link ItemStack} for a given {@link Queue}
     * @param queue the queue
     * @return the item
     */
    private static ItemStack createItem(Queue queue) {
        return ItemStackBuilder.of(queue.kit.display.clone())
                .name("&c" + queue.kit.name)
                .lore("&7&m--------------------------------")
                .lore("&cQueueing: &f" + queue.players.size())
                .lore("&cPlaying: &f" + queue.kit.getMatches().size())
                .lore("")
                .lore("&4Left-click &cto join the queue")
                .lore("&4Right-click &cto edit your layout")
                .lore("&7&m--------------------------------")
                .build();
    }

}