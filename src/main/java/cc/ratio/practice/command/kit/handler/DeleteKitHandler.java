package cc.ratio.practice.command.kit.handler;

import cc.ratio.practice.kit.Kit;
import cc.ratio.practice.kit.KitRepository;
import me.lucko.helper.Services;
import me.lucko.helper.command.CommandInterruptException;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import org.bukkit.entity.Player;

import java.util.Optional;

public class DeleteKitHandler implements FunctionalCommandHandler<Player> {

    public final KitRepository kitRepository = Services.get(KitRepository.class).get();

    @Override
    public void handle(final CommandContext<Player> c) throws CommandInterruptException {
        final String name = c.arg(0).parseOrFail(String.class);

        final Optional<Kit> optional = this.kitRepository.find(name);

        if (!optional.isPresent()) {
            c.reply("&cKit doesn't exist");
            return;
        } else {
            final Kit kit = optional.get();
            this.kitRepository.remove(kit);
            c.reply("Deleted Kit '" + name + "'");
        }
    }
}
