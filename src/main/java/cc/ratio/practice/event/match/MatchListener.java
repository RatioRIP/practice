package cc.ratio.practice.event.match;

import cc.ratio.practice.match.Match;
import cc.ratio.practice.match.MatchRepository;
import cc.ratio.practice.match.MatchState;
import cc.ratio.practice.profile.Profile;
import cc.ratio.practice.profile.ProfileRepository;
import cc.ratio.practice.profile.ProfileState;
import me.lucko.helper.Events;
import me.lucko.helper.Services;
import me.lucko.helper.event.filter.EventFilters;
import me.lucko.helper.event.filter.EventHandlers;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public class MatchListener implements TerminableModule {

    private static final ProfileRepository profileRepository = Services.get(ProfileRepository.class).get();
    private static final MatchRepository matchRepository = Services.get(MatchRepository.class).get();

    @Override
    public void setup(@Nonnull TerminableConsumer consumer) {
        // cancel move event if game hasn't started yet
        Events.subscribe(PlayerMoveEvent.class)
                .filter(EventFilters.ignoreSameBlock())
                .filter(event -> this.inMatch(event.getPlayer().getUniqueId()))
                .filter(event -> this.getMatch(event.getPlayer().getUniqueId()).state == MatchState.STARTING)
                .handler(event -> event.setTo(event.getFrom()))
                .bindWith(consumer);


        Events.subscribe(PlayerDeathEvent.class)
                .filter(event -> this.inMatch(event.getEntity().getUniqueId()))
                .filter(event -> this.getMatch(event.getEntity().getUniqueId()).state == MatchState.PLAYING)
                .handler(event -> {
                    Player player = event.getEntity();
                    Match match = this.getMatch(player.getUniqueId());

                    event.setDeathMessage(null);
                    event.setDroppedExp(0);

                    player.spigot().respawn();

                    if(event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                        Player killer = (Player) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();

                        match.eliminate(player.getUniqueId(), Optional.of(killer.getUniqueId()));
                    } else {
                        match.eliminate(player.getUniqueId(), Optional.empty());
                    }
                })
                .bindWith(consumer);

        // cancel pearling if event hasn't started yet
        Events.subscribe(PlayerInteractEvent.class)
                .filter(event -> this.inMatch(event.getPlayer().getUniqueId()))
                .filter(event -> event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                .filter(event -> event.getItem() != null && event.getItem().getType() == Material.ENDER_PEARL)
                .filter(event -> this.getMatch(event.getPlayer().getUniqueId()).state != MatchState.PLAYING)
                .handler(EventHandlers.cancel())
                .bindWith(consumer);

        // interact event 2 (cooldown)
//        Events.subscribe(PlayerInteractEvent.class)
//                .filter(event -> this.inMatch(event.getPlayer().getUniqueId()))
//                .filter(event -> event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
//                .filter(event -> event.getItem() != null && event.getItem().getType() == Material.ENDER_PEARL)
//                .handler(event -> {
//
//                })
//                .bindWith(consumer);
    }

    private boolean hasCooldown(UUID uuid) {
        Optional<Profile> profileOptional = profileRepository.find(uuid);

        if (!profileOptional.isPresent()) {
            return false;
        }

        Profile profile = profileOptional.get();

        return false;
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
