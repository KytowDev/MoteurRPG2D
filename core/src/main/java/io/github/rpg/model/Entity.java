package io.github.rpg.model; // Si vous avez déplacé dans model, mettez: package io.github.rpg.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public abstract class Entity {

    // --- Données du Modèle (État) ---
    protected Vector2 pos;
    protected Rectangle hitbox;
    protected float speed;
    protected int health;
    protected String type; // NOUVEAU : Définit l'apparence (ex: "player", "big_monster")

    // --- États pour l'animation et la logique ---
    protected boolean facingRight = true;
    protected boolean isMoving = false;
    protected float stateTime = 0;
    protected float immunityTimer = 0;
    protected Vector2 knockback = new Vector2(0, 0);

    public Entity(Vector2 pos, float speed, int health) {
        this.pos = pos;
        this.speed = speed;
        this.health = health;
    }

    public void update(float delta, Array<Rectangle> mapCollisions, Player player, Array<Entity> allEntities) {
        if (immunityTimer > 0) immunityTimer -= delta;

        // Gestion du recul (Knockback)
        if (knockback.len() > 10f) {
            move(knockback.x * delta, knockback.y * delta, mapCollisions);
            knockback.scl(0.90f); // Amortissement
        }

        // Logique spécifique (IA ou Input joueur)
        decideNextMove(delta, mapCollisions, player, allEntities);

        // Mise à jour de la hitbox pour qu'elle suive la position visuelle
        hitbox.setPosition(pos.x, pos.y);

        // Mise à jour du temps pour l'animation
        stateTime += delta;
    }

    // Méthode abstraite que Player et Monster doivent implémenter
    protected abstract void decideNextMove(float delta, Array<Rectangle> collisions, Player player, Array<Entity> allEntities);

    public void move(float x, float y, Array<Rectangle> collisions) {
        // Déplacement X
        pos.x += x;
        hitbox.x = pos.x;
        for (Rectangle wall : collisions) {
            if (hitbox.overlaps(wall)) {
                pos.x -= x;
                hitbox.x = pos.x;
                break;
            }
        }

        // Déplacement Y
        pos.y += y;
        hitbox.y = pos.y;
        for (Rectangle wall : collisions) {
            if (hitbox.overlaps(wall)) {
                pos.y -= y;
                hitbox.y = pos.y;
                break;
            }
        }

        // Mise à jour des états
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

    // --- GETTERS (Indispensables pour la Vue et les Behaviors) ---

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

    public float getStateTime() { return stateTime; }

    public float getImmunityTimer() { return immunityTimer; }

}
