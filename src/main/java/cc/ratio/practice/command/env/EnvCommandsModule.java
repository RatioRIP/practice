package cc.ratio.practice.command.env;

import cc.ratio.practice.profile.Profile;
import cc.ratio.practice.profile.ProfileRepository;
import me.lucko.helper.Commands;
import me.lucko.helper.Events;
import me.lucko.helper.Services;
import me.lucko.helper.event.filter.EventHandlers;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import javax.annotation.Nonnull;
import java.util.UUID;

public class EnvCommandsModule implements TerminableModule {

    private final ProfileRepository profileRepository = Services.get(ProfileRepository.class).get();

    @Override
    public void setup(@Nonnull TerminableConsumer consumer) {
        Commands.create()
                .assertOp()
                .assertPlayer()
                .handler(c -> {
                    final boolean buildMode = !this.isBuildMode(c.sender().getUniqueId());
                    this.setBuildMode(c.sender().getUniqueId(), buildMode);

                    c.reply(buildMode ? "&aBuild Mode Enabled" : "&cBuild Mode Disabled");
                })
                .registerAndBind(consumer, "build", "bm", "buildmode", "edit");

        Events.subscribe(BlockBreakEvent.class)
                .filter(event -> !this.isBuildMode(event.getPlayer().getUniqueId()))
                .handler(EventHandlers.cancel())
                .bindWith(consumer);

        Events.subscribe(BlockPlaceEvent.class)
                .filter(event -> !this.isBuildMode(event.getPlayer().getUniqueId()))
                .handler(EventHandlers.cancel())
                .bindWith(consumer);
    }

    public void setBuildMode(UUID uuid, boolean value) {
        final Profile profile = this.profileRepository.find(uuid).get();

        profile.build = value;
    }

    public boolean isBuildMode(UUID uuid) {
        final Profile profile = this.profileRepository.find(uuid).get();

        return profile.build;
    }
}
