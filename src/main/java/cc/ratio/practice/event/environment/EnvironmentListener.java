package cc.ratio.practice.event.environment;

import cc.ratio.practice.event.environment.handler.JoinEventHandler;
import cc.ratio.practice.event.environment.handler.QuitEventHandler;
import cc.ratio.practice.match.Match;
import cc.ratio.practice.match.MatchRepository;
import cc.ratio.practice.match.MatchState;
import cc.ratio.practice.profile.Profile;
import cc.ratio.practice.profile.ProfileRepository;
import cc.ratio.practice.profile.ProfileState;
import me.lucko.helper.Events;
import me.lucko.helper.Services;
import me.lucko.helper.event.filter.EventHandlers;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EnvironmentListener implements TerminableModule {

    private static final List<Material> BLOCKED_ITEMS = Arrays.asList(
            Material.CHEST,
            Material.ENDER_CHEST,
            Material.TRAPPED_CHEST,
            Material.ENCHANTMENT_TABLE,
            Material.WORKBENCH,
            Material.WOODEN_DOOR,
            Material.TRAP_DOOR,
            Material.FENCE_GATE
    );

    private static final ProfileRepository profileRepository = Services.get(ProfileRepository.class).get();
    private static final MatchRepository matchRepository = Services.get(MatchRepository.class).get();

    @Override
    public void setup(@Nonnull TerminableConsumer consumer) {
        // player join event
        Events.subscribe(PlayerJoinEvent.class)
                .handler(new JoinEventHandler())
                .bindWith(consumer);

        // player quit event
        Events.subscribe(PlayerQuitEvent.class)
                .filter(event -> profileRepository.find(event.getPlayer().getUniqueId()).isPresent())
                .handler(new QuitEventHandler())
                .bindWith(consumer);

        // cancel interact event if player is using a forbidden item
        Events.subscribe(PlayerInteractEvent.class)
                .filter(event -> event.getAction() == Action.RIGHT_CLICK_BLOCK)
                .filter(event -> BLOCKED_ITEMS.contains(event.getClickedBlock().getType()))
                .handler(event -> event.setCancelled(true))
                .bindWith(consumer);

        // cancel weather event
        Events.subscribe(WeatherChangeEvent.class)
                .handler(event -> event.setCancelled(true))
                .bindWith(consumer);

        // cancel mob spawn event
        Events.subscribe(CreatureSpawnEvent.class)
                .handler(event -> event.setCancelled(true))
                .bindWith(consumer);

        // hunger change event
        Events.subscribe(FoodLevelChangeEvent.class)
                .handler(event -> {
                    event.setFoodLevel(event.getFoodLevel() + 1);
                })
                .bindWith(consumer);

        // cancel drop event
        Events.subscribe(PlayerDropItemEvent.class)
                .filter(event -> this.inMatch(event.getPlayer().getUniqueId()))
                .filter(event -> this.getMatch(event.getPlayer().getUniqueId()).state != MatchState.PLAYING)
                .handler(EventHandlers.cancel())
                .bindWith(consumer);

        // cancel pickup event
        Events.subscribe(PlayerPickupItemEvent.class)
                .filter(event -> this.inMatch(event.getPlayer().getUniqueId()))
                .filter(event -> this.getMatch(event.getPlayer().getUniqueId()).state != MatchState.PLAYING)
                .handler(EventHandlers.cancel())
                .bindWith(consumer);

        // cancel damage event
        Events.subscribe(EntityDamageEvent.class)
                .filter(event -> event.getEntity().getType() == EntityType.PLAYER)
                .filter(event -> this.inMatch(event.getEntity().getUniqueId()))
                .filter(event -> this.getMatch(event.getEntity().getUniqueId()).state != MatchState.PLAYING)
                .handler(EventHandlers.cancel())
                .bindWith(consumer);

        // send players to spawn when respawning
        Events.subscribe(PlayerRespawnEvent.class)
                .handler(event -> {
                    Profile profile = profileRepository.findOrNull(event.getPlayer().getUniqueId());

                    profile.teleportToLobby();
                    profile.lobbyInit();
                })
                .bindWith(consumer);
    }

    private boolean inMatch(UUID uuid) {
        Optional<Profile> profileOptional = profileRepository.find(uuid);

        if (!profileOptional.isPresent()) {
            return false;
        }

        Profile profile = profileOptional.get();

        return profile.state == ProfileState.PLAYING && profile.match != null;
    }

    private Match getMatch(UUID uuid) {
        if (!this.inMatch(uuid)) {
            return null;
        }

        Profile profile = profileRepository.find(uuid).get();

        return profile.match;
    }

}
