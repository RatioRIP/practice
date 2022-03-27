package cc.ratio.practice.queue;

import cc.ratio.practice.arena.ArenaRepository;
import cc.ratio.practice.kit.Kit;
import cc.ratio.practice.match.Match;
import cc.ratio.practice.match.MatchRepository;
import cc.ratio.practice.match.StopReason;
import cc.ratio.practice.match.team.Team;
import me.lucko.helper.Services;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Stream;

public class Queue {

    private static final ArenaRepository arenaRepository = Services.get(ArenaRepository.class).get();

    public final Kit kit;
    public final boolean ranked;
    public final ArrayList<Player> players;

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
        this.players.add(player);

        if (this.players.size() >= 2) {
            // TODO: i think there's something missing here
            Player first = this.players.remove(0);
            Player second = this.players.remove(0);

            Match match = new Match(
                    UUID.randomUUID(),

                    this.kit,

                    arenaRepository.getRandom(),

                    Arrays.asList(
                            new Team(Collections.singletonList(first)),
                            new Team(Collections.singletonList(second))
                    )
            );

            try {
                match.start();
            } catch (Exception exception) {
                exception.printStackTrace();
                match.stop(StopReason.ERROR, null, null);
            }
        }
    }

    /**
     * Remove a {@link Player} from the {@link Queue}
     *
     * @param player the player.
     */

    public void remove(Player player) {
        this.players.remove(player);
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
