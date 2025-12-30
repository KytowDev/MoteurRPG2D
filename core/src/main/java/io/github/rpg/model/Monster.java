package io.github.rpg.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Monster extends Entity {

    private final EnemyBehavior behavior;
    private int damage;

    public Monster(Vector2 pos, String type, int health, float spd, int damage, EnemyBehavior behavior, int w, int h) {
        super(pos, spd, health); // On passe la santé du JSON (ex: 50)
        this.damage = damage;
        this.type = type;
        this.behavior = behavior;
        this.hitbox = new Rectangle(pos.x, pos.y, w, h);
    }

    public int getDamage() {
        return damage;
    }

    @Override
    protected void decideNextMove(float delta, Array<Rectangle> collisions, Player player, Array<Entity> allEntities) {
        if (knockback.len() < 10f) behavior.act(this, player, collisions, delta);
    }
}
