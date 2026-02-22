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
import blackcar.transllmate.config.TransLLMateConfig;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.SocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class LocalTranslator {
	private static final String genError = "Error while processing a request: "; // it's a bad idea to leave it like this but still
	private static final Gson GSON = new Gson();

	private static final HttpClient HTTP = HttpClient.newBuilder()
		.connectTimeout(Duration.ofSeconds(10))
		.proxy(new NoProxySelector())
		.version(HttpClient.Version.HTTP_1_1)
		.build();

	private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(r -> {
		Thread thread = new Thread(r, "TransLLMate-lm");
		thread.setDaemon(true);
		return thread;
	});
	private LocalTranslator(){}

	// Building context
	private static JsonObject message(String role, String content) {
		JsonObject message = new JsonObject();
		message.addProperty("role", role);
		message.addProperty("content", content);
		return message;
	}
	private static JsonArray buildMessages(String sysprompt, String txt) {
		JsonArray messages = new JsonArray();
		messages.add(message("system", sysprompt));
		messages.add(message("user", "<TEXT_DELIMITER>"+txt+"</TEXT_DELIMITER>"));
		return messages;
	}

	// main
	public static CompletableFuture<String> process(String text, TransLLMateConfig config) {
		JsonObject body = new JsonObject();
		body.addProperty("model", config.model);
		body.add("messages", buildMessages(config.systemPrompt.replace("{language}", config.targetLang), text));
		body.addProperty("temperature", config.temperature);

		HttpRequest.Builder req = HttpRequest.newBuilder()
			.uri(URI.create("http://"+config.api+"/v1/chat/completions")) // http is fine for local deployments
			.timeout(Duration.ofSeconds(30))
			.header("Content-Type","application/json")
			.POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(body)));

		if(!config.key.isBlank())req.header("Authorization","Bearer "+config.key);

		HttpRequest wReq = req.build();
		return CompletableFuture.supplyAsync(() -> send(wReq), EXECUTOR);
	}
	private static String send(HttpRequest req) {
		try {
			HttpResponse<String> response = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() / 100 != 2) {
				String body = response.body() == null ? "" : response.body();
				throw new IOException(genError+"Request failed with HTTP "+response.statusCode()+")\nbody: "+truncate(body,400));
			}
			return extractContent(response.body());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(genError+"interrupted",e);
		} catch (IOException e) {
			throw new RuntimeException(genError+"Make sure you have LLM server up and running.",e);
		}
	}

	private static String extractContent(String body) {
		JsonObject root = JsonParser.parseString(body).getAsJsonObject();
		if (root.has("error")) {
			JsonObject error = root.getAsJsonObject("error");
			String message = error != null && error.has("message") ? error.get("message").getAsString() : "Generation error";
			throw new RuntimeException(message);
		}

		JsonArray choices = root.getAsJsonArray("choices");
		if(choices==null||choices.size()==0)throw new RuntimeException(genError+"No choices[] detected");

		JsonObject choice = choices.get(0).getAsJsonObject();
		JsonObject message = choice.getAsJsonObject("message");
		if(message==null||!message.has("content"))throw new RuntimeException(genError+"No content in response");

		return message.get("content").getAsString().trim();
	}

	private static String truncate(String v,int m){if(v==null||v.length()<=m)return v;return v.substring(0,m)+"...";}

	private static final class NoProxySelector extends ProxySelector {
		@Override
		public List<Proxy> select(URI uri) {return List.of(Proxy.NO_PROXY);}

		@Override
		public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {} // nop
	}
}
