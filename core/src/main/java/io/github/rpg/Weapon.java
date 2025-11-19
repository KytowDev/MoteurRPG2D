package io.github.rpg;

import com.badlogic.gdx.utils.Array;

public interface Weapon {
    void attack(Entity user, Array<Entity> targets);
    void update(float delta);
}
