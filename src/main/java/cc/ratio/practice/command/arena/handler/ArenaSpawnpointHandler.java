package cc.ratio.practice.command.arena.handler;

import cc.ratio.practice.arena.Arena;
import cc.ratio.practice.arena.ArenaRepository;
import me.lucko.helper.Services;
import me.lucko.helper.command.CommandInterruptException;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ArenaSpawnpointHandler implements FunctionalCommandHandler<Player> {

    public final ArenaRepository arenaRepository = Services.get(ArenaRepository.class).get();

    @Override
    public void handle(final CommandContext<Player> c) throws CommandInterruptException {
        c.arg(0).assertPresent();
        c.arg(1).assertPresent();

        final String name = c.arg(0).parseOrFail(String.class);

        final Optional<Arena> optional = this.arenaRepository.find(name);

        if (!optional.isPresent()) {
            c.reply("&cArena doesn't exist");
            return;
        }

        final Arena arena = optional.get();

        final String subcommand = c.arg(1).parseOrFail(String.class);

        if (subcommand.equalsIgnoreCase("list")) {
            c.reply("&c" + arena.name + " spawnpoints:");
            arena.spawnpoints.forEach(location -> {
                c.reply(arena.spawnpoints.indexOf(location) + " - &c(" + location.getX() + ", " + location.getY() + ", " + location.getZ() + ")");
            });
            return;
        }

        if (subcommand.equalsIgnoreCase("add")) {
            arena.spawnpoints.add(c.sender().getLocation());
            this.arenaRepository.save();

            c.reply("Arena '" + arena.name + "' modified");
            return;
        }

        if (subcommand.equalsIgnoreCase("delete")) {
            c.arg(2).assertPresent();
            final int index = c.arg(2).parseOrFail(Integer.class);

            arena.spawnpoints.remove(index);
            this.arenaRepository.save();

            c.reply("Arena '" + arena.name + "' modified");
            return;
        }

    }
}
