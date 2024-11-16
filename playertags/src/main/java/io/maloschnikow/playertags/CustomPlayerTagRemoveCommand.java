package io.maloschnikow.playertags;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.papermc.paper.command.brigadier.CommandSourceStack;





public class CustomPlayerTagRemoveCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        //TODO implement
        //TODO remove custom player tag

        return Command.SINGLE_SUCCESS;
    }
}
