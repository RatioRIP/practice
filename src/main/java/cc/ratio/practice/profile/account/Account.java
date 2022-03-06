package cc.ratio.practice.profile.account;

import me.lucko.helper.mongo.external.morphia.annotations.Entity;
import me.lucko.helper.mongo.external.morphia.annotations.Id;

import java.util.UUID;

@Entity(value = "accounts", noClassnameStored = true)
public class Account {

    @Id
    public UUID uuid;

    public String name;

    public Account() {
    }

    public Account(final UUID uuid) {
        this.uuid = uuid;
    }

}
