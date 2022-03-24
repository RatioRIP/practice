package cc.ratio.practice.scoreboard;

import cc.ratio.practice.match.Match;
import cc.ratio.practice.match.MatchRepository;
import cc.ratio.practice.match.team.Team;
import cc.ratio.practice.profile.ProfileRepository;
import cc.ratio.practice.profile.ProfileState;
import cc.ratio.practice.queue.Queue;
import cc.ratio.practice.queue.QueueRepository;
import me.lucko.helper.Services;
import me.lucko.helper.scoreboard.ScoreboardObjective;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardUpdaters {

    public static final String TITLE = "&4&lRatio &7[Practice]";
    private static final String LINE = "&c&m---------------------";
    private static final ProfileRepository profileRepository = Services.get(ProfileRepository.class).get();
    private static final QueueRepository queueRepository = Services.get(QueueRepository.class).get();
    private static final MatchRepository matchRepository = Services.get(MatchRepository.class).get();

    public static void update(final Player player, final ScoreboardObjective objective, final ProfileState state) {
        final List<String> lines = new ArrayList<>();

        final int online = Bukkit.getOnlinePlayers().size();
        final int playing = 0;
        final int queueing = queueRepository.queues.stream().map(queue -> queue.getPlayers().size()).reduce(0, Integer::sum);

        // title
        objective.setDisplayName(TITLE);


        // lines
        lines.add(LINE);

        if (state.isLobby()) {
            lines.add("&4Online: &f" + online);
            lines.add("&4Playing: &f" + playing);
            lines.add("&4Queueing: &f" + queueing);
            lines.add("&r&r");
            lines.add("&4Coins: &f420");

            if (state == ProfileState.QUEUE) {
                final Queue queue = profileRepository.find(player.getUniqueId()).get().queue;
                final String rankity = queue.ranked ? "&cRanked" : "&bUnranked";
                lines.add("&r&r&r");
                lines.add("Queueing for:");
                lines.add(rankity + " " + queue.kit.name);
            }
        }

        if (state == ProfileState.PLAYING) {
            Match match = profileRepository.find(player.getUniqueId()).get().match;
            List<Team> opponents = match.getOpponents(player.getUniqueId());

            String opponent = opponents.get(0).formatName(ChatColor.WHITE);

            lines.add("&cOpponent: &f" + opponent);
        }

        lines.add("&r");
        lines.add("&7www.ratio.rip");
        lines.add(LINE + "&r");

        objective.applyLines(lines);
    }

}
