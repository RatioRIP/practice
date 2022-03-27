package cc.ratio.practice.event.lobby.handler;

import cc.ratio.practice.lobby.LobbyItems;
import cc.ratio.practice.profile.Profile;
import cc.ratio.practice.profile.ProfileRepository;
import cc.ratio.practice.profile.ProfileState;
import cc.ratio.practice.queue.gui.QueueGui;
import me.lucko.helper.Services;
import me.lucko.helper.text3.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class InteractEventHandler implements Consumer<PlayerInteractEvent> {

    private static final ProfileRepository profileRepository = Services.get(ProfileRepository.class).get();

    @Override
    public void accept(PlayerInteractEvent playerInteractEvent) {
        Player player = playerInteractEvent.getPlayer();
        ItemStack item = playerInteractEvent.getPlayer().getItemInHand();

        if (item.isSimilar(LobbyItems.UNRANKED_QUEUE_ITEM)) {
            new QueueGui(player, false).open();
            return;
        }

        if (item.isSimilar(LobbyItems.RANKED_QUEUE_ITEM)) {
            new QueueGui(player, true).open();
            return;
        }

        if (item.isSimilar(LobbyItems.LEAVE_QUEUE_ITEM)) {
            Profile profile = profileRepository.findOrNull(player.getUniqueId());

            profile.queue.remove(profile.toPlayer());
            profile.queue = null;
            profile.state = ProfileState.LOBBY;

            player.sendMessage(Text.colorize("&cYou've left the queue."));
            profile.lobbyInit();

            return;
        }
    }
}
