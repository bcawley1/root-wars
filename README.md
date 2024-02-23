# Root Wars
## A Minecraft server plugin using the [SpigotAPI](https://www.spigotmc.org/), that brings a fun PvP based minigame to your server.
## Features
- **Voting System:** Players can vote for the map and game mode that they would like to play. The item with the most amount of votes wins and is automatically selected for next game.
- **Custom Maps:** Using the WorldEdit API, custom maps are allowed to be loaded from `.schem` files as well as having important data stored in a JSON file.
- **Unique Game Modes:** Many game modes are available for players to play with. They allow for Root Wars to be played in many new ways, while still keeping the core concepts the same.
- **Generators And Shops:** Generators allow for players to gain resources that they can share with their team in order to buy items from the shop that can assist them.
- **Customizability:** Through the use of JSON files, server owners can customize data related to almost every aspect of the game, such as generator speed, potion effects, respawn time, max player health, and much more.
## Installation
1. Download the Root Wars plugin JAR file from the releases section of this repository.
2. Place the downloaded JAR file into the 'plugins' folder of your Minecraft server running Spigot or Paper.
3. Ensure that you have the latest version of the WorldEdit plugin installed as well.
4. Restart your server to enable the plugin.
## Dependencies
- The latest version of [WorldEdit](https://enginehub.org/worldedit).
## Configuration
- Many global variables can be configured through the `config.yml` file in the plugin's install location. Other variables that are more specific are able to be configured inside of the JSON file for a game mode.
## Commands
```/rootwars start```
