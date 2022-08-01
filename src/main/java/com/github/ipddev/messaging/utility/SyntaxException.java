package com.github.ipddev.messaging.utility;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class SyntaxException {

	public static CommandSyntaxException create(String message) {
		return new SimpleCommandExceptionType(new Message(message)).create();
	}
}
