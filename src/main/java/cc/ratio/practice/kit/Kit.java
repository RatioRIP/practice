package cc.ratio.practice.kit;

import org.bukkit.inventory.ItemStack;

public class Kit {

    public String name;
    public boolean build, ranked;
    public ItemStack display;
    public ItemStack[] contents, armor;

    public Kit(String name) {
        this.name = name;
    }
}
