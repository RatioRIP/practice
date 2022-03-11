package cc.ratio.practice.event.environment.handler;

import cc.ratio.practice.lobby.LobbyItems;
import cc.ratio.practice.profile.Profile;
import cc.ratio.practice.profile.ProfileRepository;
import cc.ratio.practice.util.PlayerUtilities;
import me.lucko.helper.Services;
import me.lucko.helper.text3.Text;
import org.bukkit.Bukkit;
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
    private final ProfileRepository repository = Services.get(ProfileRepository.class).get();

    @Override
    public void accept(final PlayerJoinEvent playerJoinEvent) {
        final Player player = playerJoinEvent.getPlayer();

        Profile profile = new Profile(playerJoinEvent.getPlayer().getUniqueId());
        this.repository.put(profile);

        for (final String line : WELCOME_MESSAGE) {
            player.sendMessage(Text.colorize(line));
        }

        player.teleport(Bukkit.getWorld("world").getSpawnLocation());

        profile.lobbyInit();
    }
}
