package cc.ratio.practice.arena;

import me.lucko.helper.serialize.Region;
import org.bukkit.Location;

import java.util.List;

public class Arena {

    public String name;
    public Region region;
    public List<Location> spawnpoints;

    public Arena(final String name) {
        this.name = name;
    }

}
