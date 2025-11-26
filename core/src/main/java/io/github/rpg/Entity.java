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

    public Entity(Vector2 pos, float speed, int health) {
        this.pos = pos;
        this.speed = speed;
        this.health = health;
    }

    public void update(float delta, Array<Rectangle> mapCollisions, Player player, Array<Entity> allEntities) {
        decideNextMove(delta, mapCollisions, player, allEntities);
        hitbox.setPosition(pos.x, pos.y);
    }

    protected abstract void decideNextMove(float delta, Array<Rectangle> collisions, Player player, Array<Entity> allEntities);

    public void render(Batch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = isMoving ? runAnim.getKeyFrame(stateTime, true) : idleAnim.getKeyFrame(stateTime, true);
        if (!currentFrame.isFlipX() && !facingRight) currentFrame.flip(true, false);
        if (currentFrame.isFlipX() && facingRight) currentFrame.flip(true, false);
        batch.draw(currentFrame, pos.x, pos.y);
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

    public float getSpeed() { return speed; }
    public void takeDamage(int amount) { health -= amount; }
    public boolean isDead() { return health <= 0; }
    public Rectangle getBounds() { return hitbox; }
    public Vector2 getPosition() { return pos; }
    public void applyKnockback(Vector2 force) {}
}
