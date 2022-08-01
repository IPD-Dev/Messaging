package com.github.ipddev.messaging.command;

import com.github.ipddev.messaging.Messaging;
import com.github.ipddev.messaging.utility.SyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Command implements ICommand {

	protected final CommandSyntaxException MUST_BE_IN_GAME = SyntaxException.create(
		"You must be in-game to run this command!");
	protected final CommandSyntaxException PLAYER_NOT_FOUND = SyntaxException.create("Player was not found!");
	protected final CommandSyntaxException MAY_NOT_CREATE_CONVERSATION_WITH_SELF = SyntaxException.create(
		"You may not create a conversation with yourself!");
	protected final Messaging plugin;
}
