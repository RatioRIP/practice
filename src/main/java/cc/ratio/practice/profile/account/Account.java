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
        // TODO: Alex evaluate this. I'm confused why there is an empty constructor (Kyle @ 22:46 (24/03/2022)
    }

    public Account(final UUID uuid) {
        this.uuid = uuid;
    }
}
