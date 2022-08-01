package com.github.ipddev.messaging.command.impl;

import com.github.ipddev.messaging.Messaging;
import com.github.ipddev.messaging.command.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ReplyCommand extends Command {

	public ReplyCommand(Messaging plugin) {
		super(plugin);
	}

	@Override
	public LiteralCommandNode<CommandSource> createNode() {
		return LiteralArgumentBuilder.<CommandSource>literal("reply")
			.then(RequiredArgumentBuilder.<CommandSource, String>argument("message", StringArgumentType.greedyString())
				.executes(this::run)).build();
	}

	@Override
	public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
		final CommandSource source = context.getSource();

		if (!(source instanceof Player)) {
			throw MUST_BE_IN_GAME;
		}

		final Player executingPlayer = (Player) source;

		final String message = StringArgumentType.getString(context, "message");

		final boolean result = plugin.reply(executingPlayer, message);

		if (!result) {
			source.sendMessage(Component.text("There is no conversation you can reply to!", NamedTextColor.RED));
			return 0;
		}

		return 1;
	}
}
