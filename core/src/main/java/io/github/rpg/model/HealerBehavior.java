package io.github.rpg.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.rpg.utils.Assets;

public class HealerBehavior implements InteractableStrategy {

    @Override
    public void act(Monster monster, Player player, Array<Rectangle> collisions, float delta) {
        //immbobile
    }

    @Override
    public boolean onInteract(Monster me, Player player) {
        GameState state = GameState.getInstance();
        int maxHealth = 6;

        if (player.getHealth() >= maxHealth) {
            state.showMessage("Votre santé est déjà pleine !");
            return false;
        }

        int COST = 10;
        if (state.getCoins() >= COST) {
            state.addCoins(-COST);
            player.setHealth(maxHealth);
            state.showMessage("Soins effectués (-10 pièces)");
            return true;
        } else {
            state.showMessage("Pas assez d'argent ! (Requis: " + COST + ")");
            return false;
        }
    }
}
