package cc.ratio.practice.profile;

import cc.ratio.practice.lobby.LobbyItems;
import cc.ratio.practice.profile.account.Account;
import cc.ratio.practice.queue.Queue;
import cc.ratio.practice.scoreboard.ScoreboardUpdaters;
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

    public ScoreboardObjective scoreboardObjective;

    public Profile(final UUID uuid) {
        this.uuid = uuid;
        this.state = ProfileState.LOBBY;
        this.queue = null;

        this.loadAccount();
    }

    public Account loadAccount() {
        final Mongo mongo = Services.get(Mongo.class).get();
        final Datastore datastore = mongo.getMorphiaDatastore();

        Account account = datastore.find(Account.class)
                .filter("uuid", this.uuid)
                .get();

        // if account exists, load it
        if (account != null) {
            this.account = account;
            return account;
        }

        // create new account
        account = new Account(this.uuid);
        account.name = this.toPlayer().getName();

        datastore.save(account);

        return account;
    }


    public void save() {
        final Mongo mongo = Services.get(Mongo.class).get();
        final Datastore datastore = mongo.getMorphiaDatastore();

        datastore.save(this.account);
    }

    public void scoreboardInit() {
        Scoreboard scoreboard = Services.get(ScoreboardProvider.class)
                .orElseGet(() -> Services.load(ScoreboardProvider.class))
                .getScoreboard();

        this.scoreboardObjective = scoreboard.createPlayerObjective(this.toPlayer(), "null", DisplaySlot.SIDEBAR);

        this.scoreboardUpdate();
    }

    public void scoreboardUpdate() {
        if(this.state == ProfileState.LOBBY) {
            ScoreboardUpdaters.LOBBY_UPDATER.accept(this.toPlayer(), this.scoreboardObjective);
        }

        if(this.state == ProfileState.QUEUE) {
            ScoreboardUpdaters.QUEUE_UPDATER.accept(this.toPlayer(), this.scoreboardObjective);
        }
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

    public void queueInit() {
        this.state = ProfileState.QUEUE;
        this.scoreboardUpdate();

        PlayerUtilities.reset(this.toPlayer());

        LobbyItems.QUEUE_ITEMS.forEach((slot, item) -> {
            this.toPlayer().getInventory().setItem(slot, item);
        });
    }

    public Player toPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }
}
