package cc.ratio.practice.event.lobby.handler;

import cc.ratio.practice.lobby.LobbyItems;
import cc.ratio.practice.queue.gui.QueueGui;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class InteractEventHandler implements Consumer<PlayerInteractEvent> {
    @Override
    public void accept(PlayerInteractEvent playerInteractEvent) {
        Player player = playerInteractEvent.getPlayer();
        ItemStack item = playerInteractEvent.getPlayer().getItemInHand();

        if(item.isSimilar(LobbyItems.UNRANKED_QUEUE_ITEM)) {
            new QueueGui(player, false).open();
            return;
        }

        if(item.isSimilar(LobbyItems.RANKED_QUEUE_ITEM)) {
            new QueueGui(player, true).open();
            return;
        }
    }
}
