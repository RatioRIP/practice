package cc.ratio.practice.kit;

import cc.ratio.practice.match.Match;
import cc.ratio.practice.match.MatchRepository;
import me.lucko.helper.Services;
import me.lucko.helper.mongo.external.morphia.annotations.*;
import me.lucko.helper.serialize.InventorySerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Entity(value = "kits")
public class Kit {

    private static final MatchRepository matchRepository = Services.get(MatchRepository.class).get();

    @Id
    public String name;

    public boolean build, ranked;

    public transient ItemStack display;
    public transient ItemStack[] contents;
    public transient ItemStack[] armor;

    @Property("display")
    public String _display;

    @Property("contents")
    public String _contents;

    @Property("armor")
    public String _armor;

    public Kit() {
        // TODO: Alex, another empty constructor (Explain)
    }

    /**
     * Constructor to create a new {@link Kit}
     *
     * @param name the name.
     */

    public Kit(String name) {
        this.name = name;
    }

    /**
     * Serialize the kit data.
     */

    @PrePersist
    public void serialize() {
        this._display = InventorySerialization.encodeItemStackToString(this.display);
        this._contents = InventorySerialization.encodeItemStacksToString(this.contents);
        this._armor = InventorySerialization.encodeItemStacksToString(this.armor);
    }

    /**
     * Deserialize the kit data.
     */

    @PostLoad
    public void deserialize() {
        this.display = InventorySerialization.decodeItemStack(this._display);
        this.contents = InventorySerialization.decodeItemStacks(this._contents);
        this.armor = InventorySerialization.decodeItemStacks(this._armor);
    }

    @Override
    public String toString() {
        return "Kit{" +
                "name='" + this.name + '\'' +
                ", build=" + this.build +
                ", ranked=" + this.ranked +
                ", display=" + this.display +
                ", contents=" + Arrays.toString(this.contents) +
                ", armor=" + Arrays.toString(this.armor) +
                '}';
    }

    /**
     * Apply the {@link Kit} contents to the {@link Player}
     *
     * @param player the player.
     */

    public void apply(Player player) {
        player.getInventory().setArmorContents(this.armor);
        player.getInventory().setContents(this.contents);
        player.updateInventory();
    }

    /**
     * Get a stream of the matches that are being played with this kit
     * @return
     */
    public Collection<Match> getMatches() {
        return matchRepository.matches.stream()
                .filter(match -> match.kit == this)
                .collect(Collectors.toList());
    }

}
