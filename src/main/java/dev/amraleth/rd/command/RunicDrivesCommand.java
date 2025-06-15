package dev.amraleth.rd.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class RunicDrivesCommand {

    public static final LiteralArgumentBuilder<CommandSourceStack> ROOT = Commands.literal("runicdrives")
            .requires(stack -> stack.getSender().hasPermission("runestone"))
            .then(RunicDrivesGiveSubcommand.RUNIC_DRIVES_GIVE_SUBCOMMAND())
            .then(RunicDrivesDataSubcommand.RUNIC_DRIVES_DATA_SUBCOMMAND())
            .then(RunicDrivesDataSubcommand.RUNIC_DRIVES_DATA_SUBCOMMAND());
}
