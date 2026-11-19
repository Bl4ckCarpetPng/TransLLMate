# TransLLMate
The ***local translation*** mod. Does not need anything but serving a local LLM for translation
## Usage
> [!CAUTION]
> 1. LLMs might translate inaccurately or lie - be careful and double-check for mistakes. Especially when using <7B models
> 2. LLMs can get tricked using specific prompts so that they'll reply with something else entirely.
> 3. The Large Language Models consume a great amount of resources, so I'd recommend to get either:
> - 16 GB of RAM (these sticks are not *that* cheap as of time of writing this README)
> - A CPU with AVX2 instructions and 2+ hyper-threaded cores should be enough
> - - OR
> - Recent enough GPU (with Vulkan 1.3 support) with at least 4-6 GB of VRAM
> - - Make sure the GPU isn't a weak integrated one (HD Graphics / Radeon HD/R[X] series)
> - - and don't try running 'em on laptops🥀
> - MoE models (such as Gemma 4 26B A4B) are fine - just ensure only experts (and KV cache, if possible) are stored in RAM
> 4. Only local API endpoints work.
1. Open the chat
2. Shift-Click message you want to translate, then wait for the magic
## Configuration
Accesible through Mod Menu and `/transllmate` command. 
The options should be self-explanatory enough
## Tested models
tested with `Q4_K_M` quant - higher quants might give a better result
- `gemma3-4b` - OK-ish accuracy
- `gemma3-12b` - Good accuracy at expense of compute and RAM
- `gemma4-12b` - Good accuracy ^
- `gemma4-e4b` - Untested
- `gemma4-e2b` - Untested
- warning: Qwen is known for overthinking everything; therefore thinking should be disabled if you favor better speed
- `qwen3.5-9b` - Untested
- `qwen3.5-4b` - Untested
- `qwen3.5-2b` - Mid-OK-ish accuracy
- `qwen3.5-0.8b` - Untested; last resort only
- `Maybe some other model[s]?`

## Other stuff
### Simple example of serving an LLM for mod using llama.cpp
#### Skip it if you use LM Studio or other software/API
1. Head over to https://github.com/ggml-org/llama.cpp/releases and download Vulkan variant of llama.cpp tools for your platform.
2. Extract the archive and open Terminal (don't forget to `cd` to the same folder with llama.cpp tools)
3. `./llama-server -hf user/model -fa on --port 1234 -t 2 -c 1024` to download the model from Hugging Face and run llama.cpp server
- `-hf` is for downloading a model from Hugging Face. If you have a local model, just point to it with `-m` as location instead of `-hf`.
- Use `./llama-server -hf user/model -fa on --port 1234 -t 0 -ngl 99 -c 1024` if you have a graphics card that can fit the entire model
4. Go to `localhost:1234` in your browser to ensure the server is up.
5. Try translating a message from a running Minecraft instance. If API works, the message should be translated without errors.
### Building
in short: `gradlew build`