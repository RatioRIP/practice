package cc.ratio.practice.command.arena.handler;

import cc.ratio.practice.arena.Arena;
import cc.ratio.practice.arena.ArenaRepository;
import me.lucko.helper.Services;
import me.lucko.helper.command.CommandInterruptException;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import org.bukkit.entity.Player;

import java.util.Optional;

public class DeleteArenaHandler implements FunctionalCommandHandler<Player> {

    public final ArenaRepository arenaRepository = Services.get(ArenaRepository.class).get();

    @Override
    public void handle(CommandContext<Player> c) throws CommandInterruptException {
        final String name = c.arg(0).parseOrFail(String.class);

        final Optional<Arena> optional = this.arenaRepository.find(name);

        if (!optional.isPresent()) {
            c.reply("&cArena doesn't exist");
            return;
        } else {
            final Arena arena = optional.get();
            this.arenaRepository.remove(arena);
            c.reply("Deleted Arena '" + name + "'");
        }
    }
}
