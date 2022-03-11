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

    public ArrayList<UUID> getPlayers() {
        return this.players;
    }

    @Override
    public String toString() {
        return "Queue{" +
                "kit=" + this.kit +
                ", ranked=" + this.ranked +
                ", players=" + this.players +
                '}';
    }
}
