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

package blackcar.transllmate.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory(){return ModMenuIntegration::createConfigScreen;}

    public static Screen createConfigScreen(Screen parentScreen) {
        return YetAnotherConfigLib.create(TransLLMateConfig.HANDLER, (config, defaults, builder) -> builder
            .title(Component.literal("TransLLMate"))
            .category(ConfigCategory.createBuilder()
                .name(Component.literal("General"))
                .group(OptionGroup.createBuilder()
                    .name(Component.literal("General"))
                    .option(Option.<Boolean>createBuilder(Boolean.class)
                        .name(Component.literal("Enabled"))
                        .description(OptionDescription.createBuilder()
                            .text(Component.literal("Toggle translation functionality."))
                            .build())
                        .binding(defaults.enabled, () -> config.enabled, value -> config.enabled = value)
                        .controller(BooleanControllerBuilder::create)
                        .build())
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Connection"))
                    .option(Option.<String>createBuilder(String.class)
                        .name(Component.literal("API Link"))
                        .description(OptionDescription.createBuilder()
                            .text(Component.literal("OpenAI-compatible endpoint."))
                            .build())
                        .binding(defaults.apiUrl, () -> config.apiUrl, value -> config.apiUrl = value)
                        .controller(StringControllerBuilder::create)
                        .build())
                    .option(Option.<String>createBuilder(String.class)
                        .name(Component.literal("API Authorization"))
                        .description(OptionDescription.createBuilder()
                            .text(Component.literal("Just an API key. Optional, fill if you use a 3rd party API."))
                            .build())
                        .binding(defaults.apiKey, () -> config.apiKey, value -> config.apiKey = value)
                        .controller(StringControllerBuilder::create)
                        .build())
                    .option(Option.<String>createBuilder(String.class)
                        .name(Component.literal("Model"))
                        .description(OptionDescription.createBuilder()
                            .text(Component.literal("Model name to be used for translation."))
                            .build())
                        .binding(defaults.model, () -> config.model, value -> config.model = value)
                        .controller(StringControllerBuilder::create)
                        .build())
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Prompt"))
                    .option(Option.<String>createBuilder(String.class)
                        .name(Component.literal("Preferred Language"))
                        .description(OptionDescription.createBuilder()
                            .text(Component.literal("Target language for translations. Use plain text for it and LLM will understand what to do"))
                            .build())
                        .binding(defaults.preferredLanguage, () -> config.preferredLanguage, value -> config.preferredLanguage = value)
                        .controller(StringControllerBuilder::create)
                        .build())
                    .option(Option.<String>createBuilder(String.class)
                        .name(Component.literal("System Prompt"))
                        .description(OptionDescription.createBuilder()
                            .text(Component.literal("System prompt for LLM.\n\nLegend:\n{language} = Language placeholder"))
                            .build())
                        .binding(defaults.systemPrompt, () -> config.systemPrompt, value -> config.systemPrompt = value)
                        .controller(StringControllerBuilder::create)
                        .build())
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Generation"))
                    .option(Option.<Double>createBuilder(Double.class)
                        .name(Component.literal("Temperature"))
                        .description(OptionDescription.createBuilder()
                            .text(Component.literal("Creativity level.\n0.1 - minimal, least creative\n2 - max, most creative"))
                            .build())
                        .binding(defaults.temperature, () -> config.temperature, value -> config.temperature = value)
                        .controller(DoubleFieldControllerBuilder::create)
                        .build())
                    .option(Option.<Integer>createBuilder(Integer.class)
                        .name(Component.literal("Request timeout"))
                        .description(OptionDescription.createBuilder()
                            .text(Component.literal("API request timeout"))
                            .build())
                        .binding(defaults.timeoutSeconds, () -> config.timeoutSeconds, value -> config.timeoutSeconds = value)
                        .controller(IntegerFieldControllerBuilder::create)
                        .build())
                    .build())
                .build())
            .save(TransLLMateConfig.HANDLER::save)
        ).generateScreen(parentScreen);
    }
}
