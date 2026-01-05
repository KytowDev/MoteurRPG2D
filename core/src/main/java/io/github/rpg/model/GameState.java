package io.github.rpg.model;

public class GameState {

    // 1. L'instance unique (Singleton)
    private static GameState instance;

    // 2. Les données sont privées (Encapsulation)
    private int level;
    private int coins;
    private int bestLevel;
    private int currentHealth;

    // 3. Constructeur privé : Personne ne peut faire "new GameState()" à l'extérieur
    private GameState() {
        this.level = 0;
        this.coins = 0;
        this.bestLevel = 0;
        this.currentHealth = -1;
    }

    // 4. Point d'accès unique
    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    // 5. Méthodes métiers (Logique encapsulée)
    public void reset() {
        if (level > bestLevel) {
            bestLevel = level;
        }
        level = 0;
        currentHealth = -1;
        // Tu peux décider si les pièces se reset ou non ici
        // coins = 0;
    }

    // --- Getters & Setters ---

    public int getLevel() {
        return level;
    }

    public void incrementLevel() {
        this.level++;
    }

    public int getCoins() {
        return coins;
    }

    public void addCoin() {
        this.coins++;
    }

    // Si tu as besoin d'ajouter beaucoup d'un coup
    public void addCoins(int amount) {
        this.coins += amount;
    }

    public int getBestLevel() {
        return bestLevel;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }
}
