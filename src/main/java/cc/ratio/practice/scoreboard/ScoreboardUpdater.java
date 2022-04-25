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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScoreboardUpdater {

    private static final String TITLE = "&4&lRatio &7[Practice]";
    private static final String LINE = "&c&m---------------------";

    private static final ProfileRepository profileRepository = Services.get(ProfileRepository.class).get();
    private static final QueueRepository queueRepository = Services.get(QueueRepository.class).get();
    private static final MatchRepository matchRepository = Services.get(MatchRepository.class).get();

    public static void update(Player player, ScoreboardObjective objective, ProfileState state) {
        List<String> lines = new ArrayList<>();

        int online = Bukkit.getOnlinePlayers().size();
        int playing = matchRepository.matches.size();
        int queueing = queueRepository.queues.stream().map(queue -> queue.players.size()).reduce(0, Integer::sum);

        objective.setDisplayName(TITLE);

        lines.add(LINE);

        if (state.isLobby()) {
            lines.add("&4Online: &f" + online);
            lines.add("&4Queueing: &f" + queueing);
            lines.add("&4Playing: &f" + playing);
            lines.add("&r&r");
            lines.add("&4Coins: &f420");

            if (state == ProfileState.QUEUE) {
                Queue queue = profileRepository.find(player.getUniqueId()).get().queue;
                String rankity = queue.ranked ? "&cRanked" : "&bUnranked";

                lines.add("&r&r&r");
                lines.add("Queueing for:");
                lines.add(rankity + " " + queue.kit.name);
            }
        }

        if (state == ProfileState.PLAYING) {
            Match match = profileRepository.find(player.getUniqueId()).get().match;
            Team team = match.getTeam(player.getUniqueId());

            System.out.println(player.getName() + " team:");
//            System.out.println(team.formatName(ChatColor.WHITE));

            System.out.println(team);

            Collection<Team> opponents = match.getOpponents(team);
            Collection<Player> players = match.getAllPlayers()
                    .stream()
                    .filter(p -> p != player)
                    .collect(Collectors.toList());

            if(players.size() > 1) {
                lines.add("&cOpponents:");

                opponents.forEach(opponent -> {
                    opponent.getPlayers().forEach(it -> {
                        lines.add("  - &c" + it.getName());
                    });
                });
            } else {
                lines.add("&cOpponent: &f" + opponents.stream().findFirst().get().formatName(ChatColor.WHITE));
            }

        }

        lines.add("&r");
        lines.add("&7www.ratio.rip");
        lines.add(LINE + "&r");

        objective.applyLines(lines);
    }
}
