package cc.ratio.practice.command.arena;

import cc.ratio.practice.command.arena.handler.ArenaSpawnpointHandler;
import cc.ratio.practice.command.arena.handler.DeleteArenaHandler;
import cc.ratio.practice.command.arena.handler.ListArenasHandler;
import cc.ratio.practice.command.arena.handler.NewArenaHandler;
import me.lucko.helper.Commands;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;

import javax.annotation.Nonnull;

public class ArenaCommandsModule implements TerminableModule {
    @Override
    public void setup(@Nonnull TerminableConsumer consumer) {
        Commands.create()
                .assertPlayer()
                .assertPermission("command.arena")
                .assertUsage("<name>")
                .description("Creates an arena")
                .handler(new NewArenaHandler())
                .registerAndBind(consumer, "newarena");

        Commands.create()
                .assertPlayer()
                .assertPermission("command.arena")
                .assertUsage("<name>")
                .description("Deletes an arena")
                .handler(new DeleteArenaHandler())
                .registerAndBind(consumer, "deletearena", "removearena");

        Commands.create()
                .assertPlayer()
                .assertPermission("command.arena")
                .description("Modifies an arena's spawnpoints (subcommaneds: [list, add, remove]")
                .assertUsage("<arena> <subcommannd>")
                .handler(new ArenaSpawnpointHandler())
                .registerAndBind(consumer, "arenaspawnpoint");

        Commands.create()
                .assertPlayer()
                .assertPermission("command.arena")
                .description("Lists arenas")
                .handler(new ListArenasHandler())
                .registerAndBind(consumer, "listarenas");
    }
}
