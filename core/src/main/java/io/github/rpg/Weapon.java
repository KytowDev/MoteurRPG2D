package io.github.rpg;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public interface Weapon {
    void update(float delta);
    void attack(Entity user, Array<Entity> targets);
    void render(Batch batch, Vector2 position, float angle, boolean facingRight);
}
