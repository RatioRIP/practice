package cc.ratio.practice.scoreboard;

import cc.ratio.practice.profile.ProfileRepository;
import cc.ratio.practice.queue.Queue;
import me.lucko.helper.Services;
import me.lucko.helper.scoreboard.ScoreboardObjective;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public class ScoreboardUpdaters {

    private static final ProfileRepository profileRepository = Services.get(ProfileRepository.class).get();

    public static final String TITLE = "&4&lRATIO";

    public static final BiConsumer<Player, ScoreboardObjective> LOBBY_UPDATER = (player, objective) -> {
        objective.setDisplayName(TITLE + " &7- &fLobby");
        objective.applyLines(
                "&7&m--------------------",
                "&cPlaying: &7" + 69420,
                "&7&m--------------------&r"
        );
    };

    public static final BiConsumer<Player, ScoreboardObjective> QUEUE_UPDATER = (player, objective) -> {
        Queue queue = profileRepository.find(player.getUniqueId()).get().queue;
        final String rankity = queue.ranked ? "&cRanked" : "&bUnranked";

        objective.setDisplayName(TITLE + " &7- &fIn Queue");
        objective.applyLines(
                "&7&m--------------------",
                "Queueing for " + rankity + " " + queue.kit.name,
                "&7&m--------------------&r"
        );
    };

}
