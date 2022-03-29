package cc.ratio.practice.command.kit.handler;

import cc.ratio.practice.kit.Kit;
import cc.ratio.practice.kit.KitRepository;
import me.lucko.helper.Services;
import me.lucko.helper.command.CommandInterruptException;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.Optional;

public class SetKitInventoryHandler implements FunctionalCommandHandler<Player> {

    public final KitRepository kitRepository = Services.get(KitRepository.class).get();

    @Override
    public void handle(CommandContext<Player> c) throws CommandInterruptException {
        String name = c.arg(0).parseOrFail(String.class);

        Optional<Kit> optional = this.kitRepository.find(name);

        if (optional.isEmpty()) {
            c.reply("&cKit doesn't exist");
        } else {
            Kit kit = optional.get();

            PlayerInventory inventory = c.sender().getInventory();

            kit.contents = inventory.getContents();
            kit.armor = inventory.getArmorContents();
            this.kitRepository.save(kit);

            c.reply("Kit '" + kit.name + "' modified");
        }
    }
}
