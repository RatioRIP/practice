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
        Player player = playerJoinEvent.getPlayer();

        this.repository.put(new Profile(playerJoinEvent.getPlayer().getUniqueId()));

        for (String line : WELCOME_MESSAGE) {
            player.sendMessage(Text.colorize(line));
        }

        PlayerUtilities.reset(player);
        player.teleport(Bukkit.getWorld("world").getSpawnLocation());

        LobbyItems.ITEMS.forEach((slot, item) -> {
            player.getInventory().setItem(slot, item);
        });
    }
}
