package cc.ratio.practice.event.environment.handler;

import cc.ratio.practice.profile.Profile;
import cc.ratio.practice.profile.ProfileRepository;
import me.lucko.helper.Services;
import me.lucko.helper.text3.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.function.Consumer;

public class JoinEventHandler implements Consumer<PlayerJoinEvent> {

    private static final String[] WELCOME_MESSAGE = {
            "Welcome to &4&lRatio",
            "",
            "&cWebsite: &fhttps://ratio.cc/",
            "&dDiscord: &fhttps://discord.ratio.cc/",
            "&bTwitter: &fhttps://twitter.com/RatioCC/"
    };

    private static final ProfileRepository repository = Services.get(ProfileRepository.class).get();

    @Override
    public void accept(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();

        Profile profile = new Profile(playerJoinEvent.getPlayer().getUniqueId());
        repository.put(profile);

        for (String line : WELCOME_MESSAGE) {
            player.sendMessage(Text.colorize(line));
        }

        profile.scoreboardInit();
        profile.lobbyInit();
        profile.teleportToLobby();
    }
}
