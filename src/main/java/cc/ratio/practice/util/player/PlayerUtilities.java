package cc.ratio.practice.util.player;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerUtilities {

    /**
     * Reset the {@link Player}
     *
     * @param player the player.
     */

    public static void reset(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setFireTicks(1);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.setExp(0);
        player.setLevel(0);
        player.getActivePotionEffects()
                .stream()
                .map(PotionEffect::getType)
                .forEach(player::removePotionEffect);
    }
}
