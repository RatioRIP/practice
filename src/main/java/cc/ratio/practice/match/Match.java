package cc.ratio.practice.match;

import cc.ratio.practice.arena.Arena;
import cc.ratio.practice.kit.Kit;
import cc.ratio.practice.match.team.Team;
import cc.ratio.practice.profile.Profile;
import cc.ratio.practice.profile.ProfileRepository;
import cc.ratio.practice.profile.ProfileState;
import cc.ratio.practice.util.PlayerUtilities;
import me.lucko.helper.Schedulers;
import me.lucko.helper.Services;
import me.lucko.helper.text3.Text;
import one.util.streamex.StreamEx;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Match {

    private static final ProfileRepository profileRepository = Services.get(ProfileRepository.class).get();
    private static final MatchRepository matchRepository = Services.get(MatchRepository.class).get();

    public final UUID uuid;

    public final List<Team> teams;
    public final Arena arena;
    public final AtomicInteger countdown;
    public final Kit kit;
    public MatchState state;
    public final List<Elimination> eliminations;

    /**
     * Constructor for a {@link Match}
     *
     * @param uuid the unique identifier.
     * @param kit the kit.
     * @param arena the arena.
     * @param teams the teams.
     */

    public Match(UUID uuid, Kit kit, Arena arena, List<Team> teams) {
        this.uuid = uuid;
        this.kit = kit;
        this.arena = arena;
        this.teams = teams;

        this.state = MatchState.STARTING;
        this.countdown = new AtomicInteger(3);

        this.eliminations = new ArrayList<>();
    }

    /**
     * Start a {@link Match}
     *
     * @throws Exception the exception to throw if an error occurs.
     */

    public void start() throws Exception {
        // associate spawnpoints with teams
        Map<Team, Location> locations = new HashMap<>();

        if (this.teams.size() > this.arena.spawnpoints.size()) {
            this.stop(StopReason.ERROR, null, null);
            throw new Exception("not enough spawnpoints");
        }

        for (int i = 0; i < this.arena.spawnpoints.size(); i++) {
            locations.put(this.teams.get(i), this.arena.spawnpoints.get(i));
        }

        // teleport all teams to their spawnpoints
        locations.forEach((team, location) -> {
            team.players.forEach(player -> player.teleport(location));
        });

        // get the players ready
        this.getAllProfiles().forEach(profile -> {
            profile.state = ProfileState.PLAYING;
            profile.match = this;

            PlayerUtilities.reset(profile.toPlayer());
            this.kit.apply(profile.toPlayer());
            profile.toPlayer().updateInventory();
        });

        // countdown
        Schedulers.sync().runRepeating(task -> {
            int count = this.countdown.getAndDecrement();

            if (count <= 0) {
                this.state = MatchState.PLAYING;
                task.stop();

                this.msg("&aGame Started");
            } else {
                this.msg("&a" + count);
            }

        }, 10L, 20L);
    }

    /**
     * Stop a {@link Match}
     *
     * @param reason the reason.
     * @param winner the winner.
     * @param losers the losers.
     */

    public void stop(StopReason reason, Team winner, List<Team> losers) {
        // if the match was stopped due to an error, inform the players
        if (reason == StopReason.ERROR) {
            this.msg("&cThe match has stopped due to an error");
        }

        // if the match was forcefully stopped, inform the players
        if (reason == StopReason.FORCED) {
            this.msg("&cThis match has been forcefully stopped");
        }

        // if the match ended, send the statistics
        if (reason == StopReason.END) {
            List<String> message = new ArrayList<>();

            message.add("&7&m--------------------------");
            message.add("&cGame Ended");
            message.add("\n");

            String winnersAndLosers = "";
            winnersAndLosers += "&aWinners: &f" + winner.formatName(ChatColor.WHITE);
            winnersAndLosers += " &r⎜";
            winnersAndLosers += "&cLosers: &f" + losers
                            .stream()
                            .map(team -> team.formatName(ChatColor.WHITE))
                            .collect(Collectors.joining("&7, "));

            message.add(winnersAndLosers);

            message.add("&7&m--------------------------");

            String[] arr = message.toArray(new String[]{});

            this.msg(arr);
        }

        // match end routine

        // teleport all players to the lobby
        this.getAllProfiles().forEach(profile -> {
            profile.state = ProfileState.LOBBY;

            profile.lobbyInit();
            profile.teleportToLobby();
        });

        matchRepository.matches.remove(this);
    }


    /**
     * Eliminates a player from the match
     */
    public void eliminate(UUID uuid, Optional<UUID> killer) {
        this.eliminations.add(new Elimination(uuid, killer));

        Team team = this.getTeam(uuid);

        if(team.getAlivePlayers(this).size() == 0) {
            team.eliminated = true;
        }

        if(killer.isPresent()) {
            this.msg("&c" + Bukkit.getPlayer(uuid).getName() + " was killed by " + Bukkit.getPlayer(killer.get()).getName());
        } else {
            this.msg("&c" + Bukkit.getPlayer(uuid).getName() + " disconnected");
        }

        if(this.canEnd()) {
            this.stop(
                    StopReason.END,
                    this.getRemainingTeams().stream().findFirst().orElse(null),
                    this.teams.stream().filter(it -> it.eliminated).collect(Collectors.toList())
            );
        }
    }

    /**
     * Message all players in the match
     */
    public void msg(String... lines) {
        this.getAllPlayers().forEach(player -> {
            for (String line : lines) {
                player.sendMessage(Text.colorize(line));
            }
        });
    }

    /**
     * Checks if the match can end
     * @return whether the match can end
     */
    public boolean canEnd() {
        return this.getRemainingTeams().size() == 1;
    }

    /**
     * Gets the remaining teams
     * @return a stream of the remaining teams
     */
    public Collection<Team> getRemainingTeams() {
        return this.teams.
                stream().
                filter(team -> !team.eliminated)
                .collect(Collectors.toList());
    }

    /**
     * Gets all eliminations from a given team
     * @param team the team
     * @return a map of the eliminations
     */
    public Collection<Elimination> getEliminations(Team team) {
        return this.eliminations
                .stream()
                .filter(elimination -> team == this.getTeam(elimination.uuid))
                .collect(Collectors.toList());
    }

    /**
     * Gets the elimination (if existent) of a given player
     * @param uuid the uuid of the player
     * @return the elimination (nullable)
     */
    @Nullable
    public Elimination getElimination(UUID uuid) {
        return this.eliminations
                .stream()
                .filter(elimination -> elimination.uuid.equals(uuid))
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks if an entire team has been eliminated
     * @param team the team
     * @return whether the team has been eliminated
     */
    public boolean isTeamEliminated(Team team) {
        return team.players
                .stream()
                .allMatch(player -> this.isEliminated(player.getUniqueId()));
    }

    /**
     * Checks if a player is eliminated
     * @param uuid the uuid of the player
     * @return whether the player is eliminated
     */
    public boolean isEliminated(UUID uuid) {
        return this.getElimination(uuid) != null;
    }

    /**
     * Returns a collection of the players in the match
     * @return the collection
     */
    public Collection<Player> getAllPlayers() {
        StreamEx<Player> stream = StreamEx.empty();

        for (Team team : this.teams) {
            stream = stream.append(team.players);
        }

        return stream.collect(Collectors.toList());
    }

    /**
     * Returns a list of the profile in the match
     * @return the list
     */
    public Collection<Profile> getAllProfiles() {
        return this.getAllPlayers()
                .stream()
                .map(Player::getUniqueId)
                .map(profileRepository::findOrNull)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Gets the team of a player
     * @param uuid the uuid
     * @return the team of the player
     */
    @Nullable
    public Team getTeam(UUID uuid) {
        return this.teams.stream()
                .filter(team -> team.players.contains(uuid))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets a list of the opponents of a team
     * @param team the team
     * @return the list
     */
    public Collection<Team> getOpponents(Team team) {
        return this.teams
                .stream()
                .filter(it -> it != team)
                .collect(Collectors.toList());
    }
}