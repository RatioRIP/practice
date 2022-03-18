package cc.ratio.practice.scoreboard;

import cc.ratio.practice.profile.ProfileRepository;
import cc.ratio.practice.profile.ProfileState;
import cc.ratio.practice.queue.Queue;
import cc.ratio.practice.queue.QueueRepository;
import me.lucko.helper.Services;
import me.lucko.helper.scoreboard.ScoreboardObjective;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardUpdaters {

    private static final ProfileRepository profileRepository = Services.get(ProfileRepository.class).get();
    private static final QueueRepository queueRepository = Services.get(QueueRepository.class).get();

    public static final String TITLE = "&4&lRATIO";

    public static void update(final Player player, final ScoreboardObjective objective, final ProfileState state) {
        final List<String> lines = new ArrayList<>();

        final int online =  Bukkit.getOnlinePlayers().size();
        final int playing = 0;
        final int queueing = queueRepository.queues.stream().map(queue -> queue.getPlayers().size()).reduce(0, Integer::sum);

        // title
        if(state == ProfileState.LOBBY) {
            objective.setDisplayName(TITLE + " &7- &fLobby");
        }

        if(state == ProfileState.QUEUE) {
            objective.setDisplayName(TITLE + " &7- &fQueueing");
        }

        // lines
        lines.add("&7&m--------------------");

        lines.add("&cOnline: &f" + online);
        lines.add("&cPlaying: &f" + playing);
        lines.add("&cQueueing: &f" + queueing);
        lines.add("&r&r");
        lines.add("&4Coins: &f420");

        if(state == ProfileState.QUEUE) {
            final Queue queue = profileRepository.find(player.getUniqueId()).get().queue;
            final String rankity = queue.ranked ? "&cRanked" : "&bUnranked";
            lines.add("&r&r&r");
            lines.add("Queueing for " + rankity + " " + queue.kit.name );
        }

        lines.add("&r");
        lines.add("&7&oratio.rip");
        lines.add("&7&m--------------------&r");

        objective.applyLines(lines);
    }

}
