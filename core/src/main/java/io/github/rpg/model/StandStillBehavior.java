package io.github.rpg.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class StandStillBehavior implements EnemyBehavior {
    @Override
    public void act(Monster monster, Player player, Array<Rectangle> collisions, float delta) {
        // Ne fait rien, le PNJ reste sur place et joue son animation "idle"
        // On pourrait ajouter ici : si le joueur est proche, se tourner vers lui.
    }
}
