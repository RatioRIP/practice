package cc.ratio.practice.queue;

import cc.ratio.practice.kit.Kit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class Queue {

    public final Kit kit;
    public final boolean ranked;
    public final ArrayList<UUID> players;

    /**
     * Constructor for a {@link Queue}
     *
     * @param kit the kit.
     * @param ranked if ranked or not.
     */

    public Queue(Kit kit, boolean ranked) {
        this.kit = kit;
        this.ranked = ranked;
        this.players = new ArrayList<>();
    }

    /**
     * Add a {@link Player} to a {@link Queue}
     *
     * @param player the player.
     */

    public void add(Player player) {
        this.players.add(player.getUniqueId());
    }

    /**
     * Remove a {@link Player} from the {@link Queue}
     *
     * @param player the player.
     */

    public void remove(Player player) {
        this.players.remove(player.getUniqueId());
    }

    /**
     * Get the {@link UUID}'s in the {@link Queue}
     *
     * @return the players in queue.
     */

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
