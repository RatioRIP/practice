package cc.ratio.practice.kit;

import cc.ratio.practice.util.InventoryUtil;
import me.lucko.helper.mongo.external.morphia.annotations.*;
import org.bukkit.inventory.ItemStack;

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
        this._display = InventoryUtil.serializeItemStack(this.display);
        this._contents = InventoryUtil.serializeInventory(this.contents);
        this._armor = InventoryUtil.serializeInventory(this.armor);
    }

    @PostLoad
    public void deserialize() {
        this.display = InventoryUtil.deserializeItemStack(this._display);
        this.contents = InventoryUtil.deserializeInventory(this._contents);
        this.armor = InventoryUtil.deserializeInventory(this._armor);
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
}
