package io.github.rpg;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Monster extends Entity {

    private final EnemyBehavior behavior;
    private final Vector2 knockback = new Vector2(0, 0);

    public Monster(Vector2 pos, Texture idle, Texture run, float spd, EnemyBehavior behavior, int w, int h, int frames) {
        super(pos, spd, 150);
        this.behavior = behavior;
        this.hitbox = new Rectangle(pos.x, pos.y, w, h);
        this.idleAnim = createAnim(idle, w, h, frames);
        this.runAnim = createAnim(run, w, h, frames);
    }

    private Animation<TextureRegion> createAnim(Texture t, int w, int h, int f) {
        TextureRegion[][] tmp = TextureRegion.split(t, w, h);
        TextureRegion[] frames = new TextureRegion[f];
        for (int i = 0; i < f; i++) frames[i] = tmp[0][i];
        return new Animation<>(0.1f, frames);
    }

    @Override
    protected void decideNextMove(float delta, Array<Rectangle> collisions, Player player, Array<Entity> allEntities) {
        if (knockback.len() > 10f) {
            handleKnockback(delta, collisions);
        } else {
            behavior.act(this, player, collisions, delta);
        }
    }

    private void handleKnockback(float delta, Array<Rectangle> collisions) {
        // Le knockback utilise aussi la méthode move pour ne pas traverser les murs
        move(knockback.x * delta, knockback.y * delta, collisions);
        knockback.scl(0.85f);
    }

    @Override
    public void applyKnockback(Vector2 force) {
        knockback.set(force);
    }
}
