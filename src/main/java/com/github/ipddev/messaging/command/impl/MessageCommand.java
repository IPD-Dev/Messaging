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
import java.util.Optional;

public class MessageCommand extends Command {

	public MessageCommand(Messaging plugin) {
		super(plugin);
	}

	@Override
	public LiteralCommandNode<CommandSource> createNode() {
		return LiteralArgumentBuilder.<CommandSource>literal("msg")
			.then(RequiredArgumentBuilder.<CommandSource, String>argument("player", StringArgumentType.string())
				.then(RequiredArgumentBuilder.<CommandSource, String>argument("message",
						StringArgumentType.greedyString())
					.executes(this::run)))
			.build();
	}

	@Override
	public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
		final CommandSource source = context.getSource();

		if (!(source instanceof Player)) {
			throw MUST_BE_IN_GAME;
		}

		final Player executingPlayer = (Player) source;
		final String username = StringArgumentType.getString(context, "player");
		final Optional<Player> playerOptional = plugin.getProxy().getAllPlayers().stream()
			.filter(p -> p.getUsername().equalsIgnoreCase(username) || p.getUsername().toLowerCase()
				.startsWith(username.toLowerCase()))
			.findFirst();
		final String message = StringArgumentType.getString(context, "message");

		if (playerOptional.isEmpty()) {
			throw PLAYER_NOT_FOUND;
		}

		final Player otherPlayer = playerOptional.get();

		if (otherPlayer.equals(executingPlayer)) {
			throw MAY_NOT_CREATE_CONVERSATION_WITH_SELF;
		}

		plugin.sendMessage(executingPlayer, otherPlayer, message);

		return 1;
	}
}
