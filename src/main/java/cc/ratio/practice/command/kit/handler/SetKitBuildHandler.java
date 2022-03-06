package cc.ratio.practice.command.kit.handler;

import cc.ratio.practice.kit.Kit;
import cc.ratio.practice.kit.KitRepository;
import me.lucko.helper.Services;
import me.lucko.helper.command.CommandInterruptException;
import me.lucko.helper.command.argument.Argument;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import org.bukkit.entity.Player;

import java.util.Optional;

public class SetKitBuildHandler implements FunctionalCommandHandler<Player> {

    public final KitRepository kitRepository = Services.get(KitRepository.class).get();

    @Override
    public void handle(CommandContext<Player> c) throws CommandInterruptException {
        String name = c.arg(0).parseOrFail(String.class);

        Optional<Kit> optional = kitRepository.find(name);

        if (!optional.isPresent()) {
            c.reply("&cKit doesn't exist");
            return;
        } else {
            Kit kit = optional.get();

            boolean value = !kit.build;

            Argument valueArgument = c.arg(1);

            if (valueArgument.isPresent()) {
                value = valueArgument.parseOrFail(Boolean.class);
            }

            kit.build = value;
            kitRepository.save(kit);

            c.reply("Option &ebuild &r" + (kit.build ? "&aenabled" : "&cdisabled"));
            c.reply("Kit '" + kit.name + "' modified");
        }
    }
}
