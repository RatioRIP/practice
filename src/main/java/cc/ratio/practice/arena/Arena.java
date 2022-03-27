package cc.ratio.practice.arena;

import me.lucko.helper.serialize.Region;
import org.bukkit.Location;

import java.util.List;

public class Arena {

    public String name;
    public Region region; // TODO: Alex review this (I Don't understand why this variable is never being used)
    // TODO: This probably has future use but i dont even know lol
    public List<Location> spawnpoints;

    /**
     * Constructor to create a new {@link Arena}
     *
     * @param name the name.
     */

    public Arena(String name) {
        this.name = name;
    }
}
