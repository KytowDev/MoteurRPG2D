package io.github.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public abstract class Entity {

    protected Vector2 pos;
    protected Rectangle hitbox;
    protected float speed;
    protected int health;
    protected boolean facingRight = true;
    protected float stateTime = 0;
    protected Animation<TextureRegion> idleAnim;
    protected Animation<TextureRegion> runAnim;
    protected boolean isMoving = false;
    protected Vector2 knockback = new Vector2(0, 0);
    protected float immunityTimer = 0;

    public Entity(Vector2 pos, float speed, int health) {
        this.pos = pos;
        this.speed = speed;
        this.health = health;
    }

    public void update(float delta, Array<Rectangle> mapCollisions, Player player, Array<Entity> allEntities) {
        if (immunityTimer > 0) immunityTimer -= delta;

        if (knockback.len() > 10f) {
            move(knockback.x * delta, knockback.y * delta, mapCollisions);
            knockback.scl(0.90f);
        }

        decideNextMove(delta, mapCollisions, player, allEntities);
        hitbox.setPosition(pos.x, pos.y);
    }

    protected abstract void decideNextMove(float delta, Array<Rectangle> collisions, Player player, Array<Entity> allEntities);

    public void render(Batch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = isMoving ? runAnim.getKeyFrame(stateTime, true) : idleAnim.getKeyFrame(stateTime, true);
        if (!currentFrame.isFlipX() && !facingRight) currentFrame.flip(true, false);
        if (currentFrame.isFlipX() && facingRight) currentFrame.flip(true, false);

        if (immunityTimer > 0 && ((int)(stateTime * 20) % 2 == 0)) batch.setColor(1, 0, 0, 0.5f);
        batch.draw(currentFrame, pos.x, pos.y);
        batch.setColor(1, 1, 1, 1);
    }

    public void move(float x, float y, Array<Rectangle> collisions) {
        pos.x += x;
        hitbox.x = pos.x;
        for (Rectangle wall : collisions) if (hitbox.overlaps(wall)) pos.x -= x;

        pos.y += y;
        hitbox.y = pos.y;
        for (Rectangle wall : collisions) if (hitbox.overlaps(wall)) pos.y -= y;

        isMoving = x != 0 || y != 0;
        if (x > 0) facingRight = true;
        if (x < 0) facingRight = false;
        hitbox.setPosition(pos.x, pos.y);
    }

    public void receiveDamage(int amount, Vector2 sourcePos) {
        if (immunityTimer > 0) return;
        health -= amount;
        immunityTimer = 0.5f;
        knockback.set(pos.cpy().sub(sourcePos).nor().scl(250f));
    }

    public float getSpeed() { return speed; }
    public boolean isDead() { return health <= 0; }
    public Rectangle getBounds() { return hitbox; }
    public Vector2 getPosition() { return pos; }
}
