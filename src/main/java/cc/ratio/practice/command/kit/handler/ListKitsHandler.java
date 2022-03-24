package cc.ratio.practice.command.kit.handler;

import cc.ratio.practice.kit.KitRepository;
import me.lucko.helper.Services;
import me.lucko.helper.command.CommandInterruptException;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import me.lucko.helper.text.Text;
import me.lucko.helper.text.TextComponent;
import me.lucko.helper.text.event.ClickEvent;
import me.lucko.helper.text.event.HoverEvent;
import me.lucko.helper.text.format.TextColor;
import org.bukkit.entity.Player;

public class ListKitsHandler implements FunctionalCommandHandler<Player> {

    public final KitRepository kitRepository = Services.get(KitRepository.class).get();

    @Override
    public void handle(CommandContext<Player> c) throws CommandInterruptException {
        c.reply("&cKits:");
        this.kitRepository.kits.forEach(kit -> {
            final TextComponent clickComponent = TextComponent.builder()
                    .content("(Click to view)")
                    .color(TextColor.GRAY)
                    .hoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.of("Click to view the kit's contents").color(TextColor.RED)))
                    .clickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewkitinventory " + kit.name))
                    .build();

            final TextComponent component = TextComponent.builder()
                    .content("  - ")
                    .append(TextComponent.of(kit.name + " ").color(TextColor.GREEN))
                    .append(clickComponent)
                    .build();

            // TOOD: Migrate to text3
            Text.sendMessage(c.sender(), component);
        });
    }
}
