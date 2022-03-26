package cc.ratio.practice.queue.gui;

import cc.ratio.practice.queue.QueueRepository;
import me.lucko.helper.Services;
import me.lucko.helper.menu.Gui;
import org.bukkit.entity.Player;

public class QueueGui extends Gui {

    private final QueueRepository queueRepository = Services.get(QueueRepository.class).get();

    private final boolean ranked;

    public QueueGui(Player player, boolean ranked) {
        super(player, 3, ranked ? "Ranked Queue" : "Unranked Queue");

        this.ranked = ranked;
    }

    @Override
    public void redraw() {
        Player player = this.getPlayer();

        this.queueRepository.queues
                .stream()
                .filter(queue -> queue.ranked == this.ranked)
                .forEach(queue -> this.addItem(new QueueButton(queue)));
    }

}
