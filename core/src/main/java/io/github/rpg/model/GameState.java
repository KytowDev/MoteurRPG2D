package io.github.rpg.model;

public class GameState {
    public static int level = 0;       // Commence à 0 comme demandé
    public static int coins = 0;       // Remplace 'score' pour être plus clair
    public static int bestLevel = 0;   // Record de la session

    public static void reset() {
        // Avant de reset, on regarde si on a battu le record
        if (level > bestLevel) {
            bestLevel = level;
        }
        level = 0;
    }

    // Petite méthode utilitaire à appeler quand on monte de niveau
    public static void levelUp() {
        level++;
        if (level > bestLevel) {
            bestLevel = level;
        }
    }
}
