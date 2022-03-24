package cc.ratio.practice.profile;

import cc.ratio.practice.lobby.LobbyItems;
import cc.ratio.practice.match.Match;
import cc.ratio.practice.profile.account.Account;
import cc.ratio.practice.queue.Queue;
import cc.ratio.practice.scoreboard.ScoreboardUpdater;
import cc.ratio.practice.util.PlayerUtilities;
import me.lucko.helper.Services;
import me.lucko.helper.mongo.Mongo;
import me.lucko.helper.mongo.external.morphia.Datastore;
import me.lucko.helper.scoreboard.Scoreboard;
import me.lucko.helper.scoreboard.ScoreboardObjective;
import me.lucko.helper.scoreboard.ScoreboardProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.UUID;

public class Profile {

    public final UUID uuid;
    public Account account;

    public ProfileState state;

    public Queue queue;
    public Match match;

    public ScoreboardObjective scoreboardObjective;

    public boolean build;

    public long pearlCooldown;

    /**
     * Constructor to create a new {@link Profile}
     *
     * @param uuid the unique identifier.
     */

    public Profile(UUID uuid) {
        this.uuid = uuid;
        this.state = ProfileState.LOBBY;

        this.queue = null;
        this.match = null;
        this.build = false;

        this.loadAccount();
    }

    /**
     * Load the {@link Account}
     *
     * @return the account.
     */

    public Account loadAccount() {
        final Mongo mongo = Services.get(Mongo.class).get();
        final Datastore datastore = mongo.getMorphiaDatastore();

        Account account = datastore.find(Account.class)
                .filter("uuid", this.uuid)
                .get();

        if (account != null) {
            this.account = account;
            return account;
        }

        this.account = new Account(this.uuid);
        this.account.name = this.toPlayer().getName();

        datastore.save(this.account);

        return this.account;
    }

    /**
     * Save the {@link Account}
     */

    public void save() {
        final Mongo mongo = Services.get(Mongo.class).get();
        final Datastore datastore = mongo.getMorphiaDatastore();

        datastore.save(this.account);
    }

    public void scoreboardInit() {
        final Scoreboard scoreboard = Services.get(ScoreboardProvider.class)
                .orElseGet(() -> Services.load(ScoreboardProvider.class))
                .getScoreboard();

        this.scoreboardObjective = scoreboard.createPlayerObjective(this.toPlayer(), "null", DisplaySlot.SIDEBAR);

        this.scoreboardUpdate();
    }

    /**
     * Update the {@link ScoreboardUpdater}
     */

    public void scoreboardUpdate() {
        ScoreboardUpdater.update(this.toPlayer(), this.scoreboardObjective, this.state);
    }

    public void lobbyInit() {
        this.state = ProfileState.LOBBY;
        this.queue = null;
        this.scoreboardUpdate();

        PlayerUtilities.reset(this.toPlayer());

        LobbyItems.LOBBY_ITEMS.forEach((slot, item) -> {
            this.toPlayer().getInventory().setItem(slot, item);
        });
    }

    /**
     * Teleport a {@link Player} to the 'world' spawn location.
     */

    public void lobbyTeleport() {
        this.toPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());
    }

    /**
     * Update the scoreboard for a {@link Player} when they join a {@link Queue}
     */

    public void queueInit() {
        this.state = ProfileState.QUEUE;
        this.scoreboardUpdate();

        PlayerUtilities.reset(this.toPlayer());

        LobbyItems.QUEUE_ITEMS.forEach((slot, item) -> {
            this.toPlayer().getInventory().setItem(slot, item);
        });
    }

    /**
     * Get the {@link Player} by a {@link UUID}
     *
     * @return the player.
     */

    public Player toPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }
}
