package cc.ratio.practice.match;

import java.util.Optional;
import java.util.UUID;

public class Elimination {

    public final UUID uuid;
    public final Optional<UUID> killer;

    public Elimination(UUID uuid, Optional<UUID>killer) {
        this.uuid = uuid;
        this.killer = killer;
    }

}
