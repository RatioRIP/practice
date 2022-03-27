package cc.ratio.practice.event.lobby;

import cc.ratio.practice.event.lobby.handler.InteractEventHandler;
import cc.ratio.practice.profile.ProfileRepository;
import me.lucko.helper.Events;
import me.lucko.helper.Services;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Objects;

public class LobbyListener implements TerminableModule {

    private static final ProfileRepository repository = Services.get(ProfileRepository.class).get();

    @Override
    public void setup(@Nonnull TerminableConsumer consumer) {
//        Events.subscribe(BlockBreakEvent.class)
//                .filter(event -> this.repository.find(event.getPlayer().getUniqueId()).get().state.isLobby())
//                .handler(event -> event.setCancelled(true))
//                .bindWith(consumer);
//
//        Events.subscribe(BlockPlaceEvent.class)
//                .filter(event -> this.repository.find(event.getPlayer().getUniqueId()).get().state.isLobby())
//                .handler(event -> event.setCancelled(true))
//                .bindWith(consumer);

        Events.subscribe(FoodLevelChangeEvent.class)
                .filter(event -> repository.findOrNull(event.getEntity().getUniqueId()).state.isLobby())
                .handler(event -> event.setCancelled(true))
                .bindWith(consumer);

        Events.subscribe(PlayerItemDamageEvent.class)
                .filter(event -> repository.findOrNull(event.getPlayer().getUniqueId()).state.isLobby())
                .handler(event -> event.setCancelled(true))
                .bindWith(consumer);

        Events.subscribe(PlayerDropItemEvent.class)
                .filter(event -> repository.findOrNull(event.getPlayer().getUniqueId()).state.isLobby())
                .handler(event -> event.setCancelled(true))
                .bindWith(consumer);

        Events.subscribe(PlayerPickupItemEvent.class)
                .filter(event -> repository.findOrNull(event.getPlayer().getUniqueId()).state.isLobby())
                .handler(event -> event.setCancelled(true))
                .bindWith(consumer);

        Events.subscribe(InventoryClickEvent.class)
                .filter(event -> repository.findOrNull(event.getWhoClicked().getUniqueId()).state.isLobby())
                .filter(event ->
                        Objects.equals(event.getClickedInventory(), event.getWhoClicked().getOpenInventory())
                                ||
                                Objects.equals(event.getClickedInventory(), event.getWhoClicked().getInventory()))
                .handler(event -> event.setCancelled(true))
                .bindWith(consumer);

        Events.subscribe(EntityDamageEvent.class)
                .filter(event -> event.getCause() != EntityDamageEvent.DamageCause.VOID)
                .filter(event -> repository.findOrNull(event.getEntity().getUniqueId()).state.isLobby())
                .handler(event -> event.setCancelled(true))
                .bindWith(consumer);

        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(event -> event.getDamager().getType() == EntityType.PLAYER)
                .filter(event -> repository.findOrNull(event.getDamager().getUniqueId()).state.isLobby())
                .handler(event -> event.setCancelled(true))
                .bindWith(consumer);

        Events.subscribe(PlayerInteractEvent.class)
                .filter(event -> event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                .filter(event -> event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() != Material.AIR)
                .filter(event -> repository.findOrNull(event.getPlayer().getUniqueId()).state.isLobby())
                .handler(new InteractEventHandler())
                .bindWith(consumer);

        Events.subscribe(PlayerMoveEvent.class)
                .filter(event -> repository.findOrNull(event.getPlayer().getUniqueId()).state.isLobby())
                .filter(event -> event.getTo().getY() <= 0)
                .handler(event -> {
                    event.setTo(event.getPlayer().getWorld().getSpawnLocation().add(new Vector(0, 5, 0)));
                })
                .bindWith(consumer);
    }
}
