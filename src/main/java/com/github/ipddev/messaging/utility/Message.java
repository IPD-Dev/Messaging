package com.github.ipddev.messaging.utility;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Message implements com.mojang.brigadier.Message {

	private final String message;

	@Override
	public String getString() {
		return message;
	}
}
