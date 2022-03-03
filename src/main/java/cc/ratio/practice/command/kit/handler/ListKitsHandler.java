package cc.ratio.practice.command.kit.handler;

import cc.ratio.practice.kit.KitRepository;
import me.lucko.helper.Services;
import me.lucko.helper.command.CommandInterruptException;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import org.bukkit.entity.Player;

public class ListKitsHandler implements FunctionalCommandHandler<Player> {

    public final KitRepository kitRepository = Services.get(KitRepository.class).get();

    @Override
    public void handle(CommandContext<Player> c) throws CommandInterruptException {
        c.reply("&cKits:");
        kitRepository.kits.forEach(kit -> {
            c.reply("  - &a" + kit.name);
        });
    }
}
