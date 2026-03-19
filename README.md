# MiniGame Plugin

A configurable multiplayer minigame framework for Bukkit/Spigot Minecraft servers. Handles match lifecycle, player state machines, in-game events, and automated CI/CD deployments.

## Features

- 🎮 Full match lifecycle management (waiting → starting → in-progress → ending)
- 👥 Player join/leave handling with auto-start on max players
- 📊 Per-player statistics (games played, wins, win rate)
- ⚙️ Fully configurable via config.yml (player limits, countdown, game mode)
- 🔒 Permission-based admin commands
- 🚀 CI/CD pipeline with GitHub Actions for automated builds

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/minigame join` | Join the game queue | All players |
| `/minigame leave` | Leave the current game | All players |
| `/minigame stats` | View your stats | All players |
| `/minigame info` | View game info | All players |
| `/minigame start` | Force-start the game | OP only |
| `/minigame stop` | Force-stop the game | OP only |

## Installation

1. Download the latest `.jar` from Releases
2. Drop into your server's `/plugins` folder
3. Restart the server
4. Edit `plugins/MiniGamePlugin/config.yml` to customize

## Building from Source
```bash
git clone https://github.com/iamvk07/minigame-plugin
cd minigame-plugin
mvn clean package
```

## Configuration
```yaml
game:
  mode: "survival"
  min-players: 2
  max-players: 8
  countdown-seconds: 10
  time-limit-minutes: 5
```

## Tech Stack

- Java 17
- Maven
- Bukkit/Spigot API 1.20
- JUnit 5
- GitHub Actions (CI/CD)

## Author

**Vedant Kadam** · UNB Computer Science · [LinkedIn](https://linkedin.com/in/vedantkadam07)
