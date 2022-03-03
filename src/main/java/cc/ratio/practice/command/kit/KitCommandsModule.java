package cc.ratio.practice.command.kit;

import cc.ratio.practice.command.kit.handler.DeleteKitCommandHandler;
import cc.ratio.practice.command.kit.handler.NewKitCommandHandler;
import cc.ratio.practice.command.kit.handler.SetKitDisplayHandler;
import cc.ratio.practice.command.kit.handler.SetKitInventoryHandler;
import me.lucko.helper.Commands;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;

import javax.annotation.Nonnull;

public class KitCommandsModule implements TerminableModule {
    @Override
    public void setup(@Nonnull TerminableConsumer consumer) {
        Commands.create()
                .assertPlayer()
                .assertUsage("<name>")
                .description("Creates a kit")
                .handler(new NewKitCommandHandler())
                .registerAndBind(consumer, "newkit");

        Commands.create()
                .assertPlayer()
                .assertUsage("<name>")
                .description("Deletes a kit")
                .handler(new DeleteKitCommandHandler())
                .registerAndBind(consumer, "deletekit", "removekit");

        Commands.create()
                .assertPlayer()
                .assertUsage("<name>")
                .description("Sets the inventory of a kit")
                .handler(new SetKitInventoryHandler())
                .registerAndBind(consumer, "setkitinventory");

        Commands.create()
                .assertPlayer()
                .assertUsage("<name>")
                .description("Sets the display item of a kit")
                .handler(new SetKitDisplayHandler())
                .registerAndBind(consumer, "setkitdisplay");

    }
}
