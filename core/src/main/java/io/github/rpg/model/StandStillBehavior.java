package io.github.rpg.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class StandStillBehavior implements EnemyBehavior {
    @Override
    public void act(Monster monster, Player player, Array<Rectangle> collisions, float delta) {
        // ne fait rien
    }
}
