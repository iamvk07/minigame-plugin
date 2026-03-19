package com.vedant.minigame;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class MiniGamePlugin extends JavaPlugin {

    private GameManager gameManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        gameManager = new GameManager(this);
        getCommand("minigame").setExecutor(this);
        getLogger().info("MiniGame Plugin v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        if (gameManager.isGameRunning()) gameManager.forceStop();
        getLogger().info("MiniGame Plugin disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Must be run by a player.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) { sendHelp(player); return true; }

        switch (args[0].toLowerCase()) {
            case "join":  handleJoin(player);  break;
            case "leave": handleLeave(player); break;
            case "start": handleStart(player); break;
            case "stop":  handleStop(player);  break;
            case "stats": handleStats(player); break;
            case "info":  handleInfo(player);  break;
            default: sendHelp(player);
        }
        return true;
    }

    private void handleJoin(Player player) {
        GameManager.JoinResult result = gameManager.joinGame(player);
        switch (result) {
            case SUCCESS:
                player.sendMessage(ChatColor.GREEN + "Joined! Players: "
                    + gameManager.getPlayerCount() + "/" + getConfig().getInt("game.max-players", 8));
                break;
            case GAME_FULL:
                player.sendMessage(ChatColor.RED + "Game is full!"); break;
            case ALREADY_IN_GAME:
                player.sendMessage(ChatColor.YELLOW + "You're already in the game."); break;
            case GAME_IN_PROGRESS:
                player.sendMessage(ChatColor.RED + "Game already running. Wait for next round!"); break;
        }
    }

    private void handleLeave(Player player) {
        if (gameManager.leaveGame(player))
            player.sendMessage(ChatColor.YELLOW + "You left the game.");
        else
            player.sendMessage(ChatColor.RED + "You are not in a game.");
    }

    private void handleStart(Player player) {
        if (!player.hasPermission("minigame.start")) {
            player.sendMessage(ChatColor.RED + "No permission."); return;
        }
        GameManager.StartResult result = gameManager.startGame();
        switch (result) {
            case SUCCESS: player.sendMessage(ChatColor.GREEN + "Game started!"); break;
            case NOT_ENOUGH_PLAYERS: player.sendMessage(ChatColor.RED + "Not enough players!"); break;
            case ALREADY_RUNNING: player.sendMessage(ChatColor.YELLOW + "Already running."); break;
        }
    }

    private void handleStop(Player player) {
        if (!player.hasPermission("minigame.stop")) {
            player.sendMessage(ChatColor.RED + "No permission."); return;
        }
        gameManager.forceStop();
        player.sendMessage(ChatColor.YELLOW + "Game stopped.");
    }

    private void handleStats(Player player) {
        PlayerStats s = gameManager.getPlayerStats(player);
        player.sendMessage(ChatColor.GOLD + "=== Your Stats ===");
        player.sendMessage(ChatColor.WHITE + "Games: " + ChatColor.AQUA + s.getGamesPlayed());
        player.sendMessage(ChatColor.WHITE + "Wins: " + ChatColor.GREEN + s.getWins());
        player.sendMessage(ChatColor.WHITE + "Win Rate: " + ChatColor.YELLOW + s.getWinRate() + "%");
    }

    private void handleInfo(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== MiniGame Info ===");
        player.sendMessage(ChatColor.WHITE + "Players: " + ChatColor.AQUA
            + gameManager.getPlayerCount() + "/" + getConfig().getInt("game.max-players", 8));
        player.sendMessage(ChatColor.WHITE + "Status: " + (gameManager.isGameRunning()
            ? ChatColor.GREEN + "Running" : ChatColor.YELLOW + "Waiting"));
    }

    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== MiniGame Commands ===");
        player.sendMessage(ChatColor.YELLOW + "/minigame join/leave/stats/info/start/stop");
    }
}
