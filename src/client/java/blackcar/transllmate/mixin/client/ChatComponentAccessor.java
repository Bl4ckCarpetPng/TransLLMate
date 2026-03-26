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

package blackcar.transllmate.mixin.client;

import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ChatComponent.class)
public interface ChatComponentAccessor {
	@Accessor("trimmedMessages")
	List<GuiMessage.Line> transllmate$getTrimmedMessages();

	@Accessor("allMessages")
	List<GuiMessage> transllmate$getAllMessages();

	@Accessor("chatScrollbarPos")
	int transllmate$getChatScrollbarPos();

	@Invoker("getScale")
	double transllmate$invokeGetScale();

	@Invoker("getWidth")
	int transllmate$invokeGetWidth();

	@Invoker("getLineHeight")
	int transllmate$invokeGetLineHeight();

	@Invoker("getLinesPerPage")
	int transllmate$invokeGetLinesPerPage();
}
