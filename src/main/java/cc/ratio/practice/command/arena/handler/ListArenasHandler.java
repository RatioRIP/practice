package cc.ratio.practice.command.arena.handler;

import cc.ratio.practice.arena.ArenaRepository;
import me.lucko.helper.Services;
import me.lucko.helper.command.CommandInterruptException;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import org.bukkit.entity.Player;

public class ListArenasHandler implements FunctionalCommandHandler<Player> {

    public final ArenaRepository arenaRepository = Services.get(ArenaRepository.class).get();

    @Override
    public void handle(CommandContext<Player> c) throws CommandInterruptException {
        c.reply("&cArenas:");
        arenaRepository.arenas.forEach(arena -> {
            c.reply("  - &a" + arena.name);
        });
    }
}
