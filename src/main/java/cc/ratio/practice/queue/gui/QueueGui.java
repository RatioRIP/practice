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

    private final boolean ranked;
    public QueueRepository queueRepository = Services.get(QueueRepository.class).get();

    public QueueGui(final Player player, final boolean ranked) {
        super(player, 3, (ranked ? "Ranked" : "Unranked") + " Queue");

        this.ranked = ranked;
    }

    @Override
    public void redraw() {
        this.queueRepository.queues
                .stream()
                .filter(queue -> queue.ranked == this.ranked)
                .forEach(queue -> {
                    final Kit kit = queue.kit;
                    final ItemStack itemStack = ItemStackBuilder.of(queue.kit.display)
                            .name("&a" + kit.name)
                            .build();

                    final Item item = Item.builder(itemStack)
                            .bind(() -> {
                                this.getPlayer().sendMessage("You should be joining the " + (this.ranked ? "Ranked" : "Unranked") + " " + kit.name + " queue.");
                            })
                            .build();

                    this.addItem(item);
                });
    }

}
