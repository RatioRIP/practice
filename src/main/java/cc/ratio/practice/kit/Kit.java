package cc.ratio.practice.kit;

import me.lucko.helper.mongo.external.morphia.annotations.*;
import me.lucko.helper.serialize.InventorySerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.swing.*;
import java.util.Arrays;

@Entity(value = "kits", noClassnameStored = true)
public class Kit {

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
    }

    public Kit(final String name) {
        this.name = name;
    }

    @PrePersist
    public void serialize() {
        this._display = InventorySerialization.encodeItemStackToString(this.display);
        this._contents = InventorySerialization.encodeItemStacksToString(this.contents);
        this._armor = InventorySerialization.encodeItemStacksToString(this.armor);
    }

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

    public void apply(Player player) {
        player.getInventory().setArmorContents(this.armor);
        player.getInventory().setContents(this.contents);
        player.updateInventory();
    }
}
