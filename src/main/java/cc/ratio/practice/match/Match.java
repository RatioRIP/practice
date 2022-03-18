package cc.ratio.practice.match;

import cc.ratio.practice.arena.Arena;
import cc.ratio.practice.match.team.Team;
import cc.ratio.practice.profile.Profile;
import cc.ratio.practice.profile.ProfileRepository;
import cc.ratio.practice.profile.ProfileState;
import me.lucko.helper.Services;
import me.lucko.helper.text3.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class Match {

    private static final ProfileRepository profileRepository = Services.get(ProfileRepository.class).get();
    private static final MatchRepository matchRepository = Services.get(MatchRepository.class).get();

    public final UUID uuid;

    public final List<Team> teams;
    public final Arena arena;

    public Match(final UUID uuid, final Arena arena, final List<Team> teams) {
        this.uuid = uuid;
        this.arena = arena;
        this.teams = teams;
    }

    public void start() throws Exception {
        Map<UUID, Location> locations = new HashMap<>();

        if (this.teams.size() > this.arena.spawnpoints.size()) {
            throw new Exception("not enough spawnpoints");
        }
    }

    public void stop(StopReason reason, Team winner, List<Team> losers) {
        if(reason == StopReason.ERROR) {
            this.msg("&cThe match has stopped due to an error");
        }

        if(reason == StopReason.FORCED) {
            this.msg("&cThis match has been forcefully stopped");
        }

        if(reason == StopReason.END) {
            final List<String> message = new ArrayList<>();

            message.add("&cGame Ended");
            message.add("\n");
            message.add("\n");
            message.add("&aWinner" + (winner.size() > 1 ? "s" : "") + ": &f" + winner.formatName(ChatColor.GREEN));
            message.add("\n");
            message.add("&cLoser" + (losers.size() > 1 ? "s" : "") + ": &f" +
                    losers
                            .stream()
                            .map(team -> team.formatName(ChatColor.RED))
                            .collect(Collectors.joining("&7, "))
            );

            String[] arr = message.toArray(new String[]{});

            this.msg(arr);
        }

        this.getProfiles().forEach(profile -> {
            profile.state = ProfileState.LOBBY;

            profile.lobbyInit();
            profile.lobbyTeleport();
        });

        matchRepository.matches.remove(this);
    }

    public void msg(String... lines) {
        for (Player player : this.getPlayers()) {
            for (String line : lines) {
                player.sendMessage(Text.colorize(line));
            }
        }
    }

    public List<Player> getPlayers() {
        // TODO: Optimize?
        final List<UUID> uuids = new ArrayList<>();

        this.teams.forEach(uuids::addAll);

        return uuids.stream()
                .map(Bukkit::getPlayer)
                .collect(Collectors.toList());
    }

    public List<Profile> getProfiles() {
        // TODO: Optimize?
        final List<UUID> uuids = new ArrayList<>();

        this.teams.forEach(uuids::addAll);

        return uuids.stream()
                .map(uuid -> profileRepository.find(uuid).get())
                .collect(Collectors.toList());
    }

}