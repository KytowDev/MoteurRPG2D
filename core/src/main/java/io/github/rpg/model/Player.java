package io.github.rpg.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
// ATTENTION : On enlève l'import com.badlogic.gdx.Input ! Le modèle ne connaît plus le clavier.

public class Player extends Entity {

    private final Weapon weapon;
    private float aimAngle = 0f;

    public Player(Vector2 spawn_pos, int health, float speed) {
        super(spawn_pos, speed, health);
        this.type = "player";
        this.weapon = new Sword();
        this.hitbox = new Rectangle(spawn_pos.x, spawn_pos.y, 14, 20);
    }

    @Override
    protected void decideNextMove(float delta, Array<Rectangle> collisions, Player player, Array<Entity> allEntities) {
        // ICI, on ne gère plus les touches !
        // On garde juste la mise à jour interne de l'arme (les cooldowns)
        weapon.update(delta);

        // La méthode handleInput() a disparu, c'est le Controller qui s'en charge.
    }

    // Garde bien tes getters/setters (getWeapon, setAimAngle, etc.)
    public Weapon getWeapon() { return this.weapon; }
    public void setAimAngle(float angle) { this.aimAngle = angle; }
    public float getAimAngle() { return this.aimAngle; }
}
