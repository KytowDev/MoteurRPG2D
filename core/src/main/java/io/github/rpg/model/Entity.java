package io.github.rpg.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public abstract class Entity {

    private Vector2 pos;
    private Rectangle hitbox;
    private float speed;
    private int health;
    private String type;

    private boolean facingRight = true;
    private boolean isMoving = false;
    private float stateTime = 0;
    private float immunityTimer = 0;
    private Vector2 knockback = new Vector2(0, 0);

    public Entity(Vector2 pos, float speed, int health, String type, float width, float height) {
        this.pos = pos;
        this.speed = speed;
        this.health = health;
        this.type = type;
        this.hitbox = new Rectangle(pos.x, pos.y, width, height);
    }

    public void update(float delta, Array<Rectangle> mapCollisions, Player player, Array<Entity> allEntities) {
        if (immunityTimer > 0) immunityTimer -= delta;

        // recul
        if (knockback.len() > 10f) {
            move(knockback.x * delta, knockback.y * delta, mapCollisions);
            knockback.scl(0.90f);
        }

        decideNextMove(delta, mapCollisions, player, allEntities);

        hitbox.setPosition(pos.x, pos.y);

        stateTime += delta;
    }

    protected abstract void decideNextMove(float delta, Array<Rectangle> collisions, Player player, Array<Entity> allEntities);

    public void move(float x, float y, Array<Rectangle> collisions) {
        pos.x += x;
        hitbox.x = pos.x;
        for (Rectangle wall : collisions) {
            if (hitbox.overlaps(wall)) {
                pos.x -= x;
                hitbox.x = pos.x;
                break;
            }
        }

        pos.y += y;
        hitbox.y = pos.y;
        for (Rectangle wall : collisions) {
            if (hitbox.overlaps(wall)) {
                pos.y -= y;
                hitbox.y = pos.y;
                break;
            }
        }

        isMoving = x != 0 || y != 0;
        if (x > 0) facingRight = true;
        if (x < 0) facingRight = false;
    }

    public void receiveDamage(int amount, Vector2 sourcePos) {
        if (immunityTimer > 0) return;
        health -= amount;
        immunityTimer = 0.5f; // 0.5 seconde d'invulnérabilité

        // Calcul du recul
        if (sourcePos != null) {
            knockback.set(pos.cpy().sub(sourcePos).nor().scl(250f));
        }
    }


    public Vector2 getPosition() { return pos; }

    public Rectangle getBounds() { return hitbox; }

    public float getSpeed() { return speed; }

    public int getHealth() { return health; }

    public boolean isDead() { return health <= 0; }

    public String getType() { return type; }

    public boolean isMoving() { return isMoving; }

    public boolean isFacingRight() {
        return facingRight;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    public void setMoving(boolean isMoving) {
        this.isMoving = isMoving;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public float getStateTime() { return stateTime; }

    public float getImmunityTimer() { return immunityTimer; }

    public Vector2 getKnockback() {
        return knockback;
    }
}
