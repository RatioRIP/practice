package cc.ratio.practice.queue.gui;

import cc.ratio.practice.arena.ArenaRepository;
import cc.ratio.practice.kit.Kit;
import cc.ratio.practice.match.Match;
import cc.ratio.practice.match.MatchRepository;
import cc.ratio.practice.match.StopReason;
import cc.ratio.practice.match.team.Team;
import cc.ratio.practice.profile.Profile;
import cc.ratio.practice.profile.ProfileRepository;
import cc.ratio.practice.queue.QueueRepository;
import me.lucko.helper.Services;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.text3.Text;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public class QueueGui extends Gui {

    private final boolean ranked;

    private final QueueRepository queueRepository = Services.get(QueueRepository.class).get();
    private final ProfileRepository profileRepository = Services.get(ProfileRepository.class).get();
    private final ArenaRepository arenaRepository = Services.get(ArenaRepository.class).get();
    private final MatchRepository matchRepository = Services.get(MatchRepository.class).get();

    public QueueGui(final Player player, final boolean ranked) {
        super(player, 3, (ranked ? "Ranked" : "Unranked") + " Queue");

        this.ranked = ranked;
    }

    @Override
    public void redraw() {
        if (!this.isFirstDraw()) return;

        final Player player = this.getPlayer();

        this.queueRepository.queues
                .stream()
                .filter(queue -> queue.ranked == this.ranked)
                .map(queue -> {
                    final Kit kit = queue.kit;
                    final String rankity = queue.ranked ? "&cRanked" : "&bUnranked";

                    return ItemStackBuilder.of(kit.display.clone())
                            .name("&b" + kit.name)
                            .lore("&7Click to join the queue for " + rankity + " " + kit.name)
                            .build(() -> {
                                this.close();

                                final Profile profile = this.profileRepository.find(player.getUniqueId()).get();

                                profile.queue = queue;
                                profile.queue.add(profile.toPlayer());
                                profile.queueInit();
                                profile.scoreboardUpdate();

                                player.sendMessage(Text.colorize("&7You've joined the " + rankity + " " + kit.name + " &7queue."));

                                if (queue.players.size() >= 2) {
                                    // TODO: i think there's something missing here
                                    final UUID first = queue.players.remove(0);
                                    final UUID second = queue.players.remove(0);

                                    final Match match = new Match(
                                            UUID.randomUUID(),

                                            queue.kit,

                                            this.arenaRepository.getRandom(),

                                            Arrays.asList(
                                                    new Team(first),
                                                    new Team(second)
                                            )
                                    );

                                    this.matchRepository.put(match);

                                    try {
                                        match.start();
                                    } catch (Exception exception) {
                                        exception.printStackTrace();
                                        match.stop(StopReason.ERROR, null, null);
                                    }
                                }
                            });

                }).forEach(this::addItem);
    }
}
