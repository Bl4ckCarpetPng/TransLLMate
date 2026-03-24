# TransLLMate
The ***local translation*** mod. Does not need anything but serving a local LLM for translation
## Usage
> [!CAUTION]
> 1. LLMs can provide inaccurate translations or lie - be careful and check for mistakes. Especially when they have less than 7B parameters
> 2. LLMs can get tricked using specific prompts so that they'll reply with something else entirely.
> 3. The Large Language Models greatly consume resources, so I'd recommend to get:
> - 16 GB of RAM (these sticks are not *that* cheap as of time of writing this README)
> - A CPU with AVX2 instructions and 2+ hyper-threaded cores should be enough
> Just don't try inferencing LLMs on laptops🥀
> 4. Only local API endpoints work.
1. Open chat (and, maybe, send a message)
2. Shift-Click message you want to translate, then wait for the magic
## Configuration
Accesible through Mod Menu and `/transllmate` command. 
The options should be self-explanatory enough
## Tested models
- `gemma3-4b` - OK-ish accuracy
- `gemma3-12b` - Good accuracy at expense of compute and RAM
- `qwen3-4b` - Overall bad accuracy, may understand Chinese better than any other language
- `qwen3.5-9b` - Loops in thinking mode, therefore `idk` accuracy for now
- `Maybe some other model[s]?`

## Other stuff
### Simple example of serving an LLM for mod using llama.cpp
#### Skip it if you use LM Studio or other software/API
1. Head over to https://github.com/ggml-org/llama.cpp/releases and download CPU variant of llama.cpp tools for your platform.
2. Extract the archive and open Terminal (don't forget to `cd` to the same folder with llama.cpp tools)
3. `./llama-server -hf user/model -fa on --port 1234 -t 2 -c 1024` to download the model from Hugging Face and run llama.cpp server
- `-hf` is for downloading a model from Hugging face. If you have a local model, just point to it with `-m` as location instead of `-hf`.
4. Go to `localhost:1234` in your browser to ensure the server is up.
5. Try translating a message from a running Minecraft instance. If API works, the message should be translated without errors.