package cc.ratio.practice.queue.gui;

import cc.ratio.practice.kit.Kit;
import cc.ratio.practice.queue.QueueRepository;
import me.lucko.helper.Services;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class QueueGui extends Gui {

    public QueueRepository queueRepository = Services.get(QueueRepository.class).get();

    private final boolean ranked;

    public QueueGui(Player player, boolean ranked) {
        super(player, 3, ranked ? "Ranked" : "Unranked" + " Queue");

        this.ranked = ranked;
    }

    @Override
    public void redraw() {
        queueRepository.queues
                .stream()
                .filter(queue -> queue.ranked == this.ranked)
                .forEach(queue -> {
                    Kit kit = queue.kit;
                    ItemStack itemStack = ItemStackBuilder.of(queue.kit.display)
                            .name("&a" + kit.name)
                            .build();

                    Item item = Item.builder(itemStack)
                            .bind(() -> {
                                this.getPlayer().sendMessage("You should be joining the " + (ranked ? "Ranked" : "Unranked") + " " + kit.name + " queue.");
                            })
                            .build();

                    this.addItem(item);
                });
    }

}
