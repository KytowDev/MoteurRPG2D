package io.github.rpg.model;

import com.badlogic.gdx.utils.Array;

public interface Weapon {
    void update(float delta);
    void attack(Entity user, Array<Entity> targets);
    String getType();
    boolean isAttacking();
    float getAttackProgress();  // 0.0 (début) à 1.0 (fin) pour calculer l'angle
}
