package cc.ratio.practice.queue;

import cc.ratio.practice.arena.ArenaRepository;
import cc.ratio.practice.kit.Kit;
import cc.ratio.practice.match.Match;
import cc.ratio.practice.match.MatchRepository;
import cc.ratio.practice.match.StopReason;
import cc.ratio.practice.match.team.Team;
import me.lucko.helper.Services;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Stream;

public class Queue {

    private static final ArenaRepository arenaRepository = Services.get(ArenaRepository.class).get();
    private static final MatchRepository matchRepository = Services.get(MatchRepository.class).get();

    public final Kit kit;
    public final boolean ranked;
    public final java.util.Queue<Player> players;

    /**
     * Constructor for a {@link Queue}
     *
     * @param kit the kit.
     * @param ranked if ranked or not.
     */

    public Queue(Kit kit, boolean ranked) {
        this.kit = kit;
        this.ranked = ranked;
        this.players = new LinkedList<>();
    }

    /**
     * Add a {@link Player} to a {@link Queue}
     *
     * @param player the player.
     */

    public void add(Player player) {
        this.players.offer(player);

        if (this.players.size() == 2) {
            // TODO: i think there's something missing here
            Player first = this.players.poll();
            Player second = this.players.poll();

            Match match = new Match(
                    UUID.randomUUID(),

                    this.kit,

                    arenaRepository.getRandom(),

                    Arrays.asList(
                            new Team(Arrays.asList(first)),
                            new Team(Arrays.asList(second))
                    )
            );

            try {
                match.start();
                matchRepository.put(match);
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
