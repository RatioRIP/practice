package cc.ratio.practice.queue.gui;

import cc.ratio.practice.kit.Kit;
import cc.ratio.practice.lobby.LobbyItems;
import cc.ratio.practice.profile.Profile;
import cc.ratio.practice.profile.ProfileRepository;
import cc.ratio.practice.profile.ProfileState;
import cc.ratio.practice.queue.QueueRepository;
import cc.ratio.practice.util.PlayerUtilities;
import me.lucko.helper.Services;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.text3.Text;
import org.bukkit.entity.Player;

public class QueueGui extends Gui {

    private final boolean ranked;

    private final QueueRepository queueRepository = Services.get(QueueRepository.class).get();
    private final ProfileRepository profileRepository = Services.get(ProfileRepository.class).get();

    public QueueGui(final Player player, final boolean ranked) {
        super(player, 3, (ranked ? "Ranked" : "Unranked") + " Queue");

        this.ranked = ranked;
    }

    @Override
    public void redraw() {
        if(!this.isFirstDraw()) return;

        Player player = this.getPlayer();

        this.queueRepository.queues
                .stream()
                .filter(queue -> queue.ranked == this.ranked)
                .map(queue -> {
                    final Kit kit = queue.kit;
                    final String rankity = queue.ranked ? "&cRanked" : "&bUnranked";

                    return ItemStackBuilder.of(kit.display.clone())
                        .name("&b" + kit.name)
                        .lore("&7Click to join the queue for " + rankity + " " + kit.name)
                        .build(() -> {
                            Profile profile = this.profileRepository.find(player.getUniqueId()).get();

                            profile.queue = queue;
                            profile.queueInit();

                            player.sendMessage(Text.colorize("&7You've joined the " + rankity + " " + kit.name + " &7queue."));

                            this.close();
                        });
                })
                .forEach(this::addItem);
    }

}
