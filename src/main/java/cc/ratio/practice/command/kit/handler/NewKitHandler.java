package cc.ratio.practice.command.kit.handler;

import cc.ratio.practice.kit.Kit;
import cc.ratio.practice.kit.KitRepository;
import me.lucko.helper.Services;
import me.lucko.helper.command.CommandInterruptException;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NewKitHandler implements FunctionalCommandHandler<Player> {

    public final KitRepository kitRepository = Services.get(KitRepository.class).get();

    @Override
    public void handle(CommandContext<Player> c) throws CommandInterruptException {
        String name = c.arg(0).parseOrFail(String.class);

        if (this.kitRepository.find(name).isPresent()) {
            c.reply("&cKit already exists");
            return;
        } else {
            Kit kit = new Kit(name);

            kit.contents = new ItemStack[]{};
            kit.armor = new ItemStack[]{};
            kit.display = ItemStackBuilder.of(Material.DEAD_BUSH).build();
            kit.build = false;

            this.kitRepository.put(kit);
            c.reply("Created Kit '" + name + "'");
        }
    }
}
