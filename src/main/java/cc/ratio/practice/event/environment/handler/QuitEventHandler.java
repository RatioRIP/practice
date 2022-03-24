package cc.ratio.practice.event.environment.handler;

import cc.ratio.practice.match.Match;
import cc.ratio.practice.match.StopReason;
import cc.ratio.practice.profile.Profile;
import cc.ratio.practice.profile.ProfileRepository;
import cc.ratio.practice.profile.ProfileState;
import me.lucko.helper.Services;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.UUID;
import java.util.function.Consumer;

public class QuitEventHandler implements Consumer<PlayerQuitEvent> {

    private final ProfileRepository repository = Services.get(ProfileRepository.class).get();

    @Override
    public void accept(final PlayerQuitEvent playerQuitEvent) {
        final Profile profile = this.repository.find(playerQuitEvent.getPlayer().getUniqueId()).get();

        profile.queue.remove(playerQuitEvent.getPlayer());
        profile.queue = null;

        if(profile.match != null && profile.state == ProfileState.PLAYING) {
            UUID loser = playerQuitEvent.getPlayer().getUniqueId();
            Match match = profile.match;

            match.stop(StopReason.END, match.getOpponents(loser).get(0), Collections.singletonList(match.getTeam(loser)));

            match.msg("&c" + playerQuitEvent.getPlayer().getName() + " disconnected");
        }

        profile.save();
        this.repository.remove(profile);
    }
}
