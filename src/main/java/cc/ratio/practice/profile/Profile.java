package cc.ratio.practice.profile;

import cc.ratio.practice.lobby.LobbyItems;
import cc.ratio.practice.profile.account.Account;
import cc.ratio.practice.queue.Queue;
import cc.ratio.practice.util.PlayerUtilities;
import me.lucko.helper.Services;
import me.lucko.helper.mongo.Mongo;
import me.lucko.helper.mongo.external.morphia.Datastore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Profile {

    public final UUID uuid;
    public Account account;

    public ProfileState state;
    public Queue queue;

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

    public void lobbyInit() {
        this.state = ProfileState.LOBBY;
        this.queue = null;

        PlayerUtilities.reset(this.toPlayer());

        LobbyItems.LOBBY_ITEMS.forEach((slot, item) -> {
            this.toPlayer().getInventory().setItem(slot, item);
        });
    }

    public void queueInit() {
        this.state = ProfileState.LOBBY;

        PlayerUtilities.reset(this.toPlayer());

        LobbyItems.QUEUE_ITEMS.forEach((slot, item) -> {
            this.toPlayer().getInventory().setItem(slot, item);
        });
    }

    public Player toPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }
}
