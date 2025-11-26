package io.github.rpg;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Rectangle;

public interface EnemyBehavior {
    void act(Monster monster, Player player, Array<Rectangle> collisions, float delta);
}
