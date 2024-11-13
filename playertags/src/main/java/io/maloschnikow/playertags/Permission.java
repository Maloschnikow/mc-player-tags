package io.maloschnikow.playertags;

import java.util.function.Predicate;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public class Permission implements Predicate<CommandSourceStack> {

    private final String permission;

    public Permission(String permission) {
        this.permission = permission;
    }

    @Override
    public boolean test(CommandSourceStack t) {
        return t.getSender().hasPermission(this.permission);
    }
    
}
