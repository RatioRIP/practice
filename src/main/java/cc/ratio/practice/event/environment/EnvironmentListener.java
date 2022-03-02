package cc.ratio.practice.event.environment;

import cc.ratio.practice.event.environment.handler.JoinEventHandler;
import cc.ratio.practice.event.environment.handler.QuitEventHandler;
import cc.ratio.practice.profile.ProfileRepository;
import cc.ratio.practice.profile.ProfileState;
import me.lucko.helper.Events;
import me.lucko.helper.Services;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nonnull;

public class EnvironmentListener implements TerminableModule {

    private final ProfileRepository repository = Services.get(ProfileRepository.class).get();

    @Override
    public void setup(@Nonnull TerminableConsumer consumer) {
        Events.subscribe(PlayerJoinEvent.class)
                .handler(new JoinEventHandler())
                .bindWith(consumer);

        Events.subscribe(PlayerQuitEvent.class)
                .filter(event -> repository.find(event.getPlayer().getUniqueId()).isPresent())
                .handler(new QuitEventHandler())
                .bindWith(consumer);

        Events.subscribe(BlockBreakEvent.class)
                .filter(event -> repository.find(event.getPlayer().getUniqueId()).get().state == ProfileState.LOBBY)
                .handler(event -> event.setCancelled(true))
                .bindWith(consumer);

        Events.subscribe(BlockPlaceEvent.class)
                .filter(event -> repository.find(event.getPlayer().getUniqueId()).get().state == ProfileState.LOBBY)
                .handler(event -> event.setCancelled(true))
                .bindWith(consumer);

        Events.subscribe(FoodLevelChangeEvent.class)
                .handler(event -> event.setCancelled(true))
                .bindWith(consumer);
    }
}
