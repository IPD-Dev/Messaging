package com.github.ipddev.messaging;

import com.github.ipddev.messaging.command.impl.MessageCommand;
import com.github.ipddev.messaging.command.impl.ReplyCommand;
import com.github.ipddev.messaging.conversation.Conversation;
import com.google.common.eventbus.AsyncEventBus;
import com.google.inject.Inject;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import lombok.Getter;

@Plugin(
	id = "messaging",
	name = "Messaging",
	version = BuildConstants.VERSION,
	description = "Allow players on your Velocity proxy to message each-other",
	authors = {"Allink"}
)
@SuppressWarnings("UnstableApiUsage")
public class Messaging {

	private final List<Conversation> conversations = new ArrayList<>();
	private final AsyncEventBus conversationBus = new AsyncEventBus(Executors.newSingleThreadExecutor());
	@Inject
	@Getter
	private ProxyServer proxy;


	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent event) {
		final CommandManager commandManager = proxy.getCommandManager();

		final MessageCommand messageCommand = new MessageCommand(this);
		final ReplyCommand replyCommand = new ReplyCommand(this);

		commandManager.register(new BrigadierCommand(messageCommand.createNode()));
		commandManager.register(new BrigadierCommand(replyCommand.createNode()));
	}

	public void endConversation(Conversation conversation) {
		this.conversations.remove(conversation);
		conversationBus.unregister(conversation);
	}

	public void startConversation(Player participant1, Player participant2, @Nullable String startingMessage) {
		final Conversation conversation = new Conversation(participant1, participant2, this);

		conversationBus.register(conversation);

		if (startingMessage == null) {
			return;
		}

		conversation.sendMessage(participant1, startingMessage);
		this.conversations.add(conversation);
	}

	public void sendMessage(Player from, Player to, String message) {
		final Optional<Conversation> existingConversationOptional = conversations.stream()
			.filter(c -> (c.getParticipant1().equals(to) && c.getParticipant2().equals(from)) || (
				c.getParticipant1().equals(from) && c.getParticipant2().equals(to)))
			.findFirst();

		if (existingConversationOptional.isEmpty()) {
			startConversation(from, to, message);
			return;
		}

		final Conversation existingConversation = existingConversationOptional.get();

		existingConversation.sendMessage(from, message);
	}

	public boolean reply(Player player, String withMessage) {
		final Optional<Conversation> toReplyWithOptional = conversations.stream()
			.filter(c -> c.getParticipant1().equals(player) || c.getParticipant2().equals(player))
			.max(Comparator.comparingLong(Conversation::getLastMessage));

		if (toReplyWithOptional.isEmpty()) {
			return false;
		}

		final Conversation toReplyWith = toReplyWithOptional.get();
		toReplyWith.sendMessage(player, withMessage);

		return true;
	}

	@Subscribe
	public void onDisconnectEvent(DisconnectEvent event) {
		conversationBus.post(event);
	}
}
