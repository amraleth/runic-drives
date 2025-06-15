package dev.amraleth.rd.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.amraleth.rd.component.Runestone;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RunicDrivesDataSubcommand {

    public static final LiteralArgumentBuilder<CommandSourceStack> RUNIC_DRIVES_DATA_SUBCOMMAND() {
        return Commands.literal("data")
                .then(Commands.literal("view")
                        .executes(ctx -> {
                            if (ctx.getSource().getExecutor() instanceof Player player) {
                                ItemStack itemStack = player.getInventory().getItemInMainHand();
                                if (itemStack.getType() != Material.AIR) {
                                    if (Runestone.isRunestone(itemStack)) {
                                        Runestone runestone = Runestone.fromItemStack(itemStack);

                                        player.sendMessage("Size: " + runestone.getSize());
                                        player.sendMessage("Used Items: " + runestone.getUsedItems());
                                        player.sendMessage("Used Types: " + runestone.getUsedTypes());
                                        player.sendMessage("Items: ");
                                        runestone.getRunestoneItems().forEach((stack, count) -> {
                                            player.sendMessage(PlainTextComponentSerializer.plainText().serialize(stack.displayName()) + " " + count);
                                        });
                                        player.sendMessage("UUID: " + runestone.getUuid());
                                    }
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("increment")
                        .then(Commands.argument("count", IntegerArgumentType.integer(1, 1025))
                                .executes(ctx -> {
                                    if (ctx.getSource().getExecutor() instanceof Player player) {
                                        ItemStack itemStack = player.getInventory().getItemInMainHand();
                                        if (itemStack.getType() != Material.AIR) {
                                            if (Runestone.isRunestone(itemStack)) {
                                                Runestone runestone = Runestone.fromItemStack(itemStack);

                                                int count = ctx.getArgument("count", Integer.class);
                                                int res;
                                                if (count == 1) {
                                                    res = runestone.addItem(new ItemStack(Material.DIAMOND_SWORD));
                                                } else {
                                                    res = runestone.addMultipleItems(new ItemStack(Material.DIAMOND_SWORD), count);
                                                }
                                                if (res != 0) {
                                                    player.sendMessage("Could not insert " + res + " items because the Runestone is full!");
                                                }
                                            }
                                        }
                                    }
                                    return Command.SINGLE_SUCCESS;
                                }))
                );
    }
}
