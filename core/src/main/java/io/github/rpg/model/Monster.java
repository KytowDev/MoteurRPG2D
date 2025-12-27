package io.github.rpg.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Monster extends Entity {

    private final EnemyBehavior behavior;

    public Monster(Vector2 pos, String type, int health, float spd, EnemyBehavior behavior, int w, int h) {
        super(pos, spd, health); // On passe la santé du JSON (ex: 50)
        this.type = type;
        this.behavior = behavior;
        this.hitbox = new Rectangle(pos.x, pos.y, w, h);
    }

    @Override
    protected void decideNextMove(float delta, Array<Rectangle> collisions, Player player, Array<Entity> allEntities) {
        if (knockback.len() < 10f) behavior.act(this, player, collisions, delta);
    }
}
