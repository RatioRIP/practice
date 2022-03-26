package cc.ratio.practice.queue;

import cc.ratio.practice.kit.Kit;
import cc.ratio.practice.util.Repository;
import cc.ratio.practice.util.Tuple;

import java.util.ArrayList;
import java.util.Optional;

public class QueueRepository implements Repository<Queue, Tuple<Kit, Boolean>> {

    public final ArrayList<Queue> queues;

    public QueueRepository() {
        this.queues = new ArrayList<>();
    }

    @Override
    public boolean put(Queue queue) {
        return this.queues.add(queue);
    }

    @Override
    public boolean remove(Queue queue) {
        return this.queues.remove(queue);
    }

    @Override
    public Optional<Queue> find(Tuple<Kit, Boolean> identifier) {
        return this.queues
                .stream()
                .filter(queue -> (queue.kit == identifier.a) && (queue.ranked == identifier.b))
                .findFirst();
    }
}
