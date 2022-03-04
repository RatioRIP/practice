package cc.ratio.practice.command.kit.handler;

import cc.ratio.practice.kit.Kit;
import cc.ratio.practice.kit.KitRepository;
import me.lucko.helper.Services;
import me.lucko.helper.command.CommandInterruptException;
import me.lucko.helper.command.argument.Argument;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ViewKitInventoryHandler implements FunctionalCommandHandler<Player> {

    public final KitRepository kitRepository = Services.get(KitRepository.class).get();

    @Override
    public void handle(CommandContext<Player> c) throws CommandInterruptException {
        String name = c.arg(0).parseOrFail(String.class);

        Optional<Kit> optional = kitRepository.find(name);

        if(!optional.isPresent()) {
            c.reply("&cKit doesn't exist");
            return;
        } else {
            Kit kit = optional.get();

            new Gui(c.sender(), 6, "Viewing Kit " + kit.name) {
                @Override
                public void redraw() {
                    {
                        int i = 0;
                        for (ItemStack item : kit.contents) {
                            if(item != null) {
                                this.setItem(i, Item.builder(item).build());
                                System.out.println(item);
                            }
                            i++;
                        }
                    }

                    {
                        int i = 6 * 9 - 4;
                        for (ItemStack item : kit.armor) {
                            if(item != null) {
                                this.setItem(i, Item.builder(item).build());
                                System.out.println(item);
                            }
                            i++;
                        }
                    }
                }
            }.open();
        }
    }

}
