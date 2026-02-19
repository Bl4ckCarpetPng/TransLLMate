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

package blackcar.transllmate;
import blackcar.transllmate.config.ModMenuIntegration;
import blackcar.transllmate.config.TransLLMateConfig;

import net.minecraft.client.Minecraft;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class TransLLMateClient implements ClientModInitializer {
	@Override // Just one command. Really.
	public void onInitializeClient() {
		TransLLMateConfig.HANDLER.load();TransLLMateConfig.HANDLER.save();
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
			dispatcher.register(ClientCommandManager.literal("transllmate")
				.executes(context -> {
					Minecraft mc = Minecraft.getInstance();
					mc.execute(() -> mc.setScreen(ModMenuIntegration.createConfigScreen(mc.screen)));
					return 1;
				})));
	}
}
