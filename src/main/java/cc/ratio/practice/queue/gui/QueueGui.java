package cc.ratio.practice.queue.gui;

import cc.ratio.practice.kit.Kit;
import cc.ratio.practice.queue.QueueRepository;
import me.lucko.helper.Services;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import org.bukkit.entity.Player;

public class QueueGui extends Gui {

    private final boolean ranked;

    private final QueueRepository queueRepository = Services.get(QueueRepository.class).get();

    public QueueGui(final Player player, final boolean ranked) {
        super(player, 3, (ranked ? "Ranked" : "Unranked") + " Queue");

        this.ranked = ranked;
    }

    @Override
    public void redraw() {
        if(!this.isFirstDraw()) return;

        this.queueRepository.queues
                .stream()
                .filter(queue -> queue.ranked == this.ranked)
                .map(queue -> {
                    final Kit kit = queue.kit;

                    return ItemStackBuilder.of(kit.display)
                        .name("&b" + kit.name)
                        .lore("&7Click to join the queue for " + (queue.ranked ? "&cRanked" : "&bUnranked") + " " + kit.name)
                        .build(() -> {
                            this.getPlayer().sendMessage("You should be joining the " + (this.ranked ? "Ranked" : "Unranked") + " " + kit.name + " queue.");
                        });
                })
                .forEach(this::addItem);
    }

}
