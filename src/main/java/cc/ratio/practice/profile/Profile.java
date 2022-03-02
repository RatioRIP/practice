package cc.ratio.practice.profile;

import cc.ratio.practice.profile.account.Account;
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

    public Profile(UUID uuid) {
        this.uuid = uuid;
        this.state = ProfileState.LOBBY;

        this.loadAccount();
    }

    public Account loadAccount() {
        Mongo mongo = Services.get(Mongo.class).get();
        Datastore datastore = mongo.getMorphiaDatastore();

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
        Mongo mongo = Services.get(Mongo.class).get();
        Datastore datastore = mongo.getMorphiaDatastore();

        datastore.save(this.account);
    }

    public Player toPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }
}
