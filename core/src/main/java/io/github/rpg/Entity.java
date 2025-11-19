package io.github.rpg;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public abstract class Entity {

    public enum State { IDLE, RUNNING }

    protected Vector2 position;
    protected Vector2 velocity;
    protected Rectangle hitbox;

    protected int hp;
    protected int maxHp;
    protected boolean isDead;

    protected State currentState;
    public boolean facingRight = true;
    protected float stateTime;
    protected float speed;

    protected Animation<TextureRegion> idleAnim;
    protected Animation<TextureRegion> runAnim;

    public Entity(Vector2 spawn_pos, float speed, int maxHp) {
        this.position = spawn_pos;
        this.speed = speed;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.velocity = new Vector2(0,0);
        this.stateTime = 0f;
        this.currentState = State.IDLE;
        this.isDead = false;
    }

    public final void update(float delta, Array<Rectangle> walls, Player player, Array<Entity> allEntities) {
        stateTime += delta;
        decideNextMove(delta, player, allEntities);
        applyCollisions(delta, walls);
    }
    public void setVelocity(float x, float y) {
        this.velocity.set(x, y);

        if (x != 0 || y != 0) {
            this.currentState = State.RUNNING;
        } else {
            this.currentState = State.IDLE;
        }

        if (x > 0) {
            this.facingRight = true;
        } else if (x < 0) {
            this.facingRight = false;
        }
    }

    public float getSpeed() {
        return this.speed;
    }

    protected abstract void decideNextMove(float delta, Player player, Array<Entity> allEntities);

    public void takeDamage(int amount) {
        this.hp -= amount;
        if (this.hp <= 0) {
            this.isDead = true;
        }
    }

    public boolean isDead() {
        return this.isDead;
    }

    public Rectangle getBounds() {
        return this.hitbox;
    }

    protected void applyCollisions(float delta, Array<Rectangle> collisionRects) {
        if (this.hitbox == null) return;

        float moveX = velocity.x * delta;
        hitbox.x += moveX;

        for (Rectangle rect : collisionRects) {
            if (hitbox.overlaps(rect)) {
                hitbox.x -= moveX;
                velocity.x = 0;
                break;
            }
        }

        float moveY = velocity.y * delta;
        hitbox.y += moveY;

        for (Rectangle rect : collisionRects) {
            if (hitbox.overlaps(rect)) {
                hitbox.y -= moveY;
                velocity.y = 0;
                break;
            }
        }
        position.set(hitbox.x, hitbox.y);
    }

    public void moveUp() { this.velocity.y = this.speed; this.currentState = State.RUNNING; }
    public void moveDown() { this.velocity.y = -this.speed; this.currentState = State.RUNNING; }
    public void moveRight() { this.velocity.x = this.speed; this.currentState = State.RUNNING; this.facingRight = true; }
    public void moveLeft() { this.velocity.x = -this.speed; this.currentState = State.RUNNING; this.facingRight = false; }

    public void stopMoving() {
        if (!velocity.isZero()) {
            this.velocity.set(0,0);
            this.currentState = State.IDLE;
        }
    }

    public Vector2 getPosition() { return this.position; }

    public void render(SpriteBatch batch) {
        if (idleAnim == null || runAnim == null) return;
        Animation<TextureRegion> animation = (currentState == State.RUNNING) ? runAnim : idleAnim;
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);

        if (!facingRight && !currentFrame.isFlipX()) currentFrame.flip(true, false);
        if (facingRight && currentFrame.isFlipX()) currentFrame.flip(true, false);

        batch.draw(currentFrame, this.position.x, this.position.y);
    }
}
