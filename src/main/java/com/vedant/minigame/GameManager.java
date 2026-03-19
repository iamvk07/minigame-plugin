package com.vedant.minigame;

import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import java.util.*;

public class GameManager {

    public enum GameState { WAITING, STARTING, IN_PROGRESS, ENDING }
    public enum JoinResult { SUCCESS, GAME_FULL, ALREADY_IN_GAME, GAME_IN_PROGRESS }
    public enum StartResult { SUCCESS, NOT_ENOUGH_PLAYERS, ALREADY_RUNNING }

    private final MiniGamePlugin plugin;
    private GameState state = GameState.WAITING;
    private final Set<Player> players = new LinkedHashSet<>();
    private final Map<Player, PlayerStats> statsMap = new HashMap<>();
    private long gameStartTime;

    public GameManager(MiniGamePlugin plugin) {
        this.plugin = plugin;
    }

    public JoinResult joinGame(Player player) {
        if (players.contains(player)) return JoinResult.ALREADY_IN_GAME;
        if (state == GameState.IN_PROGRESS) return JoinResult.GAME_IN_PROGRESS;
        if (players.size() >= plugin.getConfig().getInt("game.max-players", 8)) return JoinResult.GAME_FULL;

        players.add(player);
        statsMap.putIfAbsent(player, new PlayerStats(player.getName()));
        broadcast(ChatColor.GREEN + player.getName() + " joined! ("
            + players.size() + "/" + plugin.getConfig().getInt("game.max-players", 8) + ")");

        if (players.size() >= plugin.getConfig().getInt("game.max-players", 8)) startGame();
        return JoinResult.SUCCESS;
    }

    public boolean leaveGame(Player player) {
        if (!players.contains(player)) return false;
        players.remove(player);
        broadcast(ChatColor.YELLOW + player.getName() + " left the game.");
        if (state == GameState.IN_PROGRESS && players.size() < 2)
            endGame(players.isEmpty() ? null : players.iterator().next());
        return true;
    }

    public StartResult startGame() {
        if (state == GameState.IN_PROGRESS) return StartResult.ALREADY_RUNNING;
        if (players.size() < plugin.getConfig().getInt("game.min-players", 2)) return StartResult.NOT_ENOUGH_PLAYERS;

        state = GameState.STARTING;
        int countdown = plugin.getConfig().getInt("game.countdown-seconds", 10);
        broadcast(ChatColor.GOLD + "Game starting in " + countdown + " seconds!");

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            state = GameState.IN_PROGRESS;
            gameStartTime = System.currentTimeMillis();
            broadcast(ChatColor.GREEN + "" + ChatColor.BOLD + "GO! Game started!");
        }, countdown * 20L);

        return StartResult.SUCCESS;
    }

    public void endGame(Player winner) {
        state = GameState.ENDING;
        if (winner != null) {
            broadcast(ChatColor.GOLD + "" + ChatColor.BOLD + winner.getName() + " wins!");
            statsMap.get(winner).recordWin();
        } else {
            broadcast(ChatColor.YELLOW + "Game ended — no winner.");
        }
        for (Player p : players) statsMap.get(p).recordGame();
        long duration = (System.currentTimeMillis() - gameStartTime) / 1000;
        broadcast(ChatColor.GRAY + "Match duration: " + duration + "s");
        plugin.getServer().getScheduler().runTaskLater(plugin, this::resetGame, 100L);
    }

    public void forceStop() {
        broadcast(ChatColor.RED + "Game forcefully stopped by admin.");
        resetGame();
    }

    private void resetGame() {
        state = GameState.WAITING;
        players.clear();
    }

    private void broadcast(String message) {
        for (Player p : players) p.sendMessage(message);
        plugin.getLogger().info("[Game] " + ChatColor.stripColor(message));
    }

    public boolean isGameRunning() { return state == GameState.IN_PROGRESS; }
    public int getPlayerCount() { return players.size(); }
    public PlayerStats getPlayerStats(Player player) {
        return statsMap.getOrDefault(player, new PlayerStats(player.getName()));
    }
}
