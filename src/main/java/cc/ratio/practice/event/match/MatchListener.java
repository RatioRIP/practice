package cc.ratio.practice.event.match;

import cc.ratio.practice.match.Match;
import cc.ratio.practice.match.MatchRepository;
import cc.ratio.practice.match.MatchState;
import cc.ratio.practice.match.StopReason;
import cc.ratio.practice.profile.Profile;
import cc.ratio.practice.profile.ProfileRepository;
import cc.ratio.practice.profile.ProfileState;
import me.lucko.helper.Events;
import me.lucko.helper.Services;
import me.lucko.helper.event.filter.EventFilters;
import me.lucko.helper.event.filter.EventHandlers;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class MatchListener implements TerminableModule {

    private static final ProfileRepository profileRepository = Services.get(ProfileRepository.class).get();
    private static final MatchRepository matchRepository = Services.get(MatchRepository.class).get();

    @Override
    public void setup(@Nonnull TerminableConsumer consumer) {
//        Events.subscribe(BlockBreakEvent.class)
//                 is player in a match?
//                .filter(event -> this.inMatch(event.getPlayer().getUniqueId()))
//
//                 cancel break event
//                .handler(EventHandlers.cancel())
//                .bindWith(consumer);

//        Events.subscribe(BlockPlaceEvent.class)
//                 is player in a match?
//                .filter(event -> this.inMatch(event.getPlayer().getUniqueId()))
//
//                 cancel place event
//                .handler(EventHandlers.cancel())
//                .bindWith(consumer);

        Events.subscribe(PlayerDropItemEvent.class)
                // is the player in a match?
                .filter(event -> this.inMatch(event.getPlayer().getUniqueId()))

                // has the match not started?
                .filter(event -> this.getMatch(event.getPlayer().getUniqueId()).state != MatchState.PLAYING)

                // cancel drop event
                .handler(EventHandlers.cancel())
                .bindWith(consumer);

        Events.subscribe(PlayerPickupItemEvent.class)
                // is the player in a match?
                .filter(event -> this.inMatch(event.getPlayer().getUniqueId()))

                // has the match not started?
                .filter(event -> this.getMatch(event.getPlayer().getUniqueId()).state != MatchState.PLAYING)

                // cancel pickup event
                .handler(EventHandlers.cancel())
                .bindWith(consumer);

        Events.subscribe(EntityDamageEvent.class)
                // is entity a player?
                .filter(event -> event.getEntity().getType() == EntityType.PLAYER)

                // is player in a match?
                .filter(event -> this.inMatch(event.getEntity().getUniqueId()))

                // has the match not started?
                .filter(event -> this.getMatch(event.getEntity().getUniqueId()).state != MatchState.PLAYING)

                // cancel damage event
                .handler(EventHandlers.cancel())
                .bindWith(consumer);

        Events.subscribe(EntityDamageByEntityEvent.class)
                // are entities involved players?
                .filter(event -> event.getDamager().getType() == EntityType.PLAYER)
                .filter(event -> event.getEntity().getType() == EntityType.PLAYER)

                // are players playing?
                .filter(event -> profileRepository.find(event.getDamager().getUniqueId()).get().state == ProfileState.PLAYING)
                .filter(event -> profileRepository.find(event.getEntity().getUniqueId()).get().state == ProfileState.PLAYING)

                // is damaged player gonna die?
                .filter(event -> ((Player) event.getEntity()).getHealth() - event.getFinalDamage() <= 0)

                .handler(event -> {
                    Player loser = (Player) event.getEntity();
                    Player winner = (Player) event.getDamager();

                    Match match = this.getMatch(loser.getUniqueId());

                    event.setDamage(0);

                    match.stop(StopReason.END, match.getTeam(winner.getUniqueId()), match.getOpponents(winner.getUniqueId()));

                    match.msg("&f" + loser.getName() + " &cwas killed by &f" + winner.getName());
                })

                .bindWith(consumer);

        Events.subscribe(PlayerMoveEvent.class)
                // is the player not in the same block?
                .filter(EventFilters.ignoreSameBlock())

                // is player in a match?
                .filter(event -> this.inMatch(event.getPlayer().getUniqueId()))

                // has the match not started?
                .filter(event -> this.getMatch(event.getPlayer().getUniqueId()).state == MatchState.STARTING)

                // cancel move event
                .handler(event -> event.setTo(event.getFrom()))
                .bindWith(consumer);


        Events.subscribe(PlayerQuitEvent.class, EventPriority.HIGHEST)
                // is player in a match?
                .filter(event -> this.inMatch(event.getPlayer().getUniqueId()))

                // cancel quit event
                .handler(event -> {
                    UUID loser = event.getPlayer().getUniqueId();
                    Match match = this.getMatch(loser);

                    System.out.println(match);

                    match.stop(StopReason.END, match.getOpponents(loser).get(0), Collections.singletonList(match.getTeam(loser)));

                    match.msg("&c" + event.getPlayer() + " disconnected");
                })
                .bindWith(consumer);

        Events.subscribe(PlayerDeathEvent.class)
                // is player in a match?
                .filter(event -> this.inMatch(event.getEntity().getUniqueId()))

                // has the match not started?
                .filter(event -> this.getMatch(event.getEntity().getUniqueId()).state == MatchState.PLAYING)

                // cancel move event
                .handler(event -> {
                    Player loser = event.getEntity();
                    Match match = this.getMatch(loser.getUniqueId());

                    event.getDrops().clear();

                    if (event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                        Player winner = (Player) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();

                        match.stop(StopReason.END, match.getTeam(winner.getUniqueId()), match.getOpponents(winner.getUniqueId()));
                    } else {

                        // Here I realize that the plugin is not yet ready to handle more than 2 players.
                        // TODO: Introduce the concept of eliminations, and end the match only if there is one team left,
                        // when a player dies, they get eliminated, and if an entire team dies, we can say that the team
                        // has been eliminated
                        match.stop(
                                StopReason.END,
                                match.getOpponents(loser.getUniqueId()).get(0),
                                Collections.singletonList(match.getTeam(loser.getUniqueId()))
                        );
                    }
                })
                .bindWith(consumer);

    }

    private boolean inMatch(final UUID uuid) {
        final Optional<Profile> profileOptional = profileRepository.find(uuid);

        if (!profileOptional.isPresent()) {
            return false;
        }

        final Profile profile = profileOptional.get();

        return profile.state == ProfileState.PLAYING && profile.match != null;
    }

    private Match getMatch(final UUID uuid) {
        if (!this.inMatch(uuid)) {
            return null;
        }

        Profile profile = profileRepository.find(uuid).get();

        return profile.match;
    }

}
