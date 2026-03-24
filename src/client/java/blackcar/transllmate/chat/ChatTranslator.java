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
import blackcar.transllmate.LocalTranslator;

import net.minecraft.ChatFormatting;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import java.util.Random;

public final class ChatTranslator {
	public static final String modPf="[TransLLMate] "; // chat prefix
	private static final String[] urlPrefix = {// local prefixes
		"127.0.0.1", // localhost
		"192.168.", // local
		"172.", // also local
		"0.", // localhost
		"localhost"
	};
	private static final String[] j={// in case of broken ip/domain
		"Would you like to ChatGPT yourself?",
		"Your API is too spicy to be used.",
		"This API scares me!",
		"i cant internet\ni cant internet",
		"This mod doesn't have to be bound to online API",
		"Hello, API stranger! You've picked a wrong server. Please use a local one"
	};
	private static int l=0; // counter
	private ChatTranslator(){}

	public static void translate(GuiMessage message) {
		Random random = new Random();
		TransLLMateConfig config = TransLLMateConfig.get();
		if(!config.enabled)return; // mod is enabled by default, if disabled then annoy less

		if(config.api.endsWith(".local")) l++; // the only way of tricking it is custom DNS
		for (String s:urlPrefix) {if(config.api.startsWith(s)) l++;} // 0 = bad, more = good

		if(l==0) {// very good protection trust me 
			localSend(modPf+j[random.nextInt(j.length)], ChatFormatting.GRAY);
			return;
		}

		ChatMessageSelector.ParsedChatMessage parsed = ChatMessageSelector.parse(message.content());
		if (parsed == null || parsed.message().isBlank()){
		localSend(modPf+"Parsing failed - nothing useful was found in message.", ChatFormatting.RED);return;}

		Minecraft mc = Minecraft.getInstance();
		LocalTranslator.process(parsed.message(), config) // only after determining that we're not wasting water we can proceed
			.thenAccept(translation -> mc.execute(() ->
				localSend(modPf+"<"+parsed.playerName()+"> "+translation.trim(), ChatFormatting.GREEN)))
			.exceptionally(ex -> {
				mc.execute(() -> localSend(modPf+"Translation failed: "+ex.getMessage(), ChatFormatting.RED));
				return null;
			});
	}

	private static void localSend(String message, ChatFormatting color) {
		Minecraft mc = Minecraft.getInstance();
		mc.gui.getChat().addMessage(Component.literal(message).withStyle(color));
	}
}
