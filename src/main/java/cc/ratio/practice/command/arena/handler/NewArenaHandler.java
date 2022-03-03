package cc.ratio.practice.command.arena.handler;

import cc.ratio.practice.arena.Arena;
import cc.ratio.practice.arena.ArenaRepository;
import me.lucko.helper.Services;
import me.lucko.helper.command.CommandInterruptException;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class NewArenaHandler implements FunctionalCommandHandler<Player> {

    public final ArenaRepository arenaRepository = Services.get(ArenaRepository.class).get();

    @Override
    public void handle(CommandContext<Player> c) throws CommandInterruptException {
        String name = c.arg(0).parseOrFail(String.class);

        if(arenaRepository.find(name).isPresent()) {
            c.reply("&cArena already exists");
            return;
        } else {
            Arena arena = new Arena(name);

            arena.spawnpoints = new ArrayList<>();

            arenaRepository.put(arena);
            c.reply("Created Arena '" + name + "'");
        }
    }
}
