package cc.ratio.practice.event.environment.handler;

import cc.ratio.practice.profile.Profile;
import cc.ratio.practice.profile.ProfileRepository;
import me.lucko.helper.Services;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.function.Consumer;

public class QuitEventHandler implements Consumer<PlayerQuitEvent> {

    private final ProfileRepository repository = Services.get(ProfileRepository.class).get();

    @Override
    public void accept(PlayerQuitEvent playerQuitEvent) {
        Profile profile = this.repository.find(playerQuitEvent.getPlayer().getUniqueId()).get();

        profile.save();
        this.repository.remove(profile);
    }
}
