package com.github.ipddev.messaging.conversation;

import com.github.ipddev.messaging.Messaging;
import com.google.common.eventbus.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@SuppressWarnings("UnstableApiUsage")
@Data
public class Conversation {

	private static final LegacyComponentSerializer legacyAmpersandSerializer = LegacyComponentSerializer.legacyAmpersand();
	private final Player participant1;
	private final Player participant2;
	private final Messaging plugin;
	private long lastMessage = System.currentTimeMillis();

	public Conversation(Player participant1, Player participant2, Messaging plugin) {
		if (participant1.equals(participant2)) {
			throw new RuntimeException(
				"Attempted to create conversation with participant #1 and participant #2 being the same!");
		}

		this.participant1 = participant1;
		this.participant2 = participant2;
		this.plugin = plugin;
	}

	public void sendMessage(Player from, String message) {
		if (!from.equals(participant1) && !from.equals(participant2)) {
			return;
		}

		lastMessage = System.currentTimeMillis();
		final Player to = participant1.equals(from) ? participant2 : participant1;
		final Component messageComponent = legacyAmpersandSerializer.deserialize(message);
		final Component fromMessage = Component.empty()
			.append(Component.text(from.getUsername(), NamedTextColor.RED))
			.append(Component.text(" -> ", NamedTextColor.DARK_RED))
			.append(Component.text(to.getUsername(), NamedTextColor.RED))
			.append(Component.text(": ", NamedTextColor.WHITE))
			.append(messageComponent);
		final Component toMessage = Component.empty()
			.append(Component.text(to.getUsername(), NamedTextColor.RED))
			.append(Component.text(" <- ", NamedTextColor.DARK_RED))
			.append(Component.text(from.getUsername(), NamedTextColor.RED))
			.append(Component.text(": ", NamedTextColor.WHITE))
			.append(messageComponent);

		from.sendMessage(fromMessage);
		to.sendMessage(toMessage);
	}

	@Subscribe
	public void onDisconnectEvent(DisconnectEvent event) {
		final Player disconnectedPlayer = event.getPlayer();

		if (!disconnectedPlayer.equals(participant1) && !disconnectedPlayer.equals(participant2)) {
			return;
		}

		final Player remaining = disconnectedPlayer.equals(participant1) ? participant2 : participant1;

		remaining.sendMessage(
			Component.text(disconnectedPlayer.getUsername() + " disconnected and as such your conversation has ended.",
				NamedTextColor.RED));

		plugin.endConversation(this);
	}
}
