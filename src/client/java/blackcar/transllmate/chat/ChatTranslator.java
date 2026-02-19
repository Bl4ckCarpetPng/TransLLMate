/*  TransLLMate - Translation using LLMs
    Copyright (C) 2026  black\ car

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package blackcar.transllmate.chat;
import blackcar.transllmate.config.TransLLMateConfig;
import blackcar.transllmate.llm.LocalLlmClient;

import net.minecraft.ChatFormatting;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class ChatTranslator {
	private ChatTranslator(){}

	public static void translate(GuiMessage message) {
		TransLLMateConfig config = TransLLMateConfig.get();
		if(!config.enabled)return; // mod enabled by default, if disabled then annoy less

		ChatMessageSelector.ParsedChatMessage parsed = ChatMessageSelector.parse(message.content());
		if (parsed == null || parsed.message().isBlank()){
		postSystemMessage("[TransLLMate] Parsing failed.", ChatFormatting.RED);return;}

		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player != null) {
			String localName = minecraft.player.getName().getString();
			if (parsed.playerName()==localName){
			postSystemMessage("[TransLLMate] You cannot translate yourself.", ChatFormatting.RED);return;}
		}

		LocalLlmClient.translate(parsed.message(), config)
			.thenAccept(translation -> minecraft.execute(() ->
				postTranslation(parsed.playerName(), translation)))
			.exceptionally(ex -> {
				minecraft.execute(() -> postSystemMessage("[TransLLMate] Translation failed: "+ex.getMessage(), ChatFormatting.RED));
				return null;
			});
	}

	private static void postTranslation(String playerName, String translation) {
		String message = "[TransLLMate] <"+playerName+"> "+translation.trim();
		postSystemMessage(message, ChatFormatting.GREEN);
	}

	private static void postSystemMessage(String message, ChatFormatting color) {
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.gui.getChat().addMessage(Component.literal(message).withStyle(color));
	}
}
