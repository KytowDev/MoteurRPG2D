package io.github.rpg.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player extends Entity {

    private final Weapon weapon;
    private float aimAngle = 0f;

    public Player(Vector2 spawn_pos, int health, float speed) {
        super(spawn_pos, speed, health, "player", 14, 20);
        this.weapon = new Sword();
    }

    @Override
    protected void decideNextMove(float delta, Array<Rectangle> collisions, Player player, Array<Entity> allEntities) {
        weapon.update(delta);
    }

    public Weapon getWeapon() { return this.weapon; }
    public void setAimAngle(float angle) { this.aimAngle = angle; }
    public float getAimAngle() { return this.aimAngle; }
}
