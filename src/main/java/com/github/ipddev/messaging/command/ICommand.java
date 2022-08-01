package com.github.ipddev.messaging.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;

public interface ICommand {

	LiteralCommandNode<CommandSource> createNode();

	int run(CommandContext<CommandSource> context) throws CommandSyntaxException;
}
