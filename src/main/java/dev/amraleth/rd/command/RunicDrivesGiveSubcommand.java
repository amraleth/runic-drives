package dev.amraleth.rd.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.amraleth.rd.runestone.Runestone;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class RunicDrivesGiveSubcommand {

    @Contract(value = " -> new", pure = true)
    public static @NotNull LiteralArgumentBuilder<CommandSourceStack> RUNIC_DRIVES_GIVE_SUBCOMMAND() {
        return Commands.literal("give")
                .then(Commands.argument("item", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            builder.suggest("runestone");

                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            String item = ctx.getArgument("item", String.class);

                            if (item.equals("runestone")) {
                                if (ctx.getSource().getSender() instanceof Player player) {
                                    player.getInventory().addItem(
                                            Runestone.createEmpty(1024).getRunestoneItemStack()
                                    );
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                );
    }
}
