package io.github.rpg.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public interface Weapon {
    void update(float delta);
    void attack(Entity user, Array<Entity> targets);
    String getType();           // Ex: "rusty_sword"
    boolean isAttacking();      // Est-ce qu'on doit dessiner l'anim ?
    float getAttackProgress();  // 0.0 (début) à 1.0 (fin) pour calculer l'angle
}
