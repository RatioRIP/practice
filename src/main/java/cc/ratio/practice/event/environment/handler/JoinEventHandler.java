package cc.ratio.practice.event.environment.handler;

import cc.ratio.practice.profile.Profile;
import cc.ratio.practice.profile.ProfileRepository;
import me.lucko.helper.Services;
import me.lucko.helper.text3.Text;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.function.Consumer;

public class JoinEventHandler implements Consumer<PlayerJoinEvent> {

    private final ProfileRepository repository = Services.get(ProfileRepository.class).get();

    private final static String[] WELCOME_MESSAGE = new String[]{
            "Welcome to &4&lRatio",
            "",
            "&cWebsite: &fhttps://ratio.cc/",
            "&dDiscord: &fhttps://discord.ratio.cc/",
            "&bTwitter: &fhttps://twitter.com/RatioCC/"
    };

    @Override
    public void accept(PlayerJoinEvent playerJoinEvent) {
        this.repository.put(new Profile(playerJoinEvent.getPlayer().getUniqueId()));

        for (String line : WELCOME_MESSAGE) {
            playerJoinEvent.getPlayer().sendMessage(Text.colorize(line));
        }
    }
}
