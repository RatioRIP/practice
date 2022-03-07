package cc.ratio.practice.queue;

import cc.ratio.practice.kit.Kit;

import java.util.ArrayList;
import java.util.UUID;

public class Queue {

    public final Kit kit;
    public final boolean ranked;
    public final ArrayList<UUID> players;

    public Queue(final Kit kit, final boolean ranked) {
        this.kit = kit;
        this.ranked = ranked;
        this.players = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Queue{" +
                "kit=" + kit +
                ", ranked=" + ranked +
                ", players=" + players +
                '}';
    }
}
