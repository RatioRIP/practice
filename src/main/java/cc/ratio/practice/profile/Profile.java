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
import me.lucko.helper.text3.Text;
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
     * @param uuid the unique identifier
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
     * Load the {@link Account} from mongo
     *
     * @return the account
     */

    public Account loadAccount() {
        Mongo mongo = Services.get(Mongo.class).get();
        Datastore datastore = mongo.getMorphiaDatastore();

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
     * Save the {@link Account} to mongo
     */

    public void save() {
        Mongo mongo = Services.get(Mongo.class).get();
        Datastore datastore = mongo.getMorphiaDatastore();

        datastore.save(this.account);
    }

    /**
     * Initialize the scoreboard for the player
     */
    public void scoreboardInit() {
        Scoreboard scoreboard = Services.get(ScoreboardProvider.class)
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

    /*
     * Give the player the items for the lobby state
     */
    public void lobbyInit() {
        this.state = ProfileState.LOBBY;
        this.queue = null;
        this.match = null;

        PlayerUtilities.reset(this.toPlayer());

        LobbyItems.LOBBY_ITEMS.forEach((slot, item) -> {
            this.toPlayer().getInventory().setItem(slot, item);
        });
    }

    /**
     * Give the player the items for the queue state
     */
    public void queueInit(Queue queue) {
        this.state = ProfileState.QUEUE;
        this.queue = queue;
        this.match = null;

        queue.add(this.toPlayer());

        PlayerUtilities.reset(this.toPlayer());

        LobbyItems.QUEUE_ITEMS.forEach((slot, item) -> {
            this.toPlayer().getInventory().setItem(slot, item);
        });

        String rankity = queue.ranked ? "&cRanked" : "&bUnranked";

        this.msg("&eYou've joined the " + rankity + " &f" + queue.kit.name + " &equeue.");
    }

    /**
     * Messages the player
     */
    public void msg(String... msg) {
        for (String line : msg) {
            this.toPlayer().sendMessage(Text.colorize(line));
        }
    }

    /**
     * Teleport a {@link Player} to the 'world' spawn location.
     */
    public void teleportToLobby() {
        this.toPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());
    }

    /**
     * Get the {@link Player} that associates to this profile by their {@link UUID}
     *
     * @return the player.
     */
    public Player toPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

}
