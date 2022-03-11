package cc.ratio.practice.event.environment;

import cc.ratio.practice.event.environment.handler.JoinEventHandler;
import cc.ratio.practice.event.environment.handler.QuitEventHandler;
import cc.ratio.practice.profile.ProfileRepository;
import me.lucko.helper.Events;
import me.lucko.helper.Services;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class EnvironmentListener implements TerminableModule {

    private final ProfileRepository repository = Services.get(ProfileRepository.class).get();

    private final static List<Material> BLOCKED_ITEMS = Arrays.asList(
            Material.CHEST,
            Material.ENDER_CHEST,
            Material.TRAPPED_CHEST,
            Material.ENCHANTMENT_TABLE,
            Material.WORKBENCH,
            Material.WOODEN_DOOR,
            Material.TRAP_DOOR,
            Material.FENCE_GATE
    );

    @Override
    public void setup(@Nonnull final TerminableConsumer consumer) {
        Events.subscribe(PlayerJoinEvent.class)
                .handler(new JoinEventHandler())
                .bindWith(consumer);

        Events.subscribe(PlayerQuitEvent.class)
                .filter(event -> this.repository.find(event.getPlayer().getUniqueId()).isPresent())
                .handler(new QuitEventHandler())
                .bindWith(consumer);

        Events.subscribe(PlayerInteractEvent.class)
                .filter(event -> event.getAction() == Action.RIGHT_CLICK_BLOCK)
                .filter(event -> BLOCKED_ITEMS.contains(event.getClickedBlock().getType()))
                .handler(event -> event.setCancelled(true));

        Events.subscribe(WeatherChangeEvent.class)
                .handler(event -> event.setCancelled(true));

        Events.subscribe(CreatureSpawnEvent.class)
                .handler(event -> event.setCancelled(true));
    }
}
