package io.github.rpg.model;

public class GameState {

    private static GameState instance;

    private int level;
    private int coins;
    private int bestLevel;
    private int currentHealth;

    private String notificationMessage = "";
    private float notificationTimer = 0;

    private GameState() {
        this.level = 0;
        this.coins = 0;
        this.bestLevel = 0;
        this.currentHealth = -1;
    }

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    public void reset() {
        if (level > bestLevel) {
            bestLevel = level;
        }
        level = 0;
        currentHealth = -1;
    }

    public void showMessage(String message) {
        this.notificationMessage = message;
        this.notificationTimer = 3.0f;
    }

    public void update(float delta) {
        if (notificationTimer > 0) {
            notificationTimer -= delta;
            if (notificationTimer < 0) notificationTimer = 0;
        }
    }

    public String getNotificationMessage() {
        return notificationTimer > 0 ? notificationMessage : null;
    }

    public int getLevel() {return level;}
    public void incrementLevel() {this.level++;}
    public int getCoins() {return coins;}
    public void addCoin() {this.coins++;}
    public void addCoins(int amount) {this.coins += amount;}
    public int getBestLevel() {return bestLevel;}
    public int getCurrentHealth() {return currentHealth;}
    public void setCurrentHealth(int currentHealth) {this.currentHealth = currentHealth;}
}
