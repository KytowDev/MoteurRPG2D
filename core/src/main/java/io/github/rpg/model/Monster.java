package io.github.rpg.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Monster extends Entity {

    private final EnemyBehavior behavior;
    private int damage;

    public Monster(Vector2 pos, String type, int health, float spd, int damage, EnemyBehavior behavior, int w, int h) {
        // On passe TOUT au parent. C'est beaucoup plus propre.
        super(pos, spd, health, type, w, h);

        this.damage = damage;
        this.behavior = behavior;
        // Plus besoin de "this.type = ..." ni "this.hitbox = ..."
    }

    public int getDamage() {
        return damage;
    }

    @Override
    protected void decideNextMove(float delta, Array<Rectangle> collisions, Player player, Array<Entity> allEntities) {
        if (getKnockback().len() < 10f) {
            behavior.act(this, player, collisions, delta);
        }
    }
}
