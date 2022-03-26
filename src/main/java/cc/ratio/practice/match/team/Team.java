package cc.ratio.practice.match.team;

import cc.ratio.practice.match.Match;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Team {

    public final Collection<Player> players;
    public boolean eliminated = false;

    public Team(Collection<Player> players) {
        this.players = players;
    }

    /**
     * Formats the player names
     * @param color the color that the player names should be in
     * @return a string of a list of player names
     */
    public String formatName(ChatColor color) {
        return this.players.stream()
                .map(Player::getName)
                .map(s -> color + s)
                .collect(Collectors.joining(ChatColor.GRAY + ", "));
    }

    /**
     * Streams the players
     * @return a stream of the players
     */
    public Collection<Player> getPlayers() {
        return this.players;
    }

    /**
     * Gets the players that are alive
     * @param match the match
     * @return a collection of the players that are alive
     */
    public Collection<Player> getAlivePlayers(Match match) {
        return this.getPlayers()
                .stream()
                .filter(player -> !match.isEliminated(player.getUniqueId()))
                .collect(Collectors.toList());
    }


}
