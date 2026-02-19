package blackcar.transllmate.chat;

import blackcar.transllmate.mixin.client.ChatComponentAccessor;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Optional;

public final class ChatMessageSelector {
	private static final Pattern BRACKET = Pattern.compile("^<([^>]+)>\\s*(.+)$"); //regex
	private static final Pattern COLON = Pattern.compile("^([^:]+):\\s*(.+)$"); //regex
	private ChatMessageSelector(){}
	// Parsing
	public static ParsedChatMessage parse(Component component) {
		String raw=component.getString();
		if(raw==null||raw.isBlank())return null;

		Matcher bracket=BRACKET.matcher(raw);
		if(bracket.matches())return new ParsedChatMessage(bracket.group(1).trim(), bracket.group(2).trim(), raw);

		Matcher colon = COLON.matcher(raw);
		if(colon.matches())return new ParsedChatMessage(colon.group(1).trim(), colon.group(2).trim(), raw);
		return new ParsedChatMessage("Unknown", raw.trim(), raw);
	}

	public record ParsedChatMessage(String playerName, String message, String raw){}

	// Fetching message
	public static Optional<GuiMessage> getMessageAt(ChatComponent chat, double mouseX, double mouseY) {
		ChatComponentAccessor accessor = (ChatComponentAccessor) chat;
		List<GuiMessage.Line> trimmedMessages = accessor.transllmate$getTrimmedMessages();
		List<GuiMessage> allMessages = accessor.transllmate$getAllMessages();

		int lineIndex=getMessageLineIndexAt(chat, accessor, trimmedMessages, mouseX, mouseY);
		if(lineIndex<0||lineIndex>=trimmedMessages.size())return Optional.empty();

		int endIndex=getMessageEndIndex(trimmedMessages, lineIndex);
		if(endIndex<0)return Optional.empty();

		int messageIndex = getMessageIndexFromEndLine(trimmedMessages, endIndex);
		if(messageIndex<0||messageIndex>=allMessages.size())return Optional.empty();

		return Optional.of(allMessages.get(messageIndex));
	}

	private static int getMessageLineIndexAt(
		ChatComponent chat,
		ChatComponentAccessor accessor,
		List<GuiMessage.Line> trimmedMessages,
		double mouseX, double mouseY
	) {
		if (!chat.isChatFocused() || accessor.transllmate$invokeIsChatHidden())return -1;

		double scale = accessor.transllmate$invokeGetScale();
		double chatX = mouseX / scale - 4.0;
		double maxX = Mth.floor(accessor.transllmate$invokeGetWidth() / scale);
		if (chatX < -4.0 || chatX > maxX)return -1;

		Minecraft minecraft = Minecraft.getInstance();
		double chatY = (minecraft.getWindow().getGuiScaledHeight() - mouseY - 40.0)
			/ (scale * accessor.transllmate$invokeGetLineHeight());

		int visibleLines = Math.min(accessor.transllmate$invokeGetLinesPerPage(), trimmedMessages.size());
		if (chatY < 0.0 || chatY >= visibleLines) return -1;

		int lineIndex = Mth.floor(chatY + accessor.transllmate$getChatScrollbarPos());
		if (lineIndex < 0 || lineIndex >= trimmedMessages.size())return -1;

		return lineIndex;
	}

	private static int getMessageEndIndex(List<GuiMessage.Line> trimmedMessages, int lineIndex) {
		int index=lineIndex;
		while(index>=0) {
			GuiMessage.Line line=trimmedMessages.get(index);
			if(line.endOfEntry())return index;
			index--;
		}
		return -1;
	}

	private static int getMessageIndexFromEndLine(List<GuiMessage.Line> trimmedMessages, int endIndex) {
		int count=0;
		for (int i=0;i<=endIndex&&i<trimmedMessages.size();i++) {
			if (trimmedMessages.get(i).endOfEntry())count++;
		}
		return count-1;
	}
}
