package cc.ratio.practice.event.lobby;

import cc.ratio.practice.event.lobby.handler.InteractEventHandler;
import cc.ratio.practice.profile.ProfileRepository;
import cc.ratio.practice.profile.ProfileState;
import me.lucko.helper.Events;
import me.lucko.helper.Services;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.annotation.Nonnull;

public class LobbyListener implements TerminableModule {

    private final ProfileRepository repository = Services.get(ProfileRepository.class).get();

    @Override
    public void setup(@Nonnull TerminableConsumer consumer) {
        Events.subscribe(BlockBreakEvent.class)
                .filter(event -> repository.find(event.getPlayer().getUniqueId()).get().state == ProfileState.LOBBY)
                .handler(event -> event.setCancelled(true))
                .bindWith(consumer);

        Events.subscribe(BlockPlaceEvent.class)
                .filter(event -> repository.find(event.getPlayer().getUniqueId()).get().state == ProfileState.LOBBY)
                .handler(event -> event.setCancelled(true))
                .bindWith(consumer);

        Events.subscribe(FoodLevelChangeEvent.class)
                .filter(event -> repository.find(event.getEntity().getUniqueId()).get().state == ProfileState.LOBBY)
                .handler(event -> event.setCancelled(true))
                .bindWith(consumer);

        Events.subscribe(PlayerInteractEvent.class)
                .filter(event -> event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                .filter(event -> event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() != Material.AIR)
                .filter(event -> repository.find(event.getPlayer().getUniqueId()).get().state == ProfileState.LOBBY)
                .handler(new InteractEventHandler())
                .bindWith(consumer);
    }
}
